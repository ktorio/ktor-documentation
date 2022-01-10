[//]: # (title: HSTS)

<var name="plugin_name" value="HSTS"/>
<var name="artifact_name" value="ktor-server-hsts"/>

<microformat>
<p>
Required dependencies: <code>io.ktor:%artifact_name%</code>
</p>
<var name="example_name" value="ssl-engine-main"/>
<include src="lib.xml" include-id="download_example"/>
</microformat>

The `%plugin_name%` plugin adds the required _HTTP Strict Transport Security_ headers to the request according to the [RFC 6797](https://tools.ietf.org/html/rfc6797). When the browser receives HSTS policy headers, it no longer attempts to connect to the server with insecure connections for a given period.

> Note that HSTS policy headers are ignored over an insecure HTTP connection. For HSTS to take effect, it should be served over a [secure](ssl.md) connection.


## Add dependencies {id="add_dependencies"}

<include src="lib.xml" include-id="add_ktor_artifact_intro"/>
<include src="lib.xml" include-id="add_ktor_artifact"/>

## Install %plugin_name% {id="install_plugin"}

<include src="lib.xml" include-id="install_plugin"/>

The code above installs `%plugin_name%` with the default configuration.  

## Configure %plugin_name% {id="configure"}

`%plugin_name%` exposes its settings via [HSTS.Configuration](https://api.ktor.io/ktor-server/ktor-server-plugins/ktor-server-hsts/io.ktor.server.plugins/-h-s-t-s/-configuration/index.html). The example below shows how to use the `maxAgeInSeconds` property to specify how long the client should keep the host in a list of known HSTS hosts:

```kotlin
```
{src="snippets/ssl-engine-main/src/main/kotlin/com/example/Application.kt" lines="16-18"}

You can find the full example here: [ssl-engine-main](https://github.com/ktorio/ktor-documentation/tree/%current-branch%/codeSnippets/snippets/ssl-engine-main).
