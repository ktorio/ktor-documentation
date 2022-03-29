[//]: # (title: Creating a WebSocket chat client)

<microformat>
<var name="example_name" value="tutorial-websockets-client"/>
<include src="lib.xml" include-id="download_example"/>
</microformat>

<excerpt>
Learn how to create a client chat application that uses WebSockets.
</excerpt>

In this tutorial, you will learn how to create a client chat application that uses WebSockets. The client application will allow users to join a common chat server, send messages to other users, and see messages from other users in the terminal.

To learn how to create a chat server, see the [](creating_web_socket_chat.md) tutorial.

## Prerequisites {id="prerequisites"}
<include src="lib.xml" include-id="client_prerequisites"/>

## Create a new project {id="new-project"}

To create a WebSocket chat client, we need to create a new project first. [Open IntelliJ IDEA](https://www.jetbrains.com/help/idea/run-for-the-first-time.html) and follow
the steps below:

1. <include src="lib.xml" include-id="new_project_idea"/>
2. In the **New Project** wizard, choose **Kotlin Multiplatform** from the list on the left. On the right pane, specify the following settings:
   
   <include src="getting_started_ktor_client.xml" include-id="kotlin_app_settings"/>

   Click **Next**.

3. On the next page, change **Test framework** to _None_, click **Finish** and wait until IntelliJ IDEA generates a project and installs the dependencies.


## Configure the build script {id="build-script"}
The next thing we need is to configure the build script:
- Add dependencies required for a Ktor client.
- Add the `JavaExec` task to correctly handle a user's input when the application is running using Gradle.

### Add client dependencies {id="add-client-dependencies"}

1. Open the `gradle.properties` file and add the following line to specify the Ktor version:
   ```kotlin
   ktor_version=%ktor_version%
   ```
   {interpolate-variables="true"}

   <include src="getting_started_ktor_client.xml" include-id="eap-note"/>

2. Open the `build.gradle.kts` file and add the following artifacts to the `dependencies` block:
   ```kotlin
   ```
   {src="snippets/tutorial-websockets-client/build.gradle.kts" lines="1-2,21-25"}


### Add the JavaExec task {id="java-exec"}

In the `build.gradle.kts` file, add the `JavaExec` task and specify `standardInput` as show below:

```kotlin
```
{src="snippets/tutorial-websockets-client/build.gradle.kts" lines="17-19"}

Then, click the **Load Gradle Changes** icon in the top right corner of the `build.gradle.kts` file to install the dependencies.


## Create the chat client {id="create-chat-client"}

Now we can add a client's code to the `src/main/kotlin/Main.kt` file.

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

However, this "straightforward" implementation actually contains an issue that prevents it from being used as a proper chat client: when invoking `readLine()`, our program waits until the user enters a message. During this time, we can't see any messages which have been typed out by other users. Likewise, because we invoke `readLine()` after every received message, we would only ever see one new message at a time. Let's address this issue, and build a better solution!


### Improved solution {id="improved-solution"}

A better structure for our chat client would be to separate the message output and input mechanisms, allowing them to run concurrently: when new messages arrive, they are printed immediately, but our users can still start composing a new chat message at any point.

We know that to output messages, we need to be able to receive them from the WebSocket's `incoming` channel, and print them to the command line. Let's add a function called `outputMessages()` to the `Main.kt` file with the following implementation for this functionality:

```kotlin
```
{src="snippets/tutorial-websockets-client/src/main/kotlin/Main.kt" lines="24-33"}

Because the function operates in the context of a `DefaultClientWebSocketSession`, we define `outputMessages()` as an extension function on the type. We also don't forget to add the `suspend` modifier – because iterating over the `incoming` channel suspends the coroutine while no new message is available.

Next, let's define a second function which allows the user to input text. Add a function called `inputMessages()` in `Main.kt` with the following implementation

```kotlin
```
{src="snippets/tutorial-websockets-client/src/main/kotlin/Main.kt" lines="35-46"}

Once again defined as a suspending extension function on `DefaultClientWebSocketSession`, this function's only job is to read text from the command line and send it to the server or to return when the user types `exit`.

Where we previously had one loop which had to take care of reading input and printing output, we now have separated these tasks into their own functions, which can operate independently of each other.

### Wire it together

Let's make use of our two new functions! We can call them inside the body of our WebSocket handler by changing the code of our `main()` method in `Main.kt` to the following:

```kotlin
```
{src="snippets/tutorial-websockets-client/src/main/kotlin/Main.kt" lines="7-22"}

This new implementation improves the behavior of our application: Once the connection to our chat server is established, we use the `launch` function from Kotlin's Coroutines library to launch the two long-running functions `outputMessages()` and `inputMessages()` on a new coroutine (without blocking the current thread). The launch function also returns a `Job` object for both of them, which we use to keep the program running until the user types `exit` or encounters a network error when trying to send a message. After `inputMessages()` has returned, we cancel the execution of the `outputMessages()` function, and `close` the client.

Until this happens, both input and output can happily happen concurrently, with new messages being received while the client sits idle, and the option to start composing a new message at any point.

### Let's give it a try!

We have now finished implementing our WebSocket-based chat client with Kotlin and Ktor. To celebrate our success, let's give it a try! With the chat server running, start two instances of the chat client. You can do this by creating two identical **MainKt** [run configurations](https://www.jetbrains.com/help/idea/run-debug-configuration.html), which can be run separately. Even if you send multiple messages right after each other, they should be correctly displayed on all connected clients.

![App in action](app_in_action.png){animated="true" width="706"}

We have included the final state of the client chat application in the [codeSnippets](https://github.com/ktorio/ktor-documentation/tree/%current-branch%/codeSnippets) project: [tutorial-websockets-client](https://github.com/ktorio/ktor-documentation/tree/%current-branch%/codeSnippets/snippets/tutorial-websockets-client).

That's it for this tutorial on WebSockets with Ktor – time to congratulate yourself for building a whole application! If you're looking for some inspiration of where to take this project next, as well as related materials, continue to the next section.



## What's next

Congratulations on finishing this tutorial on creating a chat application using Kotlin, Ktor & WebSockets. We now have a basic command-line application which allows multiple clients to have a conversation over the network in a shared chat.

### Feature requests

At this point, we have implemented the absolute basics for a chat service, both on the client and server side. If you want to, you can keep expanding on this project. To get you started, here are a few ideas of how to improve the application, in no particular order:

- **Nicer UI!** So far, the client's user interface is very rudimentary, with only text input and output. If you're feeling adventurous, you can pick up a framework like [TornadoFX](https://tornadofx.io/), [Compose for Desktop](https://www.jetbrains.com/lp/compose/), or other, and try implementing a fancy user interface for the chat.
- **Mobile app!** The Ktor client libraries are also available for mobile applications. Feel free to try integrating what you have learned in this tutorial in the context of an Android application, and build the next big mobile chat product!
