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
While it is certainly possible with Ktor to load the contents of a file and [send it in response](responses.md) to a request, given this is such a common functionality, Ktor simplifies the entire process for us with the `static` plugin.

The first step in defining a static route is to define the path under which the content should be served. 
For instance, if you want everything under the route `assets` to be treated as static content, you need to add the following to your application setup:

```kotlin
routing {
    static("assets") {

    }
}
```

The next step is to define where we want the content to be served from:

* [Folders](#folders) - describes how to serve static files from a local filesystem. In this case, relative paths are resolved using the current working directory.
* [Embedded application resources](#resources) - describes how to serve static files from the classpath.

## Folders {id="folders"}

To demonstrate how to serve static files from a folder, let's suppose our sample project has the `files` directory in its root.
This directory includes the following files:

```text
files
├── index.html
├── ktor_logo.png
├── css
│   └──styles.css
└── js
    ├── script.js
    └── script.js.gz
```

In this section, we'll show how to map these physical paths to URL paths listed below.

| Physical path             | URL path                 |
|---------------------------|--------------------------|
| `files/index.html`        | `/index.html` or `/`     |
| `files/ktor_logo.png`     | `/images/ktor_logo.png`  |
| `files/css/styles.css`    | `/assets/styles.css`     |
| `files/js/script.js(.gz)` | `/assets/script.js`      |


### Change the default root folder {id="default-folder"}

By default, Ktor calculates a path for serving static files using a current working directory.
If static files of your application is stored inside a specific folder, you can set it as a default root folder from where the content is served using the `staticRootFolder` property.
For the `files` folder in the project root, the configuration looks as follows:

```kotlin
```
{src="snippets/static-files/src/main/kotlin/com/example/Application.kt" lines="10-11,22"}

This maps any request to `/` to the `files` physical folder. 
As the next step, you need to specify how to serve static files using the `file` or `files` functions.


### Serve individual files {id="serve-individual-files"}

To serve individual files, use the `file` function. 
For example, to serve the `files/index.html` file, update configuration as follows:

```kotlin
```
{src="snippets/static-files/src/main/kotlin/com/example/Application.kt" lines="10-12,22"}

As for the [Routing](Routing_in_Ktor.md#nested_routes) plugin, you can define sub-routes by nesting the `static` functions.
The example below shows how to serve the `ktor_logo.png` file under the `/images` URL path:

```kotlin
```
{src="snippets/static-files/src/main/kotlin/com/example/Application.kt" lines="10-11,14-17,22"}

Note that the `file` function optionally takes a second argument that allows you to map a physical filename to a virtual one.
So for the example above, the `ktor_logo.png` image is served for requests to the following paths:
- `/images/ktor_logo.png`
- `/images/image.png`

> Ktor automatically looks up the content type of file based on its extension and sets the appropriate `Content-Type` header.

### Define a default file {id="define-default-file"}

For a specific path, we can also define the default file to be loaded using the `default` function.
The code snippet below shows how to define `index.html` as the default file:


```kotlin
```
{src="snippets/static-files/src/main/kotlin/com/example/Application.kt" lines="10-11,13,22"}

In this case, for requests to `/` a Ktor server serves `files/index.html`.


### Serve content from a folder {id="serve-folder"}

In addition to serving individual files, you can serve the contents from a folder. 
To accomplish this, you need to specify the folder name using the `files` function. 
The snippet below shows how to serve stylesheets and scripts for our sample project:

```kotlin
```
{src="snippets/static-files/src/main/kotlin/com/example/Application.kt" lines="10-11,18-22"}

`files("css")` would then allow for any file located in the `css` folder to be served as static content under the given URL pattern, which in this case is `assets`. 
This means that a request to `/assets/styles.css` would serve the `files/css/styles.css` file.


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
For example, for a request made to `/assets/script.js`, Ktor tries to serve `js/script.js.br` first.





## Embedded application resources {id="resources"}

To demonstrate how to serve static files from application resources, let's suppose our sample project has the `static` package in the `resources` directory.
This package includes the following files:

```text
static
├── index.html
├── ktor_logo.png
├── css
│   └──styles.css
└── js
    └── script.js
```

In this section, we'll show how to map these physical paths to URL paths listed below.

| Physical path           | URL path                 |
|-------------------------|--------------------------|
| `static/index.html`     | `/index.html` or `/`     |
| `static/ktor_logo.png`  | `/images/ktor_logo.png`  |
| `static/css/styles.css` | `/assets/styles.css`     |
| `static/js/script.js`   | `/assets/script.js`      |


### Change the default resource package {id="default-resource-package"}

By default, Ktor calculates a path for serving static resources using a classpath of your application.
If static files of your application is stored inside a specific resource package, you can set it as a default package from where the content is served using the `staticBasePackage` property.
For the `static` package inside `resources`, the configuration looks as follows:

```kotlin
```
{src="snippets/static-resources/src/main/kotlin/com/example/Application.kt" lines="10-11,22"}

This maps any request to `/` to the `static` package.
As the next step, you need to specify how to serve static resources using the `resource` or `resources` functions.


### Serve individual resources {id="serve-individual-resources"}

To serve individual resources, use the `resource` function.
For example, to serve the `static/index.html` file, update configuration as follows:

```kotlin
```
{src="snippets/static-resources/src/main/kotlin/com/example/Application.kt" lines="10-12,22"}

As for the [Routing](Routing_in_Ktor.md#nested_routes) plugin, you can define sub-routes by nesting the `static` functions.
The example below shows how to serve the `ktor_logo.png` file under the `/images` URL path:

```kotlin
```
{src="snippets/static-resources/src/main/kotlin/com/example/Application.kt" lines="10-11,14-17,22"}

Note that the `resource` function optionally takes a second argument that allows you to map a physical filename to a virtual one.
So for the example above, the `ktor_logo.png` image is served for requests to the following paths:
- `/images/ktor_logo.png`
- `/images/image.png`

Ktor automatically looks up the content type of file based on its extension and sets the appropriate `Content-Type` header.

### Define a default resource {id="define-default-resource"}

For a specific path, we can also define the default resource to be loaded using the `defaultResource` function.
The code snippet below shows how to define `index.html` as the default resource:


```kotlin
```
{src="snippets/static-resources/src/main/kotlin/com/example/Application.kt" lines="10-11,13,22"}

In this case, for requests to `/` a Ktor server serves `static/index.html`.


### Serve content from a resource folder {id="serve-resources"}

In addition to serving individual files, you can serve the contents from a resource folder.
To accomplish this, you need to specify the resource folder name using the `resources` function.
The snippet below shows how to serve stylesheets and scripts for our sample project:

```kotlin
```
{src="snippets/static-resources/src/main/kotlin/com/example/Application.kt" lines="10-11,18-22"}

`files("css")` would then allow for any file located in the `css` resource folder to be served as static content under the given URL pattern, which in this case is `assets`.
This means that a request to `/assets/styles.css` would serve the `files/css/styles.css` file.



## Handle errors {id="errors"}

If the requested content is not found, Ktor will automatically respond with a `404 Not Found` HTTP status code. For more information about personalizing error handling, see [](status_pages.md).
 

## Examples {id="examples"}

Example applications that serve static files using both folders and resources can be found below:

<tabs>
<tab title="Folder">

```kotlin
```
{src="snippets/static-files/src/main/kotlin/com/example/Application.kt"}

</tab>
<tab title="Resource">

```kotlin
```
{src="snippets/static-resources/src/main/kotlin/com/example/Application.kt"}

</tab>
</tabs>

You can find the full examples here:

- [static-files](https://github.com/ktorio/ktor-documentation/tree/%current-branch%/codeSnippets/snippets/static-files)
- [static-resources](https://github.com/ktorio/ktor-documentation/tree/%current-branch%/codeSnippets/snippets/static-resources)


