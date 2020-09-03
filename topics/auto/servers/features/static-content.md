[//]: # (title: Static Content)
[//]: # (caption: Serving Static Content)
[//]: # (category: servers)
[//]: # (permalink: /servers/features/static-content.html)
[//]: # (feature: feature)
[//]: # (artifact: io.ktor)
[//]: # (class: io.ktor.routing.Routing)
[//]: # (redirect_from: redirect_from)
[//]: # (- /features/static-content.html: - /features/static-content.html)
[//]: # (ktor_version_review: 1.0.0)

Ktor has built-in support for serving static content. This can come in useful when you want to serve style sheets, scripts, images, etc. 



## Specifying Files and Folders

Using the `static` function, we can tell Ktor that we want certain URIs to be treated as static content and also define where the content resides. All content is relative to the current working directory. 
See [Defining a custom root folder](#defining-a-custom-root-folder) to set a different root. 
      
```kotlin
routing {
    static("static") {
        files("css") 
    }
}
```

The example above tells `ktor` that any request to the URI `/static` is to be treated as static content. The `files("css")` defines the folder under which these files
 are located - anything that is in the folder `css` will be served. In essence, this means that a request such as
 

**`/static/styles.css`** will serve the file **`css/styles.css`**. 

In addition to folders, we can also include specific files using `file`, which can optionally take a second parameter that maps to the actual physical filename, if it is different.

```kotlin
routing {
    static("static") {
        files("css")
        files("js")
        file("image.png")
        file("random.txt", "image.png")
        default("index.html")
    }
}
```

We can also have default files that can be served using `default`. For instance, on calling

**`/static`** with no filename,  **`index.html`** will be served.

## Defining a custom root folder

To specify a different root folder, one other than the working directory, we set the value of `staticRootFolder` which expects a type `File`.

```kotlin
static("custom") {
    staticRootFolder = File("/system/folder/docs")
    files("public")
}
```

## Defining subroutes

We can also define sub-routes, i.e. `/static/themes` for instance

```kotlin
static("static") {
    files("css")
    static("themes") {
        files("data")
    }
}
```

## Serving embedded resources

If you embed your static content as resources into your application, you can serve them right from there using the `resource` and `resources` 
functions:

```kotlin
static("static") {
    resources("css")
    resource("favicon.ico")
}
```

There is also `defaultResource` similar to `default` for serving a default page for a folder, 
and `staticBasePackage` similar to `staticRootFolder` for specifying a base resource package for static content. 

## Handling Errors

If the requested content is not found in any route the `404 Not Found` will be produced.

## Customising Content Types

When files are served, the content type is determined from the file extension, using `ContentType.defaultForFile(file)`. The data corresponding
to each file type is obtained from the `mimelist.csv` resource file which is located in `ktor-server-core`. 

## Under the covers

The function `static` is defined as

```kotlin
fun Route.static(remotePath: String, configure: Route.() -> Unit) = route(remotePath, configure)
````

which is essentially just another route definition. 

## Handling HEAD requests in static content
{id="head-requests"}

Ktor do not handle `HEAD` requests by default, thus the static content feature do not handle `HEAD` requests either.
To automatically handle `HEAD` requests for each `GET` route you can install the [`AutoHeadResponse` feature](/servers/features/autoheadresponse.html).

```kotlin
fun Application.main() {
    // ...
    install(AutoHeadResponse) 
    // ...
}
```

Not handling HEAD requests by default, means that if you use `curl -I` or its `curl --head` alias to a GET route
of your Ktor backend without installing the AutoHeadResponse feature,
[it would return a 404 Not Found](/quickstart/faq.html#curl-head-not-found).