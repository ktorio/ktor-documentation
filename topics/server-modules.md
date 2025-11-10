[//]: # (title: Modules)

<tldr>
<p>
<b>Code examples</b>: 
<a href="https://github.com/ktorio/ktor-documentation/tree/%ktor_version%/codeSnippets/snippets/embedded-server-modules">embedded-server-modules</a>, 
<a href="https://github.com/ktorio/ktor-documentation/tree/%ktor_version%/codeSnippets/snippets/engine-main-modules">engine-main-modules</a>
</p>
</tldr>

<link-summary>Modules allow you to structure your application by grouping routes.</link-summary>

Ktor allows you to use modules to [structure](server-application-structure.md) your application by defining a specific set of [routes](server-routing.md) inside a specific module. A module is an _[extension function](https://kotlinlang.org/docs/extensions.html)_ of the [Application](https://api.ktor.io/ktor-server-core/io.ktor.server.application/-application/index.html) class. In the example below, the `module1` extension function defines a module that accepts GET requests made to the `/module1` URL path.

```kotlin
```
{src="snippets/engine-main-modules/src/main/kotlin/com/example/Application.kt" include-lines="3-6,9-15"}

Loading modules in your application depends on the way used to [create a server](server-create-and-configure.topic): in code using the `embeddedServer` function or by using the `application.conf` configuration file.

> Note that [plugins](server-plugins.md#install) installed in a specified module are in effect for other loaded modules.

## embeddedServer {id="embedded-server"}

Typically, the `embeddedServer` function accepts a module implicitly as a lambda argument. 
You can see the example in the [](server-create-and-configure.topic#embedded-server) section.
You can also extract application logic into a separate module and 
pass a reference to this module as the `module` parameter:

```kotlin
```
{src="snippets/embedded-server-modules/src/main/kotlin/com/example/Application.kt"}

You can find the full example here: [embedded-server-modules](https://github.com/ktorio/ktor-documentation/tree/%ktor_version%/codeSnippets/snippets/embedded-server-modules).



## Configuration file {id="hocon"}

If you use the `application.conf` or `application.yaml` file to configure a server, you need to specify modules to load using the `ktor.application.modules` property. 

Suppose you have three modules defined in two packages: two modules in the `com.example` package and one in the `org.sample` package.

<tabs>
<tab title="Application.kt">

```kotlin
```
{src="snippets/engine-main-modules/src/main/kotlin/com/example/Application.kt"}

</tab>
<tab title="Sample.kt">

```kotlin
```
{src="snippets/engine-main-modules/src/main/kotlin/org/sample/Sample.kt"}

</tab>
</tabs>

To reference these modules in a configuration file, you need to provide their fully qualified names.
A fully qualified module name includes a fully qualified name of the class and an extension function name.

<tabs group="config">
<tab title="application.conf" group-key="hocon">

```shell
```
{src="snippets/engine-main-modules/src/main/resources/application.conf" include-lines="1,5-10"}

</tab>
<tab title="application.yaml" group-key="yaml">

```yaml
```
{src="snippets/engine-main-modules/src/main/resources/_application.yaml" include-lines="1,4-8"}

</tab>
</tabs>

You can find the full example here: [engine-main-modules](https://github.com/ktorio/ktor-documentation/tree/%ktor_version%/codeSnippets/snippets/engine-main-modules).

## Concurrent module loading

You can use suspendable functions when creating application modules. They allow events to run asynchronously when
starting the application. To do that, add the `suspend` keyword:

```kotlin
suspend fun Application.installEvents() {
    val kubernetesConnection = connect(property<KubernetesConfig>("app.events"))
}
```

You can also launch all application modules independently, so when one is suspended, the others are not blocked.
This allows for non-sequential loading for dependency injection and, in some cases, faster loading.

### Configuration options

The following configuration properties are available:

| Property                                | Type                        | Description                                              | Default      |
|-----------------------------------------|-----------------------------|----------------------------------------------------------|--------------|
| `ktor.application.startup`              | `sequential` / `concurrent` | Defines how application modules are loaded               | `sequential` |
| `ktor.application.startupTimeoutMillis` | `Long`                      | Timeout for application module loading (in milliseconds) | `10000`      |

### Enable concurrent module loading

To opt into concurrent module loading, add the following to your server configuration file:

```yaml
# application.conf

ktor {
    application {
        startup = concurrent
    }
}
```

For dependency injection, you can load the following modules in order of appearance without issues:

```kotlin
suspend fun Application.installEvents() {
    // Suspends until provided
    val kubernetesConnection = dependencies.resolve<KubernetesConnection>()
}

suspend fun Application.loadEventsConnection() {
    dependencies.provide<KubernetesConnection> {
        connect(property<KubernetesConfig>("app.events"))
    }
}
```

> Concurrent module loading is a single-threaded process. It helps avoid threading problems with unsafe collections
> in the application's internal shared state.
>
{style="note"}
