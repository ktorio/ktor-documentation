[//]: # (title: HttpsRedirect)

<var name="plugin_name" value="HttpsRedirect"/>
<var name="artifact_name" value="ktor-server-http-redirect"/>

<microformat>
<p>
<b>Required dependencies</b>: <code>io.ktor:%artifact_name%</code>
</p>
<var name="example_name" value="ssl-engine-main"/>
<include src="lib.xml" include-id="download_example"/>
</microformat>

The `%plugin_name%` plugin redirects all HTTP requests to the [HTTPS counterpart](ssl.md) before processing the call. By default, a resource returns `301 Moved Permanently`, but it can be configured to be `302 Found`.

## Add dependencies {id="add_dependencies"}

<include src="lib.xml" include-id="add_ktor_artifact_intro"/>
<include src="lib.xml" include-id="add_ktor_artifact"/>


## Install %plugin_name% {id="install_plugin"}

<include src="lib.xml" include-id="install_plugin"/>

The code above installs the `%plugin_name%` plugin with the default configuration.

>When behind a reverse proxy, you need to install the `ForwardedHeaderSupport` or the `XForwardedHeaderSupport` plugin to detect HTTPS requests properly. If you get infinite redirect after installing one of these plugins, check out [this FAQ entry](FAQ.xml#infinite-redirect) for more details.
>
{type="note"}

## Configure %plugin_name% {id="configure"}

The code snippet below shows how to configure the desired HTTPS port and return `301 Moved Permanently` for the requested resource:

```kotlin
```
{src="snippets/ssl-engine-main/src/main/kotlin/com/example/Application.kt" lines="13-16"}

You can find the full example here: [ssl-engine-main](https://github.com/ktorio/ktor-documentation/tree/%current-branch%/codeSnippets/snippets/ssl-engine-main).

## Testing {id="testing"}

Note that applying %plugin_name% changes how [testing](Testing.md) works. After applying this plugin, each request you perform results in a redirection response. Probably this is not what you want in most cases since that behavior is already tested.

### XForwardedHeaderSupport trick {id="x-forwarded-header"}

You can install the [XForwardedHeaderSupport](forward-headers.md) plugin in your application and add a `header(HttpHeaders.XForwardedProto, "https")` header to the request.

<tabs>
<tab title="Test">

```kotlin
```
{src="snippets/ssl-engine-main/src/test/kotlin/ApplicationTest.kt" lines="11-18"}

</tab>

<tab title="Application">

```kotlin
```
{src="snippets/ssl-engine-main/src/main/kotlin/com/example/Application.kt"}

</tab>
</tabs>

You can find the full example here: [ssl-engine-main](https://github.com/ktorio/ktor-documentation/tree/%current-branch%/codeSnippets/snippets/ssl-engine-main).
