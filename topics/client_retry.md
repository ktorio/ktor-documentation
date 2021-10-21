[//]: # (title: Retrying failed requests)

<microformat>
<var name="example_name" value="client-retry"/>
<include src="lib.xml" include-id="download_example"/>
</microformat>

By default, the Ktor client doesn't retry [requests](request.md) that failed due to network or server errors.
You can use the `HttpRequestRetry` plugin to configure the retry policy for failed requests in various ways: specify the number of retries, configure conditions for retrying a request, or specify delay logic.





## Add dependencies {id="add_dependencies"}
`HttpRequestRetry` only requires the [ktor-client-core](client.md#client-dependency) artifact and doesn't need any specific dependencies.

## Install HttpRequestRetry {id="install_plugin"}

To install `HttpRequestRetry`, pass it to the `install` function inside a [client configuration block](client.md#configure-client):
```kotlin
val client = HttpClient(CIO) {
    install(HttpRequestRetry)
}
```


## Configure HttpRequestRetry {id="configure_retry"}

A sample below shows how to configure the basic retry policy:

```kotlin
```
{src="snippets/client-retry/src/main/kotlin/com/example/Application.kt" lines="13-17,19"}

* The `retryOnServerErrors` function enables retrying a request if a `5xx` response is received from a server and specifies the number of retries.
* `exponentialDelay` specifies an exponential delay between retries, which is calculated using the Exponential backoff algorithm.

You can find the full example here: [client-retry](https://github.com/ktorio/ktor-documentation/tree/main/codeSnippets/snippets/client-retry).

There are also configuration settings that allow you to configure conditions for retrying a request or specify delay logic:

```kotlin
install(HttpRequestRetry) {
    maxRetries = 5
    retryIf { request, response ->
        !response.status.isSuccess()
    }
    retryOnExceptionIf { request, cause -> 
        cause is NetworkError 
    }
    delayMillis { retry -> 
        retry * 3000L 
    } // retries in 3, 6, 9, etc. seconds
}
```
