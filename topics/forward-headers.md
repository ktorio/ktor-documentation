[//]: # (title: Forwarded headers)


<var name="artifact_name" value="ktor-server-forwarded-header"/>

<microformat>
<p>
Required dependencies: <code>io.ktor:%artifact_name%</code>
</p>
<var name="example_name" value="forwarded-header"/>
<include src="lib.xml" include-id="download_example"/>
</microformat>

The `ForwardedHeaderSupport` and `XForwardedHeaderSupport` plugins allow you to handle reverse proxy headers to get information about the original [request](requests.md) when a Ktor server is placed behind a reverse proxy. This might be useful for [logging](logging.md) purposes.

* `ForwardedHeaderSupport` handles the `Forwarded` header ([RFC 7239](https://tools.ietf.org/html/rfc7239))
* `XForwardedHeaderSupport` handles the following `X-Forwarded-` headers:
   - `X-Forwarded-Host`/`X-Forwarded-Server` 
   - `X-Forwarded-For` 
   - `X-Forwarded-By`
   - `X-Forwarded-Proto`/`X-Forwarded-Protocol`
   - `X-Forwarded-SSL`/`Front-End-Https`

> To prevent manipulating the `Forwarded` headers, install these plugins if your application only accepts the reverse proxy connections.
> 
{type="note"}


## Add dependencies {id="add_dependencies"}
To use the `ForwardedHeaderSupport`/`XForwardedHeaderSupport` plugins, you need to include the `%artifact_name%` artifact in the build script:

<include src="lib.xml" include-id="add_ktor_artifact"/>


## Install plugins {id="install_plugin"}

<tabs>
<tab title="ForwardedHeaderSupport">

<var name="plugin_name" value="ForwardedHeaderSupport"/>
<include src="lib.xml" include-id="install_plugin"/>

</tab>

<tab title="XForwardedHeaderSupport">

<var name="plugin_name" value="XForwardedHeaderSupport"/>
<include src="lib.xml" include-id="install_plugin"/>

</tab>
</tabs>


`ForwardedHeaderSupport` and `XForwardedHeaderSupport` don't require any special configuration.


## Get request information {id="request_info"}

### Proxy request information {id="proxy_request_info"}

To get information about the proxy request, use the `call.request.local` property inside the [route handler](Routing_in_Ktor.md#define_route).
The code snippet below shows how to obtain information about the host and port:

```kotlin
```
{src="snippets/forwarded-header/src/main/kotlin/com/example/Application.kt" lines="14-16,22"}



### Original request information

To read information about the original request, use the `call.request.origin` property:

```kotlin
```
{src="snippets/forwarded-header/src/main/kotlin/com/example/Application.kt" lines="14,17-18,22"}

You can find the full example here: [forwarded-header](https://github.com/ktorio/ktor-documentation/tree/%current-branch%/codeSnippets/snippets/forwarded-header).
