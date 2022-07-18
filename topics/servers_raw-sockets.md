[//]: # (title: Sockets)

<show-structure for="chapter" depth="2"/>

<var name="plugin_name" value="Sockets"/>

<tldr>
<p>
<b>Required dependencies</b>: <code>io.ktor:ktor-network</code>, <code>io.ktor:ktor-network-tls</code>
</p>
<p><b>Code examples</b>:
<a href="https://github.com/ktorio/ktor-documentation/tree/%current-branch%/codeSnippets/snippets/sockets-server">sockets-server</a>,
<a href="https://github.com/ktorio/ktor-documentation/tree/%current-branch%/codeSnippets/snippets/sockets-client">sockets-client</a>,
<a href="https://github.com/ktorio/ktor-documentation/tree/%current-branch%/codeSnippets/snippets/sockets-client-tls">sockets-client-tls</a>
</p>
</tldr>

In addition to HTTP/WebSocket handling for the server and client, Ktor supports TCP and UDP raw sockets.
It exposes a suspending API that uses [java.nio](https://docs.oracle.com/javase/8/docs/api/java/nio/package-summary.html) under the hoods.

> Sockets use an experimental API that is expected to evolve in the upcoming updates with potentially breaking changes.
>
{type="note"}

## Add dependencies {id="add_dependencies"}

<var name="artifact_name" value="ktor-network"/>
<include from="lib.topic" element-id="add_ktor_artifact_intro"/>
<include from="lib.topic" element-id="add_ktor_artifact"/>

To use [secure sockets](#secure) in the client, you also need to add `io.ktor:ktor-network-tls`.


## Server {id="server"}

### Create a server socket {id="server_create_socket"}

To build a server socket, create the `SelectorManager` instance, call the `SocketBuilder.tcp()` function on it, 
and then use `bind` to bind a server socket to specific port:

```kotlin
```
{src="snippets/sockets-server/src/main/kotlin/com/example/Application.kt" lines="10-11"}

The snippet above creates a TCP socket, which is the [ServerSocket](https://api.ktor.io/ktor-network/io.ktor.network.sockets/-server-socket/index.html) instance.
To create a UDP socket, use `SocketBuilder.udp()`.


### Accept incoming connections {id="accepts_connection"}

After creating a server socket, you need to call the `ServerSocket.accept` function that accepts a socket connection and 
returns a connected socket (a [Socket](https://api.ktor.io/ktor-network/io.ktor.network.sockets/-socket/index.html) instance):

```kotlin
```
{src="snippets/sockets-server/src/main/kotlin/com/example/Application.kt" lines="14"}

Once you have a connected socket, you can receive/send data by reading from or writing to the socket.


### Receive data {id="server_receive"}

To receive data from the client, you need to call the `Socket.openReadChannel` function, which returns [ByteReadChannel](https://api.ktor.io/ktor-io/io.ktor.utils.io/-byte-read-channel/index.html):

```kotlin
```
{src="snippets/sockets-server/src/main/kotlin/com/example/Application.kt" lines="17"}

`ByteReadChannel` provides API for asynchronous reading of data.
For example, you can read a line of UTF-8 characters using `ByteReadChannel.readUTF8Line`:

```kotlin
```
{src="snippets/sockets-server/src/main/kotlin/com/example/Application.kt" lines="22"}



### Send data {id="server_send"}

To send data to the client, call the `Socket.openWriteChannel` function, which returns [ByteWriteChannel](https://api.ktor.io/ktor-io/io.ktor.utils.io/-byte-write-channel/index.html):

```kotlin
```
{src="snippets/sockets-server/src/main/kotlin/com/example/Application.kt" lines="18"}

`ByteWriteChannel` provides API for asynchronous writing of sequences of bytes.
For example, you can write a line of UTF-8 characters using `ByteWriteChannel.writeStringUtf8`:

```kotlin
```
{src="snippets/sockets-server/src/main/kotlin/com/example/Application.kt" lines="22-23"}


### Close a socket {id="server_close"}

To release resources associated with the [connected socket](#accepts_connection), call `Socket.close`:

```kotlin
```
{src="snippets/sockets-server/src/main/kotlin/com/example/Application.kt" lines="26"}

### Example {id="server-example"}

A code sample below demonstrates how to use sockets on the server side:

```kotlin
```
{src="snippets/sockets-server/src/main/kotlin/com/example/Application.kt"}

You can find the full example here: [sockets-server](https://github.com/ktorio/ktor-documentation/tree/%current-branch%/codeSnippets/snippets/sockets-server).


## Client {id="client"}

### Create a socket {id="client_create_socket"}

To build a client socket, create the `SelectorManager` instance, call the `SocketBuilder.tcp()` function on it,
and then use `connect` to establish a connection and get a connected socket (a [Socket](https://api.ktor.io/ktor-network/io.ktor.network.sockets/-socket/index.html) instance):

```kotlin
```
{src="snippets/sockets-client/src/main/kotlin/com/example/Application.kt" lines="11-12"}

Once you have a connected socket, you can receive/send data by reading from or writing to the socket.

### Create a secure socket (SSL/TLS) {id="secure"}

Secure sockets allows you to establish TLS connections. 
To use secure sockets, you need to add the [ktor-network-tls](#add_dependencies) dependency.
Then, call the `Socket.tls` function on a connected socket:

```kotlin
val selectorManager = SelectorManager(Dispatchers.IO)
val socket = aSocket(selectorManager).tcp().connect("127.0.0.1", 8443).tls()
```

The `tls` function allows you to adjust TLS parameters provided by [TLSConfigBuilder](https://api.ktor.io/ktor-network/ktor-network-tls/io.ktor.network.tls/-t-l-s-config-builder/index.html):

```kotlin
```
{src="snippets/sockets-client-tls/src/main/kotlin/com/example/Application.kt" lines="14-21"}

You can find the full example here: [sockets-client-tls](https://github.com/ktorio/ktor-documentation/tree/%current-branch%/codeSnippets/snippets/sockets-client-tls).


### Receive data {id="client_receive"}

To receive data from the server, you need to call the `Socket.openReadChannel` function, which returns [ByteReadChannel](https://api.ktor.io/ktor-io/io.ktor.utils.io/-byte-read-channel/index.html):

```kotlin
```
{src="snippets/sockets-client/src/main/kotlin/com/example/Application.kt" lines="14"}

`ByteReadChannel` provides API for asynchronous reading of data.
For example, you can read a line of UTF-8 characters using `ByteReadChannel.readUTF8Line`:

```kotlin
```
{src="snippets/sockets-client/src/main/kotlin/com/example/Application.kt" lines="19"}


### Send data {id="client_send"}

To send data to the server, call the `Socket.openWriteChannel` function, which returns [ByteWriteChannel](https://api.ktor.io/ktor-io/io.ktor.utils.io/-byte-write-channel/index.html):

```kotlin
```
{src="snippets/sockets-client/src/main/kotlin/com/example/Application.kt" lines="15"}

`ByteWriteChannel` provides API for asynchronous writing of sequences of bytes.
For example, you can write a line of UTF-8 characters using `ByteWriteChannel.writeStringUtf8`:

```kotlin
```
{src="snippets/sockets-client/src/main/kotlin/com/example/Application.kt" lines="32-33"}

### Close connection {id="client_close"}

To release resources associated with the [connected socket](#client_create_socket), call `Socket.close` and `SelectorManager.close`:

```kotlin
```
{src="snippets/sockets-client/src/main/kotlin/com/example/Application.kt" lines="24-25"}


### Example {id="client-example"}

A code sample below demonstrates how to use sockets on the client side:

```kotlin
```
{src="snippets/sockets-client/src/main/kotlin/com/example/Application.kt"}

You can find the full example here: [sockets-client](https://github.com/ktorio/ktor-documentation/tree/%current-branch%/codeSnippets/snippets/sockets-client).
