[//]: # (title: First steps with Kotlin RPC)

<show-structure for="chapter" depth="2"/>

<web-summary>
Discover the fundamentals of RPC and explore a detailed comparison between RPC and REST in this comprehensive
guide. Learn how to create your first application with Kotlin RPC.
</web-summary>

<link-summary>
Learn how to create your first application with Kotlin RPC and Ktor.
</link-summary>

<card-summary>
Learn how to create your first application with Kotlin RPC and Ktor.
</card-summary>

<tldr>
<var name="example_name" value="tutorial-kotlin-rpc-app"/>
<include from="lib.topic" element-id="download_example"/>
<p>
<b>Used plugins</b>: <a href="server-routing.md">Routing</a>,
<a href="https://kotlinlang.org/api/kotlinx.serialization/">kotlinx.serialization</a>,
<a href="https://kotlin.github.io/kotlinx-rpc/get-started.html">kotlinx.rpc</a>
</p>
</tldr>

Kotlin RPC (Remote Procedure Call) builds on well-established foundations and runs on the [`kotlinx.rpc`](https://kotlin.github.io/kotlinx-rpc/get-started.html)
library.

The `kotlinx.rpc` library enables you to make procedure calls over network
boundaries using regular Kotlin language constructs. As such, it provides an alternative to both
REST and Google RPC (gRPC).

In this article, you'll learn the core concepts of Kotlin RPC and build a simple application. You can then
go and evaluate the library in your own projects.

## Prerequisites {id="prerequisites"}

This tutorial assumes you have a fundamental understanding of Kotlin programming. If you are new to
Kotlin, consider reviewing some [introductory materials](https://kotlinlang.org/docs/getting-started.html).

For the best experience, we recommend using [IntelliJ IDEA Ultimate](https://www.jetbrains.com/idea/download) as your
integrated development environment (IDE) as it offers comprehensive support and tools to enhance your productivity.

## What is RPC? {id="what-is-rpc"}

### Local vs. remote procedure calls {id="local-vs-remote"}

Procedure calls are a fundamental concept in any programming language. Technically, they are
local procedure calls, since they always take place within the same program.

A remote procedure call is when the function call and parameters are in some way transferred over a
network, so that the implementation can occur within a separate virtual machine or executable. The return value travels
the opposite path back to the machine where the invocation was made.

It is easiest to think of the machine where the invocation occurred as the client and the machine where
the implementation resides as the server. This, however, does not necessarily have to be the case. RPC
calls can occur in both directions, as part of a peer architecture. To keep things simple in this tutorial, you can
assume a client/server deployment.

### RPC framework fundamentals {id="rpc-framework-fundamentals"}

Certain fundamentals must be provided by any RPC framework. They are inevitable when implementing remote
procedure calls within a conventional IT infrastructure. The terminology can vary, and responsibilities
can be divided in different ways, but every RPC framework must provide:

1. A way to declare the procedures that will be invoked remotely. In object-oriented programming, an
   interface is the logical choice. This could be the interface construct provided by the current
   language or some kind of language-neutral standard, such as [the Web IDL used by the W3C](https://webidl.spec.whatwg.org/)
2. A means to specify the types used for parameters and return values. Once again, you could use a
   language-neutral standard. However, it may be simpler to annotate standard data type declarations in
   the current language.
3. Helper classes, known as _client stubs_, that will be used to convert the procedure invocation into a format that
   can be sent over the network and to unpack the resulting return value. These stubs can be created either during
   the compiling process or dynamically at runtime.
4. An underlying _RPC Runtime_ that manages the helper classes and supervises the lifecycle of a remote procedure call.
   On the server side, this runtime will need to be embedded in some kind of server, so that it is available
   to process requests on an ongoing basis.
5. Protocols need to be chosen (or defined) to represent the procedure being called, serialize the data
   being sent, and transform the information over the network. In the past, some technologies have
   defined new protocols from scratch (IIOP in CORBA), while others have focused on reuse (HTTP POST in
   SOAP).

### Marshaling vs. serialization {id="marshaling-vs-serialization"}

In RPC frameworks, we speak of _marshaling_ and _unmarshaling_. This is the process of packing and unpacking
information to be sent over the network. It can be thought of as a superset of serialization. In marshaling, you are
serializing objects, but you also need to package information about the procedure being called and the context in which
that call was made.

Having introduced the core concepts of RPC, let’s see how they apply in `kotlinx.rpc` by building
a sample application.

## Hello, kotlinx.rpc {id="hello-kotlinx-rpc"}

You will now create an application for ordering pizza over the network. In order to keep the code as simple
as possible, you’ll use a console-based client.

### Create the project {id="create-project"}

First, you will create a project that includes both the client and server implementations.

In more complex applications, it's best practice to use separate modules for the client and server.
However, for simplicity in this tutorial, you will use a single module for both.

<procedure id="create-project-procedure">

1. Launch [intelliJ IDEA](https://www.jetbrains.com/idea/download/).
2. <include from="lib.topic" element-id="new_project_idea"/>
3. In the **Name** field, enter <path>KotlinRpcPizzaApp</path> as the name of your project.

   ![IntelliJ New Kotlin Project window](tutorial_kotlin_rpc_intellij_project.png){style="block" width="706" border-effect="rounded"}
4. Leave the rest of the default settings and click **Create**.

</procedure>

Normally, you would immediately configure the project build file. However, that’s an
implementation detail that won't enhance your understanding of the technology, so you'll get back to
that step at the end.

### Add the shared types {id="shared-types"}

The heart of any RPC project is the interface that defines the procedures to be called remotely, along
with the types used in the definition of those procedures.

In a multi-module project, these types will need to be shared. However, in this example, this step won't
be necessary.

<procedure id="shared-types-procedure">

1. Navigate to the <path>src/main/kotlin</path> folder and create a new subpackage called <path>model</path>.
2. Inside the <path>model</path> package, create a new <path>PizzaShop.kt</path> file with the following implementation:

   ```kotlin
   ```
   {src="snippets/tutorial-kotlin-rpc-app/src/main/kotlin/model/PizzaShop.kt" include-lines="1-8,10,14-20"}

   The interface needs to have the `@Rpc` annotation from the `kotlinx.rpc` library.

   Because you are using [`kotlinx.serialization`](https://github.com/Kotlin/kotlinx.serialization)
   to help transfer information over the network, then the types used in parameters must be marked with the
   `Serializable` annotation.

</procedure>

### Implement the client {id="client-implementation"}

<procedure id="client-impl-procedure">

1. Navigate to <path>src/main/kotlin</path> and create a new <path>Client.kt</path> file.
2. Open <path>Client.kt</path> and add the following implementation:

   ```kotlin
   ```
   {src="snippets/tutorial-kotlin-rpc-app/src/main/kotlin/Client.kt" include-lines="1-14,15-38,40-41,60-61"}

</procedure>

With the above code, you prepared and then executed an RPC call.

The `kotlinx.rpc` library uses the [Ktor client](client-create-new-application.topic) to host its runtime on the client
side. The runtime is not coupled to Ktor, and other choices are possible, but this promotes reuse and makes it easy to
integrate `kotlinx.rpc` into existing Kotlin Multiplatform applications.

Both the Ktor client and Kotlin RPC are built around coroutines, so you use the `runBlocking {}` block to
create the initial coroutine, and execute the rest of the client within it:

```kotlin
```
{src="snippets/tutorial-kotlin-rpc-app/src/main/kotlin/Client.kt" include-lines="16,61"}

> Note that `runBlocking {}` is designed for spikes and tests, rather than production code.
>
{style="tip"}

Next, you create an instance of the Ktor client in the standard way. `kotlinx.rpc` uses
the [WebSockets](client-websockets.topic) plugin to transfer information. You
ensure that the plugin is loaded by using the `installKrpc()` function:

```kotlin
```
{src="snippets/tutorial-kotlin-rpc-app/src/main/kotlin/Client.kt" include-lines="17-21"}

Having created the Ktor client, you then create a `KtorRpcClient` object for invoking remote
procedures. You configure the location of the server and the mechanism being used to
transfer information:

```kotlin
```
{src="snippets/tutorial-kotlin-rpc-app/src/main/kotlin/Client.kt" include-lines="23-35"}

At this point, the standard setup is completed, and you are ready to use the functionality
specific to the problem domain. You can use the client to create a client proxy object that implements
the methods of the `PizzaShop` interface:

```kotlin
```
{src="snippets/tutorial-kotlin-rpc-app/src/main/kotlin/Client.kt" include-symbol="pizzaShop"}

You can then make the remote procedure call and use the result:

```kotlin
```
{src="snippets/tutorial-kotlin-rpc-app/src/main/kotlin/Client.kt" include-lines="40-41"}

At this point, a lot of work is being done. The details of the call and
all parameters are converted into a message, sent over the network, and then the return value
received and decoded.

Finally, you close the client:

```kotlin
```
{src="snippets/tutorial-kotlin-rpc-app/src/main/kotlin/Client.kt" include-lines="60"}

### Implement the server {id="server-implementation"}

The implementation on the server side breaks down into two parts. First, you create an
implementation of the interface, and then, you host it within a server.

<procedure id="create-interface">

1. Navigate to <path>src/main/kotlin</path> and create a new <path>Server.kt</path> file.
2. Open <path>Server.kt</path> and add the following interface:

   ```kotlin
   ```
   {src="snippets/tutorial-kotlin-rpc-app/src/main/kotlin/Server.kt" include-lines="1-4,15-16,18-20,45"}

   Although this is not a fully functional implementation, it is sufficient to get our demo up and
   running.

   The second part of the implementation builds on Ktor.
3. Add the following code into the same file:

   ```kotlin
   ```
   {src="snippets/tutorial-kotlin-rpc-app/src/main/kotlin/Server.kt" include-lines="47-68"}

   Here's the breakdown:

   First, you create an instance of Ktor server using Netty, with the specified extension function used for
   configuration:

   ```kotlin
   ```
   {src="snippets/tutorial-kotlin-rpc-app/src/main/kotlin/Server.kt" include-lines="48-51"}

   Then, you declare a setup function that extends the Ktor `Application` type. This installs the
   `kotlinx.rpc` plugin and declares one or more routes:

   ```kotlin
   ```
   {src="snippets/tutorial-kotlin-rpc-app/src/main/kotlin/Server.kt" include-lines="54-57,67-68"}

   Inside the routing section, you use `kotlinx.rpc` extensions to the Ktor Routing DSL
   to declare an endpoint. As with on the client, you specify the URL and configure serialization.
   But in this case, our implementation will be listening at that URL for incoming requests:

   ```kotlin
   ```
   {src="snippets/tutorial-kotlin-rpc-app/src/main/kotlin/Server.kt" include-lines="58-66"}

   You use `registerService {}` to provide the implementation of your interface
   to the RPC runtime.

</procedure>

### Add dependencies {id="add-dependencies"}

You now have all the code necessary to run the application, but at the moment, it would not even
compile.

While you can use the Ktor Project Generator with the [`kotlinx.rpc`](https://kotlin.github.io/kotlinx-rpc/get-started.html)
plugin, you can also configure the build file manually:

<procedure id="configure-build-files">

1. Open or create the <path>gradle/libs.versions.toml</path> file and define the Kotlin,
   `kotlinx.rpc`, and Logback versions, as well as the library and plugin aliases:

   ```toml
   ```
   {src="snippets/tutorial-kotlin-rpc-app/gradle/libs.versions.toml"}

   Gradle automatically makes these aliases available through `libs` in your build file.
2. Open the <path>settings.gradle.kts</path> file and import the Ktor version catalog:

   ```kotlin
   ```
   {src="snippets/tutorial-kotlin-rpc-app/settings.gradle.kts" include-lines="7-14"}

   This catalog provides the Ktor plugin and dependency aliases through `ktorLibs`.
   The version in the catalog coordinate determines the Ktor version used by these aliases.
3. In the <path>build.gradle.kts</path> file, add the following plugins:

   ```kotlin
   ```
   {src="snippets/tutorial-kotlin-rpc-app/build.gradle.kts" include-lines="1-6"}

   * The `kotlinx.serialization` plugin is required to generate the helper types for
     converting Kotlin objects into JSON. Remember that `kotlinx.serialization` makes
     no use of reflection.
   * The Ktor plugin is used to build fat JARs that bundle the app with all its dependencies.
   * The RPC plugin is needed to generate the stubs for the client-side.
4. Add the following dependencies:

   ```kotlin
   ```
   {src="snippets/tutorial-kotlin-rpc-app/build.gradle.kts" include-lines="15-25"}

   This adds the Ktor client and server, the client and server parts of the
   `kotlinx.rpc` runtime, and the library for integrating `kotlinx.rpc` and
   `kotlinx-serialization`.

   With this, you can now run the projects and start making RPC calls.

</procedure>

### Run the application {id="run-application"}

To run the application, follow the steps below:

<procedure id="run-app-procedure">

1. Navigate to the <path>Server.kt</path> file.
2. In IntelliJ IDEA, click on the run button
   (![intelliJ IDEA run icon](intellij_idea_gutter_icon.svg){style="inline" height="16" width="16"})
   next to the `main()` function to start the application.

   You should see the output in the **Run** tool panel:

   ![Run server output in intelliJ IDEA](tutorial_kotlin_rpc_run_server.png){style="block" width="706" border-effect="line"}
3. Navigate to the <path>Client.kt</path> file and run the application. You should see the following output in the
   console:

   ```shell
   Your pizza cost 7.89

   Process finished with exit code 0
   ```

</procedure>

## Extend the example {id="extend-the-example"}

Finally, you can enhance the complexity of your example application to establish a solid foundation for future
development.

<procedure id="extend-server">

1. In the <path>PizzaShop.kt</path> file, extend the `orderPizza` method by including the client’s ID, and add a
   `viewOrders()` method that returns all the pending orders for a specified client:

   ```kotlin
   ```
   {src="snippets/tutorial-kotlin-rpc-app/src/main/kotlin/model/PizzaShop.kt" include-lines="1-8,12-14"}

   You can take advantage of the coroutines library by returning a `Flow` rather than a
   `List` or `Set`. This will allow you to steam the information to the client
   one pizza order at a time.
2. Navigate to the <path>Server.kt</path> file and implement this functionality by storing the current orders in a map
   of lists:

   ```kotlin
   ```
   {src="snippets/tutorial-kotlin-rpc-app/src/main/kotlin/Server.kt" include-lines="16,22-45"}

   The server creates a new `PizzaShopImpl` instance for each connected client. This avoids conflicts between clients
   by isolating their state.
   > This implementation isn't thread-safe. If the same `PizzaShopImpl` instance receives concurrent calls,
   > they can modify `openOrders` at the same time and corrupt it. In production code, use a thread-safe
   > structure.
   > 
   {style="warning"}
3. In the <path>Client.kt</path> file, submit multiple orders using two different client IDs:

   ```kotlin
   ```
   {src="snippets/tutorial-kotlin-rpc-app/src/main/kotlin/Client.kt" include-lines="37,43-50"}

   Then you iterate over the results, using the `Coroutines` library and
   `collect` method:

   ```kotlin
   ```
   {src="snippets/tutorial-kotlin-rpc-app/src/main/kotlin/Client.kt" include-lines="52-58"}
4. Run the server and the client. When you run the client, you will see the results being displayed
   incrementally:

   ![Client output incrementally displaying results](tutorial_kotlin_rpc_run_client.gif){style="block" width="706" border-effect="line"}

</procedure>

Having created a working example, let’s now dig deeper into how everything works. In particular, let’s
compare and contrast Kotlin RPC with the two main alternatives – REST and gRPC.

## RPC vs. REST {id="rpc-vs-rest"}

The idea of RPC is considerably older than REST, [dating back at least to 1981](https://en.wikipedia.org/wiki/Remote_procedure_call).
When compared to REST, the RPC-based approach does not constrain you to a uniform interface (such as the HTTP
request types), is much simpler to work with in code, and can be more performant thanks to binary messaging.

There are, however, three major advantages to REST:

1. It can be used directly by JavaScript clients in the browser and, hence, as part of single-page
   applications. Because RPC frameworks rely on generated stubs and binary messaging, they do not fit in
   well with the JavaScript ecosystem.
2. REST makes it obvious when a feature involves networking. This helps avoid
   [the distributed objects antipattern](https://martinfowler.com/articles/distributed-objects-microservices.html)
   identified by Martin Fowler. This occurs when a team splits its OO design into
   two or more pieces without considering the performance and reliability implications of making local
   procedure calls remote.
3. REST APIs are built on a series of conventions that make them relatively easy to create, document,
   monitor, debug, and test. There is a huge ecosystem of tools to support this.

These trade-offs mean that Kotlin RPC is best used in two scenarios. Firstly, in KMP clients using
[Compose Multiplatform](https://www.jetbrains.com/lp/compose-multiplatform/), and secondly,
amongst collaborating microservices in the cloud. Future developments in
[Kotlin/Wasm](https://kotlinlang.org/docs/wasm-overview.html) may make `kotlinx.rpc`
more applicable to browser-based applications.

## Kotlin RPC vs. Google RPC {id="kotlin-rpc-vs-google-rpc"}

Google RPC is the dominant RPC technology in the software industry at present. A standard called Protocol
Buffers (protobuf) is used to define data structures and message payloads using a language-neutral Interface
Definition Language (IDL). These IDL definitions can be converted into a wide variety of programming
languages and are serialized using a compact and efficient binary format. Microservice frameworks like
Quarkus and Micronaut already have support for gRPC.

It would be difficult for Kotlin RPC to compete with gRPC, and there would be no benefit in this for the
Kotlin community. Thankfully, there are no plans to do this. Instead, the intention is for kotlinx.rpc to be
compatible and interoperable with gRPC. It will be possible for kotlinx.rpc services to use gRPC as their
networking protocol and for kotlinx.rpc clients to call gRPC services. `kotlinx.rpc` will use
[its own kRPC protocol](https://kotlin.github.io/kotlinx-rpc/transport.html) as the default
option (as is the case in our current example), but there will be nothing to prevent you from choosing gRPC
instead.

## Next steps {id="next-steps"}

Kotlin RPC extends the Kotlin ecosystem in a new direction, offering an alternative to REST and GraphQL for
creating and consuming services. It is built on proven libraries and frameworks, such as Ktor, Coroutines,
and `kotlinx-serialization`. For teams
seeking to make use of Kotlin Multiplatform and Compose Multiplatform, it will provide a simple and
efficient option for distributed messaging.

If this introduction has piqued your interest, make sure to check out
the [official `kotlinx.rpc` documentation](https://kotlin.github.io/kotlinx-rpc/get-started.html) and
[examples](https://github.com/Kotlin/kotlinx-rpc/tree/main/samples).

The `kotlinx.rpc`
library is in its early stages, so we encourage you to explore it and share your feedback.
Bugs and feature requests can be found on [YouTrack](https://youtrack.jetbrains.com/issues/KRPC)
while general discussions take place on [Slack](https://kotlinlang.slack.com/archives/C072YJ3Q91V)
([request access](https://surveys.jetbrains.com/s3/kotlin-slack-sign-up)).
