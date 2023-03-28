[//]: # (title: Serving static content)

<show-structure for="chapter" depth="2"/>

<tldr>
<p><b>Code examples</b>:
<a href="https://github.com/ktorio/ktor-documentation/tree/%ktor_version%/codeSnippets/snippets/static-files">static-files</a>,
<a href="https://github.com/ktorio/ktor-documentation/tree/%ktor_version%/codeSnippets/snippets/static-resources">static-resources</a>
</p>
</tldr>

<link-summary>
Ktor allows you to serve static files, such as stylesheets, scripts, images, and so on.
</link-summary>

Whether we're creating a website or an HTTP endpoint, many applications need to serve files (such as stylesheets, scripts, images, etc.). 
While it is certainly possible with Ktor to load the contents of a file and [send it in response](responses.md) to a request, given this is such a common functionality, Ktor simplifies the entire process for us with the static [1](https://api.ktor.io/ktor-server/ktor-server-core/io.ktor.server.http.content/staticFiles.html) and [2](https://api.ktor.io/ktor-server/ktor-server-core/io.ktor.server.http.content/staticResources.html) plugins.

The first step is to define where we want the content to be served from:

* [Folders](#folders) - describes how to serve static files from a local filesystem. In this case, relative paths are resolved using the current working directory.
* [Embedded application resources](#resources) - describes how to serve static files from the classpath.


## Folders {id="folders"}

```kotlin
routing {
    staticFiles("/static", File("files"))
}
```
This maps any request to `/static` to the `files` physical folder in a current working directory.
In this case, Ktor recursively serves up any file from `files` as long as a URL path and physical filename match.

## Serving resources {id="resources"}

```kotlin
routing {
    staticResources("/static", "assets")
}
```
This maps any request to `/static` to the `assets` package in application resources.
In this case, Ktor recursively serves up any file from `assets` package as long as a URL path and path to resource match.


### Additional configuration {id="configuration"}

Ktor provides more configurations to static files and resources.

#### Define an index file {id="index"}

We can also define the default file to be loaded when the directory is requested.
By default, index file name is `index.html`, you can set custom one using `index` parameter:

<tabs>
<tab title="Folder">

```kotlin
routing {
    staticFiles("/static", File("files"), index = "my_index.html")
}
```

</tab>
<tab title="Resource">

```kotlin
routing {
    staticResources("/static", "assets", index = "my_index.html")
}
```

</tab>
</tabs>

In this case, for requests to `/static/path/to/dir` a Ktor server serves `files/path/to/dir/my_index.html` for files and `assets/path/to/dir/my_index.html` for resources.

#### Pre-compressed files {id="precompressed"}
Ability to serve pre-compressed files and avoid using [dynamic compression](compression.md).

<tabs>
<tab title="Folder">

```kotlin
routing {
    staticFiles("/static", File("files")) {
        preCompressed(CompressedFileType.BROTLI, CompressedFileType.GZIP)
    }
}
```

</tab>
<tab title="Resource">

```kotlin
routing {
    staticResources("/static", "assets") {
        preCompressed(CompressedFileType.BROTLI, CompressedFileType.GZIP)
    }
}
```

</tab>
</tabs>

In this example, for a request made to `/static/script.js`, Ktor can serve `js/script.js.br` or `js/script.js.gz`.

#### HEAD requests {id="autohead"}
Ability to automatically responds to a `HEAD` request for every path inside static route that has a `GET` defined.

<tabs>
<tab title="Folder">

```kotlin
routing {
    staticFiles("/static", File("files")) {
        enableAutoHeadResponse()
    }
}
```

</tab>
<tab title="Resource">

```kotlin
routing {
    staticResources("/static", "assets") {
        enableAutoHeadResponse()
    }
}
```

</tab>
</tabs>

#### Default file {id="default-file"}
Ability to reply with default file for any request inside static route, that has no corresponding file

<tabs>
<tab title="Folder">

```kotlin
routing {
    staticFiles("/static", File("files")) {
        default("/path/to/default/file")
    }
}
```

</tab>
<tab title="Resource">

```kotlin
routing {
    staticResources("/static", "assets") {
        default("/path/to/default/file")
    }
}
```

</tab>
</tabs>

#### Content type {id="content-type"}
By default, Ktor tries to guess value of `Content-Type` header from a file extension.

<tabs>
<tab title="Folder">

```kotlin
routing {
    staticFiles("/static", File("files")) {
        contentType { file ->
            when (file.name) {
                "index.txt" -> ContentType.Text.Html
                else -> null
            }
        }
    }
}
```

</tab>
<tab title="Resource">

```kotlin
routing {
    staticResources("/static", "assets") {
        contentType { url ->
            when (url.file) {
                "index.txt" -> ContentType.Text.Html
                else -> null
            }
        }
    }
}
```

</tab>
</tabs>

In this example, response for file `index.txt` will have `Content-Type: text/html` header and apply default behaviour for every other file.

#### Caching headers {id="caching"}
Ktor can add [Caching headers](caching.md) for static files.

<tabs>
<tab title="Folder">

```kotlin
routing {
    staticFiles("/static", File("files")) {
        cacheControl { file ->
            when (file.name) {
                "index.txt" -> listOf(Immutable, CacheControl.MaxAge(10000))
                else -> emptyList()
            }
        }   
    }
}
```

</tab>
<tab title="Resource">

```kotlin
routing {
    staticResources("/static", "assets") {
        cacheControl { url ->
            when (url.file) {
                "index.txt" -> listOf(Immutable, CacheControl.MaxAge(10000))
                else -> emptyList()
            }
        }   
    }
}
```

</tab>
</tabs>

#### Excluding files {id="exclude"}
Ktor can exclude some files and respond with 403 Forbidden status code when such files are requested.

<tabs>
<tab title="Folder">

```kotlin
routing {
    staticFiles("/static", File("files")) {
        exclude { file -> file.path.contains("secret_file") }
    }
}
```

</tab>
<tab title="Resource">

```kotlin
routing {
    staticResources("/static", "assets") {
        exclude { url -> url.path.contains("secret_file") }
    }
}
```

</tab>
</tabs>

#### File extensions fallbacks {id="extensions"}
When a requested file is not found, Ktor can add the given extensions to the file name and search for it. 

<tabs>
<tab title="Folder">

```kotlin
routing {
    staticFiles("/static", File("files")) {
        extensions("html", "htm")
    }
}
```

</tab>
<tab title="Resource">

```kotlin
routing {
    staticResources("/static", "assets") {  
        extensions("html", "htm")
    }
}
```

</tab>
</tabs>

In this example, when `/static/index` is requested, ktor will serve `index.html` content.

#### Custom modifications {id="modify"}
Ktor can apply custom modification to a resulting response.

<tabs>
<tab title="Folder">

```kotlin
routing {
    staticFiles("/static", File("files")) {
        modify { file, call ->
            call.response.headers.append(HttpHeaders.ETag, calculateEtag(file))
        }
    }
}
```

</tab>
<tab title="Resource">

```kotlin
routing {
    staticResources("/static", "assets") {
        modify { url, call ->
            call.response.headers.append(HttpHeaders.ETag, calculateEtag(file))
        }
    }
}
```

</tab>
</tabs>

## Handle errors {id="errors"}

If the requested content is not found, Ktor will automatically respond with a `404 Not Found` HTTP status code. For more information about personalizing error handling, see [Status Pages](status_pages.md).
 

## Examples {id="examples"}

You can find the full examples here:

- [static-files](https://github.com/ktorio/ktor-documentation/tree/%ktor_version%/codeSnippets/snippets/static-files)
- [static-resources](https://github.com/ktorio/ktor-documentation/tree/%ktor_version%/codeSnippets/snippets/static-resources)

