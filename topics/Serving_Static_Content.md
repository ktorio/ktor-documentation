[//]: # (title: Serving static content)

<show-structure for="chapter" depth="2"/>

<tldr>
<p><b>Code examples</b>:
<a href="https://github.com/ktorio/ktor-documentation/tree/%ktor_version%/codeSnippets/snippets/static-files">static-files</a>,
<a href="https://github.com/ktorio/ktor-documentation/tree/%ktor_version%/codeSnippets/snippets/static-resources">static-resources</a>,
<a href="https://github.com/ktorio/ktor-documentation/tree/%ktor_version%/codeSnippets/snippets/static-zip">static-zip</a>
</p>
</tldr>

<link-summary>
Ktor allows you to serve static files, such as stylesheets, scripts, images, and so on.
</link-summary>

Whether you're creating a website or an HTTP endpoint, your application will likely need to serve files, such as
stylesheets,
scripts, or images.
While it is certainly possible with Ktor to load the contents of a file and [send it in a response](responses.md) to a
client, Ktor simplifies this process by providing additional functions for serving static content.

With Ktor, you can serve content from [folders](#folders),[ZIP files](#zipped)
,and [embedded application resources](#resources).

## Serving Folders {id="folders"}

To serve static files from a local filesystem, use the `staticFiles()` function. In this case, relative paths are
resolved using the current working directory.

 ```kotlin
 ```

{src="snippets/static-files/src/main/kotlin/com/example/Application.kt" include-lines="10,12,37"}

In the example above, any request from the root URL `/` is mapped to the `files` physical folder in the current working
directory.
Ktor recursively serves up any file from `files` as long as a URL path and a physical filename match.

For the full example,
see [static-files](https://github.com/ktorio/ktor-documentation/tree/%ktor_version%/codeSnippets/snippets/static-files).

## Serving ZIP files {id="zipped"}

To serve static content from a ZIP file, Ktor provides the `staticZip()` function.

 ```kotlin
 ```

{src="snippets/static-zip/src/main/kotlin/com/example/Application.kt" include-lines="10,12,20"}

Similarly to serving folders, the above function maps any request from the root URL `/` directly to the contents of the
zip file specified.

For the full example,
see [static-zip](https://github.com/ktorio/ktor-documentation/tree/%ktor_version%/codeSnippets/snippets/static-zip).

## Serving resources {id="resources"}

To serve content from the classpath, use the `staticResources()` function.

```kotlin
```

{src="snippets/static-resources/src/main/kotlin/com/example/Application.kt" include-lines="8,10,19"}

This maps any request from the root URL `/` to the `static` package in application `resources`.
In this case, Ktor recursively serves up any file from the `static` package as long as a URL path and a path to resource
match.

For the full example,
see [static-resources](https://github.com/ktorio/ktor-documentation/tree/%ktor_version%/codeSnippets/snippets/static-resources).

## Configuration {id="configuration"}

All functions provided by Ktor to serve static content can be customized further.

### Index file {id="index"}

If a file with the name `index.html` exists, Ktor will serve it by default when the directory
is requested. You can set a custom index file using the `index` parameter:

```kotlin
```

{src="snippets/static-resources/src/main/kotlin/com/example/Application.kt" include-lines="11"}

In this case, for requests to the root directory `/`, Ktor serves `/custom_index.html`.

### Pre-compressed files {id="precompressed"}

Ktor provides the ability to serve pre-compressed files and avoid using [dynamic compression](compression.md).
To use this functionality, define the `preCompressed()` function inside a block statement:

```kotlin
```

{src="snippets/static-files/src/main/kotlin/com/example/Application.kt" include-lines="14,16,20"}

In this example, for a request made to `/js/script.js`, Ktor can serve `/js/script.js.br` or `/js/script.js.gz`.

### HEAD requests {id="autohead"}

The `enableAutoHeadResponse()` function allows you to automatically respond to a `HEAD` request for every path inside a
static route that has a `GET` defined.

```kotlin
```

{src="snippets/static-resources/src/main/kotlin/com/example/Application.kt" include-lines="13,15,18"}

### Default file response {id="default-file"}

The `default()` function provides the ability to reply with a file for any request inside static route that has no
corresponding file.

```kotlin
```

{src="snippets/static-files/src/main/kotlin/com/example/Application.kt" include-lines="14-15,20"}

In this example when the client requests a resource that doesn't exist, the `index.html` file will
be served as a response.

### Content type {id="content-type"}

By default, Ktor tries to guess value of the `Content-Type` header from the file extension. You can use
the `contentType()` function to set the `Content-Type` header explicitly.

```kotlin
```

{src="snippets/static-files/src/main/kotlin/com/example/Application.kt" include-lines="21,24-29,36"}

In this example, the response for file `html-file.txt` will have `Content-Type: text/html` header and for every other
file default behaviour will be applied.

### Caching {id="caching"}

The `cacheControl()` function allows you to configure the `Cache-Control` header for HTTP caching.

```kotlin
```

{src="snippets/static-files/src/main/kotlin/com/example/Application.kt" include-lines="9-10,21,30-38,40-42"}

> For more information on caching in Ktor, see [Caching headers](caching.md).
>
{style="tip"}

### Excluded files {id="exclude"}

The `exclude()` function allows you to exclude files from being served. When excluded files are requested by the client,
the server will respond with a `403 Forbidden` status code.

```kotlin
```

{src="snippets/static-files/src/main/kotlin/com/example/Application.kt" include-lines="21,23,36"}

### File extensions fallbacks {id="extensions"}

When a requested file is not found, Ktor can add the given extensions to the file name and search for it.

```kotlin
```

{src="snippets/static-resources/src/main/kotlin/com/example/Application.kt" include-lines="13,14,18"}

In this example, when `/index` is requested, Ktor will search for `/index.html` and serve the found content.

### Custom modifications {id="modify"}

The `modify()` function allows you to apply custom modification to a resulting response.

```kotlin
```

{src="snippets/static-files/src/main/kotlin/com/example/Application.kt" include-lines="14,17-20"}

## Handle errors {id="errors"}

If the requested content is not found, Ktor will automatically respond with a `404 Not Found` HTTP status code.

To learn how to configure error handling, see [Status Pages](status_pages.md).
