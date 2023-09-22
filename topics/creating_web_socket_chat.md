[//]: # (title: Creating a WebSocket chat)

<show-structure for="chapter" depth="2"/>

<tldr>
<var name="example_name" value="tutorial-websockets-server"/>
<include from="lib.topic" element-id="download_example"/>
<p>
<b>Used plugins</b>: <a href="Routing_in_Ktor.md">Routing</a>, <a href="websocket.md">WebSockets</a>
</p>
</tldr>

<link-summary>Learn how to create a server chat application that uses WebSockets.</link-summary>

In this tutorial, you will learn how to create a simple chat application that uses [WebSockets](websocket.md). The
solution consists of two parts:

- The **chat server application** will accept and manage connections from the chat users, receive messages, and
  distribute them to all connected clients.
- The **chat client application** will allow users to join a common chat server, send messages, and read user messages
  in the terminal. To follow this tutorial, see [](getting_started_ktor_client_chat.md).

![App in action](app_in_action.gif){preview-src="app_in_action.png" width="706"}

Because Ktor is both a server-side and a client-side framework, you will reuse the knowledge you acquire from this part
of the tutorial onto the client-side implementation.

This tutorial will teach you how to:

- Work with WebSockets using Ktor.
- Exchange information between a client and a server.
- Manage multiple WebSocket connections simultaneously.

## Why WebSockets? {id="why"}

WebSockets are exceptionally well-suited for applications like chats or simple games. Chat sessions are usually
long-lived, with the client receiving messages from other participants over a long period of time. These chat sessions
operate bidirectionally, allowing clients to both send and receive chat messages.

Unlike standard HTTP requests, WebSocket connections can remain open for extended durations, providing a straightforward
interface for data exchange between the client and server through frames. You can think of frames as WebSocket messages
which come in different types (text, binary, close, ping/pong). Because Ktor provides high-level abstractions over the
WebSocket protocol, you can focus on handling text and binary frames, while Ktor takes care of managing other frame
types.

Furthermore, WebSockets are a widely supported technology. All modern browsers support WebSockets out of the box, and
many programming languages and platforms have existing support.

## Prerequisites {id="prerequisites"}

<include from="lib.topic" element-id="plugin_prerequisites"/>

## Create a new Ktor project {id="create_ktor_project"}

To create a base project for the application using the Ktor
plugin, [open IntelliJ IDEA](https://www.jetbrains.com/help/idea/run-for-the-first-time.html) and follow the steps
below:

1. <include from="lib.topic" element-id="new_project_idea"/>
2. In the **New Project** wizard, choose **Ktor** from the list on the left. On the right pane, specify the following
   settings:

   ![New Ktor project](tutorial_websockets_server_new_project.png){width="706" border-effect="rounded"}

    * **Name**: Specify a project name.
    * **Location**: Specify a directory for your project.
    * **Build System**: Make sure that _Gradle Kotlin_ is selected as a [build system](server-dependencies.topic).
    * **Website**: Leave the default `example.com` value as a domain used to generate a package name.
    * **Artifact**: This field shows a generated artifact name.
    * **Ktor version**: Choose the latest Ktor version.
    * **Engine**: Leave the default _Netty_ [engine](Engines.md).
    * **Configuration in**: Choose _HOCON file_ to specify server parameters in
      a [dedicated configuration file](create_server.topic).
    * **Add sample code**: Disable this option to skip adding sample code for plugins.

   Click **Next**.

3. On the next page, add the **Routing** and **WebSockets** plugins:

   ![Ktor plugins](tutorial_websockets_server_new_project_plugins.png){width="706" border-effect="rounded"}

   Click **Create** and wait until IntelliJ IDEA generates a project and installs the dependencies.

## Examine the project {id="project_setup"}

To see the structure of the [generated project](#create_ktor_project), invoke
the [Project view](https://www.jetbrains.com/help/idea/project-tool-window.html):

![Initial project structure](tutorial_websockets_server_project_structure.png){width="515"}

* The `build.gradle.kts` file contains [dependencies](#dependencies) required for a Ktor server and plugins.
* The `main/resources` folder includes [configuration files](#configurations).
* The `main/kotlin` folder contains the generated [source code](#source_code).

### Dependencies {id="dependencies"}

First, open the `build.gradle.kts` file and examine the added dependencies:

```kotlin
```

{src="snippets/tutorial-websockets-server/build.gradle.kts" include-lines="20-23,25-27"}

- `ktor-server-core` adds Ktor's core components to the project.
- `ktor-server-netty` adds the Netty [engine](Engines.md) to the project, allowing you to use server functionality
  without having to rely on an external application container.
- `ktor-server-websockets` allows you to use the [WebSocket plugin](websocket.md), the main communication mechanism for
  the chat.

[//]: # (- `logback-classic` provides an implementation of SLF4J, allowing you to see nicely formatted [logs]&#40;logging.md&#41; in a console.)

- `ktor-server-test-host` and `kotlin-test-junit` allow you to [test](Testing.md) parts of your Ktor application without
  having to use the whole HTTP stack in the process.

### Configurations: application.conf and logback.xml {id="configurations"}

The generated project also includes the `application.conf` and `logback.xml` configuration files located in
the `resources` folder:

* `application.conf` is a configuration file in [HOCON](https://en.wikipedia.org/wiki/HOCON) format. Ktor uses this file
  to determine the port on which it should run, and it also defines the entry point of the application.
   ```
   ```
  {src="snippets/tutorial-websockets-server/src/main/resources/application.conf" style="block"}

  To learn more about how a Ktor server is configured, see the [](Configuration-file.topic) help topic.
* `logback.xml` sets up the basic logging structure for the server. To learn more about logging in Ktor, see
  the [](logging.md) topic.

### Source code {id="source_code"}

The [application.conf](#configurations) file configures the entry point of your application to
be `com.example.ApplicationKt.module`. This corresponds to the `Application.module()` function in `Application.kt`,
which is an application [module](Modules.md):

```kotlin
```

{src="snippets/tutorial-websockets-server/src/main/kotlin/com/example/Application.kt" include-lines="6-11"}

This module, in turn, calls the following extension functions:

* `configureRouting` is a function defined in `plugins/Routing.kt`, which currently doesn't do anything:
   ```kotlin
   fun Application.configureRouting() {
       routing {
       }
   }
   ```

* `configureSockets` is a function defined in `plugins/Sockets.kt`, which installs and configures the `WebSockets`
  plugin:
   ```kotlin
   ```
  {src="snippets/tutorial-websockets-server/src/main/kotlin/com/example/plugins/Sockets.kt" include-lines="12-19,42-43"}

## A first echo server {id="echo_server"}

### Implement an echo server {id="implement_echo_server"}

You will begin by building an “echo” service which accepts WebSocket connections, receives text content, and sends it
back to the client. To implement this service with Ktor, add the following implementation
for `Application.configureSockets()` to `plugins/Sockets.kt`:

```kotlin
import io.ktor.websocket.*
import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*

fun Application.configureSockets() {
    install(WebSockets) {
        // ...
    }
    routing {
        webSocket("/chat") {
            send("You are connected!")
            for(frame in incoming) {
                frame as? Frame.Text ?: continue
                val receivedText = frame.readText()
                send("You said: $receivedText")
            }
        }
    }
}
```

First, Ktor installs the `WebSockets` plugin to the server to enable routing to endpoints responding to the WebSocket
protocol (in this case, the route is `/chat`). Within the scope of the `webSocket` route function, Ktor provides access
to various methods for interacting with the clients (through the `DefaultWebSocketServerSession` receiver type). This
includes convenience methods to send messages and iterate over received messages.

When iterating over the incoming channel, the server checks if the received `Frame` is of type text and only then reads
the text and sends it back to the user with the prefix `"You said:"`.

With this, you have built a fully-functioning echo server.

### Test the application {id="try_echo_server"}

To test the application, use a web-based WebSocket client, such
as [Postman](https://learning.postman.com/docs/sending-requests/supported-api-frameworks/websocket/), to connect to the
echo service, send a message, and receive the echoed reply.

To start the server, click on the gutter icon next to the `main` function in `Application.kt`. After the project has
finished compiling, you should see a confirmation that the server is running in IntelliJ IDEA's Run tool window:

```Bash
Application - Responding at http://0.0.0.0:8080
```

Using a web-based client, you can now connect to `ws://localhost:8080/chat` and make a WebSocket request.

![Echo Test](ktor_websocket_chat_postman.png){width="706"}

Enter a message in the editor pane and send it to the local server. You will then see sent and received messages in the
**Messages** pane, indicating that the echo-server is functioning as intended.

> IntelliJ IDEA Ultimate allows you to test your WebSocket server using
the [HTTP Client](https://www.jetbrains.com/help/idea/http-client-in-product-code-editor.html#websocket).

You now have a solid foundation for establishing bidirectional communication through WebSockets. In the following
chapter, you will expand the application to allow multiple participants to send messages to one another.

## Exchange messages {id="messages"}

To enable message exchange among multiple users, you will ensure that each user's messages are labeled with their
respective usernames. Additionally, you will make sure that messages are sent to all other connected users, effectively
broadcasting them.

### Model connections {id="connections"}

Both of these features require to keep track of the connections the server is holding – to know which user is sending
the messages, and to know who to broadcast them to.

Ktor handles WebSocket connections using a `DefaultWebSocketSession` object, which contains all the required components
for WebSocket communication, such as the `incoming` and `outgoing` channels, convenient communication methods, and more.
To simplify the task of assigning usernames, one solution would be to automatically generate usernames for participants
based on a counter:

Create a new file in the `com.example` package called `Connection.kt` and add the following implementation to it:

```kotlin
```

{src="snippets/tutorial-websockets-server/src/main/kotlin/com/example/Connection.kt"}

Note that `AtomicInteger` is used as a thread-safe data structure for the counter. This ensures that two users will
never receive the same ID for their username – even when their `Connection` objects are created simultaneously on
separate threads.

### Implement connection handling and message propagation {id="connection_handling"}

You can now adjust the server to keep track of the `Connection` objects, and send messages to all connected clients,
prefixed with the correct username. Adjust the implementation of the `routing` block in `plugins/Sockets.kt` to the
following:

```kotlin
```

{src="snippets/tutorial-websockets-server/src/main/kotlin/com/example/plugins/Sockets.kt" include-lines="3-12,19-43"}

The server now stores a (thread-safe) collection of type `Connection`. When a user connects, the server creates
a `Connection` object, which also self-assigns a unique username, and adds it to the collection.

It then sends a message to the user indicating the number of currently connected users. Upon receiving a message from a
user, the server appends a unique identifier prefix associated with the user's `Connection` object and broadcasts it to
all active connections. When the connection is terminated, the client's `Connection` object is removed from the
collection – either gracefully, when the incoming channel gets closed, or with an `Exception` when an unexpected network
interruption occurs between the client and server.

To test the new functionality, run the application by clicking on the gutter icon next to the `main` function
in `Application.kt` and
use [Postman](https://learning.postman.com/docs/sending-requests/supported-api-frameworks/websocket/) to connect
to `ws://localhost:8080/chat`. This time, use two or more separate tabs to validate that messages are exchanged
properly.

<tabs>
<tab title="Client 1">

![Echo Test](ktor_websocket_chat_postman_client_1.png){width="706"}
</tab>
<tab title="Client 2">

![Echo Test](ktor_websocket_chat_postman_client_2.png){width="706"}
</tab>
</tabs>


The finished chat server can now receive and send messages from and to multiple participants.

For the full example of the application,
see [tutorial-websockets-server](https://github.com/ktorio/ktor-documentation/tree/%ktor_version%/codeSnippets/snippets/tutorial-websockets-server).

## What's next

Congratulations on creating a chat application using Kotlin, Ktor and WebSockets.

In the [next tutorial](getting_started_ktor_client_chat.md), you will create a chat client for the server, which will
allow you to send and receive messages directly from the command line. Because the client will also be implemented with
Ktor, you will reuse much of what you just learned about managing WebSockets.

Additionally, you can expand on the server-side functionality. Use the following ideas to improve the application:

- Ask users to enter a username on application startup, and persist this name alongside the `Connection` information on
  the server.
- Implement a `/whisper` command, to allow users to share a message to a certain person only or a select group of
  participants.
