[//]: # (title: Timeout)

<microformat>
<var name="example_name" value="client-timeout"/>
<include src="lib.md" include-id="download_example"/>
</microformat>

The `HttpTimeout` feature allows you to configure the following timeouts:
* __request timeout__ — a time period required to process an HTTP call: from sending a request to receiving a response.
* __connection timeout__ — a time period in which a client should establish a connection with a server.
* __socket timeout__ — a maximum time of inactivity between two data packets when exchanging data with a server.

You can specify these timeouts for all requests or only specific ones.

## Add dependencies {id="add_dependencies"}
`HttpTimeout` only requires the [ktor-client-core](client.md#client-dependency) artifact and doesn't need any specific dependencies.


## Install HttpTimeout {id="install_feature"}

To install `HttpTimeout`, pass it to the `install` function inside a [client configuration block](client.md#configure-client):
```kotlin
val client = HttpClient(CIO) {
    install(HttpTimeout)
}
```


## Configure timeouts {id="configure_feature"}

To configure timeouts, you can use corresponding properties:
* [requestTimeoutMillis](https://api.ktor.io/%ktor_version%/io.ktor.client.features/-http-timeout/-http-timeout-capability-configuration/request-timeout-millis.html) for a request timeout.
* [connectTimeoutMillis](https://api.ktor.io/%ktor_version%/io.ktor.client.features/-http-timeout/-http-timeout-capability-configuration/connect-timeout-millis.html) for a connection timeout.
* [socketTimeoutMillis](https://api.ktor.io/%ktor_version%/io.ktor.client.features/-http-timeout/-http-timeout-capability-configuration/socket-timeout-millis.html) for a socket timeout.

You can specify timeouts for all requests inside the `install` block. The code sample below shows how to set a request timout using `requestTimeoutMillis`:
```kotlin
```
{src="/snippets/client-timeout/src/main/kotlin/com/example/Application.kt" lines="13-17"}

If you need to set a timeout only for a specific request, use the [HttpRequestBuilder.timeout](https://api.ktor.io/%ktor_version%/io.ktor.client.features/timeout.html) property:

```kotlin
```
{src="/snippets/client-timeout/src/main/kotlin/com/example/Application.kt" lines="20-24"}

Note that timeouts specified for a specific request override global timeouts from the `install` block.

In a case of a timeout, Ktor throws `HttpRequestTimeoutException`, `HttpConnectTimeoutException`, or `HttpSocketTimeoutException`.


## Limitations {id="limitations"}

`HttpTimeout` has some limitations for specific [engines](http-client_engines.md):
* [iOS](http-client_engines.md#ios) doesn't support a connection timeout.
* [JavaScript](http-client_engines.md#js) supports only a request timeout.
* [Curl](http-client_engines.md#desktop) doesn't support a socket timeout.
