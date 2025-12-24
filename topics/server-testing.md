[//]: # (title: Testing in Ktor Server)

<show-structure for="chapter" depth="3"/>

<tldr>
<p>
<b>Required dependencies</b>: <code>io.ktor:ktor-server-test-host</code>, <code>org.jetbrains.kotlin:kotlin-test</code>
</p>
</tldr>

<link-summary>
Learn how to test your server application using a special testing engine.
</link-summary>

Ktor provides a testing engine that runs application calls directly without starting a real web server or binding to
sockets. Requests are processed internally, which makes tests faster and more reliable compared to running a
full server.

## Add dependencies {id="add-dependencies"}

To test a Ktor server application, include the following dependencies in your build script:

* The `ktor-server-test-host` dependency provides the testing engine:

   <var name="artifact_name" value="ktor-server-test-host"/>
   <include from="lib.topic" element-id="add_ktor_artifact_testing"/>

* The `kotlin-test` dependency provides a set of utility functions for performing assertions:

  <var name="group_id" value="org.jetbrains.kotlin"/>
  <var name="artifact_name" value="kotlin-test"/>
  <var name="version" value="kotlin_version"/>
  <include from="lib.topic" element-id="add_artifact_testing"/>

> For [Native server](server-native.md#add-dependencies) tests, add these artifacts to the `nativeTest` source set.

## Testing overview {id="overview"}

You can test a Ktor application using the [`testApplication {}`](https://api.ktor.io/ktor-server-test-host/io.ktor.server.testing/test-application.html)
function and the provided [HTTP client](client-create-and-configure.md). A typical workflow includes the following steps:

1. Create a JUnit test class and a test function.
2. Use the `testApplication {}` function to [configure and run a test instance](#configure-test-app) of your application.
3. Optionally, [configure the HTTP client](#configure-client).
4. Use the client to [make HTTP requests](#make-request) to your test application and receive responses.
5. [Verify responses](#assert) using assertions from `kotlin.test`, including status codes, headers, and body content.

The following example tests a simple Ktor application that responds to `GET /` requests with plain text:

<tabs>
<tab title="Test">

```kotlin
```
{src="snippets/engine-main/src/test/kotlin/EngineMainTest.kt"}

</tab>

<tab title="Application">

```kotlin
```
{src="snippets/engine-main/src/main/kotlin/com/example/Application.kt"}

</tab>
</tabs>

> For the complete code example, see [engine-main](https://github.com/ktorio/ktor-documentation/tree/%ktor_version%/codeSnippets/snippets/engine-main).

## Configure a test application {id="configure-test-app"}

When configuring a test application, you may:

- [Add application modules](#add-modules)
- [(Optional) Add routes](#add-routing)
- [(Optional) Customize the environment](#environment)
- [(Optional) Mock external services](#external-services)

> By default, the configured test application starts on the [first client call](#make-request).
> Optionally, you can call the `startApplication()` function to start the application manually.
> This might be useful if you need to test your application's [lifecycle events](server-events.md#predefined-events).

### Add application modules {id="add-modules"}

[Modules](server-modules.md) must be loaded to a test application either [by explicitly
loading them](#explicit-module-loading) or [by configuring the environment](#configure-env).

#### Explicit module loading {id="explicit-module-loading"}

To add modules to a test application manually, use the `application {}` block:

```kotlin
```
{src="snippets/embedded-server-modules/src/test/kotlin/EmbeddedServerTest.kt" include-symbol="testModule1"}

#### Load modules from a configuration file {id="configure-env"}

To load modules from a configuration file, use the `environment {}` block to specify the configuration
file for your test:

```kotlin
```
{src="snippets/auth-oauth-google/src/test/kotlin/ApplicationTest.kt" include-lines="17-21,51"}

This method is useful when you need to mimic different environments or use custom configuration settings during testing.

### Access the application instance {id="access-application"}

Inside the `application {}` block, you can access the `Application` instance being configured:

```kotlin
testApplication {
    application {
        val app: Application = this
        // Interact with the application instance here
    }
}
```
Additionally, the `testApplication` scope exposes the `application` property, which returns the same `Application`
instance used by the test. This allows you to inspect or interact with the application directly from your test code.

```kotlin
```
{src="snippets/embedded-server-modules/src/test/kotlin/EmbeddedServerTest.kt" include-symbol="testAccessApplicationInstance"}

> Accessing the `application` property before calling `startApplication()` or making the first client request returns
> the `Application` instance, but it may not have been started yet.
> 
{style="note"}

### Add routes {id="add-routing"}

You can add routes to your test application using the `routing {}` block. This approach is useful for testing routes
without loading full modules or for adding test-specific endpoints.

The following example adds the `/login-test` endpoint used to initialize a user session in tests:

```kotlin
```
{src="snippets/auth-oauth-google/src/test/kotlin/ApplicationTest.kt" include-lines="18,31-35,51"}
   
> For a complete test example, see [auth-oauth-google](https://github.com/ktorio/ktor-documentation/tree/%ktor_version%/codeSnippets/snippets/auth-oauth-google).

### Customize the environment {id="environment"}

To configure a custom environment for your test application, use the `environment {}` function.

For example, to load a configuration file from the `test/resources` folder:

```kotlin
```
{src="snippets/auth-oauth-google/src/test/kotlin/ApplicationTest.kt" include-lines="17-21,51"}

Alternatively, you can provide configuration properties programmatically using [`MapApplicationConfig`](https://api.ktor.io/ktor-server-core/io.ktor.server.config/-map-application-config/index.html).
This is useful when you need access to application configuration before the application starts.

```kotlin
```
{src="snippets/engine-main-custom-environment/src/test/kotlin/ApplicationTest.kt" include-lines="10-14,21"}

### Mock external services {id="external-services"}

You can simulate external services using the `externalServices {}` function. Inside its block, use the `hosts() {}`
function for each service you want to mock. Within the `hosts() {}` block, you can configure an `Application` that acts
as the mock service by defining routes and installing plugins.

The following example simulates a JSON response from a Google API:

```kotlin
```
{src="snippets/auth-oauth-google/src/test/kotlin/ApplicationTest.kt" include-lines="18,36-47,51"}

> For the full test example, see [auth-oauth-google](https://github.com/ktorio/ktor-documentation/tree/%ktor_version%/codeSnippets/snippets/auth-oauth-google).

## Configure a client {id="configure-client"}

The `testApplication {}` function provides a configured HTTP client through the `client` property. 
To customize the client and install additional plugins, use the `createClient {}` function.

For example, to [send JSON data](#json-data) in a `POST/PUT` request, you can install the [`ContentNegotiation`](client-serialization.md)
plugin:

```kotlin
```
{src="snippets/json-kotlinx/src/test/kotlin/jsonkotlinx/ApplicationTest.kt" include-lines="31-40,48"}

## Make a request {id="make-request"}

Use the configured client to [make requests](client-requests.md) and [receive responses](client-responses.md).

The following example tests the `/customer` endpoint that handles `POST` requests:

```kotlin
```
{src="snippets/json-kotlinx/src/test/kotlin/jsonkotlinx/ApplicationTest.kt" include-lines="31-44,48"}

> For the complete test example, see [json-kotlinx](https://github.com/ktorio/ktor-documentation/tree/%ktor_version%/codeSnippets/snippets/json-kotlinx).

## Assert results {id="assert"}

After receiving a response, you can verify the results using assertions from the [`kotlin.test`](https://kotlinlang.org/api/latest/kotlin.test/) library:

```kotlin
```
{src="snippets/json-kotlinx/src/test/kotlin/jsonkotlinx/ApplicationTest.kt" include-lines="31-48"}


## Test POST/PUT requests {id="test-post-put"}

### Send form data {id="form-data"}

To send form data in a test request, set the `Content-Type` header and the request body using the [`header()`](client-requests.md#headers)
and [`setBody()`](client-requests.md#body) functions.

#### Key/value pairs {id="x-www-form-urlencoded"}

To send key/value form parameters in a POST request, set the `Content-Type` header to `application/x-www-form-urlencoded`
and encode the parameters using the [`formUrlEncode()`](https://api.ktor.io/ktor-http/io.ktor.http/form-url-encode.html)
function:

<tabs>
<tab title="Test">

```kotlin
```
{src="snippets/post-form-parameters/src/test/kotlin/formparameters/ApplicationTest.kt"}

</tab>

<tab title="Application">

```kotlin
```
{src="snippets/post-form-parameters/src/main/kotlin/formparameters/Application.kt" include-lines="3-16,45-46"}

</tab>
</tabs>

> For the full code example, see [post-form-parameters](https://github.com/ktorio/ktor-documentation/tree/%ktor_version%/codeSnippets/snippets/post-form-parameters).

#### Multipart form data {id="multipart-form-data"}

You can use the `multipart/form-data` content type to build multipart form data and test file uploads:

<tabs>
<tab title="Test">

```kotlin
```
{src="snippets/upload-file/src/test/kotlin/uploadfile/UploadFileTest.kt"}

</tab>

<tab title="Application">

```kotlin
```
{src="snippets/upload-file/src/main/kotlin/uploadfile/UploadFile.kt"}

</tab>
</tabs>

> For the full code example, see [upload-file](https://github.com/ktorio/ktor-documentation/tree/%ktor_version%/codeSnippets/snippets/upload-file).

### Send JSON data {id="json-data"}

To serialize and deserialize JSON data in `POST/PUT` requests, install the [`ContentNegotiation`](client-serialization.md)
plugin to a new client.

Inside the request, you can specify the `Content-Type` header using the `contentType()` function and the request body
using the `setBody()` function.

<tabs>
<tab title="Test">

```kotlin
```
{src="snippets/json-kotlinx/src/test/kotlin/jsonkotlinx/ApplicationTest.kt" include-lines="3-11,31-48"}

</tab>

<tab title="Application">

```kotlin
```
{src="snippets/json-kotlinx/src/main/kotlin/jsonkotlinx/Application.kt" include-lines="3-16,25-31,38-44"}

</tab>
</tabs>

> For the full example, see [json-kotlinx](https://github.com/ktorio/ktor-documentation/tree/%ktor_version%/codeSnippets/snippets/json-kotlinx).

## Preserve cookies during testing {id="preserving-cookies"}

To preserve cookies between requests, install the [`HttpCookies`](client-cookies.md) plugin to a new client.

In the following example, the reload count increases after each request due to cookies being preserved:


<tabs>
<tab title="Test">

```kotlin
```
{src="snippets/session-cookie-client/src/test/kotlin/cookieclient/ApplicationTest.kt"}

</tab>

<tab title="Application">

```kotlin
```
{src="snippets/session-cookie-client/src/main/kotlin/cookieclient/Application.kt" include-lines="3-38"}

</tab>
</tabs>

> For the full example, see [session-cookie-client](https://github.com/ktorio/ktor-documentation/tree/%ktor_version%/codeSnippets/snippets/session-cookie-client).

## Test HTTPS {id="https"}

To test an [HTTPS endpoint](server-ssl.md), set the request protocol using the [`URLBuilder.protocol`](client-requests.md#url) property:

```kotlin
```
{src="snippets/ssl-engine-main/src/test/kotlin/ApplicationTest.kt" include-lines="3-22"}

> For the full example, see [ssl-engine-main](https://github.com/ktorio/ktor-documentation/tree/%ktor_version%/codeSnippets/snippets/ssl-engine-main).

## Test WebSockets {id="testing-ws"}

You can test [WebSocket conversations](server-websockets.md) by using the [`WebSockets`](client-websockets.topic)
client plugin:

```kotlin
```
{src="snippets/server-websockets/src/test/kotlin/com/example/ModuleTest.kt"}

## End-to-end testing with HttpClient {id="end-to-end"}

You can use the [Ktor HTTP client](client-create-and-configure.md) for a full end-to-end testing of your server application.

In the example below, the HTTP client makes a test request to the `TestServer`:

```kotlin
```
{src="snippets/embedded-server/src/test/kotlin/EmbeddedServerTest.kt"}

> For complete end-to-end testing examples, see [embedded-server](https://github.com/ktorio/ktor-documentation/tree/%ktor_version%/codeSnippets/snippets/embedded-server)
> and [e2e](https://github.com/ktorio/ktor-documentation/tree/%ktor_version%/codeSnippets/snippets/e2e).