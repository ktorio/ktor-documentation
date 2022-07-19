[//]: # (title: Creating a static website)

<show-structure for="chapter" depth="2"/>

<tldr>
<var name="example_name" value="tutorial-website-static"/>
<include from="lib.topic" element-id="download_example"/>
<p>
<b>Used plugins</b>: <a href="Routing_in_Ktor.md">Routing</a>, <a href="Serving_Static_Content.md">Static Content</a>
</p>
</tldr>

<link-summary>Learn how to create a static website using the Static Content plugin.</link-summary>

In this series of tutorials, we'll show you how to create a simple blog application in Ktor:
- First, we'll show how to host static content like images and HTML pages.
- In the next tutorial, we'll make our application [interactive](creating_interactive_website.md) using the FreeMarker template engine.
- Finally, we'll [add persistence](interactive_website_add_persistence.md) to our website using the Exposed framework.

![](ktor_journal.png){animated="true" width="502" border-effect="rounded"}

## Prerequisites {id="prerequisites"}
<include from="lib.topic" element-id="plugin_prerequisites"/>

## Create a new Ktor project {id="create_ktor_project"}

To create a base project for our application using the Ktor plugin, [open IntelliJ IDEA](https://www.jetbrains.com/help/idea/run-for-the-first-time.html) and follow the steps below:

1. <include from="lib.topic" element-id="new_project_idea"/>
2. In the **New Project** wizard, choose **Ktor** from the list on the left. On the right pane, specify the following settings:
   ![New Ktor project](tutorial_website_new_project.png){width="706" border-effect="rounded"}
* **Name**: Specify a project name.
* **Location**: Specify a directory for your project.
* **Build System**: Make sure that _Gradle Kotlin_ is selected as a [build system](server-dependencies.topic).
* **Website**: Leave the default `example.com` value as a domain used to generate a package name.
* **Artifact**: This field shows a generated artifact name.
* **Ktor version**: Choose the latest Ktor version.
* **Engine**: Leave the default _Netty_ [engine](Engines.md).
* **Configuration in**: Choose _HOCON file_ to specify server parameters in a [dedicated configuration file](create_server.topic).
* **Add sample code**: Disable this option to skip adding sample code for plugins.

Click **Next**.

3. On the next page, add the **Routing**, **Static Content**, and **FreeMarker** plugins:
   ![Ktor plugins](tutorial_website_new_project_plugins.png){width="706" border-effect="rounded"}

   Click **Create** and wait until IntelliJ IDEA generates a project and installs the dependencies.


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
{src="snippets/tutorial-website-static/build.gradle.kts" lines="19-26"}

Let's briefly go through these dependencies one by one:

- `ktor-server-core` adds Ktor's core components to our project.
- `ktor-server-netty` adds the Netty [engine](Engines.md) to our project, allowing us to use server functionality without having to rely on an external application container.
- `ktor-server-freemarker` allows us to use the [FreeMarker](freemarker.md) template engine, which we'll use to create the main page of our journal.
- `logback-classic` provides an implementation of SLF4J, allowing us to see nicely formatted [logs](logging.md) in a console.
- `ktor-server-test-host` and `kotlin-test-junit` allow us to [test](Testing.md) parts of our Ktor application without having to use the whole HTTP stack in the process.

### Configurations: application.conf and logback.xml {id="configurations"}

The generated project also includes the `application.conf` and `logback.xml` configuration files located in the `resources` folder:
* `application.conf` is a configuration file in [HOCON](https://en.wikipedia.org/wiki/HOCON) format. Ktor uses this file to determine the port on which it should run, and it also defines the entry point of our application.
   ```
   ```
  {src="snippets/tutorial-website-static/src/main/resources/application.conf" style="block"}

  If you'd like to learn more about how a Ktor server is configured, check out the [](Configurations.topic) help topic.
* `logback.xml` sets up the basic logging structure for our server. If you'd like to learn more about logging in Ktor, check out the [](logging.md) topic.

### Source code {id="source_code"}

The [application.conf](#configurations) configures the entry point of our application to be `com.example.ApplicationKt.module`. This corresponds to the `Application.module()` function in `Application.kt`, which is an application [module](Modules.md):

```kotlin
```
{src="snippets/tutorial-website-static/src/main/kotlin/com/example/Application.kt" lines="6-11"}

This module, in turn, calls the following extension functions:

* `configureRouting` is a function defined in `plugins/Routing.kt`, which is currently doesn't do anything:
   ```kotlin
   fun Application.configureRouting() {
       routing {
       }
   }
   ```

* `configureTemplating` is a function defined in `plugins/Templating.kt`, which installs and configures the `FreeMarker` plugin:
   ```kotlin
   ```
  {src="snippets/tutorial-website-static/src/main/kotlin/com/example/plugins/Templating.kt" lines="7-11"}

  We'll show how to use FreeMarker templates in the next tutorial: [](creating_interactive_website.md).



## Static files and pages {id="static_files"}

Before we dive into making a _[dynamic](creating_interactive_website.md)_ application, let's start by doing something a bit easier, but probably just as important – let's get Ktor to serve some *static* files. In the context of our journal, there are a number of things that we probably want to serve as static files – one example being a header image (a logo that identifies our site).

1. Create the `files` folder inside `src/main/resources`.
2. Download the [ktor_logo.png](https://github.com/ktorio/ktor-documentation/blob/%ktor_version%/codeSnippets/snippets/tutorial-website-static/src/main/resources/files/ktor_logo.png) image file and add it to the created `files` folder.
3. To serve static content, we can use a specific routing function already built into Ktor named [static](Serving_Static_Content.md). The function takes two parameters: the route under which the static content should be made available, and a lambda where we can define the location from where the content should be served.

   In the `plugins/Routing.kt` file, let's change the implementation for `Application.configureRouting()` to look like this:

   ```kotlin
   ```
   {src="snippets/tutorial-website-static/src/main/kotlin/com/example/plugins/Routing.kt" lines="3-13"}

   This instructs Ktor that everything under the URL `/static` should be served using the `files` directory inside `resources`.

### Run the application {id="run_app"}

Let's *see* if our application is performing as expected. We can run our application by pressing the **Run** button next to `fun main(...)` in our `Application.kt`:

![Run Server](run-app.png){width="706"}

IntelliJ IDEA will start the application, and after a few seconds, we should see the confirmation that the app is running:

```Bash
[main] INFO  Application - Responding at http://0.0.0.0:8080
```

Let's open [`http://localhost:8080/static/ktor_logo.png`](http://localhost:8080/static/ktor_logo.png) in a browser. We see that Ktor serves the static file:

![](ktor_logo_in_browser.png){width="552"}

### Add HTML page {id="html_page"}

Of course, we are not limited to images – HTML files, or CSS and JavaScript would work just as well. We can take advantage of this fact to add a small 'About me' page as the first real part of our journal application – a static page that can contain some information about us, this project, or whatever else we might fancy.

To do so, let's create a new file inside `src/main/resources/files/` called `aboutme.html`, and fill it with the following contents:

```html
```
{src="snippets/tutorial-website-static/src/main/resources/files/aboutme.html"}

If we re-run the application and navigate to [`http://localhost:8080/static/aboutme.html`](http://localhost:8080/static/aboutme.html), we can see our first page in all its glory. As you can see, we can even reference other static files – like `ktor.png` – inside this HTML.

![](aboutme.png){width="552"}

Of course, we could also organize our files in subdirectories inside `files`; Ktor will automatically take care of mapping these paths to the correct URLs.

However, a static page that contains a few paragraphs can hardly be called a journal yet. Let's move on and learn about how templates can help us in writing pages that contain dynamic content, and how to control them from within our application: [](creating_interactive_website.md).

> You can find the resulting project for this tutorial here: [tutorial-website-static](https://github.com/ktorio/ktor-documentation/tree/%ktor_version%/codeSnippets/snippets/tutorial-website-static).