[//]: # (title: Testing)

<microformat>
<p>
<b>Required dependencies</b>: <code>io.ktor:ktor-server-test-host</code>, <code>org.jetbrains.kotlin:kotlin-test</code>
</p>
</microformat>

<excerpt>
Learn how to test server Ktor applications using a special testing engine.
</excerpt>

Ktor provides a special testing engine that doesn't create a web server, doesn't bind to sockets, and doesn't make any real HTTP requests. Instead, it hooks directly into internal mechanisms and processes an application call directly. This results in quicker tests execution compared to running a complete web server for testing. 


## Add dependencies {id="add-dependencies"}
To test a server Ktor application, you need to include the following artifacts in the build script:
* Add the `ktor-server-test-host` dependency:
   <var name="artifact_name" value="ktor-server-test-host"/>
   <include src="lib.xml" include-id="add_ktor_artifact_testing"/>

* Add the `kotlin-test` dependency providing a set of utility functions for performing assertions in tests:
  <var name="group_id" value="org.jetbrains.kotlin"/>
  <var name="artifact_name" value="kotlin-test"/>
  <var name="version" value="kotlin_version"/>
  <include src="lib.xml" include-id="add_artifact_testing"/>

  

## Testing overview {id="overview"}

To use a testing engine, follow the steps below:
1. Create a JUnit test class and a test function.
2. Use `testApplication` function to set up a configured instance of a test application running locally.
3. Use the [Ktor HTTP client](create-client.md) instance inside a test application to make a request to your server and receive a response.
4. Verify the results by making assertions provided by the [kotlin.test](https://kotlinlang.org/api/latest/kotlin.test/) library.

The code below demonstrates how to test the most simple Ktor application that accepts GET requests made to the `/` path and responds with a plain text response.

<tabs>
<tab title="Test">

```kotlin
```
{src="snippets/engine-main/src/test/kotlin/EngineMainTest.kt" lines="3-16,27"}

</tab>

<tab title="Application">

```kotlin
```
{src="snippets/engine-main/src/main/kotlin/com/example/Application.kt" lines="3-15"}

</tab>
</tabs>

The runnable code example is available here: [engine-main](https://github.com/ktorio/ktor-documentation/tree/%current-branch%/codeSnippets/snippets/engine-main).

## Configure a test application {id="configure-test-app"}
### Step 1: Load application modules {id="load-modules"}
To test an application, its [modules](Modules.md) should be loaded to `testApplication`.
Loading modules to `testApplication` depends on the way used to [create a server](create_server.xml): by using the `application.conf` configuration file or in code using the `embeddedServer` function.

#### Automatic loading {id="auto-load"}

If you have the `application.conf` file in the `resources` folder, `testApplication` loads all modules specified in the configuration file automatically.

> You can disable loading modules by using a [custom configuration file](#configuration-properties) for tests.

#### Manual loading {id="manual-load"}
If you use `embeddedServer`, you can add modules to a test application manually using the `application` function:
```kotlin
```
{src="snippets/embedded-server-modules/src/test/kotlin/EmbeddedServerTest.kt" lines="11-15,19"}

### Step 2: (Optional) Add routing {id="add-routing"}

[](#load-modules)

```kotlin
```
{src="snippets/auth-oauth-google/src/test/kotlin/ApplicationTest.kt" lines="18,31-35,51"}

### Step 3: (Optional) Customize environment {id="environment"}

To build a custom environment for a test application, use the `environment` function.
For example, to use a custom configuration for tests, you can create a custom configuration file in a test's `test/resources` folder and load it using the `config` property:

```kotlin
```
{src="snippets/auth-oauth-google/src/test/kotlin/ApplicationTest.kt" lines="17-21,51"}

#### Define configuration properties {id="configuration-properties"}

If you have the [application.conf](Configurations.xml#hocon-file) file in the `resources` folder, `testApplication` loads all modules and properties specified in the configuration file automatically. In a case you need to specify a separate configuration for tests, you can create a custom configuration file in a test's `test/resources` folder and load it using the `config` property:

```kotlin
```
{src="snippets/auth-oauth-google/src/test/kotlin/ApplicationTest.kt" lines="17-21,51"}

Another way to specify configuration properties is using [MapApplicationConfig](https://api.ktor.io/ktor-server/ktor-server-core/io.ktor.server.config/-map-application-config/index.html). This might be useful if you want to access application configuration before the application starts. The example below shows how to pass `MapApplicationConfig` to the `testApplication` function using the `config` property:

```kotlin
@Test
fun testRequest() = testApplication {
    environment {
        config = MapApplicationConfig("ktor.environment" to "test")
    }
    // Request and assertions
}
```

### Step 4: (Optional) Mock external services {id="external-services"}

```kotlin
```
{src="snippets/auth-oauth-google/src/test/kotlin/ApplicationTest.kt" lines="18,36-47,51"}

You can find the full example with a test here: [auth-oauth-google](https://github.com/ktorio/ktor-documentation/tree/%current-branch%/codeSnippets/snippets/auth-oauth-google).

### Step 5: (Optional) Configure a client {id="configure-client"}

The `testApplication` provides access to an HTTP client with default configuration using the `client` property. 
If you need to customize the client and install additional plugins, you can use the `createClient` function. For example, to [send JSON data](#json-data) in a test POST/PUT request, you can install the [ContentNegotiation](serialization-client.md) plugin:
```kotlin
```
{src="snippets/json-kotlinx/src/test/kotlin/ApplicationTest.kt" lines="32-37,44"}



### Step 6: Make a request and assert results {id="assert"}

Make a [request](request.md), receive a [response](response.md), and make assertions provided by the [kotlin.test](https://kotlinlang.org/api/latest/kotlin.test/) library:


```kotlin
```
{src="snippets/engine-main/src/test/kotlin/EngineMainTest.kt" lines="11-16"}



## Test POST/PUT requests {id="test-post-put"}

### Send form data {id="form-data"}

To send form data in a test POST/PUT request, you need to set the `Content-Type` header and specify the request body. To do this, you can use 
 the [header](request.md#headers) and [setBody](request.md#body) functions, respectively. The examples below show how to send form data using both `x-www-form-urlencoded` and `multipart/form-data` types.

#### x-www-form-urlencoded {id="x-www-form-urlencoded"}

A test below from the [post-form-parameters](https://github.com/ktorio/ktor-documentation/tree/%current-branch%/codeSnippets/snippets/post-form-parameters) example shows how to make a test request with form parameters sent using the `x-www-form-urlencoded` content type. Note that the [formUrlEncode](https://api.ktor.io/ktor-http/io.ktor.http/form-url-encode.html) function is used to encode form parameters from a list of key/value pairs.

<tabs>
<tab title="Test">

```kotlin
```
{src="snippets/post-form-parameters/src/test/kotlin/ApplicationTest.kt" lines="3-18,29"}

</tab>

<tab title="Application">

```kotlin
```
{src="snippets/post-form-parameters/src/main/kotlin/com/example/Application.kt" lines="3-16,45-46"}

</tab>
</tabs>


#### multipart/form-data {id="multipart-form-data"}

The code below demonstrates how to build `multipart/form-data` and test file uploading. You can find the full example here: [upload-file](https://github.com/ktorio/ktor-documentation/tree/%current-branch%/codeSnippets/snippets/upload-file).

<tabs>
<tab title="Test">

```kotlin
```
{src="snippets/upload-file/src/test/kotlin/UploadFileTest.kt" lines="3-34,62"}

</tab>

<tab title="Application">

```kotlin
```
{src="snippets/upload-file/src/main/kotlin/com/example/UploadFile.kt" lines="3-34"}

</tab>
</tabs>


### Send JSON data {id="json-data"}

To send JSON data in a test POST/PUT request, you need create a new client and install the [ContentNegotiation](serialization-client.md) plugin that allows serializing/deserializing the content in a specific format. Inside a request, you can specify the `Content-Type` header using the `contentType` function and the request body using [setBody](request.md#body). The [example below](https://github.com/ktorio/ktor-documentation/tree/%current-branch%/codeSnippets/snippets/json-kotlinx) shows how to test the `/customer` endpoint that handles `POST` requests.

<tabs>
<tab title="Test">

```kotlin
```
{src="snippets/json-kotlinx/src/test/kotlin/ApplicationTest.kt" lines="3-14,31-44,56"}

</tab>

<tab title="Application">

```kotlin
```
{src="snippets/json-kotlinx/src/main/kotlin/com/example/Application.kt" lines="3-16,25-31,38-44"}

</tab>
</tabs>



## Preserve cookies during testing {id="preserving-cookies"}

If you need to preserve cookies between requests when testing, you need create a new client and install the [HttpCookies](http-cookies.md) plugin. In a test below from the [session-cookie-client](https://github.com/ktorio/ktor-documentation/tree/%current-branch%/codeSnippets/snippets/session-cookie-client) example, reload count is increased after each request since cookies are preserved.


<tabs>
<tab title="Test">

```kotlin
```
{src="snippets/session-cookie-client/src/test/kotlin/ApplicationTest.kt" lines="3-27,47"}

</tab>

<tab title="Application">

```kotlin
```
{src="snippets/session-cookie-client/src/main/kotlin/com/example/Application.kt" lines="3-38"}

</tab>
</tabs>


## Test HTTPS {id="https"}

If you need to test an [HTTPS endpoint](ssl.md), change the protocol used to make a request using the [URLBuilder.protocol](request.md#url-components) property:

```kotlin
```
{src="snippets/ssl-engine-main/src/test/kotlin/ApplicationTest.kt" lines="10-18"}

You can find the full example here: [ssl-engine-main](https://github.com/ktorio/ktor-documentation/tree/%current-branch%/codeSnippets/snippets/ssl-engine-main).

### HttpsRedirect plugin {id="https-redirect"}

The `HttpsRedirect` plugin changes how testing is performed. Check the [](https-redirect.md#testing) section for more information.


## Test WebSockets {id="testing-ws"}

You can test [WebSocket conversations](websocket.md) by using the [WebSockets](websocket_client.md) plugin provided by the client:

```kotlin
```
{src="snippets/server-websockets/src/test/kotlin/com/example/ModuleTest.kt" lines="3-26,41"}






## End-to-end testing with HttpClient {id="end-to-end"}
Apart from a testing engine, you can use the [Ktor HTTP Client](create-client.md) for end-to-end testing of your server application.

```kotlin
```
{src="snippets/embedded-server/src/test/kotlin/EmbeddedServerTest.kt"}

For a full example, refer to a test of the [embedded-server](https://github.com/ktorio/ktor-documentation/tree/%current-branch%/codeSnippets/snippets/embedded-server) example.