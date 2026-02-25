[//]: # (title: Content encoding)

<primary-label ref="client-plugin"/>

<var name="artifact_name" value="ktor-client-encoding"/>

<tldr>
<p>
<b>Required dependencies</b>: <code>io.ktor:%artifact_name%</code>
</p>
<var name="example_name" value="client-content-encoding"/>
<include from="lib.topic" element-id="download_example"/>
</tldr>

<link-summary>
The ContentEncoding plugin allows you to enable specified compression algorithms (such as 'gzip' and 'deflate') and configure their settings.
</link-summary>

The Ktor client provides the [`ContentEncoding`](https://api.ktor.io/ktor-client-encoding/io.ktor.client.plugins.compression/-content-encoding) 
plugin that allows you to enable specified compression algorithms (such as `gzip` and `deflate`) and configure their settings. 

This plugin provides the following functionality:
* Sets the `Accept-Encoding` header with the specified quality value.
* Optionally encodes request body.
* Decodes [content received from a server](client-responses.md#body) to obtain the original payload.

## Add dependencies {id="add_dependencies"}

To use `ContentEncoding`, add the `%artifact_name%` artifact to your build script:

<include from="lib.topic" element-id="add_ktor_artifact"/>
<include from="lib.topic" element-id="add_ktor_client_artifact_tip"/>

## Install `ContentEncoding` {id="install_plugin"}

To install `ContentEncoding`, pass it to the `install` function inside a [client configuration block](client-create-and-configure.md#configure-client):

```kotlin
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.compression.*
//...
val client = HttpClient(CIO) {
    install(ContentEncoding)
}
```

## Configure `ContentEncoding` {id="configure_plugin"}

### Enable encoders

You can configure which encoders are supported and specify their quality values (used in the `Accept-Encoding` header).

The [example](https://github.com/ktorio/ktor-documentation/tree/%ktor_version%/codeSnippets/snippets/client-content-encoding) below enables the `deflate` and `gzip` encoders with custom quality values:

```kotlin
```
{src="snippets/client-content-encoding/src/main/kotlin/com/example/Application.kt" include-lines="16-21"}

If necessary, you can implement the [`ContentEncoder`](https://api.ktor.io/ktor-utils/io.ktor.util/-content-encoder/index.html)
interface to create a custom encoder and register it using the `customEncoder()` function.

### Set the `mode` property

By default, `ContentEncoding` handles response decompression only. You can use the `mode` property to define how the
plugin operates.

The available values are:
<deflist>
<def>
<title><code>ContentEncodingConfig.Mode.DecompressResponse</code></title>
Decompresses responses only. This is the default mode.
</def>
<def>
<title><code>ContentEncodingConfig.Mode.CompressRequest</code></title>
Enables request body compression only.
</def>
<def>
<title><code>ContentEncodingConfig.Mode.All</code></title>
Enables both response decompression and request compression.
</def>
</deflist>

## Encode request body {id="encode_request_body"}

To enable request compression, set the `mode` property and use the `compress()` function inside the
[`HttpRequestBuilder`](https://api.ktor.io/ktor-client-core/io.ktor.client.request/-http-request-builder/index.html) block:

```kotlin
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.compression.*
//...
val client = HttpClient(CIO) {
    install(ContentEncoding) {
        mode = ContentEncodingConfig.Mode.CompressRequest
        gzip()
    }
}
client.post("/upload") {
    compress("gzip")
    setBody(someLongBody)
}
```

In this example:

* `mode = ContentEncodingConfig.Mode.CompressRequest` enables request compression.
* `gzip()` registers the gzip encoder.
* `compress("gzip")` applies gzip compression to this specific request.
* The `Content-Encoding` header is added automatically.

> For more details about handling responses, see [](client-responses.md).
>
{style="tip"}