[//]: # (title: Dependency Injection)

<show-structure for="chapter" depth="2"/>
<primary-label ref="server-plugin"/>
<var name="artifact_name" value="ktor-server-di" />

<tldr>
<p>
<b>Required dependencies</b>: <code>io.ktor:ktor-server-di</code>
</p>
</tldr>

The Dependency Injection (DI) plugin allows you to register services and configuration objects once and inject them
into your application modules, plugins, routes, and other components throughout your project. Ktor's DI is designed to
integrate naturally with its existing application lifecycle, supporting scoping and structured configuration
out of the box.

## Add dependencies

To use DI, include the `%artifact_name%` artifact in your build script:

<include from="lib.topic" element-id="add_ktor_artifact"/>

## Basic dependency registration

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

## Configuration-based dependency registration

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

Ktor resolves constructor and function parameters automatically using the DI container. You can use annotations like
`@Property` or `@Named` to override or explicitly bind parameters in special cases, such as when the type alone is not
enough to distinguish a value. If omitted, Ktor will attempt to resolve parameters by type using the DI container.

## Dependency resolution and injection

### Resolving dependencies

To resolve dependencies, you can use property delegation or direct resolution:

```kotlin
// Using property delegation
val service: GreetingService by dependencies

// Direct resolution
val service = dependencies.resolve<GreetingService>()
```

### Asynchronous dependency resolution

To support asynchronous loading, you can use suspending functions:

```kotlin
suspend fun Application.installEvents() {
  val kubernetesConnection: EventsConnection = dependencies.resolve() // suspends until provided
}

suspend fun Application.loadEventsConnection() {
  dependencies.provide {
    connect(property<KubernetesConfig>("app.events"))
  }
}
```

The DI plugin will automatically suspend `resolve()` calls until all dependencies are ready.

### Injecting into application modules

You can inject dependencies directly into application modules by specifying parameters in the module function. Ktor 
will resolve these dependencies from the DI container based on type matching.

First, register your dependency providers in the `dependencies` section of the config:

```yaml
ktor:
  application:
    dependencies:
      - com.example.PrintStreamProviderKt.stdout
    modules:
      - com.example.LoggingKt.logging
```

Here’s what the dependency provider and module function look like:

```kotlin
// com.example.PrintStreamProvider.kt
fun stdout(): () -> PrintStream = { System.out }
```

```kotlin
// com.example.Logging.kt
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

### Property and configuration injection

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

## Advanced dependency features

### Optional and nullable dependencies

Use nullable types to handle optional dependencies gracefully:

```kotlin
// Using property delegation
val config: Config? by dependencies

// Or direct resolution
val config = dependencies.resolve<Config?>()
```

### Covariant generics

Ktor's DI system supports type covariance, which allows injecting a value as one of its supertypes when the type
parameter is covariant. This is especially useful for collections and interfaces that work with subtypes.

```kotlin
dependencies {
  provide<List<String>> { listOf("one", "two") }
}

// This will work due to type parameter covariance support
val stringList: List<CharSequence> by dependencies
// This will also work
val stringCollection: Collection<CharSequence> by dependencies
```

Covariance also works with non-generic supertypes:

```kotlin
dependencies {
    provide<BufferedOutputStream> { BufferedOutputStream(System.out) }
}

// This works because BufferedOutputStream is a subtype of OutputStream
val outputStream: OutputStream by dependencies
```

#### Limitations

While the DI system supports covariance for generic types, it currently does not support resolving parameterized types
across type argument subtypes. That means you cannot retrieve a dependency using a type that is more specific or more
general than what was registered.

For example, the following code will not resolve:

```kotlin
dependencies {
    provide<Sink<CharSequence>> { CsqSink() }
}

// Will not resolve
val charSequenceSink: Sink<String> by dependencies
```

## Resource lifecycle management

The DI plugin handles lifecycle and cleanup automatically when the application shuts down.

### AutoCloseable support

By default, any dependency that implements `AutoCloseable` is automatically closed when your application stops:

```kotlin
class DatabaseConnection : AutoCloseable {
  override fun close() {
    // Close connections, release resources
  }
}

dependencies {
  provide<DatabaseConnection> { DatabaseConnection() }
}
```

### Custom cleanup logic

You can define custom cleanup logic by specifying a `cleanup` function:

```kotlin
dependencies {
  provide<ResourceManager> { ResourceManagerImpl() } cleanup { manager ->
    manager.releaseResources()
  }
}
```

### Scoped cleanup with key

Use `key` to manage named resources and their cleanup:

```kotlin
dependencies {
  key<Closer>("second") {
    provide { CustomCloser() }
    cleanup { it.closeMe() }
  }
}
```

Dependencies are cleaned up in reverse order of declaration to ensure proper teardown.

## Testing with dependency injection

The DI plugin provides tooling to simplify testing. You can override dependencies before loading your application
modules:

```kotlin
fun test() = testApplication {
  application {
    dependencies.provide<MyService> {
      MockService()
    }
    loadServices()
  }
}
```

### Loading configuration in tests

Use `configure()` to load configuration files easily in your tests:

```kotlin
fun test() = testApplication {
  // Load properties from the default config file path
  configure()
  // Load multiple files with overrides
  configure("root-config.yaml", "test-overrides.yaml")
}
```

Conflicting declarations are ignored by the test engine to let you override freely.
