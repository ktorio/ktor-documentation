[//]: # (title: Status pages)

<show-structure for="chapter" depth="2"/>

<var name="plugin_name" value="StatusPages"/>
<var name="package_name" value="io.ktor.server.plugins.statuspages"/>
<var name="artifact_name" value="ktor-server-status-pages"/>

<tldr>
<p>
<b>Required dependencies</b>: <code>io.ktor:%artifact_name%</code>
</p>
<var name="example_name" value="status-pages"/>
<include from="lib.topic" element-id="download_example"/>
<include from="lib.topic" element-id="native_server_supported"/>
</tldr>

<link-summary>
%plugin_name% allows Ktor applications to respond appropriately to any failure state based on a thrown exception or status code.
</link-summary>

The [%plugin_name%](https://api.ktor.io/ktor-server/ktor-server-plugins/ktor-server-status-pages/io.ktor.server.plugins.statuspages/-status-pages.html) plugin allows Ktor applications to [respond](responses.md) appropriately to any failure state based on a thrown exception or status code.

## Add dependencies {id="add_dependencies"}

<include from="lib.topic" element-id="add_ktor_artifact_intro"/>
<include from="lib.topic" element-id="add_ktor_artifact"/>

## Install %plugin_name% {id="install_plugin"}

<include from="lib.topic" element-id="install_plugin"/>

## Configure %plugin_name% {id="configure"}

There are three main configuration options provided by the `%plugin_name%` plugin:

- [exceptions](#exceptions): configures a response based on mapped exception classes
- [status](#status): configures a response to a status code value
- [statusFile](#status-file): configures a file response from the classpath


### Exceptions {id="exceptions"}

The `exception` handler allows you to handle calls that result in a `Throwable` exception. In the most basic case, the `500` HTTP status code can be configured for any exception:

```kotlin
install(StatusPages) {
    exception<Throwable> { call, cause ->
        call.respondText(text = "500: $cause" , status = HttpStatusCode.InternalServerError)
    }
}
```

You can also check specific exceptions and respond with the required content:

```kotlin
```
{src="snippets/status-pages/src/main/kotlin/com/example/Application.kt" include-lines="12-19,24"}


### Status {id="status"}

The `status` handler provides the capability to respond with specific content based on the status code. The example below shows how to respond on requests if the resource is missing on a server (the `404` status code):

```kotlin
```
{src="snippets/status-pages/src/main/kotlin/com/example/Application.kt" include-lines="12,20-22,24"}

### Status file {id="status-file"}

The `statusFile` handler allows you to serve HTML pages based on the status code. Suppose your project contains the `error401.html` and `error402.html` HTML pages in the `resources` folder. In this case, you can handle the `401` and `402` status codes using `statusFile` as follows:
```kotlin
```
{src="snippets/status-pages/src/main/kotlin/com/example/Application.kt" include-lines="12,23-24"}

The `statusFile` handler replaces any `#` character with the value of the status code within the list of configured statuses.

> You can find the full example here: [status-pages](https://github.com/ktorio/ktor-documentation/tree/%ktor_version%/codeSnippets/snippets/status-pages).

