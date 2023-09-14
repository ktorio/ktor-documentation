[//]: # (title: BOM remover)

<var name="artifact_name" value="ktor-client-bom-remover"/>

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

[Byte Order Mark (BOM)](https://en.wikipedia.org/wiki/Byte_order_mark) is a character encoded in a Unicode file or stream. In the UTF-8 encoding, the presence of BOM is optional, and it may cause problems when it's read by software that does not know how to handle the BOM.

The Ktor client provides the [BOMRemover](https://api.ktor.io/ktor-client/ktor-client-plugins/ktor-client-bom-remover/io.ktor.client.plugins.bomremover/index.html)
plugin that allows you to remove BOM from the response body.

## Add dependencies {id="add_dependencies"}
To use `BOMRemover`, you need to include the `%artifact_name%` artifact in the build script:

<include from="lib.topic" element-id="add_ktor_artifact"/>
<include from="lib.topic" element-id="add_ktor_client_artifact_tip"/>

## Install BOMRemover {id="install_plugin"}
To install `BOMRemover`, pass it to the `install` function inside a [client configuration block](create-client.md#configure-client):
```kotlin
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.compression.*
//...
val client = HttpClient(CIO) {
    install(BOMRemover)
}
```

> Note that the `Content-Length` header will contain the length of the initial response.
