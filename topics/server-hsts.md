[//]: # (title: HSTS)

<primary-label ref="server-plugin"/>

<var name="plugin_name" value="HSTS"/>
<var name="package_name" value="io.ktor.server.plugins.hsts"/>
<var name="artifact_name" value="ktor-server-hsts"/>

<tldr>
<p>
<b>Required dependencies</b>: <code>io.ktor:%artifact_name%</code>
</p>
<var name="example_name" value="ssl-engine-main-hsts"/>
<include from="lib.topic" element-id="download_example"/>
<include from="lib.topic" element-id="native_server_supported"/>
</tldr>

The [%plugin_name%](https://api.ktor.io/ktor-server/ktor-server-plugins/ktor-server-hsts/io.ktor.server.plugins.hsts/-h-s-t-s.html) plugin adds the required _HTTP Strict Transport Security_ headers to the request according to the [RFC 6797](https://tools.ietf.org/html/rfc6797). When the browser receives HSTS policy headers, it no longer attempts to connect to the server with insecure connections for a given period.

> Note that HSTS policy headers are ignored over an insecure HTTP connection. For HSTS to take effect, it should be served over a [secure](server-ssl.md) connection.


## Add dependencies {id="add_dependencies"}

<include from="lib.topic" element-id="add_ktor_artifact_intro"/>
<include from="lib.topic" element-id="add_ktor_artifact"/>

## Install %plugin_name% {id="install_plugin"}

<include from="lib.topic" element-id="install_plugin"/>
<include from="lib.topic" element-id="install_plugin_route"/>


## Configure %plugin_name% {id="configure"}

`%plugin_name%` exposes its settings via [HSTSConfig](https://api.ktor.io/ktor-server/ktor-server-plugins/ktor-server-hsts/io.ktor.server.plugins.hsts/-h-s-t-s-config/index.html). The example below shows how to use the `maxAgeInSeconds` property to specify how long the client should keep the host in a list of known HSTS hosts:

```kotlin
```
{src="snippets/ssl-engine-main-hsts/src/main/kotlin/com/example/Application.kt" include-lines="11-12,17"}

You can also provide different HSTS configurations for different hosts using `withHost`:

```kotlin
```
{src="snippets/ssl-engine-main-hsts/src/main/kotlin/com/example/Application.kt" include-lines="11-17"}

You can find the full example here: [ssl-engine-main-hsts](https://github.com/ktorio/ktor-documentation/tree/%ktor_version%/codeSnippets/snippets/ssl-engine-main-hsts).
