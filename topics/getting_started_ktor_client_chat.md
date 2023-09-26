[//]: # (title: Creating a WebSocket chat client)

<show-structure for="chapter" depth="2"/>

<tldr>
<var name="example_name" value="tutorial-websockets-client"/>
<include from="lib.topic" element-id="download_example"/>
<p>
<b>Used plugins</b>: <a href="websocket_client.md">WebSockets</a>
</p>
</tldr>

<link-summary>
Learn how to create a client chat application that uses WebSockets.
</link-summary>

This is the second part of the [](creating_web_socket_chat.md) tutorial.

In this section, you will learn how to create a Ktor client chat application that
uses [WebSockets](websocket_client.md). The application will allow users to join a common chat server, send messages to
users, and read messages from users in the terminal.

## Prerequisites {id="prerequisites"}

<include from="lib.topic" element-id="client_prerequisites"/>

* To test the client, you will need a running server application. Follow the preceding tutorial
  for [](creating_web_socket_chat.md) or download the code
  example [tutorial-websockets-server](https://github.com/ktorio/ktor-documentation/tree/2.3.4/codeSnippets/snippets/tutorial-websockets-server).

## Create a new project {id="new-project"}

To create a new project for the WebSocket chat
client, [open IntelliJ IDEA](https://www.jetbrains.com/help/idea/run-for-the-first-time.html) and follow
the steps below:

1. <include from="lib.topic" element-id="new_project_idea"/>
2. In the **New Project** wizard, choose **Kotlin Multiplatform** from the list on the left. On the right pane, specify
   the following settings:

   ![Kotlin Multiplatform](ktor_idea_new_gradle_project_settings_chat.png){width="706"}

   <include from="getting_started_ktor_client.topic" element-id="kotlin_app_settings"/>

   Click **Next**.

3. On the next page, change **Test framework** to _None_.

   ![Kotlin Gradle Project Settings](ktor_idea_new_gradle_project_settings_chat_2.png){width="706"}

4. Click **Finish** and wait until IntelliJ IDEA generates a project and installs the dependencies.

## Configure the build script {id="build-script"}

Next, you will configure the build script by adding the required project dependencies and tasks.

### Add client dependencies {id="add-client-dependencies"}

1. Open the `gradle.properties` file and add the following line to specify the Ktor version:
   ```kotlin
   ktor_version=%ktor_version%
   ```
   {interpolate-variables="true"}

   <include from="getting_started_ktor_client.topic" element-id="eap-note"/>

2. Open the `build.gradle.kts` file and add the following artifacts to the `dependencies` block:
   ```kotlin
   ```
   {src="snippets/tutorial-websockets-client/build.gradle.kts" include-lines="1-2,21-25"}

### Add the JavaExec task {id="java-exec"}

In order to correctly handle the user's input when the application is running with Gradle, the `JavaExec` task is
required.

In the `build.gradle.kts` file, add the `JavaExec` task and specify `standardInput`:

```kotlin
```

{src="snippets/tutorial-websockets-client/build.gradle.kts" include-lines="17-19"}

Click the **Load Gradle Changes** icon in the top right corner of the `build.gradle.kts` file to install the
dependencies.

![Load Gradle Changes](client_websockets_load_gradle_changes_name.png){width="706"}

## Create the chat client {id="create-chat-client"}

Now that all dependencies are in place, you can implement the logic for the client in the `src/main/kotlin/Main.kt`
file.

### First implementation {id="first-implementation"}

Open the `src/main/kotlin/Main.kt` file and add the following code for sending and receiving messages:

```kotlin
import io.ktor.client.*
import io.ktor.client.plugins.websocket.*
import io.ktor.http.*
import io.ktor.websocket.*
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
                val myMessage = readlnOrNull()
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

Here, you create an `HttpClient` and install Ktor's `WebSockets` plugin to enable connections to endpoints responding to
the WebSocket protocol.

Functions in Ktor responsible for making network calls use the suspension mechanism from Kotlin's coroutines. Therefore,
all network-related code is wrapped in
a [runBlocking](https://kotlinlang.org/api/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines/run-blocking.html)
block.

Inside the WebSocket session handler, the client prints the received `Frame` only if it is of text type. Then, it reads
the user's input and sends it to the server.

Although this solution works, it has one big flaw: when invoking `readLine()`, the program waits until the user enters a
message. During this time, incoming messages from other users won't be read. Similarly, invoking `readLine()` after
every received message means the user would only ever see one new message at a time.

In the next section, you will improve the solution by separating the message output and input mechanisms.

### Improved solution {id="improved-solution"}

A better solution for the chat client would be to separate the message output and input mechanisms, allowing them to run
concurrently. This means that when new messages arrive they can be displayed immediately, and users can start composing
a new chat message at the same time without interruption.

To receive messages from the WebSocket's `incoming` channel, and print them to the console, add a function
called `outputMessages()` to the `Main.kt` file with the following implementation:

```kotlin
```

{src="snippets/tutorial-websockets-client/src/main/kotlin/Main.kt" include-lines="24-33"}

Because the function operates in the context of a `DefaultClientWebSocketSession`, `outputMessages()` is an extension
function on the type. By using the `suspend` modifier, iterating over the `incoming` channel suspends the coroutine
while no new message is available.

To allow the user to input text, create a new function called `inputMessages()` in `Main.kt` with the following
implementation:

```kotlin
```

{src="snippets/tutorial-websockets-client/src/main/kotlin/Main.kt" include-lines="35-46"}

Once again defined as a suspending extension function on `DefaultClientWebSocketSession`, this function reads text from
the command line and either sends it to the server or returns if the user types `exit`.

Now, instead of reading and printing output within the same loop, the client can utilise the two functions by allowing
them to operate independently of each other.

### Wire it together

Navigate to the `main()` method in `Main.kt` and update the code to the following:

```kotlin
```

{src="snippets/tutorial-websockets-client/src/main/kotlin/Main.kt" include-lines="7-22"}

This new implementation improves the behavior of the application: Once the connection to the chat server is established,
the `launch` function from [Kotlin's Coroutines library](https://kotlinlang.org/docs/coroutines-overview.html) is used
to launch the two long-running functions `outputMessages()` and `inputMessages()` on a new coroutine (without blocking
the current thread).
The launch function also returns a `Job` object for both of them, which keeps the program running until the user
types `exit` or encounters a network error when trying to send a message.
After `inputMessages()` has returned, the client cancels the execution of `outputMessages()`, and closes the connection.

While the connection is still present, both input and output can execute concurrently, with new messages being received
while the client sits idle. In this way the user has the option to start composing a new message at any point.

### Test the application

Now that you have finished implementing your WebSocket-based chat client with Kotlin and Ktor, it is time to validate
your solution by running the application.

With the chat server running, start two instances of the chat client. You can do this by creating two identical **MainKt
** [run configurations](https://www.jetbrains.com/help/idea/run-debug-configuration.html), which can be run separately.
Even if you send multiple messages right after each other, they should be correctly displayed on all connected clients.

![App in action](app_in_action.gif){preview-src="app_in_action.png" width="706"}

For the full example of the chat application,
see [tutorial-websockets-client](https://github.com/ktorio/ktor-documentation/tree/%ktor_version%/codeSnippets/snippets/tutorial-websockets-client).

## What's next

Congratulations on creating a chat service using WebSockets with Ktor.
At this point, you have implemented a functional chat service, both on the client and server side. You can keep
expanding on this project. To get you started, here are a few ideas of how to improve the application:

- Integrate what you have learned in this tutorial in the context of a multi-platform application. Learn
  about [](getting_started_ktor_client_multiplatform_mobile.md).
- Improve the User Interface for the chat
  using [TornadoFX](https://tornadofx.io/), [Compose for Desktop](https://www.jetbrains.com/lp/compose/) or another
  framework of your choice.