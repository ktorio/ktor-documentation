[//]: # (title: Response validation)

<include src="lib.md" include-id="outdated_warning"/>

This plugin allows validating HTTP response and handle transformation exceptions from engine and pipelines.



## Configuration

To configure response validation plugin, use `validateResponse` and `handleResponseException` methods:

```kotlin
HttpResponseValidator {
    validateResponse { response: HttpResponse ->
        // ...
    }

    handleResponseException { cause: Throwable ->
        // ...
    }
}
```

This plugin could be configured multiple times; all validators and handlers are saved and called in order of install.

## Expect success

The `ExpectSuccess` plugin implemented using response validation:

```kotlin
HttpResponseValidator {
    validateResponse { response ->
        val statusCode = response.status.value
        when (statusCode) {
            in 300..399 -> throw RedirectResponseException(response)
            in 400..499 -> throw ClientRequestException(response)
            in 500..599 -> throw ServerResponseException(response)
        }

        if (statusCode >= 600) {
            throw ResponseException(response)
        }
    }
}
```

The plugin is installed by default, but could be disabled in the client configuration:

```kotlin
val client = HttpClient() {
    expectSuccess = false
}
```