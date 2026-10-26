[//]: # (title: Integrate a database with Kotlin, Ktor, and Exposed)

<show-structure for="chapter" depth="2"/>

<tldr>
<var name="example_name" value="tutorial-server-db-integration"/>
<include from="lib.topic" element-id="download_example"/>
<p>
<b>Used plugins</b>: <a href="server-static-content.md">Static Content</a>,
<a href="server-serialization.md">Content Negotiation</a>, <a href="server-status-pages.md">Status Pages</a>,
<a href="server-serialization.md">kotlinx.serialization</a>,
<a href="server-dependency-injection.md">Dependency Injection</a>,
<a href="https://github.com/ktorio/ktor-plugin-registry/tree/main/repository/org.jetbrains/server-exposed">Exposed</a>,
<a href="https://github.com/ktorio/ktor-plugin-registry/tree/main/repository/org.jetbrains/server-postgres">PostgreSQL</a>
</p>
</tldr>

<card-summary>
Learn the process of connecting Ktor services to database repositories with the Exposed SQL Library.
</card-summary>

<link-summary>
Learn the process of connecting Ktor services to database repositories with the Exposed SQL Library.
</link-summary>

<web-summary>
Learn how to use Kotlin and Ktor to build a single-page application (SPA) in which RESTful services link to
database repositories. It uses the Exposed SQL library and allows you to fake repositories for testing.
</web-summary>

In this article, you will learn how to integrate your Ktor services with relational databases using [Exposed](https://www.jetbrains.com/exposed/), the
SQL library for Kotlin.

By the end of this tutorial, you’ll learn how to do the following:

* Create RESTful services that use JSON serialization.
* Inject different repositories into these services.
* Create unit tests for your services using in-memory repositories.
* Build working repositories using Exposed and dependency injection (DI).
* Deploy services that access external databases.

In previous tutorials, we used the task manager example to cover basics, such as [handling requests](server-requests-and-responses.topic),
[creating RESTful APIs](server-create-restful-apis.topic), or
[building web apps with Thymeleaf templates](server-create-website.topic).

While those tutorials focused on frontend functionality using a simple in-memory `TaskRepository`,
this guide shifts focus to show how your Ktor services can interact with relational databases through the
Exposed SQL library.

Even though this guide is longer and more complex, you’ll still produce working code quickly and gradually
introduce new features.

This guide consists of two parts:

1. [Creating your application with an in-memory repository.](#create-restful-service-and-repository)
2. [Swapping the in-memory repository for one that uses PostgreSQL.](#add-postgresql-repository)

## Prerequisites {id="prerequisites"}

You can do this tutorial independently, however, we recommend that you complete the [Create RESTful APIs](server-create-restful-apis.topic) tutorial to get familiar with content
negotiation and REST.

We recommend that you install [IntelliJ IDEA](https://www.jetbrains.com/help/idea/installation-guide.html), but you could use another IDE of your choice.

## Create a RESTful service and an in-memory repository {id="create-restful-service-and-repository"}

First, you will recreate your task manager's RESTful service. Initially, you will use an in-memory
repository, but you will structure a design that allows it to be substituted with minimal effort.

You will do this in six stages:

1. [Create the initial project.](#create-project)
2. [Add starter code.](#add-starter-code)
3. [Add the CRUD routes.](#add-routes)
4. [Add a Single Page Application (SPA).](#add-client-page)
5. [Test the application manually.](#test-manually)
6. [Add automated tests.](#add-automated-tests)

### Create a new project with plugins {id="create-project"}

To create a new project with the Ktor Project Generator, follow the steps below:

<procedure id="create-project-procedure">

1. <include from="lib.topic" element-id="generate_project_step_1"/>
2. In the **Project artifact** field, enter `com.example.ktor-exposed-task-app` as the name of your project artifact.

   ![Naming the project artifact in the Ktor Project Generator](tutorial_server_db_integration_project_artifact.png){width="706" border-effect="line" style="block"}
3. In the plugins section, search for and add the following plugins by clicking on the **Add** button:
   * Content Negotiation
   * kotlinx.serialization
   * Static Content
   * Status Pages
   * Dependency Injection
   * Exposed
   * PostgreSQL

   ![Adding plugins in the Ktor Project Generator](ktor_project_generator_add_plugins.gif){width="706" border-effect="line" style="block"}
4. Once you have added the plugins, click on the **7 plugins** button at the top right of the plugins section to see the added plugins.

   You will then see a list of all the plugins that will be added to your project:
   ![Plugins dropdown in the Ktor Project Generator](tutorial_server_db_integration_plugin_list.png){width="706" border-effect="line" style="block"}
5. <include from="lib.topic" element-id="generate_project_final_step"/>
6. Open the generated project in [IntelliJ IDEA](https://www.jetbrains.com/help/idea/installation-guide.html) or another IDE of your choice.
7. Navigate to <path>src/main/kotlin</path> and delete the <path>CitySchema.kt</path>, <path>UsersService.kt</path>, <path>GreetingService.kt</path> and <path>Postgres.kt</path> files.
8. Open the <path>Exposed.kt</path> file and remove the content of the `.configureExposed()` function: {id="delete-function"}

   ```kotlin
   suspend fun Application.configureExposed() {
   }
   ```

   The reason for removing this functionality is that the Ktor Project Generator has added sample
   code to show how to persist data about users and cities to HSQLDB or PostgreSQL. You will not be
   requiring that sample code in this tutorial.
9. Open the <path>gradle/libs.versions.toml</path> file and ensure the latest Exposed version is specified:

   ```kotlin
   [versions]
   #...
   exposed = %exposed_version%
   ```

</procedure>

### Add starter code {id="add-starter-code"}

<procedure id="add-starter-code-procedure">

1. Navigate to <path>src/main/kotlin</path> and create a subpackage called <path>model</path>.
2. Inside the <path>model</path> package, create a new <path>Task.kt</path> file.
3. Open <path>Task.kt</path> and add an enum to represent priorities and a class to represent tasks:

   ```kotlin
   ```
   {src="snippets/tutorial-server-db-integration/src/main/kotlin/model/Task.kt"}

   The `Task` class is annotated with the `Serializable` type from the [`kotlinx.serialization`](server-serialization.md) library.

   As in previous tutorials, you will create an in-memory repository. However, this time the
   repository will implement an interface so that you can easily replace it later.
4. In the <path>model</path> subpackage, create a new <path>TaskRepository.kt</path> file.
5. Open <path>TaskRepository.kt</path> and add the following interface:

   ```kotlin
   package com.example.model

   interface TaskRepository {
       fun allTasks(): List<Task>
       fun tasksByPriority(priority: Priority): List<Task>
       fun taskByName(name: String): Task?
       fun addTask(task: Task)
       fun removeTask(name: String): Boolean
   }
   ```
6. Create a new <path>FakeTaskRepository.kt</path> file inside the same directory.
7. Open <path>FakeTaskRepository.kt</path> and add the following class:

   ```kotlin
   package com.example.model

   class FakeTaskRepository : TaskRepository {
       private val tasks = mutableListOf(
           Task("cleaning", "Clean the house", Priority.Low),
           Task("gardening", "Mow the lawn", Priority.Medium),
           Task("shopping", "Buy the groceries", Priority.High),
           Task("painting", "Paint the fence", Priority.Medium)
       )

       override fun allTasks(): List<Task> = tasks

       override fun tasksByPriority(priority: Priority) = tasks.filter {
           it.priority == priority
       }

       override fun taskByName(name: String) = tasks.find {
           it.name.equals(name, ignoreCase = true)
       }

       override fun addTask(task: Task) {
           if (taskByName(task.name) != null) {
               throw IllegalStateException("Cannot duplicate task names!")
           }
           tasks.add(task)
       }

       override fun removeTask(name: String): Boolean {
           return tasks.removeIf { it.name == name }
       }
   }
   ```

</procedure>

### Add routes {id="add-routes"}

<procedure id="add-routes-procedure">

1. Open the <path>Serialization.kt</path> file in <path>src/main/kotlin</path>.
2. Replace the existing `Application.configureSerialization()` function with the implementation below:

   ```kotlin
   ```
   {src="snippets/tutorial-server-db-integration/src/main/kotlin/Serialization.kt"}

   This is the same routing that you implemented in the [Create RESTful APIs](server-create-restful-apis.topic) tutorial, except that you are now resolving the repository from Ktor's
   dependency injection container. Because the type of the dependency is an `interface`,
   many different implementations can be injected.

   Because `dependencies.resolve()` is a suspend function, `.configureSerialization()` must also be declared as suspend.

   Now that you have added a dependency to `.configureSerialization()`, existing calls will no longer compile. Fortunately, this function is only called once.
3. Open the <path>DI.kt</path> file within <path>src/main/kotlin</path>.
4. Replace the contents of the `.configureDependencyInjection()` function with the implementation below:

   ```kotlin
   import com.example.model.FakeTaskRepository
   import com.example.model.TaskRepository
   // ...

   fun Application.configureDependencyInjection() {
       dependencies {
           provide<TaskRepository> {
               FakeTaskRepository()
           }
       }
   }
   ```

   You are now registering `FakeTaskRepository` as the implementation for `TaskRepository`.
   The long-term goal is to be able to replace this with a `PostgresTaskRepository`.
5. Open your <path>application.yaml</path> file located within the <path>src/main/resources</path> folder and ensure the `.configureDependencyInjection()` function is listed before `.configureSerialization()`:

   ```yaml
   ktor:
       deployment:
           port: 8080
       application:
           modules:
               - com.example.DIKt.configureDependencyInjection
               - com.example.SerializationKt.configureSerialization
               - com.example.ExposedKt.configureExposed
               - com.example.StatusPagesKt.configureStatusPages
               - com.example.RoutingKt.configureRouting
   ```

</procedure>

### Add a client page {id="add-client-page"}

<procedure id="add-client-page-procedure">

1. Open the <path>index.html</path> file in <path>src/main/resources/static</path>.
2. Replace the current content with the implementation below:

   ```html
   ```
   {src="snippets/tutorial-server-db-integration/src/main/resources/static/index.html"}

   This is the same SPA that was used in previous tutorials. Because it is written in JavaScript
   and only uses libraries available within the browser, you do not have to worry about client-side
   dependencies.

</procedure>

### Test the application manually {id="test-manually"}

<procedure id="test-manually-procedure">

1. <include from="lib.topic" element-id="intellij_idea_start_application"/>
2. Navigate to [http://0.0.0.0:8080/static/index.html](http://0.0.0.0:8080/static/index.html) in your browser. You should see the client page
   consisting of three forms and a table displaying the filtered results.

   ![A browser window showing the Task Manager Client](tutorial_server_db_integration_index_page.png){width="706" border-effect="rounded"}
3. Test the application by filling out and sending the forms using the **Go** buttons. Use the **View** and **Delete** buttons on the table items.

   ![A browser window showing the Task Manager Client](tutorial_server_db_integration_manual_test.gif){width="706" border-effect="rounded"}

</procedure>

### Add automated unit tests {id="add-automated-tests"}

<procedure id="add-automated-tests-procedure">

1. Open the <path>ServerTest.kt</path> file in <path>src/test/kotlin</path> and add the following tests:

   ```kotlin
   ```
   {src="snippets/tutorial-server-db-integration/src/test/kotlin/ServerTest.kt"}

   For these tests to compile and run, you will need to add a dependency on the [Content Negotiation](https://api.ktor.io/ktor-server-content-negotiation/io.ktor.server.plugins.contentnegotiation/-content-negotiation.html) plugin for the Ktor Client.
2. Open the <path>build.gradle.kts</path> file and add the following dependency:

   ```kotlin
   dependencies {
       //...
       testImplementation(ktorLibs.client.contentNegotiation)
   }
   ```
3. <include from="lib.topic" element-id="intellij_idea_load_gradle_changes"/>
4. <include from="lib.topic" element-id="intellij_idea_run_test"/>

   You should then see that the tests ran successfully in the **Run** pane:

   ![Successful Test results show in the Run pane in IntelliJ IDEA](tutorial_server_db_integration_test_results.png){width="706" border-effect="line"}

</procedure>

## Add a PostgreSQL repository {id="add-postgresql-repository"}

Now that you have a working application that uses in-memory data, the next step is to externalize the data
storage to a PostgreSQL database.

You will achieve this by doing the following:

1. [Create the database schema within PostgreSQL.](#create-schema)
2. [Adapt the `TaskRepository` for asynchronous access.](#adapt-repo)
3. [Configure a database connection within the application.](#config-db-connection)
4. [Map the `Task` type to the associated database table.](#create-mapping)
5. [Create a new repository based on the mapping.](#create-new-repo)
6. [Switch to the new repository in the startup code.](#switch-repo)

### Create the database schema {id="create-schema"}

<procedure id="create-schema-procedure">

1. Using your database management tool of choice, create a new database within PostgreSQL.
   The name does not matter as long as you remember it. In this example, we will use `ktor_tutorial_db`.

   > For more information on PostgreSQL, refer to the [official documentation](https://www.postgresql.org/docs/current/).
   >
   > In IntelliJ IDEA, you can then make use of the Database Tools to [connect to and manage your PostgreSQL database](https://www.jetbrains.com/help/idea/postgresql.html).
   >
   {style="tip"}
2. Run the SQL commands below against your database. These commands will create and populate the database schema:

   ```sql
   DROP TABLE IF EXISTS task;
   CREATE TABLE task(id SERIAL PRIMARY KEY, name VARCHAR(50), description VARCHAR(50), priority VARCHAR(50));

   INSERT INTO task (name, description, priority) VALUES ('cleaning', 'Clean the house', 'Low');
   INSERT INTO task (name, description, priority) VALUES ('gardening', 'Mow the lawn', 'Medium');
   INSERT INTO task (name, description, priority) VALUES ('shopping', 'Buy the groceries', 'High');
   INSERT INTO task (name, description, priority) VALUES ('painting', 'Paint the fence', 'Medium');
   INSERT INTO task (name, description, priority) VALUES ('exercising', 'Walk the dog', 'Medium');
   INSERT INTO task (name, description, priority) VALUES ('meditating', 'Contemplate the infinite', 'High');
   ```

   Note the following:
   * You are creating a single table called `task`, with columns for the `name`, `description`, and `priority`. These will need to be mapped to the properties of the `Task` class.
   * You are re-creating the table if it already exists, so you can run the script repeatedly.
   * There is an additional column called `id`, which is of type `SERIAL`. This will be an integer value, used to give each row its primary key. These values will be assigned by the database on your behalf.

</procedure>

### Adapt the existing repository {id="adapt-repo"}

When executing queries against the database, it's preferable for them to run asynchronously to avoid
blocking the thread handling the HTTP request. In Kotlin, this is best managed through [coroutines](https://kotlinlang.org/docs/coroutines-overview.html).

<procedure id="adapt-repo-procedure">

1. Open the <path>TaskRepository.kt</path> file in <path>src/main/kotlin/model</path>.
2. Add the `suspend` keyword to all interface methods:

   ```kotlin
   interface TaskRepository {
       suspend fun allTasks(): List<Task>
       suspend fun tasksByPriority(priority: Priority): List<Task>
       suspend fun taskByName(name: String): Task?
       suspend fun addTask(task: Task)
       suspend fun removeTask(name: String): Boolean
   }
   ```

   This will allow implementations of the interface methods to start jobs of work on a different
   coroutine dispatcher.

   You now need to adjust the methods of the `FakeTaskRepository` to
   match, although you do not need to switch dispatcher in that implementation.
3. Open the <path>FakeTaskRepository.kt</path> file and add the `suspend` keyword to all methods:

   ```kotlin
   class FakeTaskRepository : TaskRepository {
       //...

       override suspend fun allTasks(): List<Task> = tasks

       override suspend fun tasksByPriority(priority: Priority) = tasks.filter {
           //...
       }

       override suspend fun taskByName(name: String) = tasks.find {
           //...
       }

       override suspend fun addTask(task: Task) {
           //...
       }

       override suspend fun removeTask(name: String): Boolean {
           //...
       }
   }
   ```

   Up to this point, you haven’t introduced any new functionality. Instead, you’ve laid the
   groundwork for creating a `PostgresTaskRepository` that will asynchronously run
   queries against the database.

</procedure>

### Configure the database connection {id="config-db-connection"}

In [the first part of this tutorial](#delete-function), you deleted the sample code in
the `.configureExposed()` function, found within <path>Exposed.kt</path>. You are now ready to add in your own implementation.

Support for the DAO types hasn’t been added by the Ktor Project Generator, so you first need to
add the relevant dependencies in your Gradle build file.

<procedure id="config-db-connection-procedure">

1. In your <path>gradle/libs.versions.toml</path> file, add the `exposed-jdbc` and `exposed-dao` libraries:

   ```toml
   [libraries]
   exposed-jdbc = { module = "org.jetbrains.exposed:exposed-jdbc", version.ref = "exposed" }
   exposed-dao = { module = "org.jetbrains.exposed:exposed-dao", version.ref = "exposed" }
   ```

   This will allow you to work with the Exposed Data Access Object (DAO) pattern.
2. In your <path>build.gradle.kts</path> file, add the following dependencies:

   ```kotlin
   dependencies {
       // ...
       implementation(libs.exposed.jdbc)
       implementation(libs.exposed.dao)
   }
   ```
   > You only need one transport module – `exposed-jdbc` or `exposed-r2dbc`. If the `exposed-r2dbc` dependency has been
   > added by the Ktor project generator, remove it before proceeding.
   >
   {style="note"}
3. <include from="lib.topic" element-id="intellij_idea_load_gradle_changes"/>
4. Open the <path>Exposed.kt</path> file in <path>src/main/kotlin</path>.
5. Use the `Database.connect()` function to connect to your database, adjusting the values of the settings to match your environment:

   ```kotlin
   suspend fun Application.configureExposed() {
       Database.connect(
           "jdbc:postgresql://localhost:5432/ktor_tutorial_db",
           user = "postgres",
           password = "password"
       )
   }
   ```

   Note that the `url` includes the following components:
   * `localhost:5432` is the host and port on which the PostgreSQL database is running.
   * `ktor_tutorial_db` is the name of the database created when running services.

   > For more information, see [Working with Databases in Exposed](https://www.jetbrains.com/help/exposed/working-with-database.html).
   >
   {style="tip"}

</procedure>

### Create the object / relational mapping {id="create-mapping"}

<procedure id="create-mapping-procedure">

1. Navigate to <path>src/main/kotlin</path> and create a new package called <path>db</path>.
2. Inside the <path>db</path> package, create a new <path>mapping.kt</path> file.
3. Open <path>mapping.kt</path> and add the types `TaskTable` and `TaskDAO`:

   ```kotlin
   ```
   {src="snippets/tutorial-server-db-integration/src/main/kotlin/db/mapping.kt" include-lines="1-2,7-10,13-26"}

   These types use the Exposed library to map the properties in the `Task` type to the columns in the `task` table in the database. The `TaskTable` type defines the basic mapping, whilst the `TaskDAO` type adds the helper methods to create, find, update, and delete tasks.
4. In the same <path>mapping.kt</path> file, add the following helper functions:

   ```kotlin
   ```
   {src="snippets/tutorial-server-db-integration/src/main/kotlin/db/mapping.kt" include-symbol="withTransaction, daoToModel"}

   * The `withTransaction()` function runs a suspending block within a new top-level database transaction. The coroutine context is switched to `Dispatchers.IO` so that blocking JDBC operations run on a thread pool optimized for IO tasks.
   * The `daoToModel()` function transforms an instance of the `TaskDAO` type to the `Task` object.
5. Add the following missing imports:

   ```kotlin
   ```
   {src="snippets/tutorial-server-db-integration/src/main/kotlin/db/mapping.kt" include-lines="3-6,11-12"}

</procedure>

### Write the new repository {id="create-new-repo"}

You now have all the resources needed to create a database-specific repository.

<procedure id="create-new-repo-procedure">

1. Navigate to <path>src/main/kotlin/model</path> and create a new <path>PostgresTaskRepository.kt</path> file.
2. Open the <path>PostgresTaskRepository.kt</path> file and create a new type with the following implementation:

   ```kotlin
   ```
   {src="snippets/tutorial-server-db-integration/src/main/kotlin/model/PostgresTaskRepository.kt"}

   In this implementation, you use the helper methods of the `TaskDAO` and `TaskTable` types to interact with the database. Having created this new repository, the only remaining task is to switch to using it within your routes.

</procedure>

### Switch to the new repository {id="switch-repo"}

To switch to an external database, you simply need to change the repository type.

<procedure id="switch-repo-procedure">

1. Open the <path>DI.kt</path> file in <path>src/main/kotlin</path>.
2. In the `Application.configureDependencyInjection()` function, replace `FakeTaskRepository` with `PostgresTaskRepository`:

   ```kotlin
   //...
   import com.example.model.PostgresTaskRepository

   //...

   fun Application.configureDependencyInjection() {
       dependencies {
           provide<TaskRepository> {
               PostgresTaskRepository()
           }
       }
   }
   ```

   Because you are injecting the dependency through the interface, the switch in implementation is transparent to the code for managing routes.
3. <include from="lib.topic" element-id="intellij_idea_restart_application"/>
4. Navigate to [http://0.0.0.0:8080/static/index.html](http://0.0.0.0:8080/static/index.html). The UI remains unchanged, but it now fetches the data from the database.
5. To verify this, add a new task using the form and query the data held in the tasks table in PostgreSQL.

   > In IntelliJ IDEA, you can use a [Query console](https://www.jetbrains.com/help/idea/query-consoles.html#create_console) and the `SELECT` SQL statement to query table data:
   >
   > ```sql
   > SELECT * FROM task;
   > ```
   >
   > Once queried, the data should be displayed in the **Service** pane, including the new task:
   >
   > ![A table of tasks displayed in the Service pane in IntelliJ IDEA](tutorial_server_db_integration_task_table.png){width="706" border-effect="line"}
   >
   {style="tip"}

</procedure>

With this, you have successfully completed integrating a database into your application.

As the `FakeTaskRepository` type is no longer needed in production code, you can move it to the testing source set, in <path>src/test</path>.

The final project structure should look something like this:

![The src folder shown within the Project View in IntelliJ IDEA](tutorial_server_db_integration_src_folder.png){width="400" border-effect="line"}

## Next steps {id="next-steps"}

You now have an application communicating with a Ktor RESTful service. This in turn uses a
repository written with [Exposed](https://github.com/JetBrains/Exposed) to access
[PostgreSQL](https://www.postgresql.org/docs/). You also have [a suite of tests](#add-automated-tests) that verifies the core functionality, without requiring either a web server or a
database.

This structure can be expanded as required to support arbitrary functionality, however, you
might want to consider non-functional aspects of the design first, such as fault-tolerance, security, and
scalability. You can start by [extracting the database connection settings](docker-compose.topic#extract-db-settings) to a [configuration file](server-configuration-file.topic).
