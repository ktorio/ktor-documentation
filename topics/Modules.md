[//]: # (title: Modules)

<microformat>
<p>
Code examples: 
<a href="https://github.com/ktorio/ktor-documentation/tree/main/codeSnippets/snippets/embedded-server-modules">embedded-server-modules</a>, 
<a href="https://github.com/ktorio/ktor-documentation/tree/main/codeSnippets/snippets/engine-main-modules">engine-main-modules</a>
</p>
</microformat>

Ktor allows you to use modules to [structure](Structuring_Applications.md) your application by defining a specific set of [routes](Routing_in_Ktor.md) inside a specific module. A module is an _[extension function](https://kotlinlang.org/docs/extensions.html)_ of the [Application](https://api.ktor.io/ktor-server/ktor-server-core/ktor-server-core/io.ktor.application/-application/index.html) class. In the example below, the `module1` extension function defines a module that accepts GET requests made to the `/module1` URL path.

```kotlin
```
{src="snippets/engine-main-modules/src/main/kotlin/com/example/Application.kt" lines="9-15"}

Loading modules in your application depends on the way used to [create a server](create_server.xml): in code using the `embeddedServer` function or by using the `application.conf` configuration file.

> Note that [plugins](Plugins.md#install) installed in a specified module are in effect for other loaded modules.

## embeddedServer {id="embedded-server"}

Typically, the `embeddedServer` function accepts a module implicitly as a lambda argument. You can see the example in the [](create_server.xml#embedded-server) section.

If you want to load a module defined as an extension function, use one of the ways described below.

### Load a single module {id="single-module"}
To load a single module, pass its name in the `module` parameter.

```kotlin
```
{src="snippets/embedded-server-modules/src/main/kotlin/com/example/Application.kt"}

You can find the full example here: [embedded-server-modules](https://github.com/ktorio/ktor-documentation/tree/main/codeSnippets/snippets/embedded-server-modules).


### Load multiple modules {id="multiple-modules"}

To use multiple modules with `embeddedServer`, call the required extension functions inside the block.

```kotlin
fun main() {
    embeddedServer(Netty, port = 8080) {
        module1()
        module2()
    }.start(wait = true)
}
```


## HOCON file {id="hocon"}

If you use the `application.conf` file to configure a server, you need to specify modules to load using the `ktor.application.modules` property. 

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

To reference these modules in a configuration file, you need to provide their fully qualified names, separated by a comma.
A fully qualified module name includes a fully qualified name of the class and an extension function name.

```kotlin
```
{src="snippets/engine-main-modules/src/main/resources/application.conf" lines="1,5-10"}

You can find the full example here: [engine-main-modules](https://github.com/ktorio/ktor-documentation/tree/main/codeSnippets/snippets/engine-main-modules).
