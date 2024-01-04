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

By default, Ktor doesn't validate a [response](response.md) depending on its status code.
If required, you can use the following validation strategies:

- Use the `expectSuccess` property to throw exceptions for non-2xx responses.
- Add stricter validation of 2xx responses.
- Customize validation of non-2xx responses.

## Enable default validation {id="default"}

Ktor allows you to enable default validation by setting the `expectSuccess` property to `true`.
This can be done on a [client configuration](create-client.md#configure-client) level ...

```kotlin
```

{src="snippets/_misc_client/BasicClientConfig.kt"}

... or by using the same property on a [request](request.md#parameters) level.
In this case, the following exceptions will be thrown for non-2xx error responses:

* [RedirectResponseException](https://api.ktor.io/ktor-client/ktor-client-core/io.ktor.client.plugins/-redirect-response-exception/index.html)
  for 3xx responses.
* [ClientRequestException](https://api.ktor.io/ktor-client/ktor-client-core/io.ktor.client.plugins/-client-request-exception/index.html)
  for 4xx responses.
* [ServerResponseException](https://api.ktor.io/ktor-client/ktor-client-core/io.ktor.client.plugins/-server-response-exception/index.html)
  for 5xx responses.

## Custom validation {id="custom"}

You can add additional validation for 2xx responses or customize default validation by using
the [HttpCallValidator](https://api.ktor.io/ktor-client/ktor-client-core/io.ktor.client.plugins/-http-call-validator)
plugin. To install `HttpCallValidator`, call
the [HttpResponseValidator](https://api.ktor.io/ktor-client/ktor-client-core/io.ktor.client.plugins/-http-response-validator.html)
function inside a [client configuration block](create-client.md#configure-client):

```kotlin
val client = HttpClient(CIO) {
    HttpResponseValidator {
        // ...
    }
}
```

### Validate 2xx responses {id="2xx"}

As mentioned above, [default validation](#default) throws exceptions for non-2xx error responses. If you need to add
stricter validation and check 2xx responses, use
the [validateResponse](https://api.ktor.io/ktor-client/ktor-client-core/io.ktor.client.plugins/-http-cal-validator-config/validate-response.html)
function available in `HttpCallValidator`.

In
the [example](https://github.com/ktorio/ktor-documentation/tree/%ktor_version%/codeSnippets/snippets/client-validate-2xx-response)
below, a client receives a 2xx response with error details in a [JSON](serialization-client.md) format.
The `validateResponse` is used to raise a `CustomResponseException`:

```kotlin
```

{src="snippets/client-validate-2xx-response/src/main/kotlin/com/example/Application.kt" include-lines="26-36"}

### Handle non-2xx exceptions {id="non-2xx"}

If you need to customize [default validation](#default) and handle exceptions for non-2xx responses in a specific way,
use [handleResponseExceptionWithRequest](https://api.ktor.io/ktor-client/ktor-client-core/io.ktor.client.plugins/-http-cal-validator-config/handle-response-exception-with-request.html).
In
the [example](https://github.com/ktorio/ktor-documentation/tree/%ktor_version%/codeSnippets/snippets/client-validate-non-2xx-response)
below, a client raises a custom `MissingPageException` for 404 responses instead of the
default `ClientRequestException`:

```kotlin
```

{src="snippets/client-validate-non-2xx-response/src/main/kotlin/com/example/Application.kt" include-lines="18-30"}
