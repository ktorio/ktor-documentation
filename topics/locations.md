[//]: # (title: Locations)

<microformat>
<var name="example_name" value="locations"/>
<include src="lib.md" include-id="download_example"/>
</microformat>


<include src="lib.md" include-id="outdated_warning"/>



Ktor provides a mechanism to create routes in a typed way, for both:
constructing URLs and reading the parameters.

>Locations are an experimental plugin (previously known as feature).
>
{type="note"}


## Add dependencies {id="add_dependencies"}
<var name="feature_name" value="Locations"/>
<var name="artifact_name" value="ktor-locations"/>
<include src="lib.md" include-id="add_ktor_artifact_intro"/>
<include src="lib.md" include-id="add_ktor_artifact"/>


## Install Locations {id="install_feature"}

<var name="feature_name" value="Locations"/>
<include src="lib.md" include-id="install_feature"/>


## Define route classes
{id="route-classes"}

For each typed route you want to handle, you need to create a class (usually a data class)
containing the parameters that you want to handle.

The parameters must be of any type supported by the [Data Conversion](data-conversion.md) plugin.
By default, you can use `Int`, `Long`, `Float`, `Double`, `Boolean`, `String`, enums and `Iterable` as parameters.

### URL parameters
{id="parameters-url"}

That class must be annotated with `@Location` specifying
a path to match with placeholders between curly brackets `{` and `}`. For example: `{propertyName}`.
The names between the curly braces must match the properties of the class.

```kotlin
@Location("/list/{name}/page/{page}")
data class Listing(val name: String, val page: Int)
```

* Will match: `/list/movies/page/10`
* Will construct: `Listing(name = "movies", page = 10)`

### GET parameters
{id="parameters-get"}

If you provide additional class properties that are not part of the path of the `@Location`,
those parameters will be obtained from the GET's query string or POST parameters:

```kotlin
@Location("/list/{name}")
data class Listing(val name: String, val page: Int, val count: Int)
```

* Will match: `/list/movies?page=10&count=20`
* Will construct: `Listing(name = "movies", page = 10, count = 20)`

## Define route handlers
{id="route-handlers"}

Once you have [defined the classes](#route-classes) annotated with `@Location`,
this plugin artifact exposes new typed methods for defining route handlers:
`get`, `options`, `header`, `post`, `put`, `delete` and `patch`.

```kotlin
routing {
    get<Listing> { listing ->
        call.respondText("Listing ${listing.name}, page ${listing.page}")
    }
}
```

>Some of these generic methods with one type parameter, defined in the `io.ktor.locations`, have the same name as other methods defined in the `io.ktor.routing` package. If you import the routing package before the locations one, the IDE might suggest you generalize those methods instead of importing the right package. You can manually add `import io.ktor.locations.*` if that happens to you.
>Remember this API is experimental. This issue is already [reported at github](https://github.com/ktorio/ktor/issues/368).
>
{type="note"}

## Build URLs
{id="building-urls"}

You can construct URLs to your routes by calling `application.locations.href` with
an instance of a class annotated with `@Location`:

```kotlin
val path = application.locations.href(Listing(name = "movies", page = 10, count = 20))
```

So for this class, `path` would be `"/list/movies?page=10&count=20""`.

```kotlin
@Location("/list/{name}") data class Listing(val name: String, val page: Int, val count: Int)
```

If you construct the URLs like this, and you decide to change the format of the URL,
you will just have to update the `@Location` path, which is really convenient.

## Subroutes with parameters
{id="subroutes"}

You have to create classes referencing to another class annotated with `@Location` like this, and register them normally:

```kotlin
routing {
    get<Type.Edit> { typeEdit -> // /type/{name}/edit
        // ...
    }
    get<Type.List> { typeList -> // /type/{name}/list/{page}
        // ...
    }
}
```
 
To obtain parameters defined in the superior locations, you just have to include
those property names in your classes for the internal routes. For example:

```kotlin
@Location("/type/{name}") data class Type(val name: String) {
	// In these classes we have to include the `name` property matching the parent.
	@Location("/edit") data class Edit(val parent: Type)
	@Location("/list/{page}") data class List(val parent: Type, val page: Int)
}
```