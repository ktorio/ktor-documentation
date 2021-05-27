[//]: # (title: Default request)

<include src="lib.xml" include-id="outdated_warning"/>

This feature allows you to configure some defaults for all the requests for a specific client.



## Installation

When configuring the client, there is an extension method provided by this feature to set some defaults for this client.
For example, if you want to add a header to all the requests, or configure the host, port, and method or just set the path.

```kotlin
val client = HttpClient() {
    defaultRequest { // this: HttpRequestBuilder ->
        method = HttpMethod.Head
        host = "127.0.0.1"
        port = 8080
        header("X-My-Header", "MyValue")
    }
}
```

## Example

An example showing how to the client behaves using the [MockEngine](http-client_testing.md):

```kotlin
import io.ktor.client.*
import io.ktor.client.engine.*
import io.ktor.client.engine.mock.*
import io.ktor.client.features.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.experimental.*
import kotlinx.coroutines.experimental.io.*

fun main(args: Array<String>) = runBlocking {
    val client = HttpClient(MockEngine) {
        engine {
            // Register request handler.
            addHandler { request ->
                with(request) {
                    val responseText = buildString{
                        append("method=$method,")
                        append("host=${url.host},")
                        append("port=${url.port},")
                        append("path=${url.fullPath},")
                        append("headers=$headers")
                    }
                    val responseHeaders = headersOf("Content-Type" to listOf(ContentType.Text.Plain.toString()))

                    respond(responseText, headers = responseHeaders)
                }
            }
        }

        // Configure default request feature.
        defaultRequest {
            method = HttpMethod.Head
            host = "127.0.0.1"
            port = 8080
            header("X-My-Header", "MyValue")
        }
    }

    val result = client.get<String> {
        url {
            encodedPath = "/demo"
        }
    }

    println(result)
    // Prints: method=HttpMethod(value=HEAD), host=127.0.0.1, port=8080, path=/demo, headers=Headers [X-My-Header=[MyValue], Accept=[*/*]]
}

```