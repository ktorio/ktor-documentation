[//]: # (title: Creating an interactive website)

<microformat>
<var name="example_name" value="tutorial-website"/>
<include src="lib.xml" include-id="download_example"/>
<p>
Used plugins: Routing, Freemarker, HTML DSL
</p>
</microformat>

In this tutorial, we're going to create an interactive website. Using the different plugins and integrations provided by Ktor, we will see how to host static content like images and HTML pages. We will see how supported HTML templating engines like [Freemarker](freemarker.md) make it easy to control how data from our application is rendered in the browser. By using [kotlinx.html](html_dsl.md), we'll learn about a domain-specific language that allows us to mix Kotlin code and markup directly, allowing us to write our site's display logic in pure Kotlin.

The goal of this tutorial is to write a minimal journal app. We'll start by seeing how Ktor can serve static files and pages, and then move on to dynamically rendering Kotlin objects representing blog entries in a nicely formatted fashion, making use of our template engine. To make things interactive, we will add the ability to submit new entries to our journal directly from the browser – leaving us with a nice way to temporarily store and view our thoughts, for example, our opinion on working through this tutorial:

![](ktor_journal.png){animated="true" width="706"}

## Prerequisites {id="prerequisites"}
<include src="lib.xml" include-id="plugin_prerequisites"/>

## Create a new Ktor project {id="create_ktor_project"}

To create a base project for our application using the Ktor plugin, [open IntelliJ IDEA](https://www.jetbrains.com/help/idea/run-for-the-first-time.html) and follow the steps below:

1. <include src="lib.xml" include-id="new_project_idea"/>
2. In the **New Project** wizard, choose **Ktor** from the list on the left. On the right pane, specify the following settings:
   ![New Ktor project](tutorial_website_new_project.png){width="729"}
  * **Name**: Specify a project name.
  * **Location**: Specify a directory for your project.
  * **Build System**: Make sure that _Gradle Kotlin_ is selected as a [build system](Gradle.xml).
  * **Website**: Leave the default `com.example` value as a domain used to generate a package name.
  * **Artifact**: This field shows a generated artifact name.
  * **Ktor Version**: Choose the latest Ktor version.
  * **Engine**: Leave the default _Netty_ [engine](Engines.md).
  * **Configuration in**: Choose _HOCON file_ to specify server parameters in a [dedicated configuration file](create_server.xml).
  * **Add sample code**: Disable this option to skip adding sample code for plugins.

   Click **Next**.
   
3. On the next page, add the **Routing**, **Freemarker**, and **HTML DSL** plugins:
   ![Ktor plugins](tutorial_website_new_project_plugins.png){width="729"}

   Click **Finish** and wait until IntelliJ IDEA generates a project and installs the dependencies.


## Examine the project {id="project_setup"}

To look at the structure of the [generated project](#create_ktor_project), let's invoke the [Project view](https://www.jetbrains.com/help/idea/project-tool-window.html):
![Initial project structure](tutorial_website_project_structure.png){width="481"}

* The `build.gradle.kts` file contains [dependencies](#dependencies) required for a Ktor server and plugins.
* The `main/resources` folder includes [configuration files](#configurations).
* The `main/kotlin` folder contains the generated [source code](#source_code).

### Dependencies {id="dependencies"}

First, let's open the `build.gradle.kts` file and examine added dependencies:
```kotlin
```
{src="snippets/tutorial-website/build.gradle.kts" lines="19-27"}

Let's briefly go through these dependencies one by one:

- `ktor-server-core` adds Ktor's core components to our project.
- `ktor-server-netty` adds the Netty [engine](Engines.md) to our project, allowing us to use server functionality without having to rely on an external application container.
- `ktor-server-freemarker` allows us to use the [Freemarker](freemarker.md) template engine, which we'll use to create the main page of our journal.
- `ktor-server-html-builder` adds the ability to use [kotlinx.html](html_dsl.md) directly from within the code. We'll use it to create code that can mix Kotlin logic with HTML markup.
- `logback-classic` provides an implementation of SLF4J, allowing us to see nicely formatted [logs](logging.md) in a console.
- `ktor-server-tests` and `kotlin-test-junit` allow us to [test](Testing.md) parts of our Ktor application without having to use the whole HTTP stack in the process. We will use this to define unit tests for our project.

### Configurations: application.conf and logback.xml {id="configurations"}

The generated project also includes the `application.conf` and `logback.xml` configuration files located in the `resources` folder:
* `application.conf` is a configuration file in [HOCON](https://en.wikipedia.org/wiki/HOCON) format. Ktor uses this file to determine the port on which it should run, and it also defines the entry point of our application.
   ```
   ```
  {src="snippets/tutorial-website/src/main/resources/application.conf" style="block"}

  If you'd like to learn more about how a Ktor server is configured, check out the [](Configurations.xml) help topic.
* `logback.xml` sets up the basic logging structure for our server. If you'd like to learn more about logging in Ktor, check out the [](logging.md) topic.

### Source code {id="source_code"}

The [application.conf](#configurations) configures the entry point of our application to be `com.example.ApplicationKt.module`. This corresponds to the `Application.module()` function in `Application.kt`, which is an application [module](Modules.md):

```kotlin
```
{src="snippets/tutorial-website/src/main/kotlin/com/example/Application.kt" lines="6-11"}

This module, in turn, calls the following extension functions:

* `configureRouting` is a function defined in `plugins/Routing.kt`, which is currently doesn't do anything:
   ```kotlin
   fun Application.configureRouting() {
       routing {
       }
   }
   ```
  We'll define the routes for our journal in the next chapters.

* `configureTemplating` is a function defined in `plugins/Templating.kt`, which installs and configures the `Freemarker` plugin:
   ```kotlin
   ```
  {src="snippets/tutorial-website/src/main/kotlin/com/example/plugins/Templating.kt" lines="8-10,12-13"}



## Static files and pages {id="static_files"}

Before we dive into making a _dynamic_ application, let's start by doing something a bit easier, but probably just as important – let's get Ktor to serve some *static* files. In the context of our journal, there are a number of things that we probably want to serve as static files – one example being a header image (a logo that identifies our site). 

1. Create the `files` folder inside `src/main/resources`.
2. Download the [ktor_logo.png](https://github.com/ktorio/ktor-documentation/blob/main/codeSnippets/snippets/tutorial-website/src/main/resources/files/ktor_logo.png) image file and add it to the created `files` folder.
3. To serve static content, we can use a specific routing function already built into Ktor named [static](Serving_Static_Content.md). The function takes two parameters: the route under which the static content should be made available, and a lambda where we can define the location from where the content should be served. In the `plugins/Routing.kt` file, let's change the implementation for `Application.configureRouting()` to look like this:

   ```kotlin
   ```
   {src="snippets/tutorial-website/src/main/kotlin/com/example/plugins/Routing.kt" lines="14-18,48-49"}
   
   This instructs Ktor that everything under the URL `/static` should be served using the `files` directory inside `resources`.

### Run the application {id="run_app"}

Let's *see* if our application is performing as expected. We can run our application by pressing the **Run** button next to `fun main(...)` in our `Application.kt`: 

![Run Server](run-app.png){width="706"}

IntelliJ IDEA will start the application, and after a few seconds, we should see the confirmation that the app is running:

```Bash
[main] INFO  Application - Responding at http://0.0.0.0:8080
```

Let's open [`http://localhost:8080/static/ktor_logo.png`](http://localhost:8080/static/ktor_logo.png) in a browser. We see that Ktor serves the static file:

![](ktor_logo_in_browser.png){width="706"}

### Add HTML page {id="html_page"}

Of course, we are not limited to images – HTML files, or CSS and JavaScript would work just as well. We can take advantage of this fact to add a small "About me" page as the first real part of our journal application – a static page that can contain some information about us, this project, or whatever else we might fancy.

To do so, let's create a new file inside `src/main/resources/files/` called `aboutme.html`, and fill it with the following contents:

```html
```
{src="snippets/tutorial-website/src/main/resources/files/aboutme.html"}

If we re-run the application and navigate to [`http://localhost:8080/static/aboutme.html`](http://localhost:8080/static/aboutme.html), we can see our first page in all its glory. As you can see, we can even reference other static files – like `ktor.png` – inside this HTML.

![](aboutme.png){width="706"}

Of course, we could also organize our files in subdirectories inside `files`; Ktor will automatically take care of mapping these paths to the correct URLs.

If you'd like to learn more about serving static files with Ktor, check out the [](Serving_Static_Content.md) help topic.

However, a static page that contains a few paragraphs can hardly be called a journal yet. Let's move on and learn about how *templates* can help us in writing pages that contain dynamic content, and how to control them from within our application.



## Home page with templates {id="home_page_templates"}

It's time to build the main page of our journal, which is in charge of displaying multiple journal entries. We will create this page with the help of a *template engine*. Template engines are quite common in web development, and Ktor supports a [variety of them](Working_with_views.md). In our case, we're going to choose [Freemarker](https://freemarker.apache.org/).

### Adjust Freemarker configuration {id="freemarker_config"}

The Ktor plugin for IntelliJ IDEA already [generated code](#source_code) for the Freemarker plugin:

```kotlin
```
{src="snippets/tutorial-website/src/main/kotlin/com/example/plugins/Templating.kt" lines="8-10,12-13"}

The `templateLoader` setting tells our application that Freemarker templates will be located in the `templates` directory. Let's also add the `outputFormat` as follows:

```kotlin
```
{src="snippets/tutorial-website/src/main/kotlin/com/example/plugins/Templating.kt" lines="8-13"}

The `outputFormat` setting helps convert control characters provided by the user to their corresponding HTML entities. This ensures that when one of our journal entries contains a String like `<b>Hello</b>`, it is actually printed as `<b>Hello</b>`, not **Hello**. This so-called [escaping](https://freemarker.apache.org/docs/dgui_misc_autoescaping.html) is an essential step in preventing [XSS attacks](https://owasp.org/www-community/attacks/xss/).


### Write the journal template {id="journal_template"}

Our journal main page should contain everything we need for a basic overview of our journal: a title, header image, a list of journal entries, and a form for adding new journal entries. To define this page layout, we use the Freemarker Template Language (`ftl`), which contains our HTML source as well as Freemarker variable definitions and instructions on how to use the variables in the context of the page.

Let's create a new `templates` directory inside `resources`. Inside this directory, we'll create a file called `index.ftl` and fill it with the following content:

```html
```
{src="snippets/tutorial-website/src/main/resources/templates/index.ftl"}

As you can see, we are using FTL syntax to define, access, and iterate over variables. The variable `entries` which we loop over has the type `kotlin.collections.List<com.example.BlogEntry>`. This is simply the fully-qualified name of a Kotlin `List<BlogEntry>`. However, we haven't created the `BlogEntry` class yet, but now that we are referencing it, it seems like a good time to fix that!

> If you'd like to learn more about Freemarker's syntax, check out [their official documentation](https://freemarker.apache.org/docs/dgui_quickstart.html).

### Define a model for the journal entries {id="define_model"}

As defined by our usage in the Freemarker template, the `BlogEntry` class needs two attributes: a `headline` and a `body`, both of type `String`. Let's create a file named `BlogEntry.kt` next to `Application.kt`, and fill it with the corresponding Kotlin data class, which can then be injected into the template:

```kotlin
```
{src="snippets/tutorial-website/src/main/kotlin/com/example/BlogEntry.kt" lines="1-3"}

It would also make sense if we defined a temporary storage for our entries. We can do this in the same file as a top-level declaration:

```kotlin
```
{src="snippets/tutorial-website/src/main/kotlin/com/example/BlogEntry.kt" lines="5-8"}

At this point, we have defined a template and the model that will be used for rendering it. Now, Ktor just needs to pass our stored journal entries and serve the resulting page.

### Serve the templated content {id="serve_template"}

The overview page we just templated is the center point of our application. So, it would make sense to make it available under the `/` route. Let's add a route for it to the `routing` block inside our `Application.configureRouting()` (the `plugins/Routing.kt` file):


```kotlin
```
{src="snippets/tutorial-website/src/main/kotlin/com/example/plugins/Routing.kt" lines="19-21"}

We can now run the application. Opening [`http://localhost:8080/`](http://localhost:8080/) in a browser, we should see our header image, headline and subtitle, and a list of journal entries (well, just one for now) alongside a form for submitting new entries:

![Freemarker Browser Output](main_page.png){width="706"}

It looks like our display logic is working just fine! Now, we only need to make the "Submit" button work, and we'll be able to view and add new entries for our journal!


## Submit a form {id="submit_form"}

The `<form>` we defined in the previous chapter sends a `POST` request to `/submit` containing the `headline` and `body` of our new journal entry. Let's make our application correctly consume this information and submission of new journal entries! We define a handler for the `/submit` route inside our `Application.module()`'s `routing` block like this:

```kotlin
```
{src="snippets/tutorial-website/src/main/kotlin/com/example/plugins/Routing.kt" lines="22-27,47"}

[receiveParameters](requests.md#form_parameters) allows us to parse form data (for both `urlencoded` and `multipart`). We then extract the `headline` and `body` fields from the form, ensuring they are both not null, and create a new `BlogEntry` object from this information, adding it to the beginning of our `blogEntries` list.

For more detailed information on the fancy features that are available in the context of Ktor's request model, check out the [](requests.md) topic.

To show the user that the submission was successful, we still want to send back a bit of HTML. We could re-use our knowledge and create a Freemarker template for this "Success" page as well – but to cover some more Ktor functionality, we will try an alternative approach instead. When we autocomplete on the `call` object, we can see that Ktor allows us to respond to requests using a variety of functions:

![Respond](respond.png){width="658"}

Amongst these is `call.respondHtml`. This function allows us to craft our HTML response using Kotlin syntax, thanks to Ktor's integration with [kotlinx.html](html_dsl.md) (a [DSL](https://kotlinlang.org/docs/type-safe-builders.html) designed for seamlessly combining Kotlin code and HTML-like tags).

At the bottom of the `post("/submit")` handler block, let's add the following code:

```kotlin
```
{src="snippets/tutorial-website/src/main/kotlin/com/example/plugins/Routing.kt" lines="28-46"}

Notice how this code combines Kotlin-specific logic (like `count`ing the entries or using string interpolation) with HTML-like syntax!

To test the route, let's re-run our application, navigate to [`http://localhost:8080/`](http://localhost:8080/), and submit a new entry. If everything has gone according to play, we'll now see the HTML page, courtesy of kotlinx.html!

![Static HTML](submit.png){width="706"}

## We're done! {id="done"}

It's time to pat ourselves on the back – we've put together a nice little journal application, and have learned about many topics in the meantime. From static files to templating, from basic routing to kotlinx.html, we've covered a lot of ground.

![](ktor_journal.png){animated="true" width="706"}

This concludes the guided part of this tutorial. We have included the final state of the journal application in the [codeSnippets](https://github.com/ktorio/ktor-documentation/tree/main/codeSnippets) project: [tutorial-websi](https://github.com/ktorio/ktor-documentation/tree/main/codeSnippets/snippets/tutorial-website). But of course, your journey doesn't have to stop here. Check out the _What's next_ section to get an idea of how you could expand the application, and where to go if you need help in your endeavors!


## What's next {id="whats_next"}

At this point, you should have gotten a basic idea of how to serve static files with Ktor and how the integration of plugins such as Freemarker or kotlinx.html can enable you to write basic applications that can even react to user input.

### Feature requests {id="feature_requests"}

At this point, our journal application is still rather barebones, so of course, it might be a fun challenge to add more features to the project and learn even more about building interactive sites with Kotlin and Ktor. To get you started, here are a few ideas of how the application could still be improved, in no particular order:

- **Make it consistent!** You would usually not mix Freemarker and kotlinx.html – we've taken some liberty here to explore more than one way of structuring your application. Consider powering your whole journal with kotlinx.html or Freemarker, and make it consistent!
- **Authentication!** Our current version of the journal allows all visitors to post content to our journal. We could use [Ktor's authentication plugins](authentication.md) to ensure that only select users can post to the journal while keeping read access open to everyone.
- **Persistence!** Currently, all our journal entries vanish when we stop our application, as we are only storing them in a variable. You could try integrating your application with a database like PostgreSQL or MongoDB, using one of the plenty projects that allow database access from Kotlin, like [Exposed](https://github.com/JetBrains/Exposed) or [KMongo](https://litote.org/kmongo/).
- **Make it look nicer!** The stylesheets for the journal are currently rudimentary at best. Consider creating your own style sheet and serving it as a static `.css` file from Ktor!
- **Organize your routes!** As the complexity of our application increases, so does the number of routes we try to support. For bigger applications, we usually want to add structure to our routing – like separating routes out into separate files. If you'd like to learn about different ways to organize your routes with Ktor, check out the [](Routing_in_Ktor.md) help topic.
