[//]: # (title: Connection pooling and caching)

<show-structure for="chapter" depth="2"/>

<tldr>
<var name="example_name" value="tutorial-website-interactive-persistence-advanced"/>
<include from="lib.topic" element-id="download_example"/>
<p>
<b>Used libraries</b>: <a href="https://github.com/brettwooldridge/HikariCP">HikariCP</a>, <a href="https://www.ehcache.org/">Ehcache</a>
</p>
</tldr>

<link-summary>Learn how to implement database connection pooling and caching.</link-summary>

In the [previous tutorial](interactive_website_add_persistence.md), we added persistence to a website using the Exposed framework.
In this tutorial, we'll look at how to implement database connection pooling and caching using the HikariCP and Ehcache libraries, respectively.


## Add dependencies {id="add-dependencies"}

First, you need to add dependencies for the HikariCP and Ehcache libraries. 
Open the `gradle.properties` file and specify library versions:

```kotlin
```
{src="gradle.properties" include-lines="20-21"}

Then, open `build.gradle.kts` and add the following dependencies:

```kotlin
```
{src="snippets/tutorial-website-interactive-persistence-advanced/build.gradle.kts" include-lines="6-7,23-24,32-33,37"}

Click the **Load Gradle Changes** icon in the top right corner of the `build.gradle.kts` file to install newly added dependencies.


## Connection pooling {id="connection-pooling"}

Exposed starts a new JDBC connection inside each `transaction` call
when it performs the first manipulation with the database in the scope of this `transaction`.
But establishing multiple JDBC connections is resource-expensive:
reusing the existing connections could help to improve performance.
The _connection pooling_ mechanism solves this problem.

In this section, we'll use the HikariCP framework to manage JDBC connection pooling in our application.

### Extract connection settings into a configuration file {id="connection-settings-config"}

In the [previous tutorial](interactive_website_add_persistence.md#connect_db), we used hardcoded `driverClassName` and `jdbcURL` in the `com/example/dao/DatabaseFactory.kt` file to establish a database connection:

```kotlin
```
{src="snippets/tutorial-website-interactive-persistence/src/main/kotlin/com/example/dao/DatabaseFactory.kt" include-lines="10-12,17"}

Let's extract database connection settings to a [custom configuration group](Configurations.topic#configuration-file).

1. Open the `src/main/resources/application.conf` file and add the `storage` group outside the `ktor` group as follows:

   ```kotlin
   ```
   {src="snippets/tutorial-website-interactive-persistence-advanced/src/main/resources/application.conf" include-lines="11-14,16"}

2. Open `com/example/dao/DatabaseFactory.kt` and update the `init` function to load storage settings from the configuration file:

   ```kotlin
   ```
   {src="snippets/tutorial-website-interactive-persistence-advanced/src/main/kotlin/com/example/dao/DatabaseFactory.kt" include-lines="5,10-18,23,39"}
   
   The `init` function now accepts `ApplicationConfig` and uses `config.property` to load custom settings.

3. Finally, open `com/example/Application.kt` and pass `environment.config` to `DatabaseFactory.init` to load connection settings on application startup:

   ```kotlin
   ```
   {src="snippets/tutorial-website-interactive-persistence-advanced/src/main/kotlin/com/example/Application.kt" include-lines="9-13"}

### Enable connection pooling {id="enable-connection-pooling"}

To enable connection pooling in Exposed, you need to provide [DataSource](https://docs.oracle.com/en/java/javase/19/docs/api/java.sql/javax/sql/DataSource.html) as a parameter to the `Database.connect` function.
HikariCP provides the `HikariDataSource` class that implements the `DataSource` interface.

1. To create `HikariDataSource`, open `com/example/dao/DatabaseFactory.kt` and add the `createHikariDataSource` function to the `DatabaseFactory` object:

   ```kotlin
   ```
   {src="snippets/tutorial-website-interactive-persistence-advanced/src/main/kotlin/com/example/dao/DatabaseFactory.kt" include-lines="4,11-12,25-35,39"}

   Here are some notes on the data source settings:
     - The `createHikariDataSource` function takes the driver class name and database URL as the parameters.
     - The `maximumPoolSize` property specifies the maximum size the connection pool can reach.
     - `isAutoCommit` and `transactionIsolation` are set to sync with the default settings used by Exposed.

2. To use `HikariDataSource`, pass it to the `Database.connect` function:

   ```kotlin
   ```
   {src="snippets/tutorial-website-interactive-persistence-advanced/src/main/kotlin/com/example/dao/DatabaseFactory.kt" include-lines="12-13,19,23,39"}

   You can now [run the application](interactive_website_add_persistence.md#run_app) and make sure everything works as before.



## Caching {id="caching"}

You can supplement a database with a database cache. 
Caching is a technique that enables storing the frequently used data in temporary memory and 
can reduce the workload for a database and the time to read the frequently-required data.

In this tutorial, we'll use the Ehcache library to organize the cache in a file.

### Add a cache file path to the configuration {id="cache-file-path"}

Open the `src/main/resources/application.conf` file and add the `ehcacheFilePath` property to the `storage` group:

```kotlin
```
{src="snippets/tutorial-website-interactive-persistence-advanced/src/main/resources/application.conf" include-lines="11,15-16"}

This property specifies the path to a file used to store the cache data.
We'll use it later to configure a `DAOFacade` implementation for working with a cache.


### Implement caching {id="implement-caching"}

To implement caching, we need to provide another `DAOFacade` implementation that returns a value from the cache 
and delegates it to the database interface if there is no cached value.

1. Create a new `DAOFacadeCacheImpl.kt` file in the `com.example.dao` package and add the following implementation to it:

   ```kotlin
   ```
   {src="snippets/tutorial-website-interactive-persistence-advanced/src/main/kotlin/com/example/dao/DAOFacadeCacheImpl.kt" include-lines="1-28,51"}

   Here is a short overview of this code sample:
     - To initialize and configure the cache, we define an Ehcache `CacheManager` instance. We provide `storagePath` as the root directory to be used for disk storage.
     - We create a cache for entries that stores articles by their IDs: `articlesCache` maps `Int` keys to `Article` values. 
     - Then we provide size constraints for local memory and disk resources. You can read more about these parameters in the [Ehcache documentation](https://www.ehcache.org/documentation/2.8/configuration/cache-size.html).
     - Finally, we obtain the created cache by calling `cacheManager.getCache()` with the provided name, key, and value types.

2. To be used in a cache, the `Article` class should be serializable and implement `java.io.Serializable`.
   Open `com/example/models/Article.kt` and update the code as follows:

   ```kotlin
   ```
   {src="snippets/tutorial-website-interactive-persistence-advanced/src/main/kotlin/com/example/models/Article.kt" include-lines="4-6"}

3. Now we're ready to implement the members of `DAOFacade`. 
   Back in `DAOFacadeCacheImpl.kt`, add the following methods:

   ```kotlin
   ```
   {src="snippets/tutorial-website-interactive-persistence-advanced/src/main/kotlin/com/example/dao/DAOFacadeCacheImpl.kt" include-lines="30-50"}

   - `allArticles`: we don't try to cache all the articles; we delegate this to the main database.
   - `article`: when we get an article, we first check whether it's present in the cache, and only if it's not the case, we delegate this to the main `DAOFacade` and also add this article to the cache.
   - `addNewArticle`: when we add a new article, we delegate it to the main `DAOFacade`, but we also add this article to the cache.
   - `editArticle`: when editing the existing article, we update both the cache and the database.
   - `deleteArticle`: on delete, we need to delete the article both from the cache and from the main database.


### Initialize DAOFacadeCacheImpl {id="init-dao-facade"}

Let's create an instance of `DAOFacadeCacheImpl` and add a sample article to be inserted into the database before the application is started:

1. First, open the `DAOFacadeImpl.kt` file and remove the `dao` variable initialization at the bottom of the file.

2. Then, open `com/example/plugins/Routing.kt` and initialize the `dao` variable inside the `configureRouting` block:

   ```kotlin
   ```
   {src="snippets/tutorial-website-interactive-persistence-advanced/src/main/kotlin/com/example/plugins/Routing.kt" include-lines="11-24,73"}

   And that's it. 
   You can now [run the application](interactive_website_add_persistence.md#run_app) and make sure everything works as before.

> You can find the complete example with connection pooling and caching here: [tutorial-website-interactive-persistence-advanced](https://github.com/ktorio/ktor-documentation/tree/%ktor_version%/codeSnippets/snippets/tutorial-website-interactive-persistence-advanced).