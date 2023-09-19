[//]: # (title: Server-Sent Events)

<show-structure for="chapter" depth="2"/>

<var name="plugin_name" value="SSE"/>
<var name="example_name" value="server-sse"/>
<var name="package_name" value="io.ktor.server.sse"/>
<var name="artifact_name" value="ktor-server-sse"/>

<tldr>

<include from="lib.topic" element-id="download_example"/>
</tldr>

<link-summary>
Ktor supports SSE and allows you to create applications that receive event-based updates from the server without requiring constant polling by the client.
</link-summary>

Server-Sent Events (SSE) allows a server to continuously push events to a client over an HTTP connection.
It's particularly useful in cases where the server needs to send event-based updates to clients without requiring the client to repeatedly poll the server.

This makes it a good alternative to [Websockets](websocket.md) for creating a one-way connection between the server and the client.

## Add dependencies {id="add_dependencies"}

<include from="lib.topic" element-id="add_ktor_artifact_intro"/>
<include from="lib.topic" element-id="add_ktor_artifact"/>


## Install SSE {id="install_plugin"}

<include from="lib.topic" element-id="install_plugin"/>

## Handle SSE sessions {id="handle-sessions"}

### API overview {id="api-overview"}

Once you have installed the `SSE` plugin, you can add a route to handle an SSE session.
To do that, call the `sse` function inside the [routing](Routing_in_Ktor.md#define_route) block:

```kotlin
routing { 
    sse("/events") {
        //send events to clients
    }
}
```
It is also possible to define an SSE route without specifying a path:

```kotlin
routing {
    sse {
        //send events to clients
    }
}
```
Within the `sse` block, you define the handler for the specified path, represented by the [ServerSSESession]() class. Session configuration might look as follows:


1. Use the `send` function to create and send a `ServerSentEvent` to the client.
2. Use the `call` property to access the associated received `ApplicationCall` that originated the session.
3. (Optional) Use the `close` function to close the session and terminate the connection with the client. The `close` method is called automatically when all `send` operations are completed.
> It's important to note that closing the session using this method does not send a termination event to the client.
> If you wish to send a specific event to signify the end of the SSE stream before closing the session, you can use the `send` function for it.

Below, we'll take a look at the examples of using this API.

### Example: Handle a single session {id="handle-single-session"}

The example below creates an SSE session with the `events` endpoint to handle a session with one client.
```kotlin
```
{src="snippets/server-sse/src/main/kotlin/com/example/Application.kt" include-lines="14-21"}


You can find the full example here: [server-sse](https://github.com/ktorio/ktor-documentation/tree/%ktor_version%/codeSnippets/snippets/server-sse).
