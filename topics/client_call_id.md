[//]: # (title: CallId)

<show-structure for="chapter" depth="2"/>

<var name="artifact_name" value="ktor-client-call-id"/>
<var name="package_name" value="io.ktor.client.plugins.callid"/>
<var name="plugin_name" value="CallId"/>

<tldr>
<p>
<b>Required dependencies</b>: <code>io.ktor:%artifact_name%</code>
</p>
<var name="example_name" value="client-call-id"/>
<include from="lib.topic" element-id="download_example"/>
</tldr>

<link-summary>
The %plugin_name% client plugin allows you to trace client requests by using unique call IDs.
</link-summary>

The %plugin_name% plugin allows you to trace client requests end-to-end by using unique call IDs. It is particularly
useful in microservice architectures to keep track of calls, regardless of how many services a request goes through.

A calling scope might already have a call ID in its coroutine context. By default, the plugin uses the current context
to retrieve a call ID and adds it to the context of the specific call using the `HttpHeaders.XRequestId` header.

Additionally, if a scope comes without a call ID, you can [configure the plugin](#configure) to generate and apply a new
call ID.

## Add dependencies {id="add_dependencies"}

<include from="lib.topic" element-id="add_ktor_artifact_intro"/>
<include from="lib.topic" element-id="add_ktor_artifact"/>

## Install %plugin_name% {id="install_plugin"}

<include from="lib.topic" element-id="install_plugin"/>

## Configure %plugin_name% {id="configure"}

The %plugin_name% plugin configuration, provided by the [CallIdConfig]() class, allows you to generate a call ID and add
it to the call context.

### Generate a call ID

Generate a call ID for a specific request in one of the following ways:

* The `useCoroutineContext` property, enabled by default, adds a generator that uses the current `CoroutineContext` to
  retrieve a call ID. To disable this functionality, set `useCoroutineContext` to `false`:

 ```kotlin
 ```

{src="snippets/client-call-id/src/main/kotlin/com/example/Application.kt" include-lines="34-35,37"}

> In a Ktor server, use the [CallId plugin](server-call-id.md) to add a call ID to the `CoroutineContext`.

* The `generate()` function allows you to generate a call ID for an outgoing request. If it fails to generate a call ID,
  it returns `null`.

 ```kotlin
 ```

{src="snippets/client-call-id/src/main/kotlin/com/example/Application.kt" include-lines="34,36-37"}

You can use multiple methods to generate a call ID. In this way, the first non-null value will be applied.

### Add a call ID

After you retrieve a call ID, you have the following options available to add it to the request:

* The `intercept()` function allows you to add a call ID to the request by using the [CallIdInterceptor]().

 ```kotlin
 ```

{src="snippets/client-call-id/src/main/kotlin/com/example/CallIdService.kt" include-lines="23-27"}

* The `addToHeader()` function adds a call ID to a specified header. It takes a header as a parameter, which defaults
  to `HttpHeaders.XRequestId`.

 ```kotlin
 ```

{src="snippets/client-call-id/src/main/kotlin/com/example/Application.kt" include-lines="18,20-21"}

## Example

In the following example, the `%plugin_name%` plugin for the Ktor client is configured to generate a new call ID and add
it to the header:

 ```kotlin
 ```

{src="snippets/client-call-id/src/main/kotlin/com/example/Application.kt" include-lines="17-22"}

The plugin uses the coroutine context to get a call ID and utilizes the `generate()` function to generate a new one. The
first non-null call ID is then applied to the request header using the `addToHeader()` function.

In a Ktor server, the call ID can then be retrieved from the header using the [retrieve](server-call-id.md#retrieve) functions
from the [CallId plugin for the server](server-call-id.md).

 ```kotlin
 ```

{src="snippets/client-call-id/src/main/kotlin/com/example/CallIdService.kt" include-lines="29-30,33"}

In this way the Ktor server retrieves the ID of the specified header of the request and applies it to the `callId`
property of the call.

For the full example,
see [client-call-id](https://github.com/ktorio/ktor-documentation/tree/%ktor_version%/codeSnippets/snippets/client-call-id)