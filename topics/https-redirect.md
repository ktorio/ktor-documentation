[//]: # (title: HttpsRedirect)

<var name="plugin_name" value="HttpsRedirect"/>
<var name="package_name" value="io.ktor.server.plugins.httpsredirect"/>
<var name="artifact_name" value="ktor-server-http-redirect"/>

<tldr>
<p>
<b>Required dependencies</b>: <code>io.ktor:%artifact_name%</code>
</p>
<var name="example_name" value="ssl-engine-main-redirect"/>
<include src="lib.xml" element-id="download_example"/>
</tldr>

The [%plugin_name%](https://api.ktor.io/ktor-server/ktor-server-plugins/ktor-server-http-redirect/io.ktor.server.plugins.httpsredirect/-https-redirect.html) plugin redirects all HTTP requests to the [HTTPS counterpart](ssl.md) before processing the call. By default, a resource returns `301 Moved Permanently`, but it can be configured to be `302 Found`.

## Add dependencies {id="add_dependencies"}

<include src="lib.xml" element-id="add_ktor_artifact_intro"/>
<include src="lib.xml" element-id="add_ktor_artifact"/>


## Install %plugin_name% {id="install_plugin"}

<include src="lib.xml" element-id="install_plugin"/>

The code above installs the `%plugin_name%` plugin with the default configuration.

>When behind a reverse proxy, you need to install the `ForwardedHeader` or the `XForwardedHeader` plugin to detect HTTPS requests properly. If you get infinite redirect after installing one of these plugins, check out [this FAQ entry](FAQ.xml#infinite-redirect) for more details.
>
{type="note"}

## Configure %plugin_name% {id="configure"}

The code snippet below shows how to configure the desired HTTPS port and return `301 Moved Permanently` for the requested resource:

```kotlin
```
{src="snippets/ssl-engine-main-redirect/src/main/kotlin/com/example/Application.kt" lines="11-14"}

You can find the full example here: [ssl-engine-main-redirect](https://github.com/ktorio/ktor-documentation/tree/%current-branch%/codeSnippets/snippets/ssl-engine-main-redirect).
