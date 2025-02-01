[//]: # (title: Timeout)

<primary-label ref="client-plugin"/>

<tldr>
<var name="example_name" value="client-timeout"/>
<include from="lib.topic" element-id="download_example"/>
</tldr>

The [HttpTimeout](https://api.ktor.io/ktor-client/ktor-client-core/io.ktor.client.plugins/-http-timeout) plugin allows
you to configure the following timeouts:
* __request timeout__ — a time period required to process an HTTP call: from sending a request to receiving a response.
* __connection timeout__ — a time period in which a client should establish a connection with a server.
* __socket timeout__ — a maximum time of inactivity between two data packets when exchanging data with a server.

You can specify these timeouts for all requests or only specific ones.

## Add dependencies {id="add_dependencies"}
`HttpTimeout` only requires the [ktor-client-core](client-dependencies.md) artifact and doesn't need any specific dependencies.


## Install HttpTimeout {id="install_plugin"}

To install `HttpTimeout`, pass it to the `install` function inside a [client configuration block](client-create-and-configure.md#configure-client):
```kotlin
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
//...
val client = HttpClient(CIO) {
    install(HttpTimeout)
}
```


## Configure timeouts {id="configure_plugin"}

To configure timeouts, you can use the corresponding properties:

* [requestTimeoutMillis](https://api.ktor.io/ktor-client/ktor-client-core/io.ktor.client.plugins/-http-timeout-config/request-timeout-millis.html)
  specifies a timeout for a whole HTTP call, from sending a request to receiving a response.
* [connectTimeoutMillis](https://api.ktor.io/ktor-client/ktor-client-core/io.ktor.client.plugins/-http-timeout-config/connect-timeout-millis.html)
  specifies a timeout for establishing a connection with a server.
* [socketTimeoutMillis](https://api.ktor.io/ktor-client/ktor-client-core/io.ktor.client.plugins/-http-timeout-config/socket-timeout-millis.html)
  specifies a timeout for the maximum time in between two data packets when exchanging data with a server.

You can specify timeouts for all requests inside the `install` block. The code sample below shows how to set a request timout using `requestTimeoutMillis`:
```kotlin
```
{src="/snippets/client-timeout/src/main/kotlin/com/example/Application.kt" include-lines="17-21"}

If you need to set a timeout only for a specific request, use the [HttpRequestBuilder.timeout](https://api.ktor.io/ktor-client/ktor-client-core/io.ktor.client.plugins/timeout.html) property:

```kotlin
```
{src="/snippets/client-timeout/src/main/kotlin/com/example/Application.kt" include-lines="24-28"}

Note that timeouts specified for a specific request override global timeouts from the `install` block.

In a case of a timeout, Ktor throws `HttpRequestTimeoutException`, `ConnectTimeoutException`, or `SocketTimeoutException`.


## Limitations {id="limitations"}

`HttpTimeout` has some limitations for specific [engines](client-engines.md). The table below shows which timeouts are
supported by those engines:

| Engine                             | Request Timeout | Connect Timeout | Socket Timeout |
|------------------------------------|-----------------|-----------------|----------------|
| [Darwin](client-engines.md#darwin) | ✅️              | ✖️              | ✅️             |
| [JavaScript](client-engines.md#js) | ✅               | ✖️              | ✖️             |
| [Curl](client-engines.md#curl)     | ✅               | ✅️              | ✖️             |
| [MockEngine](client-testing.md)    | ✅               | ✖️              | ✅              |
