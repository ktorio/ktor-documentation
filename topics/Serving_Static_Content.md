[//]: # (title: Serving static content)

<microformat>
<p><b>Code examples</b>:
<a href="https://github.com/ktorio/ktor-documentation/tree/%current-branch%/codeSnippets/snippets/static-files">static-files</a>,
<a href="https://github.com/ktorio/ktor-documentation/tree/%current-branch%/codeSnippets/snippets/static-resources">static-resources</a>
</p>
</microformat>

<excerpt>
Ktor allows you to serve static files, such as stylesheets, scripts, images, and so on.
</excerpt>

Whether we're creating a website or an HTTP endpoint, many applications need to serve files (such as stylesheets, scripts, images, etc.).  
While it is certainly possible with Ktor to load the contents of a file and send it in response to a request,
given this is such a common functionality, Ktor simplifies the entire process for us with the `static` plugin.

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

* [A folder](#folders) 
* [Embedded application resources](#resources)

## Folders {id="folders"}

### Serve content from a folder {id="serve-folder"}

In order to serve the contents from a folder, you need to specify the folder name using the `files` function. The path is always relative to the application path:

```kotlin
```
{src="snippets/static-files/src/main/kotlin/com/example/Application.kt" lines="13-16"}

`files("css")` would then allow for any file located in the `css` folder to be served as static content under the given URL pattern, which in this case is `assets`. 
This means that a request to `/assets/stylesheet.css` would serve the file `/css/stylesheet.css` 


### Serve individual files {id="serve-individual-files"}

In addition to serving files from folders, we can also specify individuals files we would like to make available by 
using the `file` function. Optionally this takes a second argument which allows us to map a physical filename to a virtual one:

```kotlin
```
{src="snippets/static-files/src/main/kotlin/com/example/Application.kt" lines="17-20"}

In the example above, the `ktor_logo.png` image is served for requests to the following paths:
- `/images/ktor_logo.png`
- `/images/image.png`


### Define a default file {id="define-default-file"}

For a specific path, we can also define the default file to be loaded:

```kotlin
```
{src="snippets/static-files/src/main/kotlin/com/example/Application.kt" lines="10,12,21"}
 
which would cause a request to `/` to serve `index.html`. 

### Serve pre-compressed files {id="precompressed"}

Ktor provides the ability to serve pre-compressed files and avoid using [dynamic compression](compression.md). 
For example, to serve pre-compressed files from the `css` and `js` folders, call `files` inside the `preCompressed` function in the following way:
```kotlin
static("assets") {
    preCompressed {
        files("css")
        files("js")
    }
}
```
You can also raise the priority of one compression type over another.
In the example below, Ktor tries to serve `*.br` files over `*.gz`:

```kotlin
static("assets") {
    preCompressed(CompressedFileType.BROTLI, CompressedFileType.GZIP) {
        files("css")
        files("js")
    }
}
```
For example, for a request made to `/assets/script.js` Ktor tries to serve `js/script.js.br` first.



### Change the default root folder {id="default-folder"}

Ktor also provides us the ability to specify a different root folder from where contents is served. This is useful for instance
if we want to dynamically define where contents should be served from, or even use absolute paths.

We can do this by setting the value of the `staticRootFolder` property: 

```kotlin
```
{src="snippets/static-files/src/main/kotlin/com/example/Application.kt" lines="10-11,21"}

which would then map any request to `/` to the `/files` physical folder.

## Embedded application resources {id="resources"}

### Serve content from a resource folder {id="serve-resources"}

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

Ktor automatically looks up the content type of file based on its extension and sets the appropriate `Content-Type` header. The list of supported MIME types 
is defined in the `mimelist.csv` resource file located in `ktor-server-core` artifact. 
 

## Example 

Example applications that serve static files using both folders and resources can be found below:

```kotlin
```
{src="snippets/static-files/src/main/kotlin/com/example/Application.kt"}






 


