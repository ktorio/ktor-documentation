[//]: # (title: AutoHeadResponse)

<var name="plugin_name" value="AutoHeadResponse"/>
<var name="artifact_name" value="ktor-server-auto-head-response"/>

<microformat>
<p>
<b>Required dependencies</b>: <code>io.ktor:%artifact_name%</code>
</p>
<var name="example_name" value="autohead"/>
<include src="lib.xml" include-id="download_example"/>
</microformat>

<excerpt>
%plugin_name% provides the ability to automatically respond to HEAD requests for every route that has a GET defined.
</excerpt>

The [%plugin_name%](https://api.ktor.io/ktor-server/ktor-server-plugins/ktor-server-auto-head-response/io.ktor.server.plugins.autohead/-auto-head-response.html) plugin provides us with the ability to automatically respond to a `HEAD` request for every route that has a `GET` defined. You can use `%plugin_name%` to avoid creating a separate [head](Routing_in_Ktor.md#define_route) handler if you need to somehow process a response on the client before getting the actual content. For example, calling the [respondFile](responses.md#file) function adds the `Content-Length` and `Content-Type` headers to a response automatically, and you can get this information on the client before downloading the file.

## Add dependencies {id="add_dependencies"}

<include src="lib.xml" include-id="add_ktor_artifact_intro"/>
<include src="lib.xml" include-id="add_ktor_artifact"/>

## Usage
In order to take advantage of this functionality, we need to install the `AutoHeadResponse` plugin in our application.


```kotlin
```
{src="snippets/autohead/src/main/kotlin/com/example/Application.kt" include-symbol="main"}

In our case the `/home` route will now respond to `HEAD` request even though there is no explicit definition for this verb.

It's important to note that if we're using this plugin, custom `HEAD` definitions for the same `GET` route will be ignored.


## Options
`%plugin_name%` does not provide any additional configuration options.
