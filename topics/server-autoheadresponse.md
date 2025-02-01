[//]: # (title: AutoHeadResponse)

<var name="plugin_name" value="AutoHeadResponse"/>
<var name="artifact_name" value="ktor-server-auto-head-response"/>
<primary-label ref="server-plugin"/>

<tldr>
<p>
<b>Required dependencies</b>: <code>io.ktor:%artifact_name%</code>
</p>
<var name="example_name" value="autohead"/>
<include from="lib.topic" element-id="download_example"/>
<include from="lib.topic" element-id="native_server_supported"/>
</tldr>

<link-summary>
%plugin_name% provides the ability to automatically respond to HEAD requests for every route that has a GET defined.
</link-summary>

The [%plugin_name%](https://api.ktor.io/ktor-server/ktor-server-plugins/ktor-server-auto-head-response/io.ktor.server.plugins.autohead/-auto-head-response.html) plugin provides us with the ability to automatically respond to a `HEAD` request for every route that has a `GET` defined. You can use `%plugin_name%` to avoid creating a separate [head](server-routing.md#define_route) handler if you need to somehow process a response on the client before getting the actual content. For example, calling the [respondFile](server-responses.md#file) function adds the `Content-Length` and `Content-Type` headers to a response automatically, and you can get this information on the client before downloading the file.

## Add dependencies {id="add_dependencies"}

<include from="lib.topic" element-id="add_ktor_artifact_intro"/>
<include from="lib.topic" element-id="add_ktor_artifact"/>

## Usage
In order to take advantage of this functionality, we need to install the `AutoHeadResponse` plugin in our application.


```kotlin
```
{src="snippets/autohead/src/main/kotlin/com/example/Application.kt" include-lines="3-15"}

In our case the `/home` route will now respond to `HEAD` request even though there is no explicit definition for this verb.

It's important to note that if we're using this plugin, custom `HEAD` definitions for the same `GET` route will be ignored.


## Options
`%plugin_name%` does not provide any additional configuration options.
