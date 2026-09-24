[//]: # (title: Testing in Ktor Client)

<show-structure for="chapter" depth="3"/>

<var name="artifact_name" value="ktor-client-mock"/>

<tldr>
<p>
<b>Required dependencies</b>: <code>io.ktor:%artifact_name%</code>
</p>
<var name="example_name" value="client-testing-mock"/>
<include from="lib.topic" element-id="download_example"/>
</tldr>

<web-summary>
Ktor provides a MockEngine that simulates HTTP calls without connecting to the endpoint.
</web-summary>

<link-summary>
Learn how to test your client with MockEngine by simulating HTTP calls.
</link-summary>

Ktor provides a [MockEngine](https://api.ktor.io/ktor-client-mock/io.ktor.client.engine.mock/-mock-engine/index.html) that simulates HTTP calls without connecting to the endpoint.

## Add dependencies {id="add_dependencies"}
Before using `MockEngine`, you need to include the `%artifact_name%` artifact in the build script.

<include from="lib.topic" element-id="add_ktor_artifact_testing"/>


## Usage {id="usage"}

### Share client configuration {id="share-config"}

Let's see how to use `MockEngine` to test a client. Suppose the client has the following configuration:
* The `CIO` [engine](client-engines.md) is used to make requests.
* The [Json](client-serialization.md) plugin is installed to deserialize incoming JSON data.

To test this client, its configuration needs to be shared with a test client, which uses `MockEngine`. To share a configuration, you can create a client wrapper class that takes an engine as a constructor parameter and contains a client configuration.

```kotlin
```
{src="snippets/client-testing-mock/src/main/kotlin/com/example/Application.kt" include-lines="13-14,24-36"}

Then, you can use the `ApiClient` as follows to create an HTTP client with the `CIO` engine and make a request.

```kotlin
```
{src="snippets/client-testing-mock/src/main/kotlin/com/example/Application.kt" include-lines="16-21"}

### Test a client {id="test-client"}

To test a client, you need to create a `MockEngine` instance with a handler that can check request parameters and respond with the required content (a JSON object in our case).

```kotlin
```
{src="snippets/client-testing-mock/src/test/kotlin/ApplicationTest.kt" include-lines="19-25"}

Then, you can pass the created `MockEngine` to initialize `ApiClient` and make required assertions.

```kotlin
```
{src="snippets/client-testing-mock/src/test/kotlin/ApplicationTest.kt" include-lines="15-30"}

### Mock multiple endpoints {id="multiple-endpoints"}

When a client calls several URLs, use a single reusable handler and branch on the request
(for example, `request.url.encodedPath` or the host). This keeps mocks independent of call order:

```kotlin
```
{src="snippets/client-testing-mock/src/test/kotlin/ApplicationTest.kt" include-lines="35-41"}

In the `else` branch, fail on unexpected URLs so tests do not silently accept missing mocks.
You can also match on `request.url.host` when the same path is used on different hosts.

### Mock a call chain {id="call-chain"}

To return different responses for consecutive calls in a fixed order, register multiple handlers with
[`addHandler`](https://api.ktor.io/ktor-client-mock/io.ktor.client.engine.mock/-mock-engine-config/add-handler.html)
and set `reuseHandlers = false` so each handler is used once:

```kotlin
```
{src="snippets/client-testing-mock/src/test/kotlin/ApplicationTest.kt" include-lines="52-56"}

By default, `reuseHandlers` is `true` and handlers are reused in a cycle. With `reuseHandlers = false`,
a further request after the last handler fails with an “Unhandled …” error.

For tests that build the client first and enqueue responses later, use
[`MockEngine.Queue`](https://api.ktor.io/ktor-client-mock/io.ktor.client.engine.mock/-mock-engine/-queue/index.html):

```kotlin
```
{src="snippets/client-testing-mock/src/test/kotlin/ApplicationTest.kt" include-lines="70-74"}

You can find the full example here: [client-testing-mock](https://github.com/ktorio/ktor-documentation/tree/main/codeSnippets/snippets/client-testing-mock).
