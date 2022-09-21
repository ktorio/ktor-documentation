[//]: # (title: Forwarded headers)

<show-structure for="chapter" depth="2"/>

<var name="artifact_name" value="ktor-server-forwarded-header"/>
<var name="package_name" value="io.ktor.server.plugins.forwardedheaders"/>

<tldr>
<p>
<b>Required dependencies</b>: <code>io.ktor:%artifact_name%</code>
</p>
<var name="example_name" value="forwarded-header"/>
<include from="lib.topic" element-id="download_example"/>
<include from="lib.topic" element-id="native_server_supported"/>
</tldr>

The [ForwardedHeaders](https://api.ktor.io/ktor-server/ktor-server-plugins/ktor-server-forwarded-header/io.ktor.server.plugins.forwardedheaders/-forwarded-headers.html) and [XForwardedHeaders](https://api.ktor.io/ktor-server/ktor-server-plugins/ktor-server-forwarded-header/io.ktor.server.plugins.forwardedheaders/-x-forwarded-headers.html) plugins allow you to handle reverse proxy headers to get information about the original [request](requests.md) when a Ktor server is placed behind a reverse proxy. This might be useful for [logging](logging.md) purposes.

* `ForwardedHeaders` handles the `Forwarded` header ([RFC 7239](https://tools.ietf.org/html/rfc7239))
* `XForwardedHeaders` handles the following `X-Forwarded-` headers:
   - `X-Forwarded-Host`/`X-Forwarded-Server` 
   - `X-Forwarded-For` 
   - `X-Forwarded-By`
   - `X-Forwarded-Proto`/`X-Forwarded-Protocol`
   - `X-Forwarded-SSL`/`Front-End-Https`

> To prevent manipulating the `Forwarded` headers, install these plugins if your application only accepts the reverse proxy connections.
> 
{type="note"}


## Add dependencies {id="add_dependencies"}
To use the `ForwardedHeaders`/`XForwardedHeaders` plugins, you need to include the `%artifact_name%` artifact in the build script:

<include from="lib.topic" element-id="add_ktor_artifact"/>


## Install plugins {id="install_plugin"}

<tabs>
<tab title="ForwardedHeader">

<var name="plugin_name" value="ForwardedHeaders"/>
<include from="lib.topic" element-id="install_plugin"/>

</tab>

<tab title="XForwardedHeader">

<var name="plugin_name" value="XForwardedHeaders"/>
<include from="lib.topic" element-id="install_plugin"/>

</tab>
</tabs>


`ForwardedHeaders` and `XForwardedHeaders` don't require any special configuration.


## Get request information {id="request_info"}

### Proxy request information {id="proxy_request_info"}

To get information about the proxy request, use the `call.request.local` property inside the [route handler](Routing_in_Ktor.md#define_route).
The code snippet below shows how to obtain information about the host and port:

```kotlin
```
{src="snippets/forwarded-header/src/main/kotlin/com/example/Application.kt" include-lines="15-17,23"}



### Original request information

To read information about the original request, use the `call.request.origin` property:

```kotlin
```
{src="snippets/forwarded-header/src/main/kotlin/com/example/Application.kt" include-lines="15,18-19,23"}

You can find the full example here: [forwarded-header](https://github.com/ktorio/ktor-documentation/tree/%ktor_version%/codeSnippets/snippets/forwarded-header).
