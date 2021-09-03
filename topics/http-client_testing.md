[//]: # (title: Testing)

<microformat>
<var name="example_name" value="client-testing-mock"/>
<include src="lib.xml" include-id="download_example"/>
</microformat>

Ktor provides a [MockEngine](https://api.ktor.io/ktor-client/ktor-client-mock/ktor-client-mock/io.ktor.client.engine.mock/-mock-engine/index.html) that simulates HTTP calls without connecting to the endpoint.

## Add dependencies {id="add_dependencies"}
Before using `MockEngine`, you need to include the `ktor-client-mock` artifact in the build script.

<var name="artifact_name" value="ktor-client-mock"/>
<include src="lib.xml" include-id="add_ktor_artifact_testing"/>


## Usage {id="usage"}

### Share client configuration {id="share-config"}

Let's see how to use `MockEngine` to test a client. Suppose the client has the following configuration:
* The `CIO` [engine](http-client_engines.md) is used to make requests.
* The [Json](json.md) plugin is installed to deserialize incoming JSON data.

To test this client, its configuration needs to be shared with a test client, which uses `MockEngine`. To share a configuration, you can create a client wrapper class that takes an engine as a constructor parameter and contains a client configuration.

```kotlin
```
{src="snippets/client-testing-mock/src/main/kotlin/com/example/Application.kt" lines="12-14,23-31"}

Then, you can use the `ApiClient` as follows to create an HTTP client with the `CIO` engine and make a request.

```kotlin
```
{src="snippets/client-testing-mock/src/main/kotlin/com/example/Application.kt" lines="15-21"}

### Test a client {id="test-client"}

To test a client, you need to create a `MockEngine` instance with a handler that can check request parameters and respond with the required content (a JSON object in our case).

```kotlin
```
{src="snippets/client-testing-mock/src/test/kotlin/ApplicationTest.kt" lines="14-20"}

Then, you can pass the created `MockEngine` to initialize `ApiClient` and make required assertions.

```kotlin
```
{src="snippets/client-testing-mock/src/test/kotlin/ApplicationTest.kt" lines="10-26"}

You can find the full example here: [client-testing-mock](https://github.com/ktorio/ktor-documentation/tree/main/codeSnippets/snippets/client-testing-mock).
