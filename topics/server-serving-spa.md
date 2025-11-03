[//]: # (title: Serving single-page applications)

<show-structure for="chapter" depth="2"/>

<tldr>
<var name="example_name" value="single-page-application"/>
<include from="lib.topic" element-id="download_example"/>
</tldr>

<link-summary>
Ktor provides the ability to serve single-page applications, including React, Angular, Vue, and so on.
</link-summary>

Ktor provides the ability to serve single-page applications, including React, Angular, or Vue.


## Add dependencies {id="add_dependencies"}

To serve a single-page application, you only need the [ktor-server-core](server-dependencies.topic#add-ktor-dependencies) dependency.
Any specific dependencies are not required.


## Serve an application {id="configure"}

To serve a single-page application, you need to define where you want the content to be served from: a local filesystem or the classpath.
You need at least to specify a folder/resource package containing a single-page application.

### Serve framework-specific applications {id="serve-framework"}

You can serve a build of your single-page application created using a specific framework, such as React, Angular, Vue, and so on. 
Suppose we have the `react-app` folder in a project root containing a React application.
The application has the following structure and the `index.html` file as the main page:

```text
react-app
├── index.html
├── ...
└── static
    └── ...
```

To serve this application, call [singlePageApplication](https://api.ktor.io/ktor-server-core/io.ktor.server.http.content/single-page-application.html) inside the [routing](server-routing.md) block 
and pass the folder name to the `react` function:

```kotlin
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.routing.*

fun Application.module() {
    routing {
        singlePageApplication {
            react("react-app")
        }
    }
}
```

Ktor looks up `index.html` automatically. 
To learn how to customize a default page, see [](#serve-customize).

> For other frameworks, use corresponding functions, such as `angular`, `vue`, `ember`, and so on.



### Customize serving settings {id="serve-customize"}

To demonstrate how to serve a single-page application from resources, let's suppose our application is placed inside the `sample-web-app` resource package, which has the following structure:

```text
sample-web-app
├── main.html
├── ktor_logo.png
├── css
│   └──styles.css
└── js
    └── script.js
```

To serve this application, the following configuration is used:

```kotlin
```
{src="snippets/single-page-application/src/main/kotlin/com/example/Application.kt" include-lines="3-13,15-17"}

- `useResources`: Enables serving an application from a resource package.
- `filesPath`: Specifies the path under which an application is located.
- `defaultPage`: Specifies `main.html` as a default resource to serve.
- `ignoreFiles`: Ignores paths that contain `.txt` at the end.

You can find the full example here: [single-page-application](https://github.com/ktorio/ktor-documentation/tree/%ktor_version%/codeSnippets/snippets/single-page-application).
