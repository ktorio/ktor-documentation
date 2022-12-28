[//]: # (title: Type-safe routing)

<show-structure for="chapter" depth="2"/>

<var name="plugin_name" value="Resources"/>
<var name="package_name" value="io.ktor.server.resources"/>
<var name="artifact_name" value="ktor-server-resources"/>

<tldr>
<p>
<b>Required dependencies</b>: <code>io.ktor:%artifact_name%</code>
</p>
<var name="example_name" value="resource-routing"/>
<include from="lib.topic" element-id="download_example"/>
<include from="lib.topic" element-id="native_server_supported"/>
</tldr>

<link-summary>Ktor provides the Resources plugin that allows you to implement type-safe routing.</link-summary>

Ktor provides the [Resources](https://api.ktor.io/ktor-shared/ktor-resources/io.ktor.resources/-resources/index.html) plugin that allows you to implement type-safe [routing](Routing_in_Ktor.md). To accomplish this, you need to create a class that should act as a typed route and then annotate this class using the `@Resource` keyword. Such classes should also have the `@Serializable` annotation provided by the kotlinx.serialization library.

> The Ktor client provides the capability to make [typed requests](type-safe-request.md) to a server.


## Add dependencies {id="add_dependencies"}

### Add kotlinx.serialization {id="add_serialization"}

Given that [resource classes](#resource_classes) should have the `@Serializable` annotation, you need to add the Kotlin serialization plugin as described in the [Setup](https://github.com/Kotlin/kotlinx.serialization#setup) section.

### Add %plugin_name% dependencies {id="add_plugin_dependencies"}

<include from="lib.topic" element-id="add_ktor_artifact_intro"/>
<include from="lib.topic" element-id="add_ktor_artifact"/>


## Install %plugin_name% {id="install_plugin"}

<include from="lib.topic" element-id="install_plugin"/>


## Create resource classes {id="resource_classes"}

<snippet id="resource_classes_server">

Each resource class should have the following annotations:
* The `@Serializable` annotation, which is provided by the [kotlinx.serialization library](#add_serialization).
* The `@Resource` annotation.

Below, we'll take a look at several examples of resource classes - defining a single path segment, query and path parameters, and so on.

### Resource URL {id="resource_url"}

The example below shows how to define the `Articles` class that specifies a resource responding on the `/articles` path. 

```kotlin
import io.ktor.resources.*

@Serializable
@Resource("/articles")
class Articles()
```

### Resources with a query parameter {id="resource_query_param"}

The `Articles` class below has the `sort` string property that acts as a [query parameter](requests.md#query_parameters) and allows you to define a resource responding on the following path with the `sort` query parameter: `/articles?sort=new`. 

```kotlin
@Serializable
@Resource("/articles")
class Articles(val sort: String? = "new")
```

Note that properties can be primitives or types annotated with the `@Serializable` annotation.

### Resources with nested classes {id="resource_nested"}

You can nest classes to create resources that contain several path segments. Note that in this case nested classes should have a property with an outer class type. 
The example below shows a resource responding on the `/articles/new` path.


```kotlin
@Serializable
@Resource("/articles")
class Articles() {
    @Serializable
    @Resource("new")
    class New(val parent: Articles = Articles())
}
```

### Resources with a path parameter {id="resource_path_param"}

The example below demonstrates how to add the [nested](#resource_nested) `{id}` integer [path parameter](Routing_in_Ktor.md#path_parameter) that matches a path segment and captures it as a parameter named `id`.

```kotlin
@Serializable
@Resource("/articles")
class Articles() {
    @Serializable
    @Resource("{id}")
    class Id(val parent: Articles = Articles(), val id: Long)
}
```

As an example, this resource can be used to respond on `/articles/12`.

</snippet>

### Example: A resource for CRUD operations {id="example_crud"}

Let's summarize the examples above and create the `Articles` resource for CRUD operations.

```kotlin
```
{src="snippets/resource-routing/src/main/kotlin/resourcerouting/Application.kt" include-lines="16-30"}

This resource can be used to list all articles, post a new article, edit it, and so on. We'll see how to [define route handlers](#define_route) for this resource in the next chapter.

> You can find the full example here: [resource-routing](https://github.com/ktorio/ktor-documentation/tree/%ktor_version%/codeSnippets/snippets/resource-routing).



## Define route handlers {id="define_route"}

To [define a route handler](Routing_in_Ktor.md#define_route) for a typed resource, you need to pass a resource class to a verb function (`get`, `post`, `put`, and so on).
For example, a route handler below responds on the `/articles` path.

```kotlin
@Serializable
@Resource("/articles")
class Articles()

fun Application.module() {
    install(Resources)
    routing {
        get<Articles> {
            // Get all articles ...
            call.respondText("List of articles")
        }
    }
}
```

The example below shows how to define route handlers for the `Articles` resource created in [](#example_crud). Note that inside the route handler you can access the `Article` as a parameter and obtain its property values.

```kotlin
```
{src="snippets/resource-routing/src/main/kotlin/resourcerouting/Application.kt" include-lines="34-64,92-93"}

Here are several tips on handling requests for each endpoint:

- `get<Articles>`

   This route handler is supposed to return all articles sorted according to the `sort` query parameter.
   For instance, this might be an [HTML page](responses.md#html) or a [JSON object](responses.md#object) with all articles.

- `get<Articles.New>`

   This endpoint responds with a [web form](responses.md#html) containing fields for creating a new article.
- `post<Articles>`

   The `post<Articles>` endpoint is supposed to receive [parameters](requests.md#form_parameters) sent using a web form.
   Ktor also allows you to receive JSON data as an [object](requests.md#objects) using the `ContentNegotiation` plugin.
- `get<Articles.Id>`

   This route handler is supposed to return an article with the specified identifier.
   This might be an [HTML page](responses.md#html) showing an article or a [JSON object](responses.md#object) with article data.
- `get<Articles.Id.Edit>`

  This endpoint responds with a [web form](responses.md#html) containing fields for editing an existing article.
- `put<Articles.Id>`

   Similarly to the `post<Articles>` endpoint, the `put` handler receives [form parameters](requests.md#form_parameters) sent using a web form.
- `delete<Articles.Id>`

   This route handler deletes an article with the specified identifier.

You can find the full example here: [resource-routing](https://github.com/ktorio/ktor-documentation/tree/%ktor_version%/codeSnippets/snippets/resource-routing).


## Build links from resources {id="resource_links"}

Besides using resource definitions for routing, they can also be used to build links.
This is sometimes called _reverse routing_.
Building links from resources might be helpful if you need to add these links to 
an HTML document created with [HTML DSL](html_dsl.md) or if you need to generate a [redirection response](responses.md#redirect).

The `Resources` plugin extends `Application` with the overloaded [href](https://api.ktor.io/ktor-server/ktor-server-plugins/ktor-server-resources/io.ktor.server.resources/href.html) method, which allows you to generate a link from a `Resource`. For instance, the code snippet below creates a link for the `Edit` resource [defined above](#example_crud):

```kotlin
```
{src="snippets/resource-routing/src/main/kotlin/resourcerouting/Application.kt" include-lines="79"}

Since the grandparent `Articles` resource defines the `sort` query parameter with the default value `new`, the `link` variable contains:

```
/articles/123/edit?sort=new
```

To generate URLs that specify the host and protocol, you can supply the `href` method with `URLBuilder`.
Additional query params can also be specified using the `URLBuilder`, as shown in this example:

```kotlin
```
{src="snippets/resource-routing/src/main/kotlin/resourcerouting/Application.kt" include-lines="83-85"}

The `link` variable subsequently contains:

```
https://ktor.io/articles?token=123
```

### Example {id="example_build_links"}

The example below shows how to add links built from resources to the HTML response:

```kotlin
```
{src="snippets/resource-routing/src/main/kotlin/resourcerouting/Application.kt" include-lines="66-91"}

You can find the full example here: [resource-routing](https://github.com/ktorio/ktor-documentation/tree/%ktor_version%/codeSnippets/snippets/resource-routing).
