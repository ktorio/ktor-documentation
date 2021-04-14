[//]: # (title: Send a request)

After adding the necessary [dependencies](client.md#add-dependencies) and [creating the client](client.md#create-client), you can send HTTP requests. You can configure the following request parameters:
* Specify an HTTP method, such as `GET`, `POST`, `PUT`, and so on.
* Add headers and cookies.
* Set the body of a request, for example, a plain text, a data object, form parameters, and so on.

The main way for making HTTP requests is the [request](https://api.ktor.io/%ktor_version%/io.ktor.client.request/request.html) function that takes a URL as a parameter. Inside this function, you can configure various request parameters: specify an HTTP method, add headers, specify the request body, and so on. These parameters are exposed by the [HttpRequestBuilder](https://api.ktor.io/%ktor_version%/io.ktor.client.request/-http-request-builder/index.html) class.

```kotlin
```
{src="snippets/_misc_client/RequestMethodWithoutParams.kt" interpolate-variables="true" disable-links="false"}

Note that this function allows you to [receive a response](response.md) in various ways. In this example, we receive a response as an `HttpResponse` object.

> `request` is a suspending function, so requests should be executed only from a coroutine or another suspend function. You can learn more about calling suspending functions from [Coroutines basics](https://kotlinlang.org/docs/coroutines-basics.html).


## Set request parameters {id="parameters"}
In this section, we'll see on how to specify various request parameters, including an HTTP method, headers, and cookies. If you need to configure some default parameters for all requests of a specific client, use the [](default-request.md) feature.


### Specify an HTTP method {id="http-method"}

When calling the `request` function, you can specify the desired HTTP method using the `method` property:

```kotlin
```
{src="snippets/_misc_client/RequestMethodWithParams.kt"}

In addition to the `request` function, `HttpClient` provides specific functions for basic HTTP methods: [get](https://api.ktor.io/%ktor_version%/io.ktor.client.request/get.html), [post](https://api.ktor.io/%ktor_version%/io.ktor.client.request/post.html), [put](https://api.ktor.io/%ktor_version%/io.ktor.client.request/put.html), and so on. For example, you can replace the example above with the following code:
```kotlin
```
{src="snippets/_misc_client/GetMethodWithoutParams.kt"}

### Add headers {id="headers"}
To add headers to the request, use the [headers](https://api.ktor.io/%ktor_version%/io.ktor.client.request/-http-request-builder/headers.html) function as follows:
```kotlin
```
{src="snippets/_misc_client/GetMethodWithHeaders.kt"}



### Add cookies {id="cookies"}
To send cookies, use the [cookie](https://api.ktor.io/%ktor_version%/io.ktor.client.request/cookie.html) function:

```kotlin
```
{src="snippets/_misc_client/GetMethodWithCookies.kt"}

Note that Ktor provides the [](http-cookies.md) feature that allows you to keep cookies between calls.




## Set request body {id="body"}
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
The Ktor client provides the [submitForm](https://api.ktor.io/%ktor_version%/io.ktor.client.request.forms/submit-form.html) function for sending form parameters using both `x-www-form-urlencoded` and `multipart/form-data` types. In a code snippet below, this function accepts the following parameters:
* `url` specifies a URL for sending a request.
* `formParameters` a set of form parameters built using [Parameters.build](https://api.ktor.io/%ktor_version%/io.ktor.http/-parameters/build.html).
* `encodeInQuery` is used to send form data in URL parameters by using the `GET` request.

```kotlin
```
{src="snippets/_misc_client/SubmitForm.kt"}


### Upload a file {id="upload_file"}

If you need to send a file with a form, use the [submitFormWithBinaryData](https://api.ktor.io/%ktor_version%/io.ktor.client.request.forms/submit-form-with-binary-data.html) function. When calling this function, you need to specify the `formData` parameter, which can be initialized using the [formData](https://api.ktor.io/%ktor_version%/io.ktor.client.request.forms/form-data.html) function. A code example below shows how to do this:

```kotlin
```
{src="snippets/client-upload-file/src/main/kotlin/com/example/Application.kt" include-symbol="main"}

Learn how to run this sample from [client-upload-file](https://github.com/ktorio/ktor-documentation/tree/main/codeSnippets/snippets/client-upload-file).




## Parallel requests {id="parallel_requests"}

When sending two requests at once, the client suspends the second request execution until the first is finished. If you need to perform several requests at once, you can use [launch](https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines/launch.html) or [async](https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines/async.html) functions. The code snippet below shows how to perform two requests asynchronously:
```kotlin
```
{src="snippets/client-parallel-requests/src/main/kotlin/com/example/Application.kt" lines="12,19-22"}

To see a full example, go to [client-parallel-requests](https://github.com/ktorio/ktor-documentation/tree/main/codeSnippets/snippets/client-parallel-requests).


## Cancel a request {id="cancel-request"}

If you need to cancel a request, you can cancel a coroutine that runs this request. For instance, the [launch](https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines/launch.html) function returns a `Job` that can be used to cancel the running coroutine:
```kotlin
```
{src="snippets/_misc_client/CancelRequest.kt"}

Learn more from the [Cancellation and timeouts](https://kotlinlang.org/docs/cancellation-and-timeouts.html) help topic.