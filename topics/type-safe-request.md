[//]: # (title: Type-safe requests)

<var name="plugin_name" value="Resources"/>
<var name="artifact_name" value="ktor-client-resources"/>

<microformat>
<p>
Required dependencies: <code>io.ktor:%artifact_name%</code>
</p>
<var name="example_name" value="client-type-safe-requests"/>
<include src="lib.xml" include-id="download_example"/>
</microformat>

Ktor provides the `%plugin_name%` plugin that allows you to implement type-safe [requests](request.md). To accomplish this, you need to create a class that describes resources available on a server and then annotate this class using the `@Resource` keyword. Such classes should also have the `@Serializable` annotation provided by the kotlinx.serialization library.

> The Ktor server provides the capability to implement [type-safe routing](type-safe-routing.md).


## Add dependencies {id="add_dependencies"}

### Add kotlinx.serialization {id="add_serialization"}

Given that [resource classes](#resource_classes) should have the `@Serializable` annotation, you need to add the Kotlin serialization plugin as described in the [Setup](https://github.com/Kotlin/kotlinx.serialization#setup) section.

### Add %plugin_name% dependencies {id="add_plugin_dependencies"}

<include src="lib.xml" include-id="add_ktor_artifact_intro"/>
<include src="lib.xml" include-id="add_ktor_artifact"/>


## Install %plugin_name% {id="install_plugin"}

To install `%plugin_name%`, pass it to the `install` function inside a [client configuration block](client.md#configure-client):
```kotlin
val client = HttpClient(CIO) {
    install(Resources)
}
```


## Create resource classes {id="resource_classes"}

<include src="type-safe-routing.md" include-id="resource_classes"/>


### Example: A resource for CRUD operations {id="example_crud"}

Let's summarize the examples above and create the `Articles` resource for CRUD operations.

```kotlin
```
{src="snippets/client-type-safe-requests/src/main/kotlin/com/example/Application.kt" lines="15-29"}

This resource can be used to list all articles, post a new article, edit it, and so on. We'll see how to [make type-safe requests](#make_requests) to this resource in the next chapter.

> You can find the full example here: [client-type-safe-requests](https://github.com/ktorio/ktor-documentation/tree/main/codeSnippets/snippets/client-type-safe-requests).


## Make type-safe requests {id="make_requests"}

To [make a request](request.md) to a typed resource, you need to pass a resource class instance to a request function (`request`, `get`, `post`, `put`, and so on). For example, a sample below shows how to make a request to the `/articles` path.

```kotlin
@Serializable
@Resource("/articles")
class Articles()

fun main() {
    runBlocking {
        val client = HttpClient(CIO) {
            install(Resources)
            // ...
        }
        val getAllArticles = client.get(Articles())
    }
}
```

The example below shows how to make typed requests to the `Articles` resource created in [](#example_crud). 

```kotlin
```
{src="snippets/client-type-safe-requests/src/main/kotlin/com/example/Application.kt" lines="31-48,60"}

The [defaultRequest](default-request.md) function is used to specify a default URL for all requests.

> You can find the full example here: [client-type-safe-requests](https://github.com/ktorio/ktor-documentation/tree/main/codeSnippets/snippets/client-type-safe-requests).
