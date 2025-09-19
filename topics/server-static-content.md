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
Learn how to serve static content, such as stylesheets, scripts, images, and so on.
</link-summary>

Whether you're creating a website or an HTTP endpoint, your application will likely need to serve files, such as
stylesheets, scripts, or images.
While it is certainly possible with Ktor to load the contents of a file and [send it in a response](server-responses.md)
to a client, Ktor simplifies this process by providing additional functions for serving static content.

With Ktor, you can serve content from [folders](#folders), [ZIP files](#zipped),
and [embedded application resources](#resources).

## Folders {id="folders"}

To serve static files from a local filesystem, use the
[`staticFiles()`](https://api.ktor.io/ktor-server/ktor-server-core/io.ktor.server.http.content/static-files.html)
function. In this case, relative paths are resolved using the current working directory.

 ```kotlin
 ```

{src="snippets/static-files/src/main/kotlin/com/example/Application.kt" include-lines="15-16,57"}

In the example above, any request from `/resources` is mapped to the `files` physical folder in the current working
directory.
Ktor recursively serves up any file from `files` as long as a URL path and a physical filename match.

For the full example,
see [static-files](https://github.com/ktorio/ktor-documentation/tree/%ktor_version%/codeSnippets/snippets/static-files).

## ZIP files {id="zipped"}

To serve static content from a ZIP file, Ktor provides the [
`staticZip()`](https://api.ktor.io/ktor-server/ktor-server-core/io.ktor.server.http.content/static-zip.html) function.
This allows you to map requests directly to the contents of a ZIP archive, as shown in the example below:

 ```kotlin
 ```

{src="snippets/static-zip/src/main/kotlin/com/example/Application.kt" include-lines="10,12,20"}

In this example, any request from the root URL `/` is mapped directly to the contents of the ZIP file `text-files.zip`.

### Auto-reloading support {id="zip-auto-reload"}

The `staticZip()` function also supports automatic reloading. If any changes are detected in the ZIP file's parent
directory, the ZIP file system is reloaded on the next request. This ensures that the served content remains
up to date without requiring a server restart.

For the full example,
see [static-zip](https://github.com/ktorio/ktor-documentation/tree/%ktor_version%/codeSnippets/snippets/static-zip).

## Resources {id="resources"}

To serve content from the classpath, use the
[`staticResources()`](https://api.ktor.io/ktor-server/ktor-server-core/io.ktor.server.http.content/static-resources.html)
function.

```kotlin
```

{src="snippets/static-resources/src/main/kotlin/com/example/Application.kt" include-lines="8,9,17"}

This maps any request from `/resources` to the `static` package in application resources.
In this case, Ktor recursively serves up any file from the `static` package as long as a URL path and a path-to-resource
match.

For the full example,
see [static-resources](https://github.com/ktorio/ktor-documentation/tree/%ktor_version%/codeSnippets/snippets/static-resources).

## Additional configuration {id="configuration"}

Ktor provides more configurations to static files and resources.

### Index file {id="index"}

If a file with the name `index.html` exists, Ktor will serve it by default when the directory
is requested. You can set a custom index file using the `index` parameter:

```kotlin
```

{src="snippets/static-resources/src/main/kotlin/com/example/Application.kt" include-lines="10"}

In this case, when `/custom` is requested, Ktor serves `/custom_index.html`.

### Pre-compressed files {id="precompressed"}

Ktor provides the ability to serve pre-compressed files and avoid using [dynamic compression](server-compression.md).
To use this functionality, define the `preCompressed()` function inside a block statement:

```kotlin
```

{src="snippets/static-files/src/main/kotlin/com/example/Application.kt" include-lines="17,19,23"}

In this example, for a request made to `/js/script.js`, Ktor can serve `/js/script.js.br` or `/js/script.js.gz`.

### HEAD requests {id="autohead"}

The `enableAutoHeadResponse()` function allows you to automatically respond to a `HEAD` request for every path inside a
static route that has a `GET` defined.

```kotlin
```

{src="snippets/static-resources/src/main/kotlin/com/example/Application.kt" include-lines="11,13,16"}

### Default file response {id="default-file"}

The `default()` function provides the ability to reply with a file for any request inside a static route that has no
corresponding file.

```kotlin
```

{src="snippets/static-files/src/main/kotlin/com/example/Application.kt" include-lines="17-18,23"}

In this example when the client requests a resource that doesn't exist, the `index.html` file will
be served as a response.

### Content type {id="content-type"}

By default, Ktor tries to guess the value of the `Content-Type` header from the file extension. You can use
the `contentType()` function to set the `Content-Type` header explicitly.

```kotlin
```

{src="snippets/static-files/src/main/kotlin/com/example/Application.kt" include-lines="24,35-40,47"}

In this example, the response for the file `html-file.txt` will have the `Content-Type: text/html` header, and for every
other file the default behavior will be applied.

### Caching {id="caching"}

The `cacheControl()` function allows you to configure the `Cache-Control` header for HTTP caching.

```kotlin
```

{src="snippets/static-files/src/main/kotlin/com/example/Application.kt" include-lines="14-15,24,41-47,57-58,60-62"}

When both the [`CachingHeaders`](server-caching-headers.md) and [`ConditionalHeaders`](server-conditional-headers.md)
plugin is installed, Ktor can serve static resources with `ETag` and `LastModified` headers and process conditional headers to avoid sending the body of content if it has not changed since the last request.:

```kotlin
```

{src="snippets/static-files/src/main/kotlin/com/example/Application.kt" include-lines="49-52"}

In this example, `etag` and `lastModified` values are calculated dynamically based on each resource and applied to the response.

To simplify ETag generation, you can also use a predefined ETag provider:

```kotlin
```

{src="snippets/static-files/src/main/kotlin/com/example/Application.kt" include-lines="54-56"}

In this example, a strong `etag` is generated using the SHA‑256 hash of the resource content.
If an I/O error occurs, no ETag is generated.

> For more information on caching in Ktor, see [Caching headers](server-caching-headers.md).
>
{style="tip"}

### Excluded files {id="exclude"}

The `exclude()` function allows you to exclude files from being served. When excluded files are requested by the client,
the server will respond with a `403 Forbidden` status code.

```kotlin
```

{src="snippets/static-files/src/main/kotlin/com/example/Application.kt" include-lines="24,26,47"}

### File extensions fallbacks {id="extensions"}

When a requested file is not found, Ktor can add the given extensions to the file name and search for it.

```kotlin
```

{src="snippets/static-resources/src/main/kotlin/com/example/Application.kt" include-lines="11,12,16"}

In this example, when `/index` is requested, Ktor will search for `/index.html` and serve the found content.

### Custom fallback

To configure custom fallback behavior when a requested static resource is not found, use the `fallback()` function.
With `fallback()`, you can inspect the requested path and decide how to respond. For example, you might redirect to
another resource, return a specific HTTP status, or serve an alternative file.

You can add `fallback()` inside `staticFiles()`, `staticResources()`, `staticZip()`, or `staticFileSystem()`. The callback provides
the requested path and the current `ApplicationCall`.

The example below shows how to redirect certain extensions, return a custom status, or fall back to `index.html`:

```kotlin
```

{src="snippets/static-files/src/main/kotlin/com/example/Application.kt" include-lines="24,27-34,47"}

### Custom modifications {id="modify"}

The `modify()` function allows you to apply custom modification to a resulting response.

```kotlin
```

{src="snippets/static-files/src/main/kotlin/com/example/Application.kt" include-lines="17,20-23"}

## Handle errors {id="errors"}

If the requested content is not found, Ktor will automatically respond with a `404 Not Found` HTTP status code.

To learn how to configure error handling, see [Status Pages](server-status-pages.md).
