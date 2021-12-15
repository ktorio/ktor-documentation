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

Open `gradle.properties`:

```kotlin
```
{src="gradle.properties" lines="17-18"}

Open `build.gradle.kts`:

```kotlin
```
{src="snippets/tutorial-website-interactive-persistence/build.gradle.kts" lines="3-4,20-21,25-28,32"}


## Update a model {id="model"}

A database table is represented in the Kotlin code by an object inheriting from the `org.jetbrains.exposed.sql.Table` class. You need to declare its columns and specify the column that is the primary key. Add the following object to `models/Article.kt`:

```kotlin
```
{src="snippets/tutorial-website-interactive-persistence/src/main/kotlin/com/example/models/Article.kt"}


## Connect to a database {id="connect_db"}

A [data access object](https://en.wikipedia.org/wiki/Data_access_object) (DAO) is a pattern that provides an interface to a database without exposing the details of the specific database. We’ll later define a DAOFacade interface to abstract our specific requests to the database.

Every database access using Exposed is started by obtaining a connection to the database. For that, you pass JDBC URL and the driver class name to the Database.connect() function. Put this code into the init() method of DatabaseFactory.

Create the `dao` package inside `com.example` and add a new `DatabaseFactory.kt` file:

```kotlin
```
{src="snippets/tutorial-website-interactive-persistence/src/main/kotlin/com/example/dao/DatabaseFactory.kt" lines="1-13,17,21"}

> Extract to custom configuration [](Configurations.xml#hocon-file).

After obtaining the connection all SQL statements should be placed inside a transaction.
Every database operation in Exposed needs an active transaction.

```kotlin
fun init() {
    // ...
    val database = Database.connect(jdbcURL, driverClassName)
    transaction(database) {
        // Statements here
    }
}
```

> The default database is set explicitly. If you have only one database, you can omit it, Exposed automatically uses “the default one” (the last connected one) for transactions.Note that the Database.connect() function doesn’t establish a real database connection until you call the transaction, it only creates a descriptor for future connections.


After we defined the `Articles` table, we return to the `init()` function of our `DatabaseFactory` object. Here, we call `SchemaUtils.create(Articles)` wrapped in transaction call at the bottom of the `init` function to instruct the database to create this table if it doesn’t yet exist:

```kotlin
fun init() {
    // ...
    val database = Database.connect(jdbcURL, driverClassName)
    transaction(database) {
        SchemaUtils.create(Articles)
    }
}
```

For our convenience, let’s create a utility function dbQuery inside the DatabaseFactory object, which we’ll be using for all our future requests to the database. Instead of using the transaction to access it in a blocking way, let’s take advantage of coroutines and start each query in its own coroutine:

```kotlin
```
{src="snippets/tutorial-website-interactive-persistence/src/main/kotlin/com/example/dao/DatabaseFactory.kt" lines="19-20"}

Result `DatabaseFactory.kt`:

```kotlin
```
{src="snippets/tutorial-website-interactive-persistence/src/main/kotlin/com/example/dao/DatabaseFactory.kt"}


Call the `init` function:

```kotlin
```
{src="snippets/tutorial-website-interactive-persistence/src/main/kotlin/com/example/Application.kt" lines="9-13"}


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

Open `com/example/plugins/Routing.kt` (initial, create, update, and delete).

Add sample article:

```kotlin
```
{src="snippets/tutorial-website-interactive-persistence/src/main/kotlin/com/example/plugins/Routing.kt" lines="22-28,67"}

Get all articles `dao.allArticles`:

```kotlin
```
{src="snippets/tutorial-website-interactive-persistence/src/main/kotlin/com/example/plugins/Routing.kt" lines="30-32"}


Post an article `dao.addNewArticle`:

```kotlin
```
{src="snippets/tutorial-website-interactive-persistence/src/main/kotlin/com/example/plugins/Routing.kt" lines="36-42"}

Get an article for showing and editing `dao.article`:

```kotlin
```
{src="snippets/tutorial-website-interactive-persistence/src/main/kotlin/com/example/plugins/Routing.kt" lines="43-50"}

Update (`dao.editArticle`) or delete (`dao.deleteArticle`) article:

```kotlin
```
{src="snippets/tutorial-website-interactive-persistence/src/main/kotlin/com/example/plugins/Routing.kt" lines="51-66"}




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