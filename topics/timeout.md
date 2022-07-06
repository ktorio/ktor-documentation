[//]: # (title: Timeout)

<tldr>
<var name="example_name" value="client-timeout"/>
<include src="lib.topic" element-id="download_example"/>
</tldr>

The [HttpTimeout](https://api.ktor.io/ktor-client/ktor-client-core/io.ktor.client.plugins/-http-timeout/index.html) plugin allows you to configure the following timeouts:
* __request timeout__ — a time period required to process an HTTP call: from sending a request to receiving a response.
* __connection timeout__ — a time period in which a client should establish a connection with a server.
* __socket timeout__ — a maximum time of inactivity between two data packets when exchanging data with a server.

You can specify these timeouts for all requests or only specific ones.

## Add dependencies {id="add_dependencies"}
`HttpTimeout` only requires the [ktor-client-core](client-dependencies.md) artifact and doesn't need any specific dependencies.


## Install HttpTimeout {id="install_plugin"}

To install `HttpTimeout`, pass it to the `install` function inside a [client configuration block](create-client.md#configure-client):
```kotlin
val client = HttpClient(CIO) {
    install(HttpTimeout)
}
```


## Configure timeouts {id="configure_plugin"}

To configure timeouts, you can use corresponding properties:
* [requestTimeoutMillis](https://api.ktor.io/ktor-client/ktor-client-core/io.ktor.client.plugins/-http-timeout/-http-timeout-capability-configuration/request-timeout-millis.html) specifies a timeout for a whole HTTP call, from sending a request to receiving a response.
* [connectTimeoutMillis](https://api.ktor.io/ktor-client/ktor-client-core/io.ktor.client.plugins/-http-timeout/-http-timeout-capability-configuration/connect-timeout-millis.html) specifies a timeout for establishing a connection with a server.
* [socketTimeoutMillis](https://api.ktor.io/ktor-client/ktor-client-core/io.ktor.client.plugins/-http-timeout/-http-timeout-capability-configuration/socket-timeout-millis.html) specifies a timeout for the maximum time in between two data packets when exchanging data with a server.

You can specify timeouts for all requests inside the `install` block. The code sample below shows how to set a request timout using `requestTimeoutMillis`:
```kotlin
```
{src="/snippets/client-timeout/src/main/kotlin/com/example/Application.kt" lines="13-17"}

If you need to set a timeout only for a specific request, use the [HttpRequestBuilder.timeout](https://api.ktor.io/ktor-client/ktor-client-core/io.ktor.client.plugins/timeout.html) property:

```kotlin
```
{src="/snippets/client-timeout/src/main/kotlin/com/example/Application.kt" lines="20-24"}

Note that timeouts specified for a specific request override global timeouts from the `install` block.

In a case of a timeout, Ktor throws `HttpRequestTimeoutException`, `ConnectTimeoutException`, or `SocketTimeoutException`.


## Limitations {id="limitations"}

`HttpTimeout` has some limitations for specific [engines](http-client_engines.md):
* [Darwin](http-client_engines.md#darwin) doesn't support a connection timeout.
* [JavaScript](http-client_engines.md#js) supports only a request timeout.
* [Curl](http-client_engines.md#curl) doesn't support a socket timeout.
