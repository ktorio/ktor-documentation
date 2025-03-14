[//]: # (title: Migrating from 2.2.x to 3.0.x)

<show-structure for="chapter" depth="3"/>

This guide provides instructions on how to migrate your Ktor application from the 2.2.x version to 3.0.x.

## Ktor Server {id="server"}

### `ApplicationEngine`, `ApplicationEnvironment`, and `Application`

Several design changes have been introduced to improve configurability and provide a more defined
separation between the `ApplicationEngine`, `ApplicationEnvironment` and `Application` instances.

Before v3.0.0, `ApplicationEngine` managed `ApplicationEnvironment`, which in turn managed `Application`.

In the current design, however, `Application` is responsible for creating, owning, and initiating
both `ApplicationEngine` and `ApplicationEnvironment`.

This restructuring comes with the following set of breaking changes:

- [`ApplicationEngineEnvironmentBuilder` and `applicationEngineEnvironment` classes are renamed](#renamed-classes).
- [`start()` and `stop()` methods are removed from `ApplicationEngineEnvironment`](#ApplicationEnvironment).
- [`commandLineEnvironment()` is removed](#CommandLineConfig).
- [Introduction of `ServerConfigBuilder`](#ServerConfigBuilder).
- [`embeddedServer()` returns`EmbeddedServer`](#EmbeddedServer) instead of `ApplicationEngine`.

These changes will impact existing code that relies on the previous model.

#### Renamed classes {id="renamed-classes"}

| Package                    | 2.x.x                                 | 3.0.x                           |
|----------------------------|---------------------------------------|---------------------------------|
| `io.ktor:ktor-server-core` | `ApplicationEngineEnvironmentBuilder` | `ApplicationEnvironmentBuilder` |
| `io.ktor:ktor-server-core` | `applicationEngineEnvironment`        | `applicationEnvironment`        |

#### `start()` and `stop()` methods are removed from `ApplicationEngineEnvironment` {id="ApplicationEnvironment"}

With the merge of `ApplicationEngineEnvironment` to [
`ApplicationEnvironment`](https://api.ktor.io/ktor-server/ktor-server-core/io.ktor.server.application/-application-environment/index.html),
the `start()` and `stop()` methods are now
only accessible through [
`ApplicationEngine`](https://api.ktor.io/ktor-server/ktor-server-core/io.ktor.server.engine/-application-engine/index.html).

| 2.x.x                                                 | 3.0.x                                |
|-------------------------------------------------------|--------------------------------------|
| `ApplicationEngineEnvironment.start()`                | `ApplicationEngine.start()`          |
| `ApplicationEngineEnvironment.stop()`                 | `ApplicationEngine.stop()`           |

Additionally, in the following table you can see the list of removed properties
and their current corresponding ownership:

| 2.x.x                                           | 3.0.x                                        |
|-------------------------------------------------|----------------------------------------------|
| `ApplicationEngineEnvironment.connectors`       | `ApplicationEngine.Configuration.connectors` |
| `ApplicationEnvironment.developmentMode`        | `Application.developmentMode`                |
| `ApplicationEnvironment.monitor`                | `Application.monitor`                        |
| `ApplicationEnvironment.parentCoroutineContext` | `Application.parentCoroutineContext`         |
| `ApplicationEnvironment.rootPath`               | `Application.rootPath`                       |

The ownership changes can be illustrated through the following example:

<compare first-title="2.2.x" second-title="3.0.x">

```kotlin
import io.ktor.server.application.*
import io.ktor.server.cio.*
import io.ktor.server.engine.*
import org.slf4j.helpers.NOPLogger

fun defaultServer(module: Application.() -> Unit) =
  embeddedServer(CIO,
    environment = applicationEngineEnvironment {
      log = NOPLogger.NOP_LOGGER
      connector { 
          port = 8080
      }
      module(module)
    }
  )
```

{validate="false"}

```kotlin
import io.ktor.server.application.*
import io.ktor.server.cio.*
import io.ktor.server.engine.*
import org.slf4j.helpers.NOPLogger

fun defaultServer(module: Application.() -> Unit) =
  embeddedServer(CIO,
    environment = applicationEnvironment { log = NOPLogger.NOP_LOGGER },
    configure = {
      connector {
          port = 8080
      }
    },
    module
  )
```

</compare>

#### `commandLineEnvironment()` is removed {id="CommandLineConfig"}

The `commandLineEnvironment()` function, used to create an `ApplicationEngineEnvironment` instance from command line
arguments has been removed in Ktor `3.0.0`. Instead, you can use the
[`CommandLineConfig`](https://api.ktor.io/ktor-server/ktor-server-core/io.ktor.server.engine/-command-line-config.html)
function to parse command-line arguments into a configuration object.

To migrate your application from `commandLineEnvironment` to `CommandLineConfig`, replace `commandLineEnvironment()`
with a `configure` block as shown below.

<compare first-title="2.2.x" second-title="3.0.x">

```kotlin
fun main(args: Array<String>) {
    embeddedServer(Netty, commandLineEnvironment(args) {
        connector { port = 8080 }
        module {
            routing {
                get("/") {
                    call.respondText("Hello, world!")
                }
            }
        }
    }) {
        requestReadTimeoutSeconds = 5
        responseWriteTimeoutSeconds = 5
    }.start(wait = true)
}
```

```kotlin
```

{src="snippets/embedded-server/src/main/kotlin/com/example/Application.kt" include-lines="13,58-72"}

</compare>

For more information on command-line configuration with `embeddedServer`, see the
[Configuration in code](server-configuration-code.topic#command-line) topic.

#### Introduction of `ServerConfigBuilder` {id="ServerConfigBuilder"}

A new
entity,[
`ServerConfigBuilder`](https://api.ktor.io/ktor-server/ktor-server-core/io.ktor.server.application/-server-config-builder/index.html),
has been introduced for configuring server properties and replaces the previous configuration mechanism of
`ApplicationPropertiesBuilder`.
`ServerConfigBuilder` is used to build instances of the
[`ServerConfig`](https://api.ktor.io/ktor-server/ktor-server-core/io.ktor.server.application/-server-config/index.html)
class, which now holds modules, paths, and environment details previously managed by `ApplicationProperties`.

The following table summarizes the key changes:

| Package                    | 2.x.x                          | 3.0.x                 |
|----------------------------|--------------------------------|-----------------------|
| `io.ktor:ktor-server-core` | `ApplicationProperties`        | `ServerConfig`        |
| `io.ktor:ktor-server-core` | `ApplciationPropertiesBuilder` | `ServerConfigBuilder` |

Additionally, in the `embeddedServer()` function, the `applicationProperties` attribute has been renamed to `rootConfig`
to reflect this new configuration approach.

When using `embeddedServer()`, replace the `applicationProperties` attribute with
`rootConfig`.
Here's an example of using the `serverConfig` block to configure a server with `developmentMode` explicitly set to
`true`:

```kotlin
fun main(args: Array<String>) {
    embeddedServer(Netty,
        serverConfig {
            developmentMode = true
            module(Application::module)
        },
        configure = {
            connector { port = 12345 }
        }
    ).start(wait = true)
}
```

#### Introduction of `EmbeddedServer` {id="EmbeddedServer"}

The class
[`EmbeddedServer`](https://api.ktor.io/ktor-server/ktor-server-core/io.ktor.server.engine/-embedded-server/index.html)
is introduced and used to replace `ApplicationEngine` as a return type of the `embeddedServer()` function.

For more details about the model change,
see [issue KTOR-3857 on YouTrack](https://youtrack.jetbrains.com/issue/KTOR-3857/Environment-Engine-Application-Design).

### Testing

##### `withTestApplication` and `withApplication` have been removed

The `withTestApplication` and `withApplication` functions, [previously deprecated in the
`2.0.0` release](migration-to-20x.md#testing-api), have now been removed from the `ktor-server-test-host` package.

Instead, use the `testApplication` function with the existing [Ktor client](client-create-and-configure.md)
instance to make requests to your server and verify the results.

In the test below, the `handleRequest` function is replaced with the `client.get` request:

<compare first-title="1.x.x" second-title="3.0.x">

```kotlin
```

{src="https://raw.githubusercontent.com/ktorio/ktor-documentation/refs/heads/2.3.12/codeSnippets/snippets/engine-main/src/test/kotlin/EngineMainTest.kt"
include-lines="18-26"}

```kotlin
```

{src="https://raw.githubusercontent.com/ktorio/ktor-documentation/refs/heads/2.3.12/codeSnippets/snippets/engine-main/src/test/kotlin/EngineMainTest.kt"
include-lines="11-16"}

</compare>

For more information, see [](server-testing.md).

#### `TestApplication` module loading {id="test-module-loading"}

`TestApplication` no longer automatically loads modules from a configuration file (
e.g. `application.conf`). Instead, you must [explicitly load your modules](#explicit-module-loading) within the
`testApplication` function or [load the configuration file](#configure-env) manually.

##### Explicit module loading {id="explicit-module-loading"}

To explicitly load modules, use the `application` function within `testApplication`. This approach allows you to
manually specify which modules to load, providing greater control over your test setup.

<compare first-title="2.2.x" second-title="3.0.x">

```kotlin
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlin.test.*


class ApplicationTest {
  @Test
  fun testRoot() = testApplication {
    client.get("/").apply {
      assertEquals(HttpStatusCode.OK, status)
      assertEquals("Hello World!", bodyAsText())
    }
  }
}
```

{validate="false"}

```kotlin
import com.example.plugins.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlin.test.*


class ApplicationTest {
  @Test
  fun testRoot() = testApplication {
    application {
      configureRouting()
    }
    client.get("/").apply {
      assertEquals(HttpStatusCode.OK, status)
      assertEquals("Hello World!", bodyAsText())
    }
  }
}

```

{validate="false"}


</compare>

##### Load modules from a configuration file {id="configure-env"}

If you want to load modules from a configuration file, use the `environment` function to specify the configuration
file for your test.

```kotlin
```

{src="snippets/auth-oauth-google/src/test/kotlin/ApplicationTest.kt" include-lines="17-21,51"}

For more information on configuring the test application, see the [](server-testing.md) section.

### `CallLogging` plugin package has been renamed

The [
`CallLogging`](https://api.ktor.io/ktor-server/ktor-server-plugins/ktor-server-call-logging/io.ktor.server.plugins.calllogging/index.html)
plugin package has been renamed due to a typo.

| 2.x.x                               | 3.0.x                                |
|-------------------------------------|--------------------------------------|
| `io.ktor.server.plugins.callloging` | `io.ktor.server.plugins.calllogging` |

### `ktor-server-host-common` module has been removed

Due to `Application` requiring knowledge of `ApplicationEngine`, the contents of `ktor-server-host-common` module have
been merged into `ktor-server-core`, namely
the [`io.ktor.server.engine`](https://api.ktor.io/ktor-server/ktor-server-core/io.ktor.server.engine/index.html)
package.

Ensure that your dependencies are updated accordingly. In most cases, you can simply remove
the `ktor-server-host-common` dependency.

### `Locations` plugin has been removed

The `Locations` plugin for the Ktor server has been removed. To create type-safe routing, use
the [Resources plugin](server-resources.md) instead. This requires the following changes:

* Replace the `io.ktor:ktor-server-locations` artifact with `io.ktor:ktor-server-resources`.

* The `Resources` plugin depends on the Kotlin serialization plugin. To add the serialization plugin, see the
* [kotlinx.serialization setup](https://github.com/Kotlin/kotlinx.serialization#setup).

* Update the plugin import from `io.ktor.server.locations.*` to `io.ktor.server.resources.*`.

* Additionally, import the `Resource` module from `io.ktor.resources`.

The following example shows how to implement these changes:

<compare first-title="2.2.x" second-title="3.0.x">

```kotlin
import io.ktor.server.locations.*

@Location("/articles")
class article(val value: Int)

fun Application.module() {
    install(Locations)
    routing {
        get<article> {
            // Get all articles ...
            call.respondText("List of articles")
        }
    }
}
```

```kotlin
import io.ktor.resources.Resource
import io.ktor.server.resources.*

@Resource("/articles")
class Articles(val value: Int)

fun Application.module() {
    install(Resources)
    routing {
        get<Articles> {
            // Get all articles ...
            call.respondText("List of articles")
        }
    }
}
```

</compare>

For more information on working with `Resources`, refer to [](server-resources.md).

### Replacement of `java.time` in WebSockets configuration

The [WebSockets](server-websockets.md) plugin configuration has been updated to use
Kotlin’s [Duration](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.time/-duration/) for the `pingPeriod` and
`timeout` properties. This replaces the previous use of `java.time.Duration` for a more
idiomatic Kotlin experience.

To migrate existing code to the new format, use the extension functions and properties from the `kotlin.time.Duration`
class to construct durations. In the following example, `Duration.ofSeconds()` is replaced with Kotlin’s `seconds`
extension:

<compare first-title="2.x.x" second-title="3.0.x">

```kotlin
import java.time.Duration
  
install(WebSockets) {
    pingPeriod = Duration.ofSeconds(15)
    timeout = Duration.ofSeconds(15)
    //..
}
```

```kotlin
import kotlin.time.Duration.Companion.seconds

install(WebSockets) {
    pingPeriod = 15.seconds
    timeout = 15.seconds
    //..
}
```

</compare>

You can use similar Kotlin duration extensions (`minutes`, `hours`, etc.) as needed for other duration configurations.
For more information, see the [Duration](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.time/-duration/)
documentation.

### Server socket `.bind()` is now suspending

To support asynchronous operations in JS and WasmJS environments, the `.bind()` function for server sockets in both
[`TCPSocketBuilder`](https://api.ktor.io/ktor-network/io.ktor.network.sockets/-tcp-socket-builder/index.html) and
[`UDPSocketBuilder`](https://api.ktor.io/ktor-network/io.ktor.network.sockets/-u-d-p-socket-builder/index.html) has been
updated to a suspending function. This means any calls to `.bind()` must now be made within a coroutine.

To migrate, ensure `.bind()` is only called within a coroutine or suspending function. Here's an example of using
`runBlocking`:

```kotlin
  runBlocking {
    val selectorManager = SelectorManager(Dispatchers.IO)
    val serverSocket = aSocket(selectorManager).tcp().bind("127.0.0.1", 9002)
    //...
}
```

For more information on working with sockets, see the [Sockets documentation](server-sockets.md).

## Multipart form data

### New default limit for binary and file items

In Ktor 3.0.0, a default limit of 50MB has been introduced for receiving binary and file items using
[`ApplicationCall.receiveMultipart()`](https://api.ktor.io/older/3.0.0/ktor-server/ktor-server-core/io.ktor.server.request/receive-multipart.html).
If a received file or binary item exceeds the 50MB limit, an `IOException` is thrown.

#### Override the default limit

If your application previously relied on handling files larger than 50MB without explicit configuration,
you will need to update your code to avoid unexpected behaviour.

To override the default limit, pass the `formFieldLimit` parameter when calling `.receiveMultipart()`:

```kotlin
```

{src="snippets/upload-file/src/main/kotlin/uploadfile/UploadFile.kt" include-lines="18"}


### `PartData.FileItem.streamProvider()` is deprecated

In previous versions of Ktor, the `.streamProvider()` function in `PartData.FileItem` was used to access a file
item's content as an `InputStream`. Starting with Ktor 3.0.0, this function has been deprecated.

To migrate your application, replace `.streamProvider()` with the
[`.provider()`](https://api.ktor.io/ktor-http/io.ktor.http.content/-part-data/-file-item/provider.html)
function. The `.provider()` function returns a `ByteReadChannel`, which is a coroutine-friendly, non-blocking
abstraction for reading streams of bytes incrementally.
You can then stream data directly from the channel to the file output, using the
[`.copyTo()`](https://api.ktor.io/ktor-io/io.ktor.utils.io/copy-to.html) or
[`.copyAndClose()`](https://api.ktor.io/ktor-io/io.ktor.utils.io/copy-and-close.html)
methods provided by `ByteReadChannel`.

In the example the `.copyAndClose()` method transfers data from the `ByteReadChannel`
to a file's `WritableByteChannel`.

<compare first-title="2.x.x" second-title="3.0.x">

```kotlin
fun Application.main() {
    routing {
        post("/upload") {
            val multipart = call.receiveMultipart()
            multipart.forEachPart { partData ->
                if (partData is PartData.FileItem) {
                    var fileName = partData.originalFileName as String
                    val file = File("uploads/$fileName")
                    file.writeBytes(partData.streamProvider().readBytes())
                }
                // ...
            }
        }
    }
}
```

```kotlin
fun Application.main() {
    routing {
        post("/upload") {
            val multipart = call.receiveMultipart()
            multipart.forEachPart { partData ->
                if (partData is PartData.FileItem) {
                    var fileName = partData.originalFileName as String
                    val file = File("uploads/$fileName")
                    partData.provider().copyAndClose(file.writeChannel())
                }
                // ...
            }
        }
    }
}
```

</compare>

For the full example and more information on working with multipart form data,
see [Request handling of multipart form data](server-requests.md#form_data).

### Session encryption method update

The encryption method offered by the `Sessions` plugin has been updated to enhance
security.

Specifically, the `SessionTransportTransformerEncrypt` method, which previously derived the MAC from the decrypted
session value, now computes it from the encrypted value.

To ensure compatibility with existing sessions, Ktor has introduced the `backwardCompatibleRead` property. For current
configurations, include the property in the constructor
of `SessionTransportTransformerEncrypt`:

```kotlin
install(Sessions) {
  cookie<UserSession>("user_session") {
    // ...
    transform(
      SessionTransportTransformerEncrypt(
        secretEncryptKey, // your encrypt key here
        secretSignKey, // your sign key here
        backwardCompatibleRead = true
      )
    )
  }
}
```

For more information on session encryption in Ktor, see [](server-sessions.md#sign_encrypt_session).

## Ktor Client

### Renaming of `HttpResponse`'s `content` property

Prior to Ktor 3.0.0, the `content` property of
[`HttpResponse`](https://api.ktor.io/ktor-client/ktor-client-core/io.ktor.client.statement/-http-response/index.html)
provided a raw `ByteReadChannel` to the response content as it is read from the network. Starting with Ktor 3.0.0, the
`content` property has been renamed to `rawContent` to better reflect its purpose.

### `SocketTimeoutException` is now a typealias

[`SocketTimeoutException`](https://api.ktor.io/older/3.0.0/ktor-client/ktor-client-core/io.ktor.client.network.sockets/-socket-timeout-exception/index.html)
from the `io.ktor.client.network.sockets` package has been converted from a Kotlin class to an alias for a Java class.
This change may cause a `NoClassDefFoundError` in certain cases and may require updates to existing code.

To migrate your application, ensure your code is not referencing the old class and is compiled with the latest Ktor
version. Here's how to update exception checks:

<compare type="top-bottom" first-title="2.x.x" second-title="3.0.x">
    <code-block lang="kotlin">
    if (exception is io.ktor.client.network.sockets.SocketTimeoutException) { ... }
    </code-block>
    <code-block lang="kotlin">
    if (exception is java.net.SocketTimeoutException) { ... }
    </code-block>
</compare>

## Shared modules

### Migration to `kotlinx-io`

With the 3.0.0 release, Ktor has transitioned to using the `kotlinx-io` library, which provides a standardized and
efficient I/O API across Kotlin libraries. This change improves performance, reduces memory allocations, and simplifies
I/O handling. If your project interacts with Ktor's low-level I/O APIs, you may need to update your code to ensure
compatibility.

This impacts many classes, such as
[`ByteReadChannel`](https://api.ktor.io/older/3.0.0/ktor-io/io.ktor.utils.io/-byte-read-channel.html)
and
[`ByteWriteChannel`](https://api.ktor.io/older/3.0.0/ktor-io/io.ktor.utils.io/-byte-write-channel/index.html)
. Additionally, the following Ktor classes
are now backed by `kotlinx-io`, and their previous implementations are deprecated:

| Ktor 2.x                                  | Ktor 3.x                  |
|-------------------------------------------|---------------------------|
| `io.ktor.utils.io.core.Buffer`            | `kotlinx.io.Buffer`       |
| `io.ktor.utils.io.core.BytePacketBuilder` | `kotlinx.io.Sink`         |
| `io.ktor.utils.io.core.ByteReadPacket`    | `kotlinx.io.Source`       |
| `io.ktor.utils.io.core.Input`             | `kotlinx.io.Source`       |
| `io.ktor.utils.io.core.Output`            | `kotlinx.io.Sink`         |
| `io.ktor.utils.io.core.Sink`              | `kotlinx.io.Buffer`       |
| `io.ktor.utils.io.errors.EOFException`    | `kotlinx.io.EOFException` |
| `io.ktor.utils.io.errors.IOException`     | `kotlinx.io.IOException`  |


The deprecated APIs will be supported until Ktor 4.0, but we recommend migrating as soon as possible. To migrate your
application, update your code to utilize the corresponding methods from `kotlinx-io`.

#### Example: Streaming I/O

If you are handling large file downloads and need an efficient streaming solution, you can replace manual byte array
handling with `kotlinx-io`'s optimized streaming APIs.

In Ktor 2.x, handling large file downloads typically involved manually reading available bytes using
`ByteReadChannel.readRemaining()` and writing them to a file using `File.appendBytes()`:

```Kotlin
val client = HttpClient(CIO)
val file = File.createTempFile("files", "index")

runBlocking {
    client.prepareGet("https://ktor.io/").execute { httpResponse ->
        val channel: ByteReadChannel = httpResponse.body()
        while (!channel.isClosedForRead) {
            val packet = channel.readRemaining(DEFAULT_BUFFER_SIZE.toLong())
            while (!packet.isEmpty) {
                val bytes = packet.readBytes()
                file.appendBytes(bytes)
                println("Received ${file.length()} bytes from ${httpResponse.contentLength()}")
            }
        }
        println("A file saved to ${file.path}")
    }
}
```

This approach involved multiple memory allocations and redundant data copies.

In Ktor 3.x, `ByteReadChannel.readRemaining()` now returns a `Source`, enabling streaming of data using
`Source.transferTo()`:

```Kotlin
```
{src="snippets/client-download-streaming/src/main/kotlin/com/example/Application.kt" include-lines="15-36"}

This approach transfers data directly from the channel to the file's sink, minimizing memory
allocations and improving performance.

For the full example,
see [client-download-streaming](https://github.com/ktorio/ktor-documentation/tree/%ktor_version%/codeSnippets/snippets/client-download-streaming).

> For more details on API replacements, refer to the
> [`kotlinx-io` documentation](https://kotlinlang.org/api/kotlinx-io/).

### Attribute keys now require exact type matching

In Ktor 3.0.0, [`AttributeKey`](https://api.ktor.io/older/3.0.0/ktor-utils/io.ktor.util/-attribute-key.html)
instances are now compared by identity and require exact type matching when storing
and retrieving values. This ensures type-safety and prevents unintended behaviour caused by mismatched types.

Previously, it was possible to retrieve an attribute with a different generic type than it was stored with, such as
using `getOrNull<Any>()` to fetch an `AttributeKey<Boolean>`.

To migrate your application, ensure the retrieval type matches the stored type exactly:

```kotlin
val attrs = Attributes()

attrs.put(AttributeKey<Boolean>("key"), true)
attrs.getOrNull<Boolean>("key")
```

### Removal of empty artifact

Since Ktor 1.0.0, the empty artifact `io.ktor:ktor` was published
to [Maven](https://repo1.maven.org/maven2/io/ktor/ktor/) by mistake. This artifact has been removed starting with 
Ktor 3.0.0.

If your project includes this artifact as a dependency, you can safely remove it.