[//]: # (title: Send a request)

After adding the necessary [dependencies](client.md#add-dependencies) and [creating the client](client.md#create-client), you can send HTTP requests. You can configure the following request parameters:
* Specify an HTTP method, such as `GET`, `POST`, `PUT`, and so on.
* Add headers and cookies.
* Set the body of a request, for example, plain text, data objects, forms, and so on.

The main way for making HTTP requests is the [request](https://api.ktor.io/%ktor_version%/io.ktor.client.request/request.html) function that takes a URL as a parameter. Inside this function, you can configure various request parameters: specify an HTTP method, add headers, specify the request body, and so on. These parameters are exposed by the [HttpRequestBuilder](https://api.ktor.io/%ktor_version%/io.ktor.client.request/-http-request-builder/index.html) class.

```kotlin
```
{src="snippets/_misc_client/RequestMethodWithoutParams.kt" interpolate-variables="true" disable-links="false"}

Note that this function allows you to [receive a response](response.md) in various ways. In this example, we receive a response as an `HttpResponse` object.

> `request` is a suspending function, so requests should be executed only from a coroutine or another suspend function. You can learn more about calling suspending functions from [Coroutines basics](https://kotlinlang.org/docs/coroutines-basics.html).


## Specify an HTTP method {id="http-method"}

When calling the `request` function, you can specify the desired HTTP method using the `method` property:

```kotlin
```
{src="snippets/_misc_client/RequestMethodWithParams.kt"}

In addition to the `request` function, `HttpClient` provides specific functions for basic HTTP methods: `get`, `post`, `put`, and so on. For example, you can replace the example above with the following code:
```kotlin
```
{src="snippets/_misc_client/GetMethodWithoutParams.kt"}

## Add headers {id="headers"}
To add headers to the request, use the [headers](https://api.ktor.io/%ktor_version%/io.ktor.client.request/-http-request-builder/headers.html) function as follows:
```kotlin
```
{src="snippets/_misc_client/GetMethodWithHeaders.kt"}



## Add cookies {id="cookies"}
To send cookies, use the [cookie](https://api.ktor.io/%ktor_version%/io.ktor.client.request/cookie.html) function:

```kotlin
```
{src="snippets/_misc_client/GetMethodWithCookies.kt"}

Note that Ktor provides the [](http-cookies.md) feature that allows you to keep cookies between calls.




## Specify body {id="body"}
To set the body of a request, you need to specify the [body](https://api.ktor.io/%ktor_version%/io.ktor.client.request/-http-request-builder/body.html) property exposed by `HttpRequestBuilder`. This property accepts different types of payload, including plain text, arbitrary class instances, form data, byte arrays, and so on. Note that you also need to specify a content type for a request using the [contentType](https://api.ktor.io/%ktor_version%/io.ktor.http/content-type.html) function. Below we'll take a look at several examples.

### Text {id="text"}
Sending plain text as body can be implemented as follows:
```kotlin
```
{src="snippets/_misc_client/PostMethodWithBody.kt"}


### Objects {id="objects"}
With the enabled [Json](json-feature.md) feature, you can send a class instance within a request body as JSON. To do this, assign a class instance to the `body` property and set the content type to `application/json`:

```kotlin
```
{src="snippets/_misc_client/PostMethodWithObject.kt"}

You can learn more from the [](json-feature.md) help section.

### Form parameters {id="form_parameters"}
The Ktor client provides two functions for sending form data:
* [submitForm](https://api.ktor.io/%ktor_version%/io.ktor.client.request.forms/submit-form.html) for sending form parameters using both `GET` and `POST` requests.
* [submitFormWithBinaryData](https://api.ktor.io/%ktor_version%/io.ktor.client.request.forms/submit-form-with-binary-data.html) for sending files using `POST` requests.

The example below shows `submitForm`:
```kotlin
```
{src="snippets/_misc_client/SubmitForm.kt"}


### Example: upload a file {id="upload_file"}
File example:

```kotlin
```
{src="snippets/client-upload-file/src/main/kotlin/com/example/Application.kt" include-symbol="main"}

Learn how to run this sample from [client-upload-file](https://github.com/ktorio/ktor-documentation/tree/main/codeSnippets/snippets/client-upload-file).




## Concurrency {id="concurrency"}

Remember that requests are asynchronous, but when performing requests, the API suspends further requests
and your function will be suspended until done. If you want to perform several requests at once
in the same block, you can use `launch` or `async` functions and later get the results.
For example:

### Sequential requests

```kotlin
suspend fun sequentialRequests() {
    val client = HttpClient()

    // Get the content of an URL.
    val firstBytes = client.get<ByteArray>("https://127.0.0.1:8080/a")

    // Once the previous request is done, get the content of an URL.
    val secondBytes = client.get<ByteArray>("https://127.0.0.1:8080/b")

    client.close()
}
```

### Parallel requests

```kotlin
suspend fun parallelRequests() = coroutineScope<Unit> {
    val client = HttpClient()

    // Start two requests asynchronously.
    val firstRequest = async { client.get<ByteArray>("https://127.0.0.1:8080/a") }
    val secondRequest = async { client.get<ByteArray>("https://127.0.0.1:8080/b") }

    // Get the request contents without blocking threads, but suspending the function until both
    // requests are done.
    val bytes1 = firstRequest.await() // Suspension point.
    val bytes2 = secondRequest.await() // Suspension point.

    client.close()
}
```

## Cancel a request {id="cancel-request"}