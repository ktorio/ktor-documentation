[//]: # (title: Sockets)

<include src="lib.md" include-id="outdated_warning"/>

In addition to HTTP handling for the [server](A_Ktor_Application.xml) and the [client](client.md), Ktor supports client and server, TCP and UDP raw sockets.
It exposes a suspending API that uses NIO under the hoods.

## Add dependencies {id="add_dependencies"}
<var name="feature_name" value="Sockets"/>
<var name="artifact_name" value="ktor-network"/>
<include src="lib.md" include-id="add_ktor_artifact_intro"/>
<include src="lib.md" include-id="add_ktor_artifact"/>



## Usage

In order to create either server or client sockets, you have to use the `aSocket` builder,
with a mandatory `ActorSelectorManager`: `aSocket(selector)`. For example: `aSocket(ActorSelectorManager(Dispatchers.IO))`.

Then use:

* `val socketBuilder = aSocket(selector).tcp()` for a builder using TCP sockets
* `val socketBuilder = aSocket(selector).udp()` for a builder using UDP sockets

This returns a `SocketBuilder` that can be used to:
 
* `val serverSocket = aSocket(selector).tcp().bind(address)` to listen to an address (for servers)
* `val clientSocket = aSocket(selector).tcp().connect(address)` to connect to an address (for clients)
 
If you need to control the dispatcher used by the sockets, you can instantiate a selector,
that uses, for example, a cached thread pool:
```kotlin
val exec = Executors.newCachedThreadPool()
val selector = ActorSelectorManager(exec.asCoroutineDispatcher())
val tcpSocketBuilder = aSocket(selector).tcp()
```

Once you have a `socket` open by either [binding](#server) or [connecting](#client) the builder,
you can read from or write to the socket, by opening read/write channels:

```kotlin
val input : ByteReadChannel  = socket.openReadChannel()
val output: ByteWriteChannel = socket.openWriteChannel(autoFlush = true)
```

>You can read the KDoc for [ByteReadChannel](https://api.ktor.io/%ktor_version%/io.ktor.utils.io/-byte-read-channel/index.html)
>and [ByteWriteChannel](https://api.ktor.io/%ktor_version%/io.ktor.utils.io/-byte-write-channel/index.html)
>for further information on the available methods.
>
{type="note"}

## Server

When creating a server socket, you have to `bind` to a specific `SocketAddress` to get
a `ServerSocket`:

```kotlin
val server = aSocket(selector).tcp().bind(InetSocketAddress("127.0.0.1", 2323))
```

The server socket has an `accept` method that returns, one at a time, 
a connected socket for each incoming connection pending in the *backlog*:

```kotlin
val socket = server.accept()
```

>If you want to support multiple clients at once, remember to call `launch { }` to prevent
>the function that is accepting the sockets from suspending.
>
{type="note"}

### Simple Echo Server:


```kotlin
fun main(args: Array<String>) {
    runBlocking {
        val server = aSocket(ActorSelectorManager(Dispatchers.IO)).tcp().bind(InetSocketAddress("127.0.0.1", 2323))
        println("Started echo telnet server at ${server.localAddress}")
        
        while (true) {
            val socket = server.accept()
            
            launch {
                println("Socket accepted: ${socket.remoteAddress}")
                
                val input = socket.openReadChannel()
                val output = socket.openWriteChannel(autoFlush = true)
                
                try {
                    while (true) {
                        val line = input.readUTF8Line()
                        
                        println("${socket.remoteAddress}: $line")
                        output.writeFully("$line\r\n".toByteArray(Charsets.UTF_8))
                    }
                } catch (e: Throwable) {
                    e.printStackTrace()
                    socket.close()
                }
            }
        }
    }
}
```

Then you can connect to it using *telnet* and start typing:

```text
telnet 127.0.0.1 2323
```

For each line that you type (you have to press the return key), the server will reply
with the same line:

```text
Trying 127.0.0.1...
Connected to 127.0.0.1
Escape character is '^]'.

Hello
Hello
World
World
|
``` 

## Client

When creating a socket client, you have to `connect` to a specific `SocketAddress` to get
a `Socket`:

```kotlin
val socket = aSocket(selector).tcp().connect(InetSocketAddress("127.0.0.1", 2323))
```

### Simple Client Connecting to an Echo Server:


```kotlin
fun main(args: Array<String>) {
    runBlocking {
        val socket = aSocket(ActorSelectorManager(Dispatchers.IO)).tcp().connect(InetSocketAddress("127.0.0.1", 2323))
        val input = socket.openReadChannel()
        val output = socket.openWriteChannel(autoFlush = true)

        output.writeFully("hello\r\n".toByteArray(Charsets.UTF_8))
        val response = input.readUTF8Line()
        println("Server said: '$response'")
    }
}
```

## Secure sockets (SSL/TLS)
{id="secure"}

Ktor supports secure sockets. To enable them you will need to include the
`io.ktor:ktor-network-tls:$ktor_version` artifact, and call the `.tls()` to a connected socket.

*Connect to a secure socket:*
```kotlin
runBlocking {
    val socket = aSocket(ActorSelectorManager(Dispatchers.IO)).tcp().connect(InetSocketAddress("google.com", 443)).tls()
    val w = socket.openWriteChannel(autoFlush = false)
    w.writeFully("GET / HTTP/1.1\r\n".toByteArray(Charsets.UTF_8))
    w.writeFully("Host: google.com\r\n".toByteArray(Charsets.UTF_8))
    w.writeFully("\r\n".toByteArray(Charsets.UTF_8))
    w.flush()
    val r = socket.openReadChannel()
    println(r.readUTF8Line())
}
```

You can adjust a few optional parameters for the TLS connection:

```kotlin
suspend fun Socket.tls(
        trustManager: X509TrustManager? = null,
        randomAlgorithm: String = "NativePRNGNonBlocking",
        serverName: String? = null,
        coroutineContext: CoroutineContext = Dispatchers.IO
): Socket
```
