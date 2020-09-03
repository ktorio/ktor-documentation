[//]: # (layout: post)
[//]: # (title: "OpenAPI generation in the Ktor plugin and website")
[//]: # (categories: plugin)
[//]: # (featured: true)
[//]: # (#image: /blog/images/openapi_ktor_idea_plugin.svg)

In this post I'm going to explain how the Ktor IntelliJ IDEA plugin generates code from Swagger/OpenAPI models
as 0.2.0 and why.

In case you didn't know, Swagger/OpenAPI is a specification for defining APIs as Yaml or JSON model files.
And it has some tooling available: code generation from those models, simulators, and visualizers, etc.

But first, let's take a look into how this all started:

### The ktor.io/start project

It was all started when [Hadi Hariri](https://hadihariri.com/){ target="_blank"} suggested to creating a web application
where people could create Ktor projects directly in the browser and download a ready-to-use ZIP file.

This would help people get started with Ktor without having to manually setup the environment, since IntelliJ IDEA
is already able to open Gradle and Maven projects.

In any normal circumstances you would have created a full website, and rendered a ZIP file
directly from the backend, potentially using Ktor. But in fact, with Kotlin, there is a better way:
It just so happens that Kotlin supports generating JavaScript. It is called Kotlin/JS. And nowadays JavaScript
is so powerful that you can create a ZIP file in the frontend and download it to a local file without
requiring a backend at all.

I started this project in my own GitHub account, as a proposal. It lived some time in a custom URL
using the GitHub Pages.

After sometime it was promoted, moved to the ktorio organization, and now lives at <https://ktor.io/start>{target="_blank"}.

### The Ktor Plugin for IntelliJ IDEA

In case you didn't know, starting with Kotlin 1.2, there is experimental support for MPP (Multi Platform Projects)
in Kotlin. This means that you can create a project with several modules, some of them targeting JS, others the JVM,
and others a Common subset of Kotlin that can be referenced in JS and JVM projects. (And even if it isn't relevant for this,
MPP also supports Kotlin/Native for targeting native executables without a VM).

This was a great opportunity to create a plugin for IntelliJ IDEA reusing the code for project generation and easily
maintaining both, the website and the plugin.

It worked like a charm, I was able to publish the IntelliJ IDEA plugin (that includes JVM code), and the ktor.io/start
(that includes Kotlin/JS code).

### In the meantime

In the meantime, I was doing other things too that would later help to give form to the OpenAPI generator in the plugin:

#### OpenAPI generation

I started [far from ideal PR to the swagger-codegen project](https://github.com/swagger-api/swagger-codegen/pull/8092).
It was far from ideal because I did it "fast" to get feedback and clean (squash, drop, adjust, etc.) the commits after the feedback.
It was like that also because the swagger-codegen project uses Java and Mustache for code generation.

I'm now used to using Kotlin, and Java requires a lot of ceremony for things that are super fast to code in Kotlin.
Also Mustache is not designed to handle indentations well and even when simple, it is a bit cryptic in a similar
fashion as clojure. I had to invest a lot of time, trying to get the right indentation. And being as Mustache is logicless
you end up having to put a lot of code on the Java-side for formatting and not just for the model part. So it was a bit awkward. 

In the plugin code generation, I used a Kotlin DSL with an Indenter class, this allowed me to generate the code directly in
Kotlin without having to worry about indentation.

As an anecdote: just when I did the PR, the Swagger project had a major fork called OpenAPI with a lot of the main
developers from swagger moving there. The developer that did the initial Ktor generation suggested that I do the
PR there. But the problem was that the package names changed, and it would require a lot of extra effort from me, and maybe the generation was already not that good. So I deferred that effort until I had clear picture of how to generate the model properly.
I plan to make that PR someday, after we get some feedback from the plugin and the generated code stabilizes.

#### Ktor-Springer experiment

I did an experiment with Ktor in my own user to define the routes in Ktor, instead with a DSL, with annotated methods,
similar to Spring. I created a repo here: <https://github.com/soywiz/ktor-springer>.

[Ilya Ryzhenkov](https://github.com/orangy) suggested that I declare the routes as suspend methods, in an interface,
and then define the logic in a class implementing that interface. So we can reuse the interface to create a HTTP API
client calling those methods. That was a really interesting proposal, so I did it!

My main concern there was being able to check things from the request, or the headers without having to include
it as part of the method signature. But I had an idea: since all the API methods are `suspend`, we have access
to the `CoroutineContext`, and before calling the method, we can assign an object with the ApplicationCall to the
context, so that's what I did. That only thing that is not that good is that right now Kotlin doesn't support suspend properties.
So to access the call, instead of just typing `call`, you have to type `call()`. That's a pretty minor issue though.

### Importing Swagger/OpenAPI in the generation plugin

A few weeks ago, I revisited the swagger-codegen idea. Since I was working on the plugin,
that already had a nice generic framework for code generation that worked in both JS and JVM.
Things linked together nicely, and I decided to just make the Swagger code generation a part of the plugin.

So I made a quick and dirty initial approach: parsed the JSON model files (YAML are left out at the moment for simplicity,
but you can convert YAML models to JSON easily with any tool that works for that), partially interpreted the most important parts
of the model, and rendered a simple set of route handlers for it.
It was incomplete, with no client generation, but the results were a good start for implementing a new API without
having to write it from scratch. I pushed it to the website since it doesn't require plugin reviewing.

I had a week of holidays, and when returned back to work, decided to change the generated code to something else.

### Server & Client code generation

I wanted to generate both routes for Ktor, and a client using the asynchronous Ktor HttpClient. And I recalled the
ktor-springer experiment I did and wanted to try it out.

So what I did was to create all the models as data classes, and an interface with all the routes as suspend methods,
using all the information provided by the Swagger model: documentation for the route and fields, the path, and sources
for the parameters, etc.

Then I generated a class implementing those methods returning some stubs, and with dummy ifs throwing some potential
exceptions, so you can define the code. It also uses the JSON Schema Validation to validate parameters when available
as part of the model.

The good thing about this, is that by using the interface + a JVM Proxy and some tricks, I'm able to create a client
targeting a specific endpoint without any additional code. Which is pretty cool.
 
### Ideas for the future

It would be nice if the client at least, works for MPP projects. But the JVM is the only target right now with full reflective capabilities.
With all the foundations set, we can change the generated code to something else, or for example to work
reflectionless by generating the code handling the routes, parsing the parameters, and calling the methods from the interface.
And the JSON parsing/generation can work using <https://github.com/Kotlin/kotlinx.serialization>. So it should be viable.
As long as you keep your API as described in the model, the only code you have to change is the one for the server. 

Another possible thing to do which would be nice is to generate a definition for [the HTTP Client integrated with IntelliJ IDEA Ultimate](https://ktor.io/quickstart/guides/api.html#first-request-intellij)
to test the API directly in the IDE.

### A quick look to the generated code

<ul class="nav nav-tabs" id="myTab" role="tablist">
  <li class="nav-item">
    <a class="nav-link active" id="swagger-realworld-json-tab" data-toggle="tab" href="#swagger-realworld-json" role="tab" aria-controls="swagger-realworld-json" aria-selected="true"><code>swagger-realworld.json</code></a>
  </li>
  <li class="nav-item">
    <a class="nav-link" id="application-kt-tab" data-toggle="tab" href="#application-kt" role="tab" aria-controls="application-kt" aria-selected="false"><code>application.kt</code></a>
  </li>
  <li class="nav-item">
    <a class="nav-link" id="api-kt-tab" data-toggle="tab" href="#api-kt" role="tab" aria-controls="api-kt" aria-selected="false"><code>api.kt</code></a>
  </li>
  <li class="nav-item">
    <a class="nav-link" id="backend-kt-tab" data-toggle="tab" href="#backend-kt" role="tab" aria-controls="backend-kt" aria-selected="false"><code>backend.kt</code></a>
  </li>
  <li class="nav-item">
    <a class="nav-link" id="frontend-kt-tab" data-toggle="tab" href="#frontend-kt" role="tab" aria-controls="frontend-kt" aria-selected="false"><code>frontend.kt</code></a>
  </li>
</ul>
<div class="tab-content" id="myTabContent">
  <div class="tab-pane fade show active" id="swagger-realworld-json" role="tabpanel" aria-labelledby="swagger-realworld-json-tab">
    <div class="code-snippet" data-src="{{ '/blog/samples/openapi/swagger.json' }}" data-lang="json"></div>
  </div>
  <div class="tab-pane fade show" id="application-kt" role="tabpanel" aria-labelledby="application-kt-tab">
    <div class="code-snippet" data-src="{{ '/blog/samples/openapi/application.kt' }}" data-lang="kotlin"></div>
  </div>
  <div class="tab-pane fade show" id="api-kt" role="tabpanel" aria-labelledby="api-kt-tab">
    <div class="code-snippet" data-src="{{ '/blog/samples/openapi/swagger-api.kt' }}" data-lang="kotlin"></div>
  </div>
  <div class="tab-pane fade show" id="backend-kt" role="tabpanel" aria-labelledby="backend-kt-tab">
    <div class="code-snippet" data-src="{{ '/blog/samples/openapi/swagger-backend.kt' }}" data-lang="kotlin"></div>
  </div>
  <div class="tab-pane fade show" id="frontend-kt" role="tabpanel" aria-labelledby="frontend-kt-tab">
    <div class="code-snippet" data-src="{{ '/blog/samples/openapi/swagger-frontend.kt' }}" data-lang="kotlin"></div>
  </div>
</div>

This is the code the plugin is generating right now. It is not final, so any feedback is welcome!