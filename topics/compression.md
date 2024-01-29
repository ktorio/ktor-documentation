[//]: # (title: Compression)

<show-structure for="chapter" depth="2"/>

<var name="artifact_name" value="ktor-server-compression"/>
<var name="package_name" value="io.ktor.server.plugins.compression"/>
<var name="plugin_name" value="Compression"/>

<tldr>
<p>
<b>Required dependencies</b>: <code>io.ktor:%artifact_name%</code>
</p>
<var name="example_name" value="compression"/>
<include from="lib.topic" element-id="download_example"/>
<include from="lib.topic" element-id="native_server_not_supported"/>
</tldr>

Ktor provides the capability to compress response body and decompress request body
by using
the [Compression](https://api.ktor.io/ktor-server/ktor-server-plugins/ktor-server-compression/io.ktor.server.plugins.compression/-compression.html)
plugin.
You can use different compression algorithms, including `gzip` and `deflate`,
specify the required conditions for compressing data (such as a content type or response size),
or even compress data based on specific request parameters.

> Note that the `%plugin_name%` plugin does not currently support `SSE` responses.
>
{style="warning"}

> To learn how to serve pre-compressed static files in Ktor, see [](Serving_Static_Content.md#precompressed).

## Add dependencies {id="add_dependencies"}

<include from="lib.topic" element-id="add_ktor_artifact_intro"/>
<include from="lib.topic" element-id="add_ktor_artifact"/>

## Install %plugin_name% {id="install_plugin"}

<include from="lib.topic" element-id="install_plugin"/>

This enables the `gzip`, `deflate`, and `identity` encoders on a server.
In the next chapter, we'll see how to enable only specific encoders and configure conditions for compressing data.
Note that every added encoder will be used to decompress request body if needed.

## Configure compression settings {id="configure"}

You can configure compression in multiple ways: enable only specific encoders, specify their priorities, compress only
specific content types, and so on.

### Add specific encoders {id="add_specific_encoders"}

To enable only specific encoders, call the corresponding extension functions, for example:

```kotlin
install(Compression) {
    gzip()
    deflate()
}
```

You can specify the priority for each compression algorithm by establishing the `priority` property:

```kotlin
install(Compression) {
    gzip {
        priority = 0.9
    }
    deflate {
        priority = 1.0
    }
}
```

In the example above, `deflate` has a higher priority value and takes precedence over `gzip`. Note that the server first
looks at the [quality](https://developer.mozilla.org/en-US/docs/Glossary/Quality_Values) values within
the [Accept-Encoding](https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/Accept-Encoding) header and then takes
into account the specified priorities.

### Configure content type {id="configure_content_type"}

By default, Ktor doesn't compress specific content types, such as `audio`, `video`, `image`, and `text/event-stream`.
You can choose the content types to compress by calling `matchContentType` or exclude the desired media types from
compression by using `excludeContentType`. The code snippet below shows how to compress JavaScript code using `gzip` and
all text subtypes using `deflate`:

```kotlin
```

{src="snippets/compression/src/main/kotlin/compression/Application.kt" include-lines="12-13,15-19,21-25"}

You can find the full example
here: [compression](https://github.com/ktorio/ktor-documentation/tree/%ktor_version%/codeSnippets/snippets/compression).

### Configure response size {id="configure_response_size"}

The `%plugin_name%` plugin allows you to disable compression for responses whose size doesn't exceed the specified
value. To do this, pass the desired value (in bytes) to the `minimumSize` function:

```kotlin
    install(Compression) {
        deflate {
            minimumSize(1024)
        }
    }

```

### Specify custom conditions {id="specify_custom_conditions"}

If necessary, you can provide a custom condition using the `condition` function and compress data depending on the
specific request parameters. The code snippet below shows how to compress requests for the specified URI:

```kotlin
install(Compression) {
    gzip {
        condition {
            request.uri == "/orders"
        }
    }
}
```

## HTTPS security {id="security"}

HTTPS with the enabled compression is vulnerable to the [BREACH](https://en.wikipedia.org/wiki/BREACH) attack. You can
use various ways to mitigate this attack. For example, you can disable compression whenever the referrer header
indicates a cross-site request. In Ktor, this can be done by checking the referrer header value:

```kotlin
install(Compression) {
    gzip {
        condition {
            request.headers[HttpHeaders.Referrer]?.startsWith("https://my.domain/") == true
        }
    }
}
```

## Implement custom encoder {id="custom_encoder"}

If necessary, you can provide your own encoder by implementing
the [ContentEncoder](https://api.ktor.io/ktor-utils/ktor-server-compression/io.ktor.util/-content-encoder)
interface.
See [GzipEncoder](https://github.com/ktorio/ktor/blob/b5b59ca3ae61601e6175f334e6a1252609638e61/ktor-server/ktor-server-plugins/ktor-server-compression/jvm/src/io/ktor/server/plugins/compression/Encoders.kt#L41)
as an example of implementation.
