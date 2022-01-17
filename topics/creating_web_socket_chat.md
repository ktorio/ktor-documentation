[//]: # (title: Creating a WebSocket chat)

<microformat>
<var name="example_name" value="tutorial-websockets-server"/>
<include src="lib.xml" include-id="download_example"/>
<p>
<b>Used plugins</b>: <a href="Routing_in_Ktor.md">Routing</a>, <a href="websocket.md">WebSockets</a>
</p>
</microformat>

<excerpt>In this tutorial, you will learn how to create a simple chat application that uses WebSockets.</excerpt>

In this tutorial, you will learn how to create a simple chat application that uses WebSockets.

Throughout this tutorial, we will implement a simple chat service, which will consist of two applications:

- The **chat server application** will accept and manage connections from our chat users, receive messages, and distribute them to all connected clients.
- The **chat client application** will be implemented in this tutorial: [](getting_started_ktor_client_chat.md). The client application will allow users to join a common chat server, send messages to other users, and see messages from other users in the terminal.

![App in action](app_in_action.png){animated="true" width="674"}

For both parts of the application, we will make use of Ktor's support for [WebSockets](websocket.md). Because Ktor is both a server-side and client-side framework, we will be able to reuse the knowledge we acquire building the chat server when it comes to building the client.

After completing this tutorial, you should have a basic understanding of how to work with WebSockets using Ktor, how to exchange information between the client and server, and get a basic idea of how to manage multiple connections at the same time.

## Why WebSockets? {id="why"}

WebSockets are a great fit for applications like chats or simple games. Chat sessions are usually long-lived, with the client receiving messages from other participants over a long period of time. Chat sessions are also bidirectional – clients want to send chat messages, and see chat messages from others.

Unlike regular HTTP requests, WebSocket connections can be kept open for a long time and have an easy interface for exchanging data between the client and server in the form of frames. We can think of frames as WebSocket messages which come in different types (text, binary, close, ping/pong). Because Ktor provides high-level abstractions over the WebSocket protocol, we can even concentrate on text and binary frames, and leave the handling of other frames to the framework.

WebSockets are also a widely supported technology. All modern browsers can work with WebSockets out of the box, and frameworks to work with WebSockets exist in many programming languages and on many platforms.

Now that we have confidence in the technology we want to use for the implementation of our project, let’s start with the set-up!


## Prerequisites {id="prerequisites"}
<include src="lib.xml" include-id="plugin_prerequisites"/>


## Create a new Ktor project {id="create_ktor_project"}

To create a base project for our application using the Ktor plugin, [open IntelliJ IDEA](https://www.jetbrains.com/help/idea/run-for-the-first-time.html) and follow the steps below:

1. <include src="lib.xml" include-id="new_project_idea"/>
2. In the **New Project** wizard, choose **Ktor** from the list on the left. On the right pane, specify the following settings:
   ![New Ktor project](tutorial_websockets_server_new_project.png){width="744"}
   * **Name**: Specify a project name.
   * **Location**: Specify a directory for your project.
   * **Build System**: Make sure that _Gradle Kotlin_ is selected as a [build system](server-dependencies.xml).
   * **Website**: Leave the default `com.example` value as a domain used to generate a package name.
   * **Artifact**: This field shows a generated artifact name.
   * **Ktor Version**: Choose the latest Ktor version.
   * **Engine**: Leave the default _Netty_ [engine](Engines.md).
   * **Configuration in**: Choose _HOCON file_ to specify server parameters in a [dedicated configuration file](create_server.xml).
   * **Add sample code**: Disable this option to skip adding sample code for plugins.
   
   Click **Next**.

3. On the next page, add the **Routing** and **WebSockets** plugins:
   ![Ktor plugins](tutorial_websockets_server_new_project_plugins.png){width="744"}

   Click **Finish** and wait until IntelliJ IDEA generates a project and installs the dependencies.


## Examine the project {id="project_setup"}

To look at the structure of the [generated project](#create_ktor_project), let's invoke the [Project view](https://www.jetbrains.com/help/idea/project-tool-window.html):
![Initial project structure](tutorial_websockets_server_project_structure.png){width="515"}

* The `build.gradle.kts` file contains [dependencies](#dependencies) required for a Ktor server and plugins.
* The `main/resources` folder includes [configuration files](#configurations).
* The `main/kotlin` folder contains the generated [source code](#source_code).


### Dependencies {id="dependencies"}

First, let's open the `build.gradle.kts` file and examine added dependencies:
```kotlin
```
{src="snippets/tutorial-websockets-server/build.gradle.kts" lines="19-26"}

Let's briefly go through these dependencies one by one:

- `ktor-server-core` adds Ktor's core components to our project.
- `ktor-server-netty` adds the Netty [engine](Engines.md) to our project, allowing us to use server functionality without having to rely on an external application container.
- `ktor-server-websockets` allows us to use the [WebSocket plugin](websocket.md), the main communication mechanism for our chat.
- `logback-classic` provides an implementation of SLF4J, allowing us to see nicely formatted [logs](logging.md) in a console.
- `ktor-server-test-host` and `kotlin-test-junit` allow us to [test](Testing.md) parts of our Ktor application without having to use the whole HTTP stack in the process.

### Configurations: application.conf and logback.xml {id="configurations"}

The generated project also includes the `application.conf` and `logback.xml` configuration files located in the `resources` folder:
* `application.conf` is a configuration file in [HOCON](https://en.wikipedia.org/wiki/HOCON) format. Ktor uses this file to determine the port on which it should run, and it also defines the entry point of our application.
   ```
   ```
  {src="snippets/tutorial-websockets-server/src/main/resources/application.conf" style="block"}

  If you'd like to learn more about how a Ktor server is configured, check out the [](Configurations.xml) help topic.
* `logback.xml` sets up the basic logging structure for our server. If you'd like to learn more about logging in Ktor, check out the [](logging.md) topic.

### Source code {id="source_code"}

The [application.conf](#configurations) configures the entry point of our application to be `com.example.ApplicationKt.module`. This corresponds to the `Application.module()` function in `Application.kt`, which is an application [module](Modules.md):

```kotlin
```
{src="snippets/tutorial-websockets-server/src/main/kotlin/com/example/Application.kt" lines="6-11"}

This module, in turn, calls the following extension functions:

* `configureRouting` is a function defined in `plugins/Routing.kt`, which is currently doesn't do anything:
   ```kotlin
   fun Application.configureRouting() {
       routing {
       }
   }
   ```

* `configureSockets` is a function defined in `plugins/Sockets.kt`, which installs and configures the `WebSockets` plugin:
   ```kotlin
   ```
  {src="snippets/tutorial-websockets-server/src/main/kotlin/com/example/plugins/Sockets.kt" lines="12-18,43"}




## A first echo server {id="echo_server"}

### Implement an echo server {id="implement_echo_server"}

Let’s start our server development journey by building a small “echo” service which accepts WebSocket connections, receives text content, and sends it back to the client. We can implement this service with Ktor by adding the following implementation for `Application.configureSockets()` to `plugins/Sockets.kt`:

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

We first enable WebSocket-related functionality provided by the Ktor framework by installing the `WebSockets` Ktor plugin. This allows us to define endpoints in our routing which respond to the WebSocket protocol (in our case, the route is `/chat`). Within the scope of the `webSocket` route function, we can use various methods for interacting with our clients (via the `DefaultWebSocketServerSession` receiver type). This includes convenience methods to send messages and iterate over received messages.

Because we are only interested in text content, we skip any non-text `Frame`s we receive when iterating over the incoming channel. We can then read any received text, and send it right back to the user with the prefix `"You said:"`.

At this point, we have already built a fully-functioning echo server – a little service that just sends back whatever we send it. Let's try it out!

### Try out the echo server {id="try_echo_server"}

For now, we can use a web-based WebSocket client to connect to our echo service, send a message, and receive the echoed reply. Once we have finished implementing the server-side functionality, we will also build our own chat client in Kotlin.

Let's start the server by pressing the Play button in the gutter next to the definition of `fun main` in our server's `Application.kt`. After our project has finished compiling, we should see a confirmation that the server is running in IntelliJ IDEAs Run tool window: 

```Bash
Application - Responding at http://0.0.0.0:8080
```

To try out the service, we can use [Postman](https://learning.postman.com/docs/sending-requests/supported-api-frameworks/websocket/) to connect to `ws://localhost:8080/chat` and make a WebSocket request.

![Echo Test](ktor_websocket_chat_postman.png){width="706"}

Then, we can enter any kind of message in the editor pane, and send it to our local server. If everything has gone according to plan, we should see sent and received messages and in the **Messages** pane, indicating that our echo-server is functioning just as intended.

With this, we now have a solid foundation for bidirectional communication through WebSockets. Next, let's expand our program more closely resemble a chat server, allowing multiple participants to share messages with others.



## Exchange messages {id="messages"}

Let’s turn our echo server into a real chat server! To do this, we need to make sure messages from the same user are all tagged with the same username. Also, we want to make sure that messages are actually broadcast – sent to all other connected users.

### Model connections {id="connections"}

Both of these features need us to be able to keep track of the connections our server is holding – to know which user is sending the messages, and to know who to broadcast them to.

Ktor manages a WebSocket connection with an object of the type `DefaultWebSocketSession`, which contains everything required for communicating via WebSockets, including the `incoming` and `outgoing` channels, convenience methods for communication, and more. For now, we can simplify the problem of assigning usernames, and just give each participant an auto-generated username based on a counter. 

Add the following implementation to a new file in the `com.example` package called `Connection.kt`:

```kotlin
```
{src="snippets/tutorial-websockets-server/src/main/kotlin/com/example/Connection.kt"}

Note that we are using `AtomicInteger` as a thread-safe data structure for the counter. This ensures that two users will never receive the same ID for their username – even when their two Connection objects are created simultaneously on separate threads.

### Implement connection handling and message propagation {id="connection_handling"}

We can now adjust our server's program to keep track of our Connection objects, and send messages to all connected clients, prefixed with the correct username. Adjust the implementation of the `routing` block in `plugins/Sockets.kt` to the following code:

```kotlin
```
{src="snippets/tutorial-websockets-server/src/main/kotlin/com/example/plugins/Sockets.kt" lines="3-12,19-43"}

Our server now stores a (thread-safe) collection of `Connection`s. When a user connects, we create their `Connection` object (which also assigns itself a unique username), and add it to the collection. We then greet our user and let them know how many users are currently connecting. When we receive a message from the user, we prefix it with the unique name associated with their `Connection` object, and send it to all currently active connections. Finally, we remove the client's `Connection` object from our collection when the connection is terminated – either gracefully, when the incoming channel gets closed, or with an `Exception` when the network connection between client and server gets interrupted unexpectedly.

To see that our server is now behaving correctly – assigning usernames and broadcasting them to everybody connected – we can once again run our application using the play button in the gutter and use [Postman](https://learning.postman.com/docs/sending-requests/supported-api-frameworks/websocket/) to connect to `ws://localhost:8080/chat`. This time, we can use two separate tabs to validate that messages are exchanged properly.

<tabs>
<tab title="Client 1">

![Echo Test](ktor_websocket_chat_postman_client_1.png){width="706"}
</tab>
<tab title="Client 2">

![Echo Test](ktor_websocket_chat_postman_client_2.png){width="706"}
</tab>
</tabs>


As we can see, our finished chat server can now receive and send messages with multiple participants. Feel free to open a few more tabs and play around with what we have built here! We have included the final state of the chat server in the [codeSnippets](https://github.com/ktorio/ktor-documentation/tree/%current-branch%/codeSnippets) project: [tutorial-websockets-server](https://github.com/ktorio/ktor-documentation/tree/%current-branch%/codeSnippets/snippets/tutorial-websockets-server).

In the [next tutorial](getting_started_ktor_client_chat.md), we will write a chat client for our server, which will allow us to send and receive messages directly from the command line. Because our clients will also be implemented using Ktor, we will get to reuse much of what we learned about managing WebSockets in Kotlin.



## What's next

Congratulations on finishing this tutorial on creating a chat application using Kotlin, Ktor & WebSockets. We now have a basic command-line application which allows multiple clients to have a conversation over the network in a shared chat.

### Feature requests

At this point, we have implemented the absolute basics for a chat service, both on client and server side. If you want to, you can keep expanding on this project. To get you started, here are a few ideas of how to improve the application, in no particular order:

- **Custom usernames!** Instead of automatically assigning numbers to your users, you can ask users on application startup to enter a username, and persist this name alongside the Connection information on the server.
- **Private messages!** If your users have something to say, but don't want to share it with the whole group, you could implement a `/whisper` command, which only relays the message to a certain person or select group of participants. You could even expand this functionality to handle more generic **chat commands!**
