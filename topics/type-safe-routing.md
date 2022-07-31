[//]: # (title: Type-safe routing)

<var name="plugin_name" value="Resources"/>
<var name="package_name" value="io.ktor.server.resources"/>
<var name="artifact_name" value="ktor-server-resources"/>

<microformat>
<p>
<b>Required dependencies</b>: <code>io.ktor:%artifact_name%</code>
</p>
<var name="example_name" value="resource-routing"/>
<include src="lib.xml" include-id="download_example"/>
</microformat>

<excerpt>Ktor provides the Resources plugin that allows you to implement type-safe routing.</excerpt>

Ktor provides the [Resources](https://api.ktor.io/ktor-shared/ktor-resources/io.ktor.resources/-resources/index.html) plugin that allows you to implement type-safe [routing](Routing_in_Ktor.md). To accomplish this, you need to create a class that should act as a typed route and then annotate this class using the `@Resource` keyword. Such classes should also have the `@Serializable` annotation provided by the kotlinx.serialization library.

> The Ktor client provides the capability to make [typed requests](type-safe-request.md) to a server.


## Add dependencies {id="add_dependencies"}

### Add kotlinx.serialization {id="add_serialization"}

Given that [resource classes](#resource_classes) should have the `@Serializable` annotation, you need to add the Kotlin serialization plugin as described in the [Setup](https://github.com/Kotlin/kotlinx.serialization#setup) section.

### Add %plugin_name% dependencies {id="add_plugin_dependencies"}

<include src="lib.xml" include-id="add_ktor_artifact_intro"/>
<include src="lib.xml" include-id="add_ktor_artifact"/>


## Install %plugin_name% {id="install_plugin"}

<include src="lib.xml" include-id="install_plugin"/>


## Create resource classes {id="resource_classes"}

<chunk id="resource_classes_server">

Each resource class should have the following annotations:
* The `@Serializable` annotation, which is provided by the [kotlinx.serialization library](#add_serialization).
* The `@Resource` annotation.

Below we'll take a look at several examples of resource classes - defining a single path segment, query and path parameters, and so on.

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

</chunk>

### Example: A resource for CRUD operations {id="example_crud"}

Let's summarize the examples above and create the `Articles` resource for CRUD operations.

```kotlin
```
{src="snippets/resource-routing/src/main/kotlin/com/example/Application.kt" lines="14-28"}

This resource can be used to list all articles, post a new article, edit it, and so on. We'll see how to [define route handlers](#define_route) for this resource in the next chapter.

> You can find the full example here: [resource-routing](https://github.com/ktorio/ktor-documentation/tree/%current-branch%/codeSnippets/snippets/resource-routing).

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
{src="snippets/resource-routing/src/main/kotlin/com/example/Application.kt" lines="32-64"}

> You can find the full example here: [resource-routing](https://github.com/ktorio/ktor-documentation/tree/%current-branch%/codeSnippets/snippets/resource-routing).

## Building links from resources {id="resource_links"}

Besides using resource definitions for routing, they can also be used to build links.
This is sometimes called "reverse routing".

The `Resource` plugin extends `Application` with the overloaded `href` method with which we can generate a link from a `Resource`.

For instance here we create a link for the above defined `Edit` resource:

```kotlin
val link: String = href(Articles.Id.Edit(Articles.Id(id = 123)))
```

Since the grandparent `Articles` resource defines the `sort` query parameter with the default value `new`, the `link` variable contains:

```
/articles/123/edit?sort=new
```

To generate URLs that specify host and protocol we can supply the `href` method with a `URLBuilder`.
Additional query params can also be specified using the `URLBuilder`, as shown in this example:

```kotlin
val urlBuilder = URLBuilder(URLProtocol.HTTPS, "ktor.io", parameters = parametersOf("token", "123"))
href(Articles(sort = null), urlBuilder)
val link: String = urlBuilder.buildString()
```

The `link` variable subsequently contains:

```
https://ktor.io/articles?token=123
```

> Find more examples here: [resource-routing tests](https://github.com/ktorio/ktor-documentation/tree/%current-branch%/codeSnippets/snippets/resource-routing/src/test/kotlin/ApplicationTest.kt).



