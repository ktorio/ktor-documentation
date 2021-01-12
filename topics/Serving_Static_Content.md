[//]: # (title: Serving Static Content)

Whether we're creating a website or an HTTP endpoint, many applications need to serve files (such as stylesheets, scripts, images, etc.).  
While it is certainly possible with Ktor to load the contents of a file and send it in response to a request,
given this is such a common functionality, Ktor simplifies the entire process for us with the `static` Feature.

The first step in defining a static route is to 
define the path under which the content should be served. For instance, if you want everything under the route `assets` to be treated as static content, you need to add the following
to your application setup:

```kotlin
routing {
    static("assets") {

    }
}
```

The next step is to define where we want the content to be served from, which can be either

* [A folder](Serving_Static_Content.md#folders) 
* [Embedded application resources](Serving_Static_Content.md#embedded-application-resources)

## Folders

In order to serve the contents from a folder, we need to specify the folder name using the `files` function. The path is **always relative to the application path**:

```kotlin
routing {
    static("assets") {
      files("css")
    }
}
```

`files("css")` would then allow for any file located in the folder `css` to be served as static content under the given
URL pattern, which in this case is `assets`. This means that a request to

`/assets/stylesheet.css` would serve the file `/css/stylesheet.css` 

We can have as many folders as we like under a single path. For instance the following would also be valid:

```kotlin
routing {
    static("assets") {
      files("css")
      files("js")
    }
}
```

### Serving individual files

In addition to serving files from folders, we can also specify individuals files we would like to make available by 
using the `file` function. Optionally this takes a second argument which allows us to map a physical filename to a virtual one:

```kotlin
routing {
    static("static") {
        file("image.png")
        file("random.txt", "image.png")
    }
}
```

### Defining a default file

For a specific path, we can also define the default file to be loaded:

```kotlin
routing {
    static("assets") {
      files("css")
      default("index.html")
    }
}
```
 
which would cause a request to `/assets` to serve `index.html`. 

### Serving pre-compressed files {id="precompressed"}

Ktor provides the ability to serve pre-compressed files and avoid using [dynamic compression](compression.md). 
For example, to serve pre-compressed files from the `assets/html` folder, call `files` inside the `preCompressed` function in the following way:
```kotlin
static("static") {
    preCompressed {
        files("assets/html")
    }
}
```
You can also raise the priority of one compression type over another.
In the example below, Ktor tries to serve `*.br` over `*.gz` files:

```kotlin
static("static") {
    preCompressed(CompressedFileType.BROTLI, CompressedFileType.GZIP) {
        files("assets/html")
    }
}
```
For example, a request to `/static/index.html` tries to serve `assets/html/index.html.br` first.



### Changing the default root folder

Ktor also provides us the ability to specify a different root folder from where contents is served. This is useful for instance
if we want to dynamically define where contents should be served from, or even use absolute paths.

We can do this by setting the value of the `staticRootFolder` property: 

```kotlin
static("docs") {
    staticRootFolder = File("/system/folder/docs")
    files("public")
}
```

which would then map any request to `/docs` to the physical folder `/system/folder/docs/public`.

## Embedded Application Resources

We can embed content as resources in our applications and serve these using the `resource` and `resources` functions:

```kotlin
static("assets") {
    resources("css")
}
```

`resources("css")` would then allow for any file located under the resource `css` to be served as static content under the given
URL pattern, which in this case is `assets`. This means that a request to

`/assets/stylesheet.cs` would serve the file `/css/stylesheet.cs` 

We can have as many resources as we like under a single path. For instance the following would also be valid:

```kotlin
routing {
    static("assets") {
      resources("css")
      resources("js")
    }
}
```

### Serving individual resources

In addition to serving files from resources, we can also specify individuals files we would like to make available by 
using the `resource` function. Optionally this takes a second argument which allows us to map a physical filename to a virtual one:

```kotlin
routing {
    static("static") {
        resource("image.png")
        resource("random.txt", "image.png")
    }
}
```
### Defining a default resource

For a specific path, we can also define the default file to be loaded:

```kotlin
routing {
    static("assets") {
      resources("css")
      defaultResource("index.html")
    }
}
```

### Changing the default resource package

Ktor also provides us the ability to specify a different base resource package from where contents is served.

We can do this by setting the value of the `staticBasePackage` property: 

```kotlin
static("docs") {
    staticBasePackage = File("/system/folder/docs")
    files("public")
}
```

## Sub-routes

If we want to have sub-routes, we can nest `static` functions:

```kotlin
static("assets") {
    files("css")
    static("themes") {
        files("data")
    }
}
```

allowing for `/assets/themes` to load files from the `/data` 

## Handling errors

If the request content is not found, Ktor will automatically respond with a `404 Not Found` HTTP status code. For more information about personalising error handling, please see [status pages](status_pages.md).

## Customising Content Type header

Ktor automatically looks up the content type of a file based on its extension and sets the appropriate `Content-Type` header. The list of supported MIME types 
is defined in the `mimelist.csv` resource file located in `ktor-server-core` artifact. 
 

## Example 

An example application that serves static files using both folders and resources can be found below:

```kotlin
```
{src="/snippets/static-content/src/StaticContentApplication.kt"}






 


