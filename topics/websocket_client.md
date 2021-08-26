[//]: # (title: WebSockets)

<include src="lib.xml" include-id="outdated_warning"/>

<microformat>
<p>
Required dependencies: <code>io.ktor:ktor-client-websockets</code>
</p>
<var name="example_name" value="client-websockets"/>
<include src="lib.xml" include-id="download_example"/>
</microformat>

Ktor provides Websocket client support for the following engines: CIO, OkHttp, Js. To get more information about 
the server side, follow this [section](websocket.md).

Once connected, client and server WebSockets share the same [WebSocketSession](websocket.md#WebSocketSession)
interface for communication.


## Add dependencies {id="add_dependencies"}
To use `WebSockets`, you need to include the `ktor-client-websockets` artifact in the build script:

<var name="artifact_name" value="ktor-client-websockets"/>
<include src="lib.xml" include-id="add_ktor_artifact"/>


## Usage {id="usage"}

The basic usage to create an HTTP client supporting WebSockets is pretty simple:

```kotlin
val client = HttpClient {
    install(WebSockets)
}
```

Once created we can perform a request, starting a `WebSocketSession`:

```kotlin
```
{src="snippets/client-websockets/src/main/kotlin/com/example/Application.kt" include-symbol="main"}

For more information about the WebSocketSession, check the [WebSocketSession page](websocket.md#WebSocketSession) and the [API reference](https://api.ktor.io/ktor-client/ktor-client-core/ktor-client-core/io.ktor.client.features.websocket/index.html).