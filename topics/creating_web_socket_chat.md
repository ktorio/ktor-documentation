[//]: # (title: Creating a WebSocket chat)

<microformat>
<p>
<a href="https://github.com/ktorio/ktor-websockets-chat-sample">Template project</a>
</p>
<p>
<a href="https://github.com/ktorio/ktor-websockets-chat-sample/tree/final">Final project</a>
</p>
</microformat>

In this hands-on, we will learn how to create a simple chat application which uses WebSockets. We will develop both the client and server application using [Ktor](https://ktor.io/) – an asynchronous Kotlin framework for creating web applications.

## What we will build

Throughout this tutorial, we will implement a simple chat service, which will consist of two applications:

- The **chat server application** will accept and manage connections from our chat users, receive messages, and distribute them to all connected clients.
- The **chat client application** will allow users to join a common chat server, send messages to other users, and see messages from other users in the terminal.

![App in action](app_in_action.png){animated="true" width="674"}

For both parts of the application, we will make use of Ktor's support for [WebSockets](websocket.md). Because Ktor is both a server-side and client-side framework, we will be able to reuse the knowledge we acquire building the chat server when it comes to building the client.

After completing this hands-on, you should have a basic understanding of how to work with WebSockets using Ktor and Kotlin, how to exchange information between the client and server, and get a basic idea of how to manage multiple connections at the same time.

## Why WebSockets?

WebSockets are a great fit for applications like chats or simple games. Chat sessions are usually long-lived, with the client receiving messages from other participants over a long period of time. Chat sessions are also bidirectional – clients want to send chat messages, and see chat messages from others.

Unlike regular HTTP requests, WebSocket connections can be kept open for a long time and have an easy interface for exchanging data between the client and server in the form of frames. We can think of frames as WebSocket messages which come in different types (text, binary, close, ping/pong). Because Ktor provides high-level abstractions over the WebSocket protocol, we can even concentrate on text and binary frames, and leave the handling of other frames to the framework.

WebSockets are also a widely supported technology. All modern browsers can work with WebSockets out of the box, and frameworks to work with WebSockets exist in many programming languages and on many platforms.

Now that we have confidence in the technology we want to use for the implementation of our project, let’s start with the set-up!


## Project setup

Because our application will be two independent parts (chat server and chat client), we structure our application as two separate Gradle projects. Since these two projects are completely independent, they could be created manually, via the online [Ktor Project Generator](https://start.ktor.io/#), or the [plugin for IntelliJ IDEA](intellij-idea.xml).

To skip over these configuration steps, a starter template is available for this specific tutorial, which includes all configuration and required dependencies for our two projects already.

[Please clone the repository from GitHub, and open it in IntelliJ IDEA.](https://github.com/ktorio/ktor-websockets-chat-sample)

The template repository contains two barebones Gradle projects for us to build our project: the `client` and `server` projects. Both of them are already preconfigured with the dependencies that we will need throughout the hands-on, so you **don't need to make any changes to the Gradle configuration.**

It might still be beneficial to understand what artifacts are being used for the application, so let's have a closer look at the two projects and the dependencies they rely on.

### Understanding the project configuration

Our two projects both come with their individual sets of configuration files. Let's examine each one of them a bit closer.

#### Dependencies for the `server` project

The server application specifies three dependencies in its `server/build.gradle.kts` file:

```kotlin
dependencies {
    implementation("io.ktor:ktor-server-netty:$ktor_version")
    implementation("io.ktor:ktor-server-websockets:$ktor_version")
    implementation("ch.qos.logback:logback-classic:$logback_version")
}
```

- `ktor-server-netty` adds Ktor together with the Netty engine, allowing us to use server functionality without having to rely on an external application container.
- `ktor-server-websockets` allows us to use the [WebSocket Ktor plugin](websocket.md), the main communication mechanism for our chat.
- `logback-classic` provides an implementation of [SLF4J](http://www.slf4j.org/), allowing us to see nicely formatted logs in our console.

#### Configuration for the `server` project

Ktor uses a [HOCON](https://github.com/lightbend/config/blob/master/HOCON.md) configuration file to set up its basic behavior, like its entry point and deployment port. It can be found at `server/src/main/resources/application.conf`:

```kotlin
ktor {
    deployment {
        port = 8080
    }
    application {
        modules = [ com.jetbrains.handson.chat.ApplicationKt.module ]
    }
}
```

Alongside this file is also a barebones `logback.xml` file, which sets up the `logback-classic` implementation.

#### Dependencies for the `client` project

The client application specifies two dependencies in its `client/build.gradle.kts` file:

```kotlin
dependencies {
    implementation("io.ktor:ktor-client-websockets:$ktor_version")
    implementation("io.ktor:ktor-client-cio:$ktor_version")
}
```

- `ktor-client-cio` provides a [client implementation of Ktor](http-client_engines.md#cio) on top of coroutines ("Coroutine-based I/O").
- `ktor-client-websockets` is the counterpart to the `ktor-server-websockets` dependency on the server, and allows us to consume [WebSockets from the client](websocket_client.md) with the same API as the server.

Now that we have some understanding of the parts that will make our project run, it's time to start building our project! Let's start by implementing a simple WebSocket echo server!



## A first echo server

### Implementing an echo server

Let’s start our server development journey by building a small “echo” service which accepts WebSocket connections, receives text content, and sends it back to the client. We can implement this service with Kotlin and Ktor by adding the following implementation for `Application.module()` to `server/src/main/kotlin/com/jetbrains/handson/chat/server/Application.kt`:

```kotlin
fun Application.module() {
    install(WebSockets)
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

### Trying out the echo server

For now, we can use a web-based WebSocket client to connect to our echo service, send a message, and receive the echoed reply. Once we have finished implementing the server-side functionality, we will also build our own chat client in Kotlin.

Let's start the server by pressing the play button in the gutter next to the definition of `fun main` in our server's Application.kt. After our project has finished compiling, we should see a confirmation that the server is running in IntelliJ IDEAs Run tool window: 

`Application - Responding at http://0.0.0.0:8080`. 

To try out the service, we can use [Postman](https://learning.postman.com/docs/sending-requests/supported-api-frameworks/websocket/) to connect to `ws://localhost:8080/chat` and make a WebSocket request.

![Echo Test](ktor_websocket_chat_postman.png){width="706"}

Then, we can enter any kind of message in the editor pane, and send it to our local server. If everything has gone according to plan, we should see sent and received messages and in the **Messages** pane, indicating that our echo-server is functioning just as intended.

With this, we now have a solid foundation for bidirectional communication through WebSockets. Next, let's expand our program more closely resemble a chat server, allowing multiple participants to share messages with others.



## Exchanging messages

Let’s turn our echo server into a real chat server! To do this, we need to make sure messages from the same user are all tagged with the same username. Also, we want to make sure that messages are actually broadcast – sent to all other connected users.

### Modeling connections

Both of these features need us to be able to keep track of the connections our server is holding – to know which user is sending the messages, and to know who to broadcast them to.

Ktor manages a WebSocket connection with an object of the type `DefaultWebSocketSession`, which contains everything required for communicating via WebSockets, including the `incoming` and `outgoing` channels, convenience methods for communication, and more. For now, we can simplify the problem of assigning usernames, and just give each participant an auto-generated username based on a counter. Add the following implementation to a new file in `server/src/main/kotlin/com/jetbrains/handson/chat/server/` called `Connection.kt`:

```kotlin
import io.ktor.http.cio.websocket.*
import java.util.concurrent.atomic.*

class Connection(val session: DefaultWebSocketSession) {
    companion object {
        var lastId = AtomicInteger(0)
    }
    val name = "user${lastId.getAndIncrement()}"
}
```

Note that we are using `AtomicInteger` as a thread-safe data structure for the counter. This ensures that two users will never receive the same ID for their username – even when their two Connection objects are created simultaneously on separate threads.

### Implementing connection handling and message propagation

We can now adjust our server's program to keep track of our Connection objects, and send messages to all connected clients, prefixed with the correct username. Adjust the implementation of the `routing` block in `server/src/main/kotlin/com/jetbrains/handson/chat/server/Application.kt` to the following code:

```kotlin
routing {
    val connections = Collections.synchronizedSet<Connection?>(LinkedHashSet())
    webSocket("/chat") {
        println("Adding user!")
        val thisConnection = Connection(this)
        connections += thisConnection
        try {
            send("You are connected! There are ${connections.count()} users here.")
            for (frame in incoming) {
                frame as? Frame.Text ?: continue
                val receivedText = frame.readText()
                val textWithUsername = "[${thisConnection.name}]: $receivedText"
                connections.forEach {
                    it.session.send(textWithUsername)
                }
            }
        } catch (e: Exception) {
            println(e.localizedMessage)
        } finally {
            println("Removing $thisConnection!")
            connections -= thisConnection
        }
    }
}
```

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


As we can see, our finished chat server can now receive and send messages with multiple participants. Feel free to open a few more tabs and play around with what we have built here!

In the next chapter, we will write a Kotlin chat client for our server, which will allow us to send and receive messages directly from the command line. Because our clients will also be implemented using Ktor, we will get to reuse much of what we learned about managing WebSockets in Kotlin.


## Creating the chat client

Because we use Ktor as a WebSocket client library, the code and methods we can use are very similar to those on the server. We can build a first, very simple implementation of sending and receiving messages by changing the file `client/src/main/kotlin/com/jetbrains/handson/chat/client/ChatClient.kt` to look as follows:

```kotlin
import io.ktor.client.*
import io.ktor.client.features.websocket.*
import io.ktor.http.*
import io.ktor.http.cio.websocket.*
import io.ktor.util.*
import kotlinx.coroutines.*

fun main() {
    val client = HttpClient {
        install(WebSockets)
    }
    runBlocking {
        client.webSocket(method = HttpMethod.Get, host = "127.0.0.1", port = 8080, path = "/chat") {
            while(true) {
                val othersMessage = incoming.receive() as? Frame.Text ?: continue
                println(othersMessage.readText())
                val myMessage = readLine()
                if(myMessage != null) {
                    send(myMessage)
                }
            }
        }
    }
    client.close()
    println("Connection closed. Goodbye!")
}
```

Here, we first create an `HttpClient` and set up Ktor's `WebSocket` plugin (the analog of installing the `WebSocket` plugin in our server application's module in an earlier chapter). Functions in Ktor responsible for making network calls use the suspension mechanism from Kotlin's coroutines, so we wrap our network-related code in a `runBlocking` block. Inside the WebSocket handler, we once again process incoming messages and send outgoing messages: we ignore frames which do not contain text, read incoming text, and send the user input to the server.

However, this "straightforward" implementation actually contains an issue which prevents it from being used as a proper chat client: when invoking `readLine()`, our program waits until the user enters a message. During this time, we can't see any messages which have been typed out by other users. Likewise, because we invoke `readLine()` after every received message, we would only ever see one new message at a time.

You can also validate this for yourself: with the server process running, start two instances of the chat client by clicking play icon in the gutter in `client/src/main/kotlin/com/jetbrains/handson/chat/client/ChatClient.kt`. Use the tabs in the Run tool window to navigate between the two client instances, and send some messages back and forth.

![Run tool window](image-20201111191815343.png){width="1207"}

Let's address this issue, and build a better solution!

### Improving our solution

A better structure for our chat client would be to separate the message output and input mechanisms, allowing them to run concurrently: when new messages arrive, they are printed immediately, but our users can still start composing a new chat message at any point.

We know that to output messages, we need to be able to receive them from the WebSocket's `incoming` channel, and print them to the command line. Let’s add a function called `outputMessages()` to the `ChatClient.kt` file with the following implementation for this functionality:

```kotlin
suspend fun DefaultClientWebSocketSession.outputMessages() {
    try {
        for (message in incoming) {
            message as? Frame.Text ?: continue
            println(message.readText())
        }
    } catch (e: Exception) {
        println("Error while receiving: " + e.localizedMessage)
    }
}
```

Because the function operates in the context of a `DefaultClientWebSocketSession`, we define `outputMessages()` as an extension function on the type. We also don’t forget to add the `suspend` modifier – because iterating over the `incoming` channel suspends the coroutine while no new message is available.

Next, let’s define a second function which allows the user to input text. Add a function called `inputMessages()` in `ChatClient.kt` with the following implementation

```kotlin
suspend fun DefaultClientWebSocketSession.inputMessages() {
    while (true) {
        val message = readLine() ?: ""
        if (message.equals("exit", true)) return
        try {
            send(message)
        } catch (e: Exception) {
            println("Error while sending: " + e.localizedMessage)
            return
        }
    }
}
```

Once again defined as a suspending extension function on `DefaultClientWebSocketSession`, this function's only job is to read text from the command line and send it to the server, or to return when the user types `exit`.

Where we previously had one loop which had to take care of reading input and printing output, we now have separated these tasks into their own functions, which can operate independently of each other.

#### Wiring it together

Let's make use of our two new functions! We can call them inside the body of our WebSocket handler by changing the code of our `main()` method in `ChatClient.kt` to the following:

```kotlin
fun main() {
    val client = HttpClient {
        install(WebSockets)
    }
    runBlocking {
        client.webSocket(method = HttpMethod.Get, host = "127.0.0.1", port = 8080, path = "/chat") {
            val messageOutputRoutine = launch { outputMessages() }
            val userInputRoutine = launch { inputMessages() }
            
            userInputRoutine.join() // Wait for completion; either "exit" or error
            messageOutputRoutine.cancelAndJoin()
        }
    }
    client.close()
    println("Connection closed. Goodbye!")
}
```

This new implementation improves the behavior of our application: Once the connection to our chat server is established, we use the `launch` function from Kotlin's Coroutines library to launch the two long-running functions `outputMessages()` and `inputMessages()` on a new coroutine (without blocking the current thread). The launch function also returns a `Job` object for both of them, which we use to keep the program running until the user types `exit` or encounters a network error when trying to send a message. After `inputMessages()` has returned, we cancel the execution of the `outputMessages()` function, and `close` the client.

Until this happens, both input and output can happily happen concurrently, with new messages being received while the client sits idle, and the option to start composing a new message at any point.

#### Let's give it a try!

We have now finished implementing our WebSocket-based chat client with Kotlin and Ktor. To celebrate our success, let’s give it a try! With the chat server running, start some instances of the chat client using the play button, and talk to yourself! Even if you send multiple messages right after each other, they should be correctly displayed on all connected clients.

You might still notice some smaller usability issues caused by the limitations of terminal input, like incoming messages overwriting messages which are currently being composed. Managing more complex terminal user interfaces is outside the scope of this tutorial, though, and as such left as an exercise to the reader 😉.

[You can also find the final version of the project on the final branch on GitHub.](https://github.com/ktorio/ktor-websockets-chat-sample/tree/final)

![App in action](app_in_action.png){animated="true" width="674"}

That's it for this hands-on tutorial on WebSockets with Ktor – time to congratulate yourself for building a whole application! If you're looking for some inspiration of where to take this project next, as well as related materials, continue to the next section.



## What's next

Congratulations on finishing this tutorial on creating a chat application using Kotlin, Ktor & WebSockets. We now have a basic command-line application which allows multiple clients to have a conversation over the network in a shared chat.

### Feature requests

At this point, we have implemented the absolute basics for a chat service, both on client and server side. If you want to, you can keep expanding on this project. To get you started, here are a few ideas of how to improve the application, in no particular order:

- **Custom usernames!** Instead of automatically assigning numbers to your users, you can ask users on application startup to enter a username, and persist this name alongside the Connection information on the server.
- **Private messages!** If your users have something to say, but don't want to share it with the whole group, you could implement a `/whisper` command, which only relays the message to a certain person or select group of participants. You could even expand this functionality to handle more generic **chat commands!**
- **Nicer UI!** So far, the client's user interface is very rudimentary, with only text input and output. If you're feeling adventurous, you can pick up a framework like [TornadoFX](https://tornadofx.io/), [Compose for Desktop](https://www.jetbrains.com/lp/compose/), or other, and try implementing a fancy user interface for the chat.
- **Mobile app!** The Ktor client libraries are also available for mobile applications. Feel free to try integrating what you have learned in this tutorial in the context of an Android application, and build the next big mobile chat product!

### Learning more about Ktor

You can find more hands-on tutorials on Ktor and its features on this site. For in-depth information about the framework, including further demo projects, check out [ktor.io](https://ktor.io/).

### Community, help and troubleshooting

To find more information about Ktor, check out the official website. If you run into trouble, check out the [Ktor issue tracker](https://youtrack.jetbrains.com/issues/KTOR) – and if you can't find your problem, don't hesitate to file a new issue.

You can also join the official [Kotlin Slack](https://surveys.jetbrains.com/s3/kotlin-slack-sign-up). We have channels for #ktor and more available, and a helpful community that supports each other for Kotlin related problems.
