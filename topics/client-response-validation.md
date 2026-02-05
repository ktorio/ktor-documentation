[//]: # (title: Response validation)

<show-structure for="chapter" depth="2"/>

<tldr>
<p><b>Code examples</b>:
<a href="https://github.com/ktorio/ktor-documentation/tree/%ktor_version%/codeSnippets/snippets/client-validate-2xx-response">client-validate-2xx-response</a>,
<a href="https://github.com/ktorio/ktor-documentation/tree/%ktor_version%/codeSnippets/snippets/client-validate-non-2xx-response">client-validate-non-2xx-response</a>
</p>
</tldr>

<link-summary>
Learn how to validate a response depending on its status code.
</link-summary>

By default, the Ktor HTTP client does not validate responses based on their HTTP status codes.
If needed, you can enable and customize response validation using the following strategies:

* [Use the `expectSuccess` property to throw exceptions for non-2xx responses](#default).
* [Add stricter validation for 2xx responses](#2xx).
* [Customize validation for non-2xx responses](#non-2xx).

## Enable default validation {id="default"}

Ktor allows you to enable default validation by setting the `expectSuccess` property to `true`. When enabled, the
client throws an exception for any response with a non-successful HTTP status code.

You can enable this behavior globally in the [client configuration](client-create-and-configure.md#configure-client):

```kotlin
```

{src="snippets/_misc_client/BasicClientConfig.kt"}

Alternatively, you can enable `expectSuccess` on a per-request basis. In this case, the following exceptions will be
thrown for non-2xx error responses:

* [`RedirectResponseException`](https://api.ktor.io/ktor-client-core/io.ktor.client.plugins/-redirect-response-exception/index.html)
  for 3xx responses.
* [`ClientRequestException`](https://api.ktor.io/ktor-client-core/io.ktor.client.plugins/-client-request-exception/index.html)
  for 4xx responses.
* [`ServerResponseException`](https://api.ktor.io/ktor-client-core/io.ktor.client.plugins/-server-response-exception/index.html)
  for 5xx responses.

## Custom validation {id="custom"}

In addition to the default validation behavior, you can define custom response validation logic by using the
[`HttpCallValidator`](https://api.ktor.io/ktor-client-core/io.ktor.client.plugins/-http-call-validator) plugin. This 
allows you to validate successful (2xx) responses or override how non-2xx responses are handled.

To install `HttpCallValidator`, call the [`HttpResponseValidator`](https://api.ktor.io/ktor-client-core/io.ktor.client.plugins/-http-response-validator.html)
function inside a [client configuration block](client-create-and-configure.md#configure-client):

```kotlin
val client = HttpClient(CIO) {
    HttpResponseValidator {
        // ...
    }
}
```

### Validate 2xx responses {id="2xx"}

Default validation throws exceptions only for non-2xx responses. If your application requires stricter validation, you
can validate successful responses using the
[`validateResponse {}`](https://api.ktor.io/ktor-client-core/io.ktor.client.plugins/-http-call-validator-config/validate-response.html)
function.

In the following example, the server returns a 2xx response containing an error payload in JSON format.
The `validateResponse {}` block inspects the response body and throws a custom exception when an error is detected:

```kotlin
```

{src="snippets/client-validate-2xx-response/src/main/kotlin/com/example/Application.kt" include-lines="26-36"}

> For the full example, see [client-validate-2xx-response](https://github.com/ktorio/ktor-documentation/tree/%ktor_version%/codeSnippets/snippets/client-validate-2xx-response).
> 
{style="tip"}

### Handle non-2xx exceptions {id="non-2xx"}

To customize how non-2xx response exceptions are handled, use the [`handleResponseExceptionWithRequest {}`](https://api.ktor.io/ktor-client-core/io.ktor.client.plugins/-http-call-validator-config/handle-response-exception-with-request.html)
function.

In the following example, the client throws a custom `MissingPageException` for `404 Not Found` responses instead of the
default `ClientRequestException`:

```kotlin
```

{src="snippets/client-validate-non-2xx-response/src/main/kotlin/com/example/Application.kt" include-lines="11-40"}

> For the full example, see [client-validate-non-2xx-response](https://github.com/ktorio/ktor-documentation/tree/%ktor_version%/codeSnippets/snippets/client-validate-non-2xx-response).
> 
{style="tip"}