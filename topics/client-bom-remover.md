[//]: # (title: BOM remover)

<var name="artifact_name" value="ktor-client-bom-remover"/>
<primary-label ref="client-plugin"/>

<tldr>
<p>
<b>Required dependencies</b>: <code>io.ktor:%artifact_name%</code>
</p>
<var name="example_name" value="client-bom-remover"/>
<include from="lib.topic" element-id="download_example"/>
</tldr>

<link-summary>
The BOMRemover plugin allows you to remove Byte Order Mark (BOM) from the response body.
</link-summary>

[Byte Order Mark (BOM)](https://en.wikipedia.org/wiki/Byte_order_mark) is a character encoded in a Unicode file or
stream. The main purpose of BOM is to signal the text's stream encoding and the byte order of 16-bit and 32-bit
encodings.

In some cases, it is necessary to remove BOM from the response body. For example, in the UTF-8 encoding, the
presence of BOM is optional, and it may cause problems when it's read by software
that does not know how to handle the BOM.

The Ktor client provides
the [BOMRemover](https://api.ktor.io/ktor-client-bom-remover/io.ktor.client.plugins.bomremover/index.html)
plugin that removes BOM from the response body in the UTF-8, UTF-16 (BE), UTF-16 (LE), UTF-32 (BE) and UTF-32 (LE)
encodings.

> Note that when removing the BOM, Ktor does not change the `Content-Length` header, which retains the
length of the initial response.
>
{style="note"}

## Add dependencies {id="add_dependencies"}

To use `BOMRemover`, you need to include the `%artifact_name%` artifact in the build script:

<include from="lib.topic" element-id="add_ktor_artifact"/>
<include from="lib.topic" element-id="add_ktor_client_artifact_tip"/>

## Install BOMRemover {id="install_plugin"}

To install `BOMRemover`, pass it to the `install` function inside
a [client configuration block](client-create-and-configure.md#configure-client):

```kotlin
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.bomremover.*
//...
val client = HttpClient(CIO) {
    install(BOMRemover)
}
```
