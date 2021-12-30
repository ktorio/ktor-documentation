[//]: # (title: Testing)

<microformat>
<p>
Required dependencies: <code>io.ktor:ktor-server-test-host</code>, <code>org.jetbrains.kotlin:kotlin-test</code>
</p>
<p>
Code examples: 
<a href="https://github.com/ktorio/ktor-documentation/tree/main/codeSnippets/snippets/engine-main">engine-main</a>, 
<a href="https://github.com/ktorio/ktor-documentation/tree/main/codeSnippets/snippets/post-form-parameters">post-form-parameters</a>,
<a href="https://github.com/ktorio/ktor-documentation/tree/main/codeSnippets/snippets/upload-file">upload-file</a>,
<a href="https://github.com/ktorio/ktor-documentation/tree/main/codeSnippets/snippets/session-cookie">session-cookie</a>,
<a href="https://github.com/ktorio/ktor-documentation/tree/main/codeSnippets/snippets/server-websockets">server-websockets</a>
</p>
</microformat>

<excerpt>
Learn how to test server Ktor applications using a special testing engine.
</excerpt>

Ktor provides a special testing engine that doesn't create a web server, doesn't bind to sockets, and doesn't make any real HTTP requests. Instead, it hooks directly into internal mechanisms and processes an application call directly. This results in quicker tests execution compared to running a complete web server for testing. In addition, you can set up [end-to-end tests](#end-to-end) for testing server endpoints using the [Ktor HTTP client](create-client.md).


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
3. Use the [Ktor HTTP client](create-client.md) instance inside a test application to make requests to your server and verify the results.

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

The runnable code example is available here: [engine-main](https://github.com/ktorio/ktor-documentation/tree/main/codeSnippets/snippets/engine-main).


## Send form data {id="form-data"}

To send form data in a test POST/PUT request, you need to set the `Content-Type` header and specify the request body. To do this, you can use 
 the [header](request.md#headers) and [setBody](request.md#body) functions, respectively. The examples below show how to send form data using both `x-www-form-urlencoded` and `multipart/form-data` types.

### x-www-form-urlencoded {id="x-www-form-urlencoded"}

A test below from the [post-form-parameters](https://github.com/ktorio/ktor-documentation/tree/main/codeSnippets/snippets/post-form-parameters) example shows how to make a test request with form parameters sent using the `x-www-form-urlencoded` content type. Note that the [formUrlEncode](https://api.ktor.io/ktor-http/io.ktor.http/form-url-encode.html) function is used to encode form parameters from a list of key/value pairs.

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


### multipart/form-data {id="multipart-form-data"}

The code below demonstrates how to build `multipart/form-data` and test file uploading. You can find the full example here: [upload-file](https://github.com/ktorio/ktor-documentation/tree/main/codeSnippets/snippets/upload-file).

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




## Preserve cookies during testing {id="preserving-cookies"}

If you need to preserve cookies between requests when testing, you need create a new client and install the [HttpCookies](http-cookies.md) plugin. In a test below from the [session-cookie](https://github.com/ktorio/ktor-documentation/tree/main/codeSnippets/snippets/session-cookie) example, reload count is increased after each request since cookies are preserved.


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


## Testing WebSockets {id="testing-ws"}

You can test [WebSocket conversations](websocket.md) by using the [WebSockets](websocket_client.md) plugin provided by the client:

```kotlin
```
{src="snippets/server-websockets/src/test/kotlin/com/example/ModuleTest.kt" lines="3-26,41"}


## Define configuration properties in tests {id="configuration-properties"}

If you have the [application.conf](Configurations.xml#hocon-file) file in the `resources` folder, `testApplication` loads all modules and properties specified in the configuration file. In a case you need to specify a separate configuration for tests, you can create the `application.conf` file in a test's `resources` folder (for example, `test/resources`).


### Define custom application.conf {id="hocon"}

If you want to use a custom configuration file, you can load it using the `config` property:

```kotlin
@Test
fun testRequest() = testApplication {
    environment {
        config = ApplicationConfig("custom-application.conf")
    }
    // Request and assertions
}
```

### Specify configuration in code {id="map"}

Another way to specify configuration properties is using [MapApplicationConfig](https://api.ktor.io/ktor-server/ktor-server-core/io.ktor.server.config/-map-application-config/index.html). This might be useful if you want to access application configuration before the application starts.

The example below shows how to pass `MapApplicationConfig` to the `testApplication` function using the `config` property:

```kotlin
@Test
fun testRequest() = testApplication {
    environment {
        config = MapApplicationConfig("ktor.environment" to "test")
    }
    // Request and assertions
}
```


## HttpsRedirect plugin {id="https-redirect"}

The `HttpsRedirect` plugin changes how testing is performed. Check the [](https-redirect.md#testing) section for more information.


## End-to-end testing with HttpClient {id="end-to-end"}
Apart from a testing engine, you can use the [Ktor HTTP Client](create-client.md) for end-to-end testing of your server application.

```kotlin
```
{src="snippets/embedded-server/src/test/kotlin/EmbeddedServerTest.kt"}

For a full example, refer to a test of the [embedded-server](https://github.com/ktorio/ktor-documentation/tree/main/codeSnippets/snippets/embedded-server) example.