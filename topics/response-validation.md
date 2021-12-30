[//]: # (title: Response validation)

<microformat>
<p>Code examples:
<a href="https://github.com/ktorio/ktor-documentation/tree/main/codeSnippets/snippets/client-validate-response">client-validate-response</a>,
<a href="https://github.com/ktorio/ktor-documentation/tree/main/codeSnippets/snippets/client-customize-default-validation">client-customize-default-validation</a>
</p>
</microformat>

<excerpt>
Ktor allows you to add validation for 2xx responses or customize default validation of non-2xx responses.
</excerpt>

The Ktor client includes validation of non-2xx responses by default. For example, the client throws [ClientRequestException](https://api.ktor.io/ktor-client/ktor-client-core/io.ktor.client.plugins/-client-request-exception/index.html) for 4xx error responses. If required, you can add additional validation for 2xx responses or customize default validation by using [HttpCallValidator](https://api.ktor.io/ktor-client/ktor-client-core/io.ktor.client.plugins/-http-call-validator/index.html).

## Default validation {id="default"}
By default, the Ktor client throws exceptions for non-2xx error responses:
* [RedirectResponseException](https://api.ktor.io/ktor-client/ktor-client-core/io.ktor.client.plugins/-redirect-response-exception/index.html) for 3xx responses.
* [ClientRequestException](https://api.ktor.io/ktor-client/ktor-client-core/io.ktor.client.plugins/-client-request-exception/index.html) for 4xx responses.
* [ServerResponseException](https://api.ktor.io/ktor-client/ktor-client-core/io.ktor.client.plugins/-server-response-exception/index.html) for 5xx responses.

You can add additional validation for 2xx responses by using the [validateResponse](#2xx) function available in `HttpCallValidator`. If you need to customize handling of non-2xx exceptions, you can use [handleResponseException](#non-2xx).

Default validation is controlled by the `expectSuccess` property. If necessary, you can disable it on a [client configuration](create-client.md#configure-client) level using the `expectSuccess` property ...
```kotlin
```
{src="snippets/_misc_client/BasicClientConfig.kt"}

... or by using the same property on a [request](request.md#parameters) level.

## Custom validation {id="custom"}
You can add additional validation for 2xx responses or customize default validation by using the [HttpCallValidator](https://api.ktor.io/ktor-client/ktor-client-core/io.ktor.client.plugins/-http-call-validator/index.html) plugin. To install `HttpCallValidator`, call the [HttpResponseValidator](https://api.ktor.io/ktor-client/ktor-client-core/io.ktor.client.plugins/-http-response-validator.html) function inside a [client configuration block](create-client.md#configure-client):

```kotlin
val client = HttpClient(CIO) {
    HttpResponseValidator {
        // ...
    }
}
```


### Validate 2xx responses {id="2xx"}

As mentioned above, [default validation](#default) throws exceptions for non-2xx error responses. If you need to add stricter validation and check 2xx responses, use the [validateResponse](https://api.ktor.io/ktor-client/ktor-client-core/io.ktor.client.plugins/-http-call-validator/-config/validate-response.html) function available in `HttpCallValidator`. 

In the [example](https://github.com/ktorio/ktor-documentation/tree/main/codeSnippets/snippets/client-validate-response) below, a client receives a 2xx response with error details in a [JSON](serialization-client.md) format. The `validateResponse` is used to raise a `CustomResponseException`:

```kotlin
```
{src="snippets/client-validate-response/src/main/kotlin/com/example/Application.kt" lines="22-32"}

### Handle non-2xx exceptions {id="non-2xx"}

If you need to customize [default validation](#default) and handle exceptions for non-2xx responses in a specific way, use [handleResponseException](https://api.ktor.io/ktor-client/ktor-client-core/io.ktor.client.plugins/-http-call-validator/-config/handle-response-exception.html). In the [example](https://github.com/ktorio/ktor-documentation/tree/main/codeSnippets/snippets/client-customize-default-validation) below, a client raises a custom `MissingPageException` for 404 responses instead of the default `ClientRequestException`:

```kotlin
```
{src="snippets/client-customize-default-validation/src/main/kotlin/com/example/Application.kt" lines="18-29"}
