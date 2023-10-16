[//]: # (title: Testing)

<show-structure for="chapter" depth="3"/>

<tldr>
<p>
<b>Required dependencies</b>: <code>io.ktor:ktor-server-test-host</code>, <code>org.jetbrains.kotlin:kotlin-test</code>
</p>
</tldr>

<link-summary>
Learn how to test server Ktor applications using a special testing engine.
</link-summary>

Ktor provides a special testing engine that doesn't create a web server, doesn't bind to sockets, and doesn't make any real HTTP requests. Instead, it hooks directly into internal mechanisms and processes an application call directly. This results in quicker tests execution compared to running a complete web server for testing. 


## Add dependencies {id="add-dependencies"}
To test a server Ktor application, you need to include the following artifacts in the build script:
* Add the `ktor-server-test-host` dependency:

   <var name="artifact_name" value="ktor-server-test-host"/>
   <include from="lib.topic" element-id="add_ktor_artifact_testing"/>

* Add the `kotlin-test` dependency providing a set of utility functions for performing assertions in tests:

  <var name="group_id" value="org.jetbrains.kotlin"/>
  <var name="artifact_name" value="kotlin-test"/>
  <var name="version" value="kotlin_version"/>
  <var name="version_kts" value="kotlinVersion"/>
  <include from="lib.topic" element-id="add_artifact_testing"/>

> To test the [Native server](native_server.md#add-dependencies), add the testing artifacts to the `nativeTest` source set.

  

## Testing overview {id="overview"}

To use a testing engine, follow the steps below:
1. Create a JUnit test class and a test function.
2. Use the [testApplication](https://api.ktor.io/ktor-server/ktor-server-test-host/io.ktor.server.testing/test-application.html) function to set up a configured instance of a test application running locally.
3. Use the [Ktor HTTP client](create-client.md) instance inside a test application to make a request to your server, receive a response, and make assertions.

The code below demonstrates how to test the most simple Ktor application that accepts GET requests made to the `/` path and responds with a plain text response.

<tabs>
<tab title="Test">

```kotlin
```
{src="snippets/engine-main/src/test/kotlin/EngineMainTest.kt" include-lines="3-16,27"}

</tab>

<tab title="Application">

```kotlin
```
{src="snippets/engine-main/src/main/kotlin/com/example/Application.kt" include-lines="3-15"}

</tab>
</tabs>

The runnable code example is available here: [engine-main](https://github.com/ktorio/ktor-documentation/tree/%ktor_version%/codeSnippets/snippets/engine-main).


## Test an application {id="test-app"}

### Step 1: Configure a test application {id="configure-test-app"}

A configuration of test applications might include the following steps:
- [Adding application modules](#add-modules)
- [(Optional) Adding routes](#add-routing)
- [(Optional) Customizing environment](#environment)
- [(Optional) Mocking external services](#external-services)

> By default, the configured test application starts on the [first client call](#make-request).
> Optionally, you can call the `startApplication` function to start the application manually.
> This might be useful if you need to test your application's [lifecycle events](events.md#predefined-events).

#### Add application modules {id="add-modules"}
To test an application, its [modules](Modules.md) should be loaded to `testApplication`.
Loading modules to `testApplication` depends on the way used to [create a server](create_server.topic): by using a [configuration file](Configuration-file.topic) or in code using the `embeddedServer` function.

##### Add modules automatically {id="auto"}

If you have the `application.conf` or `application.yaml` configuration file in the `resources` folder, `testApplication` loads all modules and properties specified in the configuration file automatically. To disable the automatic loading of specific modules, you need to:

1. Provide a [custom configuration file](#environment) for tests.
2. Use the [ktor.application.modules](Configuration-file.topic#configuration-file-overview) configuration property to specify modules to load.


##### Add modules manually {id="manual"}
If you use `embeddedServer`, you can add modules to a test application manually using the `application` function:
```kotlin
```
{src="snippets/embedded-server-modules/src/test/kotlin/EmbeddedServerTest.kt" include-lines="11-15,19"}



> You can also access the `Application` instance inside the `application` block.

#### Add routes {id="add-routing"}

You can add routes to your test application using the `routing` function.
This might be convenient for the following use-cases:
- Instead of [adding modules](#manual) to a test application, you can add [specific routes](Routing_in_Ktor.md#route_extension_function) that should be tested. 
- You can add routes required only in a test application. The example below shows how to add the `/login-test` endpoint used to initialize a user [session](sessions.md) in tests:
   ```kotlin
   ```
   {src="snippets/auth-oauth-google/src/test/kotlin/ApplicationTest.kt" include-lines="18,31-35,51"}
   
   You can find the full example with a test here: [auth-oauth-google](https://github.com/ktorio/ktor-documentation/tree/%ktor_version%/codeSnippets/snippets/auth-oauth-google).

#### Customize environment {id="environment"}

To build a custom environment for a test application, use the `environment` function.
For example, to use a custom configuration for tests, you can create a custom configuration file in the `test/resources` folder and load it using the `config` property:

```kotlin
```
{src="snippets/auth-oauth-google/src/test/kotlin/ApplicationTest.kt" include-lines="17-21,51"}

Another way to specify configuration properties is using [MapApplicationConfig](https://api.ktor.io/ktor-server/ktor-server-core/io.ktor.server.config/-map-application-config/index.html). This might be useful if you want to access application configuration before the application starts. The example below shows how to pass `MapApplicationConfig` to the `testApplication` function using the `config` property:

```kotlin
```
{src="snippets/engine-main-custom-environment/src/test/kotlin/ApplicationTest.kt" include-lines="10-14,21"}

#### Mock external services {id="external-services"}

Ktor allows you to mock external services using the `externalServices` function.
Inside this function, you need to call the `hosts` function that accepts two parameters:
- The `hosts` parameter accepts URLs of external services.
- The `block` parameter allows you to configure the `Application` that acts as a mock for an external service.
   You can configure routing and install plugins for this `Application`.

The sample below shows how to use `externalServices` to simulate a JSON response returned by Google API:

```kotlin
```
{src="snippets/auth-oauth-google/src/test/kotlin/ApplicationTest.kt" include-lines="18,36-47,51"}

You can find the full example with a test here: [auth-oauth-google](https://github.com/ktorio/ktor-documentation/tree/%ktor_version%/codeSnippets/snippets/auth-oauth-google).

### Step 2: (Optional) Configure a client {id="configure-client"}

The `testApplication` provides access to an HTTP client with default configuration using the `client` property. 
If you need to customize the client and install additional plugins, you can use the `createClient` function. For example, to [send JSON data](#json-data) in a test POST/PUT request, you can install the [ContentNegotiation](serialization-client.md) plugin:
```kotlin
```
{src="snippets/json-kotlinx/src/test/kotlin/jsonkotlinx/ApplicationTest.kt" include-lines="32-37,44"}


### Step 3: Make a request {id="make-request"}

To test your application, use the [configured client](#configure-client) to make a [request](request.md) and receive a [response](response.md). The [example below](https://github.com/ktorio/ktor-documentation/tree/%ktor_version%/codeSnippets/snippets/json-kotlinx) shows how to test the `/customer` endpoint that handles `POST` requests:

```kotlin
```
{src="snippets/json-kotlinx/src/test/kotlin/jsonkotlinx/ApplicationTest.kt" include-lines="32-41,44"}



### Step 4: Assert results {id="assert"}

After receiving a [response](#make-request), you can verify the results by making assertions provided by the [kotlin.test](https://kotlinlang.org/api/latest/kotlin.test/) library:

```kotlin
```
{src="snippets/json-kotlinx/src/test/kotlin/jsonkotlinx/ApplicationTest.kt" include-lines="32-44"}





## Test POST/PUT requests {id="test-post-put"}

### Send form data {id="form-data"}

To send form data in a test POST/PUT request, you need to set the `Content-Type` header and specify the request body. To do this, you can use 
 the [header](request.md#headers) and [setBody](request.md#body) functions, respectively. The examples below show how to send form data using both `x-www-form-urlencoded` and `multipart/form-data` types.

#### x-www-form-urlencoded {id="x-www-form-urlencoded"}

A test below from the [post-form-parameters](https://github.com/ktorio/ktor-documentation/tree/%ktor_version%/codeSnippets/snippets/post-form-parameters) example shows how to make a test request with form parameters sent using the `x-www-form-urlencoded` content type. Note that the [formUrlEncode](https://api.ktor.io/ktor-http/io.ktor.http/form-url-encode.html) function is used to encode form parameters from a list of key/value pairs.

<tabs>
<tab title="Test">

```kotlin
```
{src="snippets/post-form-parameters/src/test/kotlin/formparameters/ApplicationTest.kt" include-lines="3-18,29"}

</tab>

<tab title="Application">

```kotlin
```
{src="snippets/post-form-parameters/src/main/kotlin/formparameters/Application.kt" include-lines="3-16,45-46"}

</tab>
</tabs>


#### multipart/form-data {id="multipart-form-data"}

The code below demonstrates how to build `multipart/form-data` and test file uploading. You can find the full example here: [upload-file](https://github.com/ktorio/ktor-documentation/tree/%ktor_version%/codeSnippets/snippets/upload-file).

<tabs>
<tab title="Test">

```kotlin
```
{src="snippets/upload-file/src/test/kotlin/uploadfile/UploadFileTest.kt" include-lines="3-36,69"}

</tab>

<tab title="Application">

```kotlin
```
{src="snippets/upload-file/src/main/kotlin/uploadfile/UploadFile.kt" include-lines="3-38"}

</tab>
</tabs>


### Send JSON data {id="json-data"}

To send JSON data in a test POST/PUT request, you need to create a new client and install the [ContentNegotiation](serialization-client.md) plugin that allows serializing/deserializing the content in a specific format. Inside a request, you can specify the `Content-Type` header using the `contentType` function and the request body using [setBody](request.md#body). The [example below](https://github.com/ktorio/ktor-documentation/tree/%ktor_version%/codeSnippets/snippets/json-kotlinx) shows how to test the `/customer` endpoint that handles `POST` requests.

<tabs>
<tab title="Test">

```kotlin
```
{src="snippets/json-kotlinx/src/test/kotlin/jsonkotlinx/ApplicationTest.kt" include-lines="3-14,31-44,56"}

</tab>

<tab title="Application">

```kotlin
```
{src="snippets/json-kotlinx/src/main/kotlin/jsonkotlinx/Application.kt" include-lines="3-16,25-31,38-44"}

</tab>
</tabs>



## Preserve cookies during testing {id="preserving-cookies"}

If you need to preserve cookies between requests when testing, you need to create a new client and install the [HttpCookies](http-cookies.md) plugin. In a test below from the [session-cookie-client](https://github.com/ktorio/ktor-documentation/tree/%ktor_version%/codeSnippets/snippets/session-cookie-client) example, reload count is increased after each request since cookies are preserved.


<tabs>
<tab title="Test">

```kotlin
```
{src="snippets/session-cookie-client/src/test/kotlin/cookieclient/ApplicationTest.kt" include-lines="3-27,47"}

</tab>

<tab title="Application">

```kotlin
```
{src="snippets/session-cookie-client/src/main/kotlin/cookieclient/Application.kt" include-lines="3-38"}

</tab>
</tabs>


## Test HTTPS {id="https"}

If you need to test an [HTTPS endpoint](ssl.md), change the protocol used to make a request using the [URLBuilder.protocol](request.md#url) property:

```kotlin
```
{src="snippets/ssl-engine-main/src/test/kotlin/ApplicationTest.kt" include-lines="10-18"}

You can find the full example here: [ssl-engine-main](https://github.com/ktorio/ktor-documentation/tree/%ktor_version%/codeSnippets/snippets/ssl-engine-main).


## Test WebSockets {id="testing-ws"}

You can test [WebSocket conversations](websocket.md) by using the [WebSockets](websocket_client.md) plugin provided by the client:

```kotlin
```
{src="snippets/server-websockets/src/test/kotlin/com/example/ModuleTest.kt" include-lines="3-26,41"}






## End-to-end testing with HttpClient {id="end-to-end"}
Apart from a testing engine, you can use the [Ktor HTTP client](create-client.md) for end-to-end testing of your server application.
In the example below, the HTTP client makes a test request to the `TestServer`:

```kotlin
```
{src="snippets/embedded-server/src/test/kotlin/EmbeddedServerTest.kt"}

For complete examples, refer to these samples:
- [embedded-server](https://github.com/ktorio/ktor-documentation/tree/%ktor_version%/codeSnippets/snippets/embedded-server): a sample server to be tested.
- [e2e](https://github.com/ktorio/ktor-documentation/tree/%ktor_version%/codeSnippets/snippets/e2e): contains helper classes and functions for setting up a test server.