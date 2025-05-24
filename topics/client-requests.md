[//]: # (title: Making requests)

<show-structure for="chapter" depth="2"/>

[percent_encoding]: https://en.wikipedia.org/wiki/Percent-encoding

<tldr>
<var name="example_name" value="client-configure-request"/>
<include from="lib.topic" element-id="download_example"/>
</tldr>

<link-summary>
Learn how to make requests and specify various request parameters: a request URL, an HTTP method, headers, and the body of a request.
</link-summary>

After [setting up the client](client-create-and-configure.md), you can make HTTP requests. The main way of making HTTP requests is the [request](https://api.ktor.io/ktor-client/ktor-client-core/io.ktor.client.request/request.html) function that can take a URL as a parameter. Inside this function, you can configure various request parameters: 
* Specify an HTTP method, such as `GET`, `POST`, `PUT`, `DELETE`, `HEAD`, `OPTIONS`, or `PATCH`.
* Specify a URL as a string or configure URL components (a domain, a path, query parameters, etc.) separately.
* Add headers and cookies.
* Set the body of a request, for example, a plain text, a data object, or form parameters.

These parameters are exposed by the [HttpRequestBuilder](https://api.ktor.io/ktor-client/ktor-client-core/io.ktor.client.request/-http-request-builder/index.html) class.

```kotlin
import io.ktor.client.request.*
import io.ktor.client.statement.*

val response: HttpResponse = client.request("https://ktor.io/") {
  // Configure request parameters exposed by [[[HttpRequestBuilder|https://api.ktor.io/ktor-client/ktor-client-core/io.ktor.client.request/-http-request-builder/index.html]]]
}
```
{interpolate-variables="true" disable-links="false"}

Note that this function allows you to receive a response as an `HttpResponse` object. `HttpResponse` exposes the API required to get a response body in various ways (a string, a JSON object, etc.) and obtain response parameters, such as a status code, content type, headers, and so on. You can learn more from the [](client-responses.md) topic.

> `request` is a suspending function, so requests should be executed only from a coroutine or another suspend function. You can learn more about calling suspending functions from [Coroutines basics](https://kotlinlang.org/docs/coroutines-basics.html).


### Specify an HTTP method {id="http-method"}

When calling the `request` function, you can specify the desired HTTP method using the `method` property:

```kotlin
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*

val response: HttpResponse = client.request("https://ktor.io/") {
    method = HttpMethod.Get
}
```

In addition to the `request` function, `HttpClient` provides specific functions for basic HTTP methods: [get](https://api.ktor.io/ktor-client/ktor-client-core/io.ktor.client.request/get.html), [post](https://api.ktor.io/ktor-client/ktor-client-core/io.ktor.client.request/post.html), [put](https://api.ktor.io/ktor-client/ktor-client-core/io.ktor.client.request/put.html), and so on. For example, you can replace the above request with the following code:
```kotlin
```
{src="snippets/client-configure-request/src/main/kotlin/com/example/Application.kt" include-lines="21"}

In both examples, a request URL is specified as a string. You can also configure URL components separately using [HttpRequestBuilder](#url).

## Specify a request URL {id="url"}

The Ktor client allows you to configure a request URL in the following ways:

- _Pass the entire URL string_
   
   ```kotlin
   ```
   {src="snippets/client-configure-request/src/main/kotlin/com/example/Application.kt" include-lines="21"}
   
- _Configure URL components separately_
   
   ```kotlin
   ```
   {src="snippets/client-configure-request/src/main/kotlin/com/example/Application.kt" include-lines="22-28"}
   
   In this case, the `url` parameter exposed by `HttpRequestBuilder` is used. This parameter accepts [URLBuilder](https://api.ktor.io/ktor-http/io.ktor.http/-u-r-l-builder/index.html) and provides more flexibility in building URLs.

> To configure a base URL for all requests, you can use the [DefaultRequest](client-default-request.md#url) plugin.



### Path segments {id="path_segments"}

In the previous example, we've specified the entire URL path using the `URLBuilder.path` property. 
You can also pass individual path segments using the `appendPathSegments` function.

```kotlin
```
{src="snippets/client-configure-request/src/main/kotlin/com/example/Application.kt" include-lines="29-33"}

Note that `appendPathSegments` [encodes][percent_encoding] path segments.
To disable encoding, use `appendEncodedPathSegments`.



### Query parameters {id="query_parameters"}
To add <emphasis tooltip="query_string">query string</emphasis> parameters, use the `URLBuilder.parameters` property:

```kotlin
```
{src="snippets/client-configure-request/src/main/kotlin/com/example/Application.kt" include-lines="34-38"}

Note that `parameters` [encodes][percent_encoding] query parameters.
To disable encoding, use `encodedParameters`.

> The `trailingQuery` property can be used to keep the `?` character even if there are no query parameters.



### URL fragment {id="url-fragment"}

A hash mark `#` introduces the optional fragment near the end of the URL.
You can configure a URL fragment using the `fragment` property.

```kotlin
```
{src="snippets/client-configure-request/src/main/kotlin/com/example/Application.kt" include-lines="39-43"}

Note that `fragment` [encodes][percent_encoding] a URL fragment.
To disable encoding, use `encodedFragment`.


## Set request parameters {id="parameters"}
In this section, we'll see how to specify various request parameters, including an HTTP method, headers, and cookies. If you need to configure some default parameters for all requests of a specific client, use the [DefaultRequest](client-default-request.md) plugin.


### Headers {id="headers"}

To add headers to the request, you can use the following ways:
- The [headers](https://api.ktor.io/ktor-client/ktor-client-core/io.ktor.client.request/headers.html) function allows you to add several headers at once:
   ```kotlin
   ```
  {src="snippets/client-configure-request/src/main/kotlin/com/example/Application.kt" include-lines="46-52"}
- The [header](https://api.ktor.io/ktor-client/ktor-client-core/io.ktor.client.request/header.html) function allows you to append a single header.
- The `basicAuth` and `bearerAuth` functions add the `Authorization` header with a corresponding HTTP scheme.
   > For advanced authentication configuration, refer to [](client-auth.md).



### Cookies {id="cookies"}
To send cookies, use the [cookie](https://api.ktor.io/ktor-client/ktor-client-core/io.ktor.client.request/cookie.html) function:

```kotlin
```
{src="snippets/client-configure-request/src/main/kotlin/com/example/Application.kt" include-lines="55-64"}

Ktor also provides the [HttpCookies](client-cookies.md) plugin that allows you to keep cookies between calls. If this plugin is installed, cookies added using the `cookie` function are ignored.



## Set request body {id="body"}
To set the body of a request, you need to call the `setBody` function exposed by [HttpRequestBuilder](https://api.ktor.io/ktor-client/ktor-client-core/io.ktor.client.request/-http-request-builder/index.html). This function accepts different types of payloads, including plain text, arbitrary class instances, form data, byte arrays, and so on. Below, we'll take a look at several examples.

### Text {id="text"}
Sending plain text as body can be implemented in the following way:

```kotlin
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*

val response: HttpResponse = client.post("http://localhost:8080/post") {
    setBody("Body content")
}
```


### Objects {id="objects"}
With the enabled [ContentNegotiation](client-serialization.md) plugin, you can send a class instance within a request body as JSON. To do this, pass a class instance to the `setBody` function and set the content type to `application/json` using the [contentType](https://api.ktor.io/ktor-http/io.ktor.http/content-type.html) function:

```kotlin
```
{src="snippets/client-json-kotlinx/src/main/kotlin/com/example/Application.kt" include-lines="33-36"}

You can learn more from the [](client-serialization.md) help section.

### Form parameters {id="form_parameters"}

The Ktor client provides the [`submitForm()`](https://api.ktor.io/ktor-client/ktor-client-core/io.ktor.client.request.forms/submit-form.html)
function for sending form parameters with the `application/x-www-form-urlencoded` type. The following example
demonstrates its usage:

* `url` specifies a URL for making a request.
* `formParameters` is a set of form parameters built using `parameters`.

```kotlin
```
{src="snippets/client-submit-form/src/main/kotlin/com/example/Application.kt" include-lines="16-25"}

You can find the full example here: [client-submit-form](https://github.com/ktorio/ktor-documentation/tree/%ktor_version%/codeSnippets/snippets/client-submit-form).

> To send form parameters encoded in URL, set `encodeInQuery` to `true`.


### Upload a file {id="upload_file"}

If you need to send a file with a form, you can use the following approaches:

- Use the [submitFormWithBinaryData](https://api.ktor.io/ktor-client/ktor-client-core/io.ktor.client.request.forms/submit-form-with-binary-data.html) function. In this case, a boundary will be generated automatically.
- Call the `post` function and pass the [MultiPartFormDataContent](https://api.ktor.io/ktor-client/ktor-client-core/io.ktor.client.request.forms/-multi-part-form-data-content/index.html) instance to the `setBody` function. Note that the `MultiPartFormDataContent` constructor also allows you to pass a boundary value.

For both approaches, you need to build form data using the [formData](https://api.ktor.io/ktor-client/ktor-client-core/io.ktor.client.request.forms/form-data.html) function.

<tabs>

<tab title="submitFormWithBinaryData">

```kotlin
```
{src="snippets/client-upload/src/main/kotlin/com/example/Application.kt" include-lines="13-24"}

</tab>

<tab title="MultiPartFormDataContent">

```kotlin
```
{src="snippets/client-upload-progress/src/main/kotlin/com/example/Application.kt" include-lines="16-33"}

</tab>

</tabs>

`MultiPartFormDataContent` also allows you to override a boundary and content type as follows:

```kotlin
```
{src="snippets/client-upload-progress/src/main/kotlin/com/example/Application.kt" include-lines="39-43"}

You can find the full examples here:
- [client-upload](https://github.com/ktorio/ktor-documentation/tree/%ktor_version%/codeSnippets/snippets/client-upload)
- [client-upload-progress](https://github.com/ktorio/ktor-documentation/tree/%ktor_version%/codeSnippets/snippets/client-upload-progress)


### Binary data {id="binary"}

To send binary data with the `application/octet-stream` content type, pass the [ByteReadChannel](https://api.ktor.io/ktor-io/io.ktor.utils.io/-byte-read-channel/index.html) instance to the `setBody` function.
For example, you can use the [File.readChannel](https://api.ktor.io/ktor-utils/io.ktor.util.cio/read-channel.html) function to open a read channel for a file and fill it:

```kotlin
```
{src="snippets/client-upload-binary-data/src/main/kotlin/com/example/Application.kt" include-lines="14-16"}

You can find the full example here: [client-upload-binary-data](https://github.com/ktorio/ktor-documentation/tree/%ktor_version%/codeSnippets/snippets/client-upload-binary-data).


## Parallel requests {id="parallel_requests"}

When sending two requests at once, the client suspends the second request execution until the first one is finished. If you need to perform several requests at once, you can use [launch](https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines/launch.html) or [async](https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines/async.html) functions. The code snippet below shows how to perform two requests asynchronously:
```kotlin
```
{src="snippets/client-parallel-requests/src/main/kotlin/com/example/Application.kt" include-lines="12,19-23,28"}

To see a full example, go to [client-parallel-requests](https://github.com/ktorio/ktor-documentation/tree/%ktor_version%/codeSnippets/snippets/client-parallel-requests).


## Cancel a request {id="cancel-request"}

If you need to cancel a request, you can cancel a coroutine that runs this request. The [launch](https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines/launch.html) function returns a `Job` that can be used to cancel the running coroutine:

```kotlin
import kotlinx.coroutines.*

val client = HttpClient(CIO)
val job = launch {
    val requestContent: String = client.get("http://localhost:8080")
}
job.cancel()
```

Learn more from [Cancellation and timeouts](https://kotlinlang.org/docs/cancellation-and-timeouts.html).
