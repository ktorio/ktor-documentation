[//]: # (title: What's new in Ktor 3.2.0)

<show-structure for="chapter,procedure" depth="2"/>

_[Released: June 12, 2025](releases.md#release-details)_

Here are the highlights for this feature release:

* [Version Catalog](#published-version-catalog)
* [Dependency Injection](#dependency-injection)
* [First-class HTMX support](#htmx-integration)
* [Suspendable module functions](#suspendable-module-functions)

## Ktor Server

### Suspendable module functions

Starting with Ktor 3.2.0, [application modules](server-modules.md) have support for suspendable functions.

> With the introduction of suspend module support, auto-reload in development mode no longer works with blocking
> function references. For more information, see [](#regression).
>
{style="warning"}

Previously, adding asynchronous functions inside Ktor modules required the `runBlocking` block that could lead to a
deadlock on server creation:

```kotlin
fun Application.installEvents() {
    val kubernetesConnection = runBlocking {
        connect(property<KubernetesConfig>("app.events"))
    }
}
```

You can now use the `suspend` keyword, allowing asynchronous code on application startup:

```kotlin
suspend fun Application.installEvents() {
    val kubernetesConnection = connect(property<KubernetesConfig>("app.events"))
}
```

#### Concurrent module loading

You can also opt into concurrent module loading by adding the `ktor.application.startup = concurrent` Gradle property.
It launches all application modules independently, so when one suspends, the others are not blocked.
This allows for non-sequential loading for dependency injection, and, in some cases, faster loading.

For more information, see [Concurrent modules](server-modules.md#concurrent-modules).

### Configuration file deserialization

Ktor 3.2.0 introduces typed configuration loading with a new `.property()` extension on the `Application` class. You
can now deserialize structured configuration sections directly into Kotlin data classes.

This feature simplifies how you access configuration values and significantly reduces boilerplate when working with
nested or grouped settings.

Consider the following **application.yaml** file:

```yaml
database:
   url: "$DATABASE_URL:jdbc:postgresql://localhost:5432/postgres"
   username: "$DATABASE_USER:ktor_admin"
   password: "$DATABASE_PASSWORD:ktor123!"
```

Previously, you had to retrieve each configuration value individually. With the new `.property()` extension, you can
load the entire configuration section at once:

<compare>
<code-block lang="kotlin">
data class DatabaseConfig(
    val url: String,
    val username: String,
    val password: String? = null,
)

fun Application.module() {
  val databaseConfig = DatabaseConfig(
    url = environment.config.property("database.url").getString(),
    username = environment.config.property("database.username").getString(),
    password = environment.config.property("database.password").getString(),
  )
  // use configuration
}
</code-block>
<code-block lang="kotlin">
@Serializable 
data class DatabaseConfig(
    val url: String,
    val username: String,
    val password: String? = null,
)

fun Application.module() {
  val databaseConfig: DatabaseConfig = property("database")
  // use configuration
}
</code-block>
</compare>

This feature supports both HOCON and YAML configuration formats and uses `kotlinx.serialization` for
deserialization.

### `ApplicationTestBuilder` has a configurable `client`

Starting with Ktor 3.2.0, the `client` property in the `ApplicationTestBuilder` class is mutable. Previously, it was read-only.
This change lets you configure your own test client and reuse it wherever the `ApplicationTestBuilder` class
is available. For example, you can access the client from within extension functions:

```kotlin
@Test
fun testRouteAfterAuthorization() = testApplication {
    // Pre-configure the client
    client = createClient {
        install(ContentNegotiation) {
            json()
        }
            
        defaultRequest { 
            contentType(ContentType.Application.Json)
        }
    }

    // Reusable test step extracted into an extension-function
    auth(token = AuthToken("swordfish"))

    val response = client.get("/route")
    assertEquals(OK, response.status)
}

private fun ApplicationTestBuilder.auth(token: AuthToken) {
    val response = client.post("/auth") {
        setBody(token)
    }
    assertEquals(OK, response.status)
}
```

### Dependency injection

Ktor 3.2.0 introduces Dependency Injection (DI) support, making it easier to manage and wire dependencies directly
from your configuration files and application code. The new DI plugin simplifies dependency resolution, supports async
loading, provides automatic cleanup, and integrates smoothly with testing.

<var name="artifact_name" value="ktor-server-di" />

To use DI, include the `%artifact_name%` artifact in your build script:

<include from="lib.topic" element-id="add_ktor_artifact"/>

#### Basic dependency registration

You can register dependencies using lambdas, function references, or constructor references:

```kotlin
dependencies {
  // Lambda-based
  provide<GreetingService> { GreetingServiceImpl() }

  // Function references
  provide<GreetingService>(::GreetingServiceImpl)
  provide(BankServiceImpl::class)
  provide(::createBankTeller)

  // Registering a lambda as a dependency
  provide<() -> GreetingService> { { GreetingServiceImpl() } }
}
```

#### Configuration-based dependency registration

You can configure dependencies declaratively using classpath references in your configuration file. This supports
both function and class references:

```yaml
# application.yaml
ktor:
  application:
    dependencies:
      - com.example.RepositoriesKt.provideDatabase
      - com.example.UserRepository
database:
  connectionUrl: postgres://localhost:3037/admin
```

```kotlin
// Repositories.kt
fun provideDatabase(@Property("database.connectionUrl") connectionUrl: String): Database =
  PostgresDatabase(connectionUrl)

class UserRepository(val db: Database) {
  // implementation 
}
```

Arguments are resolved automatically through annotations like `@Property` and `@Named`.

#### Dependency resolution and injection

##### Resolving dependencies

To resolve dependencies, you can use property delegation or direct resolution:

```kotlin
// Using property delegation
val service: GreetingService by dependencies

// Direct resolution
val service = dependencies.resolve<GreetingService>()
```

##### Asynchronous dependency resolution

To support asynchronous loading, you can use suspending functions:

```kotlin
suspend fun Application.installEvents() {
  val kubernetesConnection = dependencies.resolve() // suspends until provided
}

suspend fun Application.loadEventsConnection() {
  dependencies.provide {
    connect(property<KubernetesConfig>("app.events"))
  }
}
```

The DI plugin will automatically suspend `resolve()` calls until all dependencies are ready.

##### Injecting into application modules

You can inject dependencies directly into application modules by specifying module parameters. Ktor will resolve them
from the DI container:

```yaml
ktor:
  application:
    dependencies:
      - com.example.PrintStreamProviderKt
    modules:
      - com.example.LoggingKt.logging
```

```kotlin
fun Application.logging(printStreamProvider: () -> PrintStream) {
    dependencies {
        provide<Logger> { SimpleLogger(printStreamProvider()) }
    }
}
```

Use `@Named` for injecting specifically keyed dependencies:

```kotlin
fun Application.userRepository(@Named("mongo") database: Database) {
    // Uses the dependency named "mongo"
}
```

##### Property and configuration injection

Use `@Property` to inject configuration values directly:

```yaml
connection:
  domain: api.example.com
  path: /v1
  protocol: https
```

```kotlin
val connection: Connection = application.property("connection")
```

This simplifies working with structured configuration and supports automatic parsing of primitive types.

For more information and advanced usage, see [](server-dependency-injection.md).

### Access the application instance in `testApplication`

You can now access the running `Application` instance directly from the `testApplication {}` block using the
`ApplicationTestBuilder.application` property.

Previously, the `Application` instance was only available inside the nested `application {}` configuration block, which
made it difficult to reference the application later in the test. The new `application` property exposes the same instance
after configuration and startup.

The following example uses the `application` property to assert that a plugin was installed:

```kotlin
@Test
fun testAccessApplicationInstance() = testApplication {
    // Configures the application
    application {
        install(CORS)
    }

    // Ensures the application is started
    startApplication()

    // Accesses the same Application instance from the test
    val app: Application = application

    assertTrue(app.pluginOrNull(CORS) != null)
}
```

### Development mode auto-reload regression {id="regression"}

As a side effect to the support of suspending functions, blocking function references (`Application::myModule`) are now
wrapped into anonymous inner classes during casting. This breaks auto-reload, because the function name is no longer
retained as a stable reference.

This means that auto-reload in `development` mode only works with suspend function modules and configuration references:

```kotlin
// Suspend function reference
embeddedServer(Netty, port = 8080, module = Application::mySuspendModule)

// Configuration reference
ktor {
    application {
        modules = [ com.example.ApplicationKt.mySuspendModule ]
    }
}
```

## Ktor Client

### `SaveBodyPlugin` and `HttpRequestBuilder.skipSavingBody()` are deprecated

Prior to Ktor 3.2.0, the `SaveBodyPlugin` was installed by default. It cached the entire response body in memory,
allowing it to be accessed multiple times. To avoid saving response body, the plugin had to be disabled explicitly.

Starting with Ktor 3.2.0, the `SaveBodyPlugin` is deprecated and replaced by a new internal plugin that automatically saves
the response body for all non-streaming requests. This improves resource management and simplifies the HTTP response
lifecycle.

The `HttpRequestBuilder.skipSavingBody()` is also deprecated. If you need to handle a response
without caching the body, use a streaming approach instead.

<compare first-title="Before" second-title="After">

```kotlin
val file = client.get("/some-file") {
    skipSavingBody()
}.bodyAsChannel()
saveFile(file)
```
```kotlin
client.prepareGet("/some-file").execute { response ->
    saveFile(response.bodyAsChannel())
}
```

</compare>

This approach streams the response directly, preventing the body from being saved in memory.

### The `.wrapWithContent()` and `.wrap()` extension functions are deprecated

In Ktor 3.2.0, the [`.wrapWithContent()`](https://api.ktor.io/ktor-client-core/io.ktor.client.plugins.observer/wrap-with-content.html) and [`.wrap()`](https://api.ktor.io/ktor-client-core/io.ktor.client.plugins.observer/wrap.html) extension functions
are deprecated in favor of the new `.replaceResponse()` function.

The `.wrapWithContent()` and `.wrap()` functions replace the original response body with a `ByteReadChannel` that can only be read once.
If the same channel instance is passed directly instead of a function that returns a new one, reading the body multiple times fails.
This can break compatibility between different plugins accessing the response body, because the first plugin to read it consumes the body:

```kotlin
// Replaces the body with a channel decoded once from rawContent
val decodedBody = decode(response.rawContent)
val decodedResponse = call.wrapWithContent(decodedBody).response

// The first call returns the body
decodedResponse.bodyAsText()

// Subsequent calls return an empty string
decodedResponse.bodyAsText() 
```

To avoid this issue, use the `.replaceResponse()` function instead.
It accepts a lambda that returns a new channel on each access, ensuring safe integration with other plugins:

```kotlin
// Replaces the body with a new decoded channel on each access
call.replaceResponse {
    decode(response.rawContent)
}
```

### Access resolved IP address

You can now use the new `.resolveAddress()` function on `io.ktor.network.sockets.InetSocketAddress` instances.
This function allows you to obtain the raw resolved IP address of the associated host:

```kotlin
val address = InetSocketAddress("sample-proxy-server", 1080)
val rawAddress = address.resolveAddress()
```

It returns the resolved IP address as a `ByteArray`, or `null` if the address cannot be resolved.
The size of the returned `ByteArray` depends on the IP version: it will contain 4 bytes for IPv4 addresses and
16 bytes for IPv6 addresses.
On JS and Wasm platforms, `.resolveAddress()` will always return `null`.

### HTTP cache clearing

You can now use new methods on [`CacheStorage`](https://api.ktor.io/ktor-client-core/io.ktor.client.plugins.cache.storage/-cache-storage/index.html)
to clear cached HTTP responses when needed.

- `.removeAll(url)` removes all cache entries that match the specified URL.
- `.remove(url, varyKeys)` removes a specific cache entry that matches the given URL and `Vary` keys.

These methods provide you with more control over cache invalidation and how you manage outdated or specific cached
responses.

## Shared

### HTMX Integration

Ktor 3.2.0 introduces experimental support for [HTMX](https://htmx.org/), a modern JavaScript library that enables 
dynamic interactions via HTML attributes like `hx-get` and `hx-swap`. Ktor’s HTMX integration provides:

- HTMX-aware routing for handling HTMX requests based on headers.
- HTML DSL extensions to generate HTMX attributes in Kotlin.
- HTMX header constants and values to eliminate string literals.

Ktor’s HTMX support is available across three experimental modules:

| Module             | Description                                |
|--------------------|--------------------------------------------|
| `ktor-htmx`        | Core definitions and header constants      |
| `ktor-htmx-html`   | Integration with the Kotlin HTML DSL       |
| `ktor-server-htmx` | Routing support for HTMX-specific requests |

All APIs are marked with `@ExperimentalKtorApi` and require opt-in via `@OptIn(ExperimentalKtorApi::class)`.
For more information, see [](htmx-integration.md).

### Unix domain sockets

With 3.2.0, you can set up Ktor clients to connect to Unix domain sockets and Ktor servers to listen to such sockets.
Currently, Unix domain sockets are only supported in the CIO engine.

Example of a server configuration:

```kotlin
val server = embeddedServer(CIO, configure = {
    unixConnector("/tmp/test-unix-socket-ktor.sock")
}) {
    routing {
        get("/") {
            call.respondText("Hello, Unix socket world!")
        }
    }
}
```

Connecting to that socket using a Ktor client:

```kotlin
val client = HttpClient(CIO)

val response: HttpResponse = client.get("/") {
    unixSocket("/tmp/test-unix-socket-ktor.sock")
}
```

You can also use a Unix domain socket in a [default request](client-default-request.md#unix-domain-sockets).

### New `.appendAll()` overloads for building headers and parameters

The [`StringValuesBuilder.appendAll()`](https://api.ktor.io/ktor-utils/io.ktor.util/append-all.html) function has new
overloads that accept a `Map` or `vararg Pair`. This allows you to append multiple values in a single call, simplifying
the construction of headers, URL parameters, and other `StringValues`-based collections.

The following example shows how to use these new overloads:

```kotlin
val headers = buildHeaders {
    // Using Map
    appendAll(mapOf("foo" to "bar", "baz" to "qux"))
    appendAll(mapOf("test" to listOf("1", "2", "3")))

    // Using vararg Pair
    appendAll("foo" to "bar", "baz" to "qux")
    appendAll("test" to listOf("1", "2", "3"))
}
```

## Infrastructure

### Published version catalog

With this release, you can now use an official
[published version catalog](server-dependencies.topic#using-version-catalog)
to manage all Ktor dependencies from a single source. This eliminates the need to manually declare Ktor versions in
your dependencies.

To add the catalog to your project, configure Gradle’s version catalog in **settings.gradle.kts**, then reference it in
your module’s **build.gradle.kts** file:

<tabs>
<tab title="settings.gradle.kts">

```kotlin
dependencyResolutionManagement {
    versionCatalogs {
        create("ktorLibs") {
            from("io.ktor:ktor-version-catalog:%ktor_version%")
        }
    }
}
```

</tab>
<tab title="build.gradle.kts">

```kotlin
dependencies {
    implementation(ktorLibs.client.core)
    implementation(ktorLibs.client.cio)
    // ...
}
```

</tab>
</tabs>

## Gradle plugin

### Enabling development mode

Ktor 3.2.0 simplifies enabling development mode. Previously, enabling development mode required explicit
configuration in the `application` block. Now, you can use the `ktor.development` property to enable it,
either dynamically or explicitly:

* Dynamically enable development mode based on a project property.
  ```kotlin
    ktor {
        development = project.ext.has("development")
    }
  ```
* Explicitly set development mode to true.

    ```kotlin
    ktor {
        development = true
    }
    ```

By default, the `ktor.development` value is automatically resolved from the Gradle project property or
the system property `io.ktor.development` if either is defined. This allows you to enable development mode
directly using a Gradle CLI flag:

```bash
./gradlew run -Pio.ktor.development=true
```
