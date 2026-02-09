[//]: # (title: Dependency resolution)

<show-structure for="chapter" depth="2"/>

After you [register dependencies](server-di-dependency-registration.md), you can resolve them from the dependency
injection (DI) container and inject them into application code.

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

### Asynchronous dependency resolution {id="async-dependency-resolution"}

To support asynchronous loading, you can use suspending functions:

```kotlin
```
{src="snippets/server-di/src/main/kotlin/com.example/AsyncDependencies.kt" include-lines="8-20"}

The DI plugin will automatically suspend `resolve()` calls until all dependencies are ready.

### Inject dependencies into application modules {id="inject-into-modules"}

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

Define the dependency provider and module function with parameters for the dependencies you want injected. You can then
use the injected dependencies directly within the module function:

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


## Advanced dependency resolution

### Optional and nullable dependencies {id="optional-dependencies"}

Use nullable types to handle optional dependencies gracefully:

```kotlin
// Uses property delegation
val config: Config? by dependencies

// Uses direct resolution
val config = dependencies.resolve<Config?>()
```

### Covariant generics {id="covariant-generics"}

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
