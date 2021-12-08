[//]: # (title: Creating a static website)

<microformat>
<var name="example_name" value="tutorial-website-static"/>
<include src="lib.xml" include-id="download_example"/>
<p>
Used plugins: Routing, Static Content
</p>
</microformat>

<excerpt>Learn how to create a static website using the Static Content plugin.</excerpt>

In this tutorial, we're going to create an interactive website. Using the different plugins and integrations provided by Ktor, we will see how to host static content like images and HTML pages. We will see how supported HTML templating engines like [FreeMarker](freemarker.md) make it easy to control how data from our application is rendered in the browser. By using [kotlinx.html](html_dsl.md), we'll learn about a domain-specific language that allows us to mix Kotlin code and markup directly, allowing us to write our site's display logic in pure Kotlin.

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

3. On the next page, add the **Routing**, **FreeMarker**, and **HTML DSL** plugins:
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
- `ktor-server-freemarker` allows us to use the [FreeMarker](freemarker.md) template engine, which we'll use to create the main page of our journal.
- `ktor-server-html-builder` adds the ability to use [kotlinx.html](html_dsl.md) directly from within the code. We'll use it to create code that can mix Kotlin logic with HTML markup.
- `logback-classic` provides an implementation of SLF4J, allowing us to see nicely formatted [logs](logging.md) in a console.
- `ktor-server-tests` and `kotlin-test-junit` allow us to [test](Testing.md) parts of our Ktor application without having to use the whole HTTP stack in the process.

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

* `configureTemplating` is a function defined in `plugins/Templating.kt`, which installs and configures the `FreeMarker` plugin:
   ```kotlin
   ```
  {src="snippets/tutorial-website/src/main/kotlin/com/example/plugins/Templating.kt" lines="8-10,12-13"}



## Static files and pages {id="static_files"}

Before we dive into making a _dynamic_ application, let's start by doing something a bit easier, but probably just as important – let's get Ktor to serve some *static* files. In the context of our journal, there are a number of things that we probably want to serve as static files – one example being a header image (a logo that identifies our site).

1. Create the `files` folder inside `src/main/resources`.
2. Download the [ktor_logo.png](https://github.com/ktorio/ktor-documentation/blob/main/codeSnippets/snippets/tutorial-website/src/main/resources/files/ktor_logo.png) image file and add it to the created `files` folder.
3. To serve static content, we can use a specific routing function already built into Ktor named [static](Serving_Static_Content.md). The function takes two parameters: the route under which the static content should be made available, and a lambda where we can define the location from where the content should be served.

   In the `plugins/Routing.kt` file, let's change the implementation for `Application.configureRouting()` to look like this:

   ```kotlin
   ```
   {src="snippets/tutorial-website/src/main/kotlin/com/example/plugins/Routing.kt" lines="3-18,48-49"}

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
