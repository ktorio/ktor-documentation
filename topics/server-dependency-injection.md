[//]: # (title: Dependency Injection)

<show-structure for="chapter" depth="2"/>
<primary-label ref="server-plugin"/>
<var name="artifact_name" value="ktor-server-di" />

<tldr>
<p>
<b>Required dependencies</b>: <code>io.ktor:ktor-server-di</code>
</p>
<var name="example_name" value="server-di"/>
<include from="lib.topic" element-id="download_example" />
</tldr>

[Dependency injection (DI)](https://en.wikipedia.org/wiki/Dependency_injection) is a design pattern that helps you
supply components with the dependencies they require. Instead of creating concrete implementations directly, modules
depend on abstractions, and a DI container is responsible for constructing and providing the appropriate instances at
runtime. This separation reduces coupling, improves testability, and makes it easier to replace or reconfigure
implementations without modifying existing code.

Ktor provides a built‑in DI plugin that lets you register services and configuration objects once and access them
throughout your application. You can inject these dependencies into modules, plugins, routes, and other Ktor components
in a consistent, type‑safe way. The plugin integrates with the Ktor application lifecycle and supports scoping,
structured configuration, and automatic resource management, making it easier to organize and maintain application‑level
services.

## Add dependencies

To use DI, include the `%artifact_name%` artifact in your build script:

<include from="lib.topic" element-id="add_ktor_artifact"/>

## Dependency registration

Ktor’s DI container needs to know how to create objects that your application depends on. This process is called
dependency registration.

### Basic dependency registration

Basic dependency registration is done in code using the `dependencies {}` block. You can register dependencies by
providing [lambdas](#lambda-registration), [function references](#function-reference), [class references](#class-reference),
or [constructor references](#constructor-reference):

#### Use a lambda {id="lambda-registration"}

Use a lambda when you want full control over how an instance is created:

```kotlin
dependencies {
    provide<GreetingService> { GreetingServiceImpl() }
}
```
This registers a provider for `GreetingService`. Whenever `GreetingService` is requested, the lambda is executed to
create an instance.

#### Use a constructor reference {id="constructor-reference"}

If a class can be created using its constructor and all constructor parameters are already registered in the DI
container, you can use a constructor reference.

```kotlin
dependencies {
    provide<GreetingService>(::GreetingServiceImpl)
}
```
This tells your application to use the constructor of `GreetingServiceImpl`, and let DI resolve its parameters.

#### Use a class reference {id="class-reference"}

You can register a concrete class without binding it to an interface:

```kotlin
dependencies {
    provide(BankServiceImpl::class)
}
```
In this case, the dependency is resolved by its `BankServiceImpl` type.
This is useful when the implementation type is injected directly and no abstraction is required.

#### Use a function reference {id="function-reference"}

You can register a function that creates and returns an instance:

```kotlin
dependencies {
    provide(::createBankTeller)
}
```

The DI container resolves the function parameters and uses the return value as the dependency instance.

#### Use a factory lambda {id="factory-lambda-registration"}

You can register a function itself as a dependency:

```kotlin
dependencies {
    provide<() -> GreetingService> {
        { GreetingServiceImpl() }
    }
}
```

This registers a function that can be injected and called manually to create new instances.

### Configuration-based dependency registration

You can configure dependencies declaratively using classpath references in your configuration file. You can list a
function that returns an object, or a class with a resolvable constructor.

List the dependencies under the `ktor.application.dependencies` group in your configuration file:

<tabs>
<tab title="application.yaml">

```yaml
```
{src="snippets/server-di/src/main/resources/application.yaml" include-lines="1,4-7"}

</tab>
</tabs>

Ktor resolves function and constructor parameters automatically using the DI container.

## Dependency resolution

After you register dependencies, you can resolve them from the DI container and inject them into application code.

You can resolve dependencies explicitly from the DI container using either [property delegation](#property-delegation)
or [direct resolution](#direct-resolution).

### Use property delegation {id="property-delegation"}

When using property delegation, the dependency is resolved lazily when the property is first accessed:

```kotlin
val service: GreetingService by dependencies
```

### Use direct resolution {id="direct-resolution"}

Direct resolution returns the dependency immediately or suspends until it becomes available:

```kotlin
val service = dependencies.resolve<GreetingService>()
```

### Parameter resolution

When resolving constructors or functions, Ktor resolves parameters using the DI container. Parameters are resolved by
type by default.

If type-based resolution is insufficient, you can use annotations to explicitly bind parameters.

#### Use named dependencies

Use the `@Named` annotation to resolve a dependency registered with the specified name:

```kotlin
fun Application.userRepository(@Named("mongo") database: Database) {
    // Uses the dependency named "mongo"
}
```

#### Use configuration properties

Use the `@Property` annotation to inject a value from the application configuration:

```kotlin
```
{src="snippets/server-di/src/main/kotlin/com.example/Repositories.kt" include-symbol="provideDatabase"}


In the above example, the `database.connectionUrl` property is resolved from the application configuration:

<tabs>
<tab title="application.yaml">

```yaml
```
{src="snippets/server-di/src/main/resources/application.yaml" include-lines="1,4-6,13-14"}

</tab>
</tabs>

### Asynchronous dependency resolution

To support asynchronous loading, you can use suspending functions:

```kotlin
```
{src="snippets/server-di/src/main/kotlin/com.example/AsyncDependencies.kt" include-lines="8-20"}

The DI plugin will automatically suspend `resolve()` calls until all dependencies are ready.

### Inject dependencies into application modules

You can inject dependencies directly into application modules by specifying parameters in the module function. Ktor 
will resolve these dependencies from the DI container based on type matching.

First, register your dependency providers in the `ktor.application.dependencies` group in your configuration file:

<tabs>
<tab title="application.yaml">

```yaml
```
{src="snippets/server-di/src/main/resources/application.yaml" include-lines="1,4-5,9-10,12"}

</tab>
</tabs>

Define the dependency provider and module function with parameters for the dependencies you want injected:

<tabs>
<tab title="PrintStreamProvider.kt">

```kotlin
```
{src="snippets/server-di/src/main/kotlin/com.example/PrintStreamProvider.kt"}

</tab>
<tab title="Logging.kt">

```kotlin
```
{src="snippets/server-di/src/main/kotlin/com.example/Logging.kt"}

</tab>
</tabs>

You can then use the injected dependencies directly within the module function.

## Advanced dependency features

### Optional and nullable dependencies

Use nullable types to handle optional dependencies gracefully:

```kotlin
// Uses property delegation
val config: Config? by dependencies

// Uses direct resolution
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

## Configure the DI plugin

You can configure the DI plugin in your application configuration file. These settings affect the behavior of
dependency resolution globally and apply to all registered dependencies.

### Dependency key mapping

The `ktor.di.keyMapping` property defines how dependency keys are generalized and matched during resolution. This
determines which registered dependencies are considered compatible when resolving a requested type.

```yaml
ktor:
  di:
    keyMapping: Supertypes * Nullables * OutTypeArgumentsSupertypes * RawTypes
```

The above example matches the default key mapping used by the DI plugin.

#### Available key mapping options

<deflist>
<def>
<title><code>Default</code></title>
Uses the default combination:
<code-block>Supertypes * Nullables * OutTypeArgumentsSupertypes * RawTypes</code-block>
</def>
<def>
<title><code>Supertypes</code></title>
Allows resolving a dependency using any of its supertypes.
</def>
<def>
<title><code>Nullables</code></title>
Allows matching nullable and non-nullable variants of a type.
</def>
<def>
<title><code>OutTypeArgumentsSupertypes</code></title>
Allows covariance on <code>out</code> type parameters.
</def>
<def>
<title><code>RawTypes</code></title>
Allows resolving generic types without considering type arguments.
</def>
<def>
<title><code>Unnamed</code></title>
Ignores dependency names (<code>@Named</code>) when matching.
</def>
</deflist>

#### Combine key mapping options

You can combine key mapping options using set operators `*` (intersection), `+` (union) and `()` (grouping).

In the following example, a dependency registered as `List<String>` can be resolved as `Collection<String>` (`Supertypes`),
`List` or `List?` (`RawTypes` and `Nullables`):

```yaml
ktor:
  di:
    keyMapping: Supertypes + (Nullables * RawTypes)
```

It will not resolve as `Collection?`, because that combination is not included in the expression

### Conflict resolution policy

The `ktor.di.conflictPolicy` property controls how the DI container behaves when multiple providers are registered for
the same dependency key:

```yaml
ktor:
  di:
    conflictPolicy: Default
```

#### Available policies

<deflist>
<def>
<title><code>Default</code></title>
Throws an exception when a conflicting dependency is declared
</def>
<def>
<title><code>OverridePrevious</code></title>
Overrides the previous dependency with the newly provided one.
</def>
<def>
<title><code>IgnoreConflicts</code></title>
In test environments, the DI plugin uses <code>IgnoreConflicts</code> by default. This allows test code to override
production dependencies without triggering errors.
</def>
</deflist>

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
