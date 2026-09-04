[//]: # (title: Type-safe requests)

<show-structure for="chapter" depth="2"/>
<primary-label ref="client-plugin"/>

<var name="plugin_name" value="Resources"/>
<var name="artifact_name" value="ktor-client-resources"/>

<tldr>
<p>
<b>Required dependencies</b>: <code>io.ktor:%artifact_name%</code>
</p>
<var name="example_name" value="client-type-safe-requests"/>
<include from="lib.topic" element-id="download_example"/>
</tldr>

<link-summary>
Learn how to make type-safe requests using the Resources plugin.
</link-summary>

Ktor provides the `%plugin_name%` plugin for making type-safe [client requests](client-requests.md).
To achieve this, you define classes that represent server endpoints and annotate them with the `@Resource` keyword.

Resource classes use `kotlinx.serialization` to convert their properties to path and query parameters.

> On the server, Ktor provides [type-safe routing](server-resources.md).
>
{style="tip"}

## Add dependencies {id="add_dependencies"}

### Add kotlinx.serialization {id="add_serialization"}

The `Resources` plugin relies on `kotlinx.serialization`. Enable the Kotlin serialization plugin as described in the
[`kotlinx.serialization` setup guide](https://github.com/Kotlin/kotlinx.serialization#setup).

### Add %plugin_name% dependencies {id="add_plugin_dependencies"}

<include from="lib.topic" element-id="add_ktor_artifact_intro"/>
<include from="lib.topic" element-id="add_ktor_artifact"/>
<include from="lib.topic" element-id="add_ktor_client_artifact_tip"/>


## Install %plugin_name% {id="install_plugin"}

To install the `%plugin_name%` plugin, pass it to the `install` function in the [client configuration block](client-create-and-configure.md#configure-client):

```kotlin
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.resources.*
//...
val client = HttpClient(CIO) {
    install(Resources)
}
```

## Create resource classes {id="resource_classes"}

<include from="server-resources.md" element-id="resource_classes_server"/>

### Example: A resource for CRUD operations {id="example_crud"}

The following example creates the `Articles` resource for CRUD operations:

```kotlin
```
{src="snippets/client-type-safe-requests/src/main/kotlin/com/example/Application.kt" include-lines="18-28"}

This resource can be used to list all articles, post a new article, and edit an existing one.

The next section shows how to [make type-safe requests](#make_requests) using this resource.

> For the full example, see [client-type-safe-requests](https://github.com/ktorio/ktor-documentation/tree/main/codeSnippets/snippets/client-type-safe-requests).
>
{style="tip"}

## Make type-safe requests {id="make_requests"}

To [make a request](client-requests.md) to a typed resource, pass a resource class instance to a request function, such as `request()`,
`get()`, `post()`, or `put()`.

The following example makes a request to the `/articles` path:

```kotlin
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

The following example makes typed requests to the `Articles` resource created in [](#example_crud). 

```kotlin
```
{src="snippets/client-type-safe-requests/src/main/kotlin/com/example/Application.kt" include-lines="30-48,60"}

The [`defaultRequest()`](client-default-request.md) function specifies a default URL for all requests.

> When developing client plugins or instrumentation, you can access the resource instance used for a type-safe request
> through the `RESOURCE` request attribute:
>  ```kotlin
>  onRequest { call, _ ->
>    val resource = call.attributes.getOrNull(RESOURCE)
>  }
>  ```
> 
{style="tip"}


> For the full example, see [client-type-safe-requests](https://github.com/ktorio/ktor-documentation/tree/main/codeSnippets/snippets/client-type-safe-requests).
>
{style="tip"}