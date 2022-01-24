[//]: # (title: Sockets)

<var name="plugin_name" value="Sockets"/>

<microformat>
<p>
<b>Required dependencies</b>: <code>io.ktor:ktor-network</code>, <code>io.ktor:ktor-network-tls</code>
</p>
</microformat>

In addition to HTTP handling for the [server](ktor-server.xml) and the [client](ktor-client.xml), Ktor supports client and server, TCP and UDP raw sockets.
It exposes a suspending API that uses [NIO](https://docs.oracle.com/javase/8/docs/api/java/nio/package-summary.html) under the hoods.

> Sockets use an experimental API that is expected to evolve in the upcoming updates with potentially breaking changes.
>
{type="note"}

## Add dependencies {id="add_dependencies"}

<var name="artifact_name" value="ktor-network"/>
<include src="lib.xml" include-id="add_ktor_artifact_intro"/>
<include src="lib.xml" include-id="add_ktor_artifact"/>


<var name="artifact_name" value="ktor-network-tls"/>

To use [secure sockets](#secure), you also need to add `%artifact_name%`:

<include src="lib.xml" include-id="add_ktor_artifact"/>



## Server {id="server"}

### Build a socket {id="server_build_socket"}

To create a server socket, create the `ActorSelectorManager` instance, call the `TcpSocketBuilder.tcp()` function on it, 
and then use `bind` to bind a server socket to specific port:

```kotlin
```
{src="snippets/sockets-server/src/main/kotlin/com/example/Application.kt" lines="10-11"}

The above snippet create a TCP socket.
To create a UDP socket, use `TcpSocketBuilder.udp()`:

```kotlin
        val selectorManager = ActorSelectorManager(Dispatchers.IO)
        val serverSocket = aSocket(selectorManager).udp().bind("127.0.0.1", 9002)
```


### Get a connected socket {id="server_get_socket"}

The server socket has an `accept` function that returns, one at a time, a connected socket for each incoming connection pending in the *backlog*:

```kotlin
```
{src="snippets/sockets-server/src/main/kotlin/com/example/Application.kt" lines="14"}

>If you want to support multiple clients at once, remember to call `launch { }` to prevent
>the function that is accepting the sockets from suspending.
>
{type="note"}

Once you have a `socket` open by either [binding](#server) or [connecting](#client) the builder,
you can read from or write to the socket, by opening read/write channels:

### Receive/send data {id="server_receive_send"}

Receive:

```kotlin
```
{src="snippets/sockets-server/src/main/kotlin/com/example/Application.kt" lines="17"}

Send data: 

```kotlin
```
{src="snippets/sockets-server/src/main/kotlin/com/example/Application.kt" lines="22-23"}

You can read the KDoc for [ByteReadChannel](https://api.ktor.io/ktor-io/ktor-io/io.ktor.utils.io/-byte-read-channel/index.html) and 
[ByteWriteChannel](https://api.ktor.io/ktor-io/ktor-io/io.ktor.utils.io/-byte-write-channel/index.html) for more information on the available API.

### Close socket {id="server_close"}

[get socket](#server_get_socket):

```kotlin
```
{src="snippets/sockets-server/src/main/kotlin/com/example/Application.kt" lines="26"}

### Example {id="server-example"}

```kotlin
```
{src="snippets/sockets-server/src/main/kotlin/com/example/Application.kt"}


## Client {id="client"}

### Build a socket {id="client_build_socket"}

When creating a socket client, you have to `connect` to a specific `SocketAddress` to get
a `Socket`:

```kotlin
```
{src="snippets/sockets-client/src/main/kotlin/com/example/Application.kt" lines="11-12"}

### Secure sockets (SSL/TLS) {id="secure"}

Link to [ktor-network-tls](#add_dependencies).

Connect to a secure socket:

```kotlin
val selectorManager = ActorSelectorManager(Dispatchers.IO)
val socket = aSocket(selectorManager).tcp().connect("127.0.0.1", 8443).tls()
```

### Receive/send data {id="client_receive_send"}

Receive:

```kotlin
```
{src="snippets/sockets-client/src/main/kotlin/com/example/Application.kt" lines="14"}

```kotlin
```
{src="snippets/sockets-client/src/main/kotlin/com/example/Application.kt" lines="19"}

Send data:

```kotlin
```
{src="snippets/sockets-client/src/main/kotlin/com/example/Application.kt" lines="15"}

```kotlin
```
{src="snippets/sockets-client/src/main/kotlin/com/example/Application.kt" lines="32-33"}

### Close connection {id="client_close"}

```kotlin
```
{src="snippets/sockets-client/src/main/kotlin/com/example/Application.kt" lines="24-25"}


### Example {id="client-example"}

```kotlin
```
{src="snippets/sockets-client/src/main/kotlin/com/example/Application.kt"}



