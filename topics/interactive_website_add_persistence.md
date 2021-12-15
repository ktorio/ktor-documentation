[//]: # (title: Adding persistence)

<microformat>
<var name="example_name" value="tutorial-website-interactive-persistence"/>
<include src="lib.xml" include-id="download_example"/>
<p>
Used libraries: <a href="https://github.com/JetBrains/Exposed">Exposed</a>, <a href="https://github.com/h2database/h2database">h2database</a>
</p>
</microformat>

<excerpt>Learn how to add persistence to a website using the Exposed ORM framework.</excerpt>

In this series of tutorials, we'll show you how to create a website in Ktor:
- In the first tutorial, we showed how to host [static content](creating_static_website.md) like images and HTML pages.
- In the second tutorial, we added interactivity and created a simple [blog application](creating_interactive_website.md) using the FreeMarker template engine.
- In this tutorial, we'll add persistence to our website using the Exposed framework.


## Add dependencies {id="add-dependencies"}

```kotlin
```
{src="snippets/tutorial-website-interactive-persistence/build.gradle.kts" lines="21,25-28,32"}


## Update a model {id="model"}

```kotlin
```
{src="snippets/tutorial-website-interactive-persistence/src/main/kotlin/com/example/models/Article.kt"}


## Connect to a database {id="connect_db"}

Create the `dao` package inside `com.example` and add a new `DatabaseFactory.kt` file:

```kotlin
```
{src="snippets/tutorial-website-interactive-persistence/src/main/kotlin/com/example/dao/DatabaseFactory.kt"}

Call the `init` function:

```kotlin
```
{src="snippets/tutorial-website-interactive-persistence/src/main/kotlin/com/example/Application.kt" lines="9-13"}

Configuration:

```kotlin
```
{src="snippets/tutorial-website-interactive-persistence/src/main/resources/application.conf" lines="11-15"}

## Create and implement DAOFacade {id="facade"}

Create the `DAOFacade.kt` file inside the `dao` package:

```kotlin
```
{src="snippets/tutorial-website-interactive-persistence/src/main/kotlin/com/example/dao/DAOFacade.kt"}

Implement `DAOFacadeImpl.kt` (intention in IDEA):

```kotlin
```
{src="snippets/tutorial-website-interactive-persistence/src/main/kotlin/com/example/dao/DAOFacadeImpl.kt"}

## Update routes {id="update_routes"}

Open `com/example/plugins/Routing.kt` (initial, create, update, and delete):

```kotlin
```
{src="snippets/tutorial-website-interactive-persistence/src/main/kotlin/com/example/plugins/Routing.kt"}



## Run the application {id="run_app"}

Let's see if our journal application is performing as expected. We can run our application by pressing the **Run** button next to `fun main(...)` in our `Application.kt`:

![Run Server](run-app.png){width="706"}

IntelliJ IDEA will start the application, and after a few seconds, we should see the confirmation that the app is running:

```Bash
[main] INFO  Application - Responding at http://0.0.0.0:8080
```

Open [`http://localhost:8080/`](http://localhost:8080/) in a browser and try to create, edit, and delete articles. Articles will be saved in `build/db.mv.db` (show Database tool window).



## What's next {id="whats_next"}

At this point, you should have gotten a basic idea of how to serve static files with Ktor and how the integration of plugins such as FreeMarker or kotlinx.html can enable you to write basic applications that can even react to user input.

### Feature requests {id="feature_requests"}

At this point, our journal application is still rather barebones, so of course, it might be a fun challenge to add more features to the project and learn even more about building interactive sites with Kotlin and Ktor. To get you started, here are a few ideas of how the application could still be improved, in no particular order:

- **Authentication!** Our current version of the journal allows all visitors to post content to our journal. We could use [Ktor's authentication plugins](authentication.md) to ensure that only select users can post to the journal while keeping read access open to everyone.
- **Make it look nicer!** The stylesheets for the journal are currently rudimentary at best. Consider creating your own style sheet and serving it as a static `.css` file from Ktor!
- **Organize your routes!** As the complexity of our application increases, so does the number of routes we try to support. For bigger applications, we usually want to add structure to our routing – like separating routes out into separate files. If you'd like to learn about different ways to organize your routes with Ktor, check out the [](Routing_in_Ktor.md) help topic.