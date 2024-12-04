[//]: # (title: Retrying failed requests)

<show-structure for="chapter" depth="2"/>

<tldr>
<var name="example_name" value="client-retry"/>
<include from="lib.topic" element-id="download_example"/>
</tldr>

<link-summary>
The HttpRequestRetry plugin allows you to configure the retry policy for failed requests.
</link-summary>

By default, the Ktor client doesn't retry [requests](client-requests.md) that failed due to network or server errors.
You can use
the [HttpRequestRetry](https://api.ktor.io/ktor-client/ktor-client-core/io.ktor.client.plugins/-http-request-retry)
plugin to configure the retry policy for failed requests in various ways: specify the number of retries, configure
conditions for retrying a request, or modify a request before retrying.



## Add dependencies {id="add_dependencies"}
`HttpRequestRetry` only requires the [ktor-client-core](client-dependencies.md) artifact and doesn't need any specific dependencies.

## Install HttpRequestRetry {id="install_plugin"}

To install `HttpRequestRetry`, pass it to the `install` function inside a [client configuration block](client-create-and-configure.md#configure-client):
```kotlin
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
//...
val client = HttpClient(CIO) {
    install(HttpRequestRetry)
}
```


## Configure HttpRequestRetry {id="configure_retry"}

### Basic retry configuration {id="basic_config"}

A [runnable example](https://github.com/ktorio/ktor-documentation/tree/%ktor_version%/codeSnippets/snippets/client-retry) below shows how to configure the basic retry policy:

```kotlin
```
{src="snippets/client-retry/src/main/kotlin/com/example/Application.kt" include-lines="17-21,23"}

* The `retryOnServerErrors` function enables retrying a request if a `5xx` response is received from a server and specifies the number of retries.
* `exponentialDelay` specifies an exponential delay between retries, which is calculated using the Exponential backoff algorithm.

You can learn more about supported configuration options
from [HttpRequestRetryConfig](https://api.ktor.io/ktor-client/ktor-client-core/io.ktor.client.plugins/-http-request-retry-config).

### Configure retry conditions {id="conditions"}

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

### Modify a request before retrying {id="modify"}

If you need to modify a request before retrying, use `modifyRequest`:

```kotlin
install(HttpRequestRetry) {
    // Retry conditions
    modifyRequest { request ->
        request.headers.append("x-retry-count", retryCount.toString())
    }
}
```
