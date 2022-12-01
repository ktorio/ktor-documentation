[//]: # (title: Custom plugins)

<show-structure for="chapter" depth="2"/>

<tldr>
<var name="example_name" value="client-custom-plugin"/>
<include from="lib.topic" element-id="download_example"/>
</tldr>

Starting with v2.2.0, Ktor provides a new API for creating custom client [plugins](http-client_plugins.md). In general, this API doesn't require an understanding of internal Ktor concepts, such as pipelines, phases, and so on. 
Instead, you have access to different stages of [handling requests and responses](#call-handling) using a set of handlers, such as `onRequest`, `onResponse`, and so on.


## Create and install your first plugin {id="first-plugin"}

In this section, we'll demonstrate how to create and install your first plugin that adds a custom header 
to each [request](request.md):

1. To create a plugin, call the `createClientPlugin` function and pass a plugin name as an argument:
   ```kotlin
   package com.example.plugins
   
   import io.ktor.client.plugins.api.*
   
   val CustomHeaderPlugin = createClientPlugin("CustomHeaderPlugin") {
       // Configure the plugin ...
   }
   ```
   
   This function returns the `ClientPlugin` instance that will be used to install the plugin.

2. To append a custom header to each request, you can use the `onRequest` handler,
   which provides access to request parameters:
   ```kotlin
   ```
   {src="snippets/client-custom-plugin/src/main/kotlin/com/example/plugins/CustomHeader.kt"}

3. To [install the plugin](http-client_plugins.md#install), pass the created `ClientPlugin` instance to the `install` function inside the client's configuration block:
   ```kotlin
   import com.example.plugins.*
   
   val client = HttpClient(CIO) {
       install(CustomHeaderPlugin)
   }
   ```
   
   
You can find the full example here: [CustomHeader.kt](https://github.com/ktorio/ktor-documentation/blob/%ktor_version%/codeSnippets/snippets/client-custom-plugin/src/main/kotlin/com/example/plugins/CustomHeader.kt). 
In the following sections, we'll look at how to provide a plugin configuration and handle requests and responses.


## Provide plugin configuration {id="plugin-configuration"}

The [previous section](#first-plugin) demonstrates how to create a plugin that appends a predefined custom header to each response. Let's make this plugin more useful and provide a configuration for passing any custom header name and value:

1. First, you need to define a configuration class:

   ```kotlin
   ```
   {src="snippets/client-custom-plugin/src/main/kotlin/com/example/plugins/CustomHeaderConfigurable.kt" include-lines="14-17"}

2. To use this configuration in a plugin, pass a configuration class reference to `createApplicationPlugin`:

   ```kotlin
   ```
   {src="snippets/client-custom-plugin/src/main/kotlin/com/example/plugins/CustomHeaderConfigurable.kt" include-lines="3-12"}

   Given that plugin configuration fields are mutable, saving them in local variables is recommended.

3. Finally, you can install and configure the plugin as follows:

   ```kotlin
   ```
   {src="snippets/client-custom-plugin/src/main/kotlin/com/example/Application.kt" include-lines="11-15,18"}

> You can find the full example here: [CustomHeaderConfigurable.kt](https://github.com/ktorio/ktor-documentation/blob/%ktor_version%/codeSnippets/snippets/client-custom-plugin/src/main/kotlin/com/example/plugins/CustomHeaderConfigurable.kt).



## Handle requests and responses {id="call-handling"}

Custom plugins provide access to different stages of handling requests and responses 
using a set of dedicated handlers, for example:
- `onRequest` and `onResponse` allow you to handle requests and responses, respectively.
- `transformRequestBody` and `transformResponseBody` can be used to apply necessary transformations to 
   request and response bodies.

There is also the `on(...)` handler that allows you to invoke specific hooks that might be useful to handle other stages of a call.
The tables below list all handlers in the order they are executed:

<tabs>
<tab title="Basic hooks" id="basic-hooks">

<table id="basic-hooks-table">
<tr>
<td>
Handler
</td>
<td>
Description
</td>
</tr>

<include from="client-custom-plugins.md" element-id="onRequest" id="on-request"/>
<include from="client-custom-plugins.md" element-id="transformRequestBody" id="transform-request-body"/>
<include from="client-custom-plugins.md" element-id="onResponse" id="on-response"/>
<include from="client-custom-plugins.md" element-id="transformResponseBody" id="transform-response-body"/>
<include from="client-custom-plugins.md" element-id="onClose" id="on-close"/>

</table>

</tab>
<tab title="All hooks" id="all-hooks">

<table id="all-hooks-table">
<tr>
<td>
Handler
</td>
<td>
Description
</td>
</tr>

<tr>
<td>
<code>on(SetupRequest)</code>
</td>
<td>
The <code>SetupRequest</code> hook is executed first in request processing.
</td>
</tr>

<tr id="onRequest">
<td>
<code>onRequest</code>
</td>
<td>
<p>
This handler is executed for each HTTP <a href="request.md">request</a> and allows you to modify it.
</p>
<p>
<emphasis>
Example: <a anchor="example-custom-header"/>
</emphasis>
</p>
</td>
</tr>

<tr id="transformRequestBody">
<td>
<code>transformRequestBody</code>
</td>
<td>
<p>
Allows you to transform a <a href="request.md" anchor="body">request body</a>.
In this handler, you need to serialize the body into 
<a href="https://api.ktor.io/ktor-http/io.ktor.http.content/-outgoing-content/index.html">OutgoingContent</a> 
(for example, <code>TextContent</code>, <code>ByteArrayContent</code>, or <code>FormDataContent</code>)
or return <code>null</code> if your transformation is not applicable.
</p>
<p>
<emphasis>
Example: <a anchor="data-transformation"/>
</emphasis>
</p>
</td>
</tr>


<tr>
<td>
<code>on(Send)</code>
</td>
<td>
<p>
The <code>Send</code> hook provides the ability to inspect a response and initiate additional requests if needed. 
This might be useful for handling redirects, retrying requests, authentication, and so on.
</p>
<p>
<emphasis>
Example: <a anchor="authentication"/>
</emphasis>
</p>
</td>
</tr>


<tr>
<td>
<code>on(SendingRequest)</code>
</td>
<td>
<p>
The <code>SendingRequest</code> hook is executed for every request, 
even if it's not initiated by a user.
For example, if a request results in a redirect, the <code>onRequest</code> handler will be executed only 
for the original request, while <code>on(SendingRequest)</code> will be executed for both original and redirected requests.
Similarly, if you used <code>on(Send)</code> to initiate an additional request,
handlers will be ordered as follows:
</p>

```Console
--> onRequest
--> on(Send)
--> on(SendingRequest)
<-- onResponse
--> on(SendingRequest)
<-- onResponse
```

<p>
<emphasis>
Examples: <a anchor="example-log-headers"/>, <a anchor="example-response-time"/>
</emphasis>
</p>
</td>
</tr>


<tr id="onResponse">
<td>
<code>onResponse</code>
</td>
<td>
<p>
This handler is executed for each incoming HTTP <a href="request.md">response</a> and allows you to 
inspect it in various ways: log a response, save cookies, and so on.
</p>
<p>
<emphasis>
Examples: <a anchor="example-log-headers"/>, <a anchor="example-response-time"/>
</emphasis>
</p>
</td>
</tr>


<tr id="transformResponseBody">
<td>
<code>transformResponseBody</code>
</td>
<td>
<p>
Allows you to transform a <a href="response.md" anchor="body">response body</a>.
This handler is invoked for each <code>HttpResponse.body</code> call.
You need to deserialize the body into an instance of <code>requestedType</code> 
or return <code>null</code> if your transformation is not applicable.
</p>
<p>
<emphasis>
Example: <a anchor="data-transformation"/>
</emphasis>
</p>
</td>
</tr>


<tr id="onClose">
<td>
<code>onClose</code>
</td>
<td>
Allows you to clean resources allocated by this plugin.
This handler is called when the client is <a href="create-client.md" anchor="close-client">closed</a>.
</td>
</tr>


</table>

</tab>
</tabs>


### Share call state {id="call-state"}

Custom plugins allow you to share any value related to a call 
so that you can access this value inside any handler processing this call. 
This value is stored as an attribute with a unique key in the `call.attributes` collection. 
The example below demonstrates how to use attributes to calculate the time between sending a request and receiving a response:

```kotlin
```
{src="snippets/client-custom-plugin/src/main/kotlin/com/example/plugins/ResponseTime.kt" include-lines="3-18"}

You can find the full example here: [ResponseTime.kt](https://github.com/ktorio/ktor-documentation/blob/%ktor_version%/codeSnippets/snippets/client-custom-plugin/src/main/kotlin/com/example/plugins/ResponseTime.kt).




## Access client configuration {id="client-config"}

You can access your client configuration using the `client` property, which returns the [HttpClient](https://api.ktor.io/ktor-client/ktor-client-core/io.ktor.client/-http-client/index.html) instance.
The example below shows how to get the [proxy address](proxy.md) used by the client:

```kotlin
import io.ktor.client.plugins.api.*

val SimplePlugin = createClientPlugin("SimplePlugin") {
    val proxyAddress = client.engineConfig.proxy?.address()
    println("Proxy address: $proxyAddress")
}
```

## Examples {id="examples"}

The code samples below demonstrate several examples of custom plugins.
You can find the resulting project here: [client-custom-plugin](https://github.com/ktorio/ktor-documentation/blob/%ktor_version%/codeSnippets/snippets/client-custom-plugin/).

### Custom header {id="example-custom-header"}

Shows how to create a plugin that adds a custom header to each request:

```kotlin
```
{src="snippets/client-custom-plugin/src/main/kotlin/com/example/plugins/CustomHeaderConfigurable.kt"}

### Logging headers {id="example-log-headers"}

Demonstrates how to create a plugin that logs request and response headers:

```kotlin
```
{src="snippets/client-custom-plugin/src/main/kotlin/com/example/plugins/LoggingHeaders.kt"}


### Response time {id="example-response-time"}

Shows how to create a plugin that measures the time between sending a request and receiving a response:

```kotlin
```
{src="snippets/client-custom-plugin/src/main/kotlin/com/example/plugins/ResponseTime.kt"}


### Data transformation {id="data-transformation"}

Shows how to transform request and response bodies using the `transformRequestBody` and `transformResponseBody` hooks:

<tabs>
<tab title="DataTransformation.kt">

```kotlin
```
{src="snippets/client-custom-plugin-data-transformation/src/main/kotlin/com/example/plugins/DataTransformation.kt"}

</tab>
<tab title="Application.kt">

```kotlin
```
{src="snippets/client-custom-plugin-data-transformation/src/main/kotlin/com/example/Application.kt"}

</tab>
<tab title="User.kt">

```kotlin
```
{src="snippets/client-custom-plugin-data-transformation/src/main/kotlin/com/example/model/User.kt"}

</tab>
</tabs>

You can find the full example here: [client-custom-plugin-data-transformation](https://github.com/ktorio/ktor-documentation/tree/%ktor_version%/codeSnippets/snippets/client-custom-plugin-data-transformation).


### Authentication {id="authentication"}

A sample Ktor project showing how to use the `on(Send)` hook to add a bearer token to the `Authorization` header if an unauthorized response is received from the server:

<tabs>
<tab title="Auth.kt">

```kotlin
```
{src="snippets/client-custom-plugin-auth/src/main/kotlin/com/example/plugins/Auth.kt"}

</tab>
<tab title="Application.kt">

```kotlin
```
{src="snippets/client-custom-plugin-auth/src/main/kotlin/com/example/Application.kt"}

</tab>
</tabs>

You can find the full example here: [client-custom-plugin-auth](https://github.com/ktorio/ktor-documentation/tree/%ktor_version%/codeSnippets/snippets/client-custom-plugin-auth).
