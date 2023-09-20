[//]: # (title: Server-Sent Events)

<show-structure for="chapter" depth="2"/>

<tldr>
<var name="example_name" value="client-sse"/>
<include from="lib.topic" element-id="download_example"/>
</tldr>

<link-summary>
The SSE plugin allows a client to receive event-based updates from a server over an HTTP connection.
</link-summary>

<include from="sse_server.md" element-id="sse-description" />


> To learn more about the SSE plugin for server-side support, see [](sse_server.md)

## Add dependencies {id="add_dependencies"}
`SSE` only requires the [ktor-client-core](client-dependencies.md) artifact and doesn't need any specific dependencies.


## Install SSE {id="install_plugin"}
To install the `SSE` plugin, pass it to the `install` function inside a [client configuration block](create-client.md#configure-client):

```kotlin
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.sse.*

//...
val client = HttpClient(CIO) {
    install(SSE)
}
```
## Configure the SSE plugin {id="configure"}

[//]: # (TODO: Add link for SSEConfig)
Optionally, you can configure the plugin inside the `install` block by passing the supported properties of the [SSEConfig]() class.

1. Use the `reconnectionTime` property to specify a reconnection time.  If the connection to the server is lost, the client will wait for the specified time before attempting to reconnect.
2. Use the `showCommentEvents` function to add events consisting only of comments in the incoming flow.
3. Use the `showRetryEvents` function to add events consisting only of the retry field in the incoming flow.

```kotlin
```
{src="snippets/client-sse/src/main/kotlin/com.example/Application.kt" include-lines="11-15"}

> Note that the `reconnectionTime` property is not supported in all engines.
To learn more, see [](http-client_engines.md#configure)


## Handle an SSE session {id="handle-session"}
### API overview {id="api-overview"}

[//]: # (TODO: Add API links)

A client's SSE session is represented by the [DefaultClientSSESession]() interface.
This interface exposes the API that allows you to receive SSE events from a server.

The `HttpClient` allows you to get access to an SSE session in one of the following ways:


- The [sse]() function creates the SSE session and allows you to act on it. It accepts `ClientSSESession` as a block argument.
  Use the `incoming` property to access the channel for receiving events.

  ```kotlin
     runBlocking {
        client.sse(host = "127.0.0.1", port = 8080, path = "/events") {
             // this: ClientSSESession
         }
     }
  ```
  
- The [sseSession]() function allows you to open an SSE session.


### Example {id="example"}

The example below creates a new SSE session with the `events` endpoint,
reads events through the `incoming` property and prints the received `ServerSentEvent`.

```kotlin
```
{src="snippets/client-sse/src/main/kotlin/com.example/Application.kt" include-symbol="main"}


For the full example, see [client-sse](https://github.com/ktorio/ktor-documentation/tree/%ktor_version%/codeSnippets/snippets/client-sse).

