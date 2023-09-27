[//]: # (title: Server-Sent Events)

<show-structure for="chapter" depth="2"/>

<var name="plugin_name" value="SSE"/>
<var name="example_name" value="server-sse"/>
<var name="package_name" value="io.ktor.server.sse"/>
<var name="artifact_name" value="ktor-server-sse"/>

<tldr>
<p>
<b>Required dependencies</b>: <code>io.ktor:%artifact_name%</code>
</p>
<include from="lib.topic" element-id="download_example"/>
</tldr>

<link-summary>
The SSE plugin allows a server to send event-based updates to a client over an HTTP connection.
</link-summary>

<snippet id="sse-description">

Server-Sent Events (SSE) is a technology that allows a server to continuously push events to a client over an HTTP
connection.
It's particularly useful in cases where the server needs to send event-based updates without requiring the client to
repeatedly poll the server.

The SSE plugins supported by Ktor provide a straightforward method for creating a one-way connection between the server
and the client.

</snippet>

> To learn more about the SSE plugin for client-side support, see the [SSE client plugin](sse_client.md).

> For multi-way communication, consider using [WebSockets](websocket.md). They provide a full-duplex communication
> between the server and the client using the Websocket protocol.
>
{style="note"}

## Add dependencies {id="add_dependencies"}

<include from="lib.topic" element-id="add_ktor_artifact_intro"/>
<include from="lib.topic" element-id="add_ktor_artifact"/>

## Install SSE {id="install_plugin"}

<include from="lib.topic" element-id="install_plugin"/>

## Handle SSE sessions {id="handle-sessions"}

### API overview {id="api-overview"}

Once you have installed the `SSE` plugin, you can add a route to handle an SSE session.
To do that, call the `sse()` function inside the [routing](Routing_in_Ktor.md#define_route) block:

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

Within the `sse` block, you define the handler for the specified path, represented by the [ServerSSESession]() class.
The following functions and properties are available within the block:

* Use the `send()` function to create and send a `ServerSentEvent` to the client.
* Use the `call` property to access the associated received `ApplicationCall` that originated the session.
* Use the `close()` function to close the session and terminate the connection with the client. The `close()` method is
  called automatically when all `send()` operations are completed.

> It's important to note that closing the session using the `close()` function does not send a termination event to the
client.
> If you wish to send a specific event to signify the end of the SSE stream before closing the session, you can use
the `send()` function for it.
>
{style="note"}

### Example: Handle a single session {id="handle-single-session"}

The example below creates an SSE session with the `events` endpoint which sends 6 separate events over a single SSE
channel with a delay of 1000ms.

```kotlin
```

{src="snippets/server-sse/src/main/kotlin/com/example/Application.kt" include-lines="14-21"}

For the full example,
see [server-sse](https://github.com/ktorio/ktor-documentation/tree/%ktor_version%/codeSnippets/snippets/server-sse).
