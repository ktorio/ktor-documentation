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
- In this tutorial, we'll add persistence to our website using the Exposed framework. We'll use the H2 local database to store articles.


## Add dependencies {id="add-dependencies"}

First, you need to add dependencies for the Exposed and H2 libraries. Open the `gradle.properties` file and specify library versions:

```kotlin
```
{src="gradle.properties" lines="17-18"}

Then, open `build.gradle.kts` and add the following dependencies:

```kotlin
```
{src="snippets/tutorial-website-interactive-persistence/build.gradle.kts" lines="3-4,20-21,25-28,32"}


## Update a model {id="model"}

Exposed uses the `org.jetbrains.exposed.sql.Table` class as a database table. You need to declare its columns and specify the primary key column. 

To update the `Article` model, open the `models/Article.kt` file and replace the existing code with the following:

```kotlin
```
{src="snippets/tutorial-website-interactive-persistence/src/main/kotlin/com/example/models/Article.kt"}

The `id`, `title`, and `body` columns will store information about our articles. The `id` column will act as a primary key. 

> If you [examine the types](https://www.jetbrains.com/help/idea/viewing-reference-information.html#type-info) of the properties in the `Articles` object, you'll see that they have the `Column` type with the necessary type argument: `id` has the type `Column<Int>`, and both `title` and `body` have the type `Column<String>`.
> 
{type="tip"}


## Connect to a database {id="connect_db"}

A [data access object](https://en.wikipedia.org/wiki/Data_access_object) (DAO) is a pattern that provides an interface to a database without exposing the details of the specific database. We'll define a `DAOFacade` interface later to abstract our specific requests to the database.

Every database access using Exposed is started by obtaining a connection to the database. For that, you pass JDBC URL and the driver class name to the `Database.connect` function. Create the `dao` package inside `com.example` and add a new `DatabaseFactory.kt` file. Then, put this code into the `init` function of `DatabaseFactory`:

```kotlin
```
{src="snippets/tutorial-website-interactive-persistence/src/main/kotlin/com/example/dao/DatabaseFactory.kt" lines="1-13,17,21"}

> Note that `driverClassName` and `jdbcURL` are hardcoded here. Ktor allows you to extract such settings to [custom configuration group](Configurations.xml#hocon-file).

### Create a table {id="create_table"}

After obtaining the connection, all SQL statements should be placed inside a transaction: 

```kotlin
fun init() {
    // ...
    val database = Database.connect(jdbcURL, driverClassName)
    transaction(database) {
        // Statements here
    }
}
```

In this code sample, the default database is passed explicitly to the `transaction` function. If you have only one database, you can omit it. In this case, Exposed automatically uses the last connected database for transactions.

> Note that the `Database.connect` function doesn't establish a real database connection until you call the transaction - it only creates a descriptor for future connections.


After defining the `Articles` table, we return to the `init` function of the `DatabaseFactory` object. Here, we call `SchemaUtils.create(Articles)` wrapped in transaction call at the bottom of the `init` function to instruct the database to create this table if it doesn't yet exist:

```kotlin
fun init() {
    // ...
    val database = Database.connect(jdbcURL, driverClassName)
    transaction(database) {
        SchemaUtils.create(Articles)
    }
}
```

### Execute queries {id="queries"}

For our convenience, let's create a utility function `dbQuery` inside the `DatabaseFactory` object, which we'll be using for all future requests to the database. Instead of using the transaction to access it in a blocking way, let's take advantage of coroutines and start each query in its own coroutine:

```kotlin
```
{src="snippets/tutorial-website-interactive-persistence/src/main/kotlin/com/example/dao/DatabaseFactory.kt" lines="19-20"}

The resulting `DatabaseFactory.kt` file should look as follows:

```kotlin
```
{src="snippets/tutorial-website-interactive-persistence/src/main/kotlin/com/example/dao/DatabaseFactory.kt"}


### Load database config at startup {id="startup"}

Finally, we need to load the created configuration at the application startup. Open `Application.kt` and call `DatabaseFactory.init` from `the Application.module` body:

```kotlin
```
{src="snippets/tutorial-website-interactive-persistence/src/main/kotlin/com/example/Application.kt" lines="9-13"}


## Implement persistence logic {id="persistence_logic"}

Now let's create an interface to abstract the necessary operations for updating articles. Create the `DAOFacade.kt` file inside the `dao` package and fill it with the following code:

```kotlin
```
{src="snippets/tutorial-website-interactive-persistence/src/main/kotlin/com/example/dao/DAOFacade.kt"}

We need to list all articles, view an article by its ID, add a new article, edit, or delete it. Since all these functions perform database queries under the hood, they are defined as suspending functions.

To implement `DAOFacade`, click a yellow bulb icon next to this interface and select **Implement interface**. In the invoked dialog, leave the default settings and click **OK**. In the **Implement members** dialog, choose all the functions and click **OK**. IntelliJ IDEA creates the `DAOFacadeImpl.kt` file inside the `dao` package. Let's implement all functions using Exposed DSL.

### Get all articles {id="get_all"}

Let's start with a function returning all entries. Our request is wrapped into a `dbQuery` call. We call the `Table.selectAll` extension function to get all the data from the database. The `Articles` object is a subclass of `Table`, so we use Exposed DSL methods to work with it.

```kotlin
```
{src="snippets/tutorial-website-interactive-persistence/src/main/kotlin/com/example/dao/DAOFacadeImpl.kt" lines="1-16,43"}

`Table.selectAll` returns an instance of `Query`, so to get the list of `Article` instances, we need to manually extract data for each row and convert it to our data class. We accomplish that using the helper function `resultRowToArticle` that builds an `Article` from the `ResultRow`.

The `ResultRow` provides a way to get the data stored in the specified `Column` by using a concise `get` operator, allowing us to use the bracket syntax, similar to an array or a map.

> The type of `Articles.id` is `Column<Int>`, which implements the `Expression` interface. That is why we can pass any column as an expression.

### Get an article {id="get_article"}

Now let's implement a function returning one article:

```kotlin
```
{src="snippets/tutorial-website-interactive-persistence/src/main/kotlin/com/example/dao/DAOFacadeImpl.kt" lines="18-23"}

The `select` function takes an extension lambda as an argument. The implicit receiver inside this lambda is of type `SqlExpressionBuilder`. You don't use this type explicitly, but it defines a bunch of useful operations on columns, which you use to build your queries. You can use comparisons (`eq`, `less`, `greater`), arithmetic operations (`plus`, `times`), check whether value belongs or doesn't belong to a provided list of values (`inList`, `notInList`), check whether the value is null or non-null, and many more.

`select` returns a list of `Query` values. As before, we convert them to articles. In our case, it should be one article, so we return it as a result.

### Add a new article {id="add_article"}

To insert a new article into the table, use the `Table.insert` function, which takes a lambda argument:

```kotlin
```
{src="snippets/tutorial-website-interactive-persistence/src/main/kotlin/com/example/dao/DAOFacadeImpl.kt" lines="25-31"}

Inside this lambda, we specify which value is supposed to be set for which column. The `it` argument has a type `InsertStatement` on which we can call the `set` operator taking column and value as arguments.


### Edit an article {id="edit_article"}

To update the existing article, the `Table.update` is used:

```kotlin
```
{src="snippets/tutorial-website-interactive-persistence/src/main/kotlin/com/example/dao/DAOFacadeImpl.kt" lines="33-38"}


### Delete an article {id="delete_article"}

Finally, use `Table.deleteWhere` to remove an article from the database:

```kotlin
```
{src="snippets/tutorial-website-interactive-persistence/src/main/kotlin/com/example/dao/DAOFacadeImpl.kt" lines="40-42"}



## Update routes {id="update_routes"}

Now we are ready to use implemented database operations inside route handlers. First, you need to create an instance of `DAOFacade`. Open the `plugins/Routing.kt` file and initialize `DAOFacade` inside the `routing` block. Let's also add a sample article to be inserted to the database before the application is started:

```kotlin
```
{src="snippets/tutorial-website-interactive-persistence/src/main/kotlin/com/example/plugins/Routing.kt" lines="22-28,67"}

To show all articles, call `dao.allArticles` inside the `get` handler:

```kotlin
```
{src="snippets/tutorial-website-interactive-persistence/src/main/kotlin/com/example/plugins/Routing.kt" lines="30-32"}


To post a new article, call the `dao.addNewArticle` function inside `post`:

```kotlin
```
{src="snippets/tutorial-website-interactive-persistence/src/main/kotlin/com/example/plugins/Routing.kt" lines="36-42"}

To get an article for showing and editing, use `dao.article` inside `get("{id}")` and `get("{id}/edit")`, respectively:

```kotlin
```
{src="snippets/tutorial-website-interactive-persistence/src/main/kotlin/com/example/plugins/Routing.kt" lines="43-50"}

Finally, go to the `post("{id}")` handler and use `dao.editArticle` to update an article and `dao.deleteArticle` to delete it:

```kotlin
```
{src="snippets/tutorial-website-interactive-persistence/src/main/kotlin/com/example/plugins/Routing.kt" lines="51-66"}

> You can find the resulting project for this tutorial here: [tutorial-website-interactive-persistence](https://github.com/ktorio/ktor-documentation/tree/main/codeSnippets/snippets/tutorial-website-interactive-persistence).


## Run the application {id="run_app"}

Let's see if our journal application is performing as expected. We can run our application by pressing the **Run** button next to `fun main(...)` in our `Application.kt`:

![Run Server](run-app.png){width="706"}

IntelliJ IDEA will start the application, and after a few seconds, we should see the confirmation that the app is running:

```Bash
[main] INFO  Application - Responding at http://0.0.0.0:8080
```

Open [`http://localhost:8080/`](http://localhost:8080/) in a browser and try to create, edit, and delete articles. Articles will be saved in the `build/db.mv.db` file. In IntelliJ IDEA, you can see the content of this file in a [Database tool window](https://www.jetbrains.com/help/idea/database-tool-window.html).
