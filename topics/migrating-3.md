[//]: # (title: Migrating from 2.2.x to 3.0.x)

<show-structure for="chapter" depth="2"/>

This guide provides instructions on how to migrate your Ktor application from the 2.2.x version to 3.0.x.

## Ktor Server {id="server"}

### `ApplicationEngine`, `ApplicationEnvironment`, and `Application`

Several design changes have been introduced to improve configurability and provide a more defined
separation between the `ApplicationEngine`, `ApplicationEnvironment` and `Application` instances.

Before v3.0.0, `ApplicationEngine` managed `ApplicationEnvironment`, which in turn managed `Application`.

In the current design, however, `Application` is responsible for creating, owning, and initiating
both `ApplicationEngine` and `ApplicationEnvironment`.

This restructuring comes with the following set of breaking changes:

- [`ApplicationEngineEnvironmentBuilder` and `applicationEngineEnvironment` classes are renamed.](#renamed-classes).
- [`start()` and `stop()` methods are removed from `ApplicationEngineEnvironment`](#ApplicationEnvironment).
- [Introduction of `ApplicationPropertiesBuilder`](#ApplicationPropertiesBuilder).
- [`embeddedServer()` returns`EmbeddedServer`](#EmbeddedServer) instead of `ApplicationEngine`.

These changes will impact existing code that relies on the previous model.

#### Renamed classes {id="renamed-classes"}

| Package                    | 2.x.x                                 | 3.0.x                           |
|----------------------------|---------------------------------------|---------------------------------|
| `io.ktor:ktor-server-core` | `ApplicationEngineEnvironmentBuilder` | `ApplicationEnvironmentBuilder` |
| `io.ktor:ktor-server-core` | `applicationEngineEnvironment`        | `applicationEnvironment`        |

#### `start()` and `stop()` methods are removed from `ApplicationEngineEnvironment` {id="ApplicationEnvironment"}

With the merge of `AplicationEngineEnvironment`
to [`ApplicationEnvironment`](https://api.ktor.io/older/3.0.0-beta-1/ktor-server/ktor-server-core/io.ktor.server.application/-application-environment/index.html),
the `start()` and `stop()` methods are now
only accessible
through [`ApplicationEngine`](https://api.ktor.io/older/3.0.0-beta-1/ktor-server/ktor-server-core/io.ktor.server.engine/-application-engine/index.html).

| 2.x.x                                                 | 3.0.x                                |
|-------------------------------------------------------|--------------------------------------|
| `ApplicationEngineEnvironment.start()`                | `ApplicationEngine.start()`          |
| `ApplicationEngineEnvironment.stop()`                 | `ApplicationEngine.stop()`           |

Additionally, in the following table you can see the list of removed properties
and their current corresponding ownership:

| 2.x.x                                           | 3.0.x                                        |
|-------------------------------------------------|----------------------------------------------|
| `ApplicationEngineEnvironment.connectors`       | `ApplciationEngine.Configuration.connectors` |
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

{validate="false" noinject}

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

#### Introduction of `ApplicationPropertiesBuilder` {id="ApplicationPropertiesBuilder"}

A new
entity,[`ApplicationPropertiesBuilder`](https://api.ktor.io/older/3.0.0-beta-1/ktor-server/ktor-server-core/io.ktor.server.application/-application-properties-builder/index.html),
is introduced for configuring `Application` properties represented by
the [`ApplicationProperties`](https://api.ktor.io/older/3.0.0-beta-1/ktor-server/ktor-server-core/io.ktor.server.application/-application-properties/index.html)
class. The class contains properties, previously available in `ApplicationEnvironment`.

#### Introduction of `EmbeddedServer` {id="EmbeddedServer"}

The
class [`EmbeddedServer`](https://api.ktor.io/older/3.0.0-beta-1/ktor-server/ktor-server-core/io.ktor.server.engine/-embedded-server/index.html)
is introduced and used to replace `ApplicationEngine` as a return type of the `embeddedServer()`
function.

For more details about the model change,
see [issue KTOR-3857 on YouTrack](https://youtrack.jetbrains.com/issue/KTOR-3857/Environment-Engine-Application-Design).

### `TestApplication` explicit loading of modules

`TestApplication` no longer automatically loads modules from a configuration file (
e.g. `application.conf`).
Instead, modules must now be explicitly loaded using the `application` function within `testApplication`.

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
{validate="false" noinject}

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
{validate="false" noinject}


</compare>

This change provides greater control over the modules used during testing. For more information on how to configure a test
application, see the [](server-testing.md) section.

### `CallLogging` plugin package has been renamed

The [`CallLogging`](https://api.ktor.io/ktor-server/ktor-server-plugins/ktor-server-call-logging/io.ktor.server.plugins.calllogging/index.html)
plugin package has been renamed due to a typo.

| 2.x.x                               | 3.0.x                                |
|-------------------------------------|--------------------------------------|
| `io.ktor.server.plugins.callloging` | `io.ktor.server.plugins.calllogging` |


### `ktor-server-host-common` module has been removed

Due to `Application` requiring knowledge of `ApplicationEngine`, the contents of `ktor-server-host-common` module have
been merged into `ktor-server-core`, namely
the [`io.ktor.server.engine`](https://api.ktor.io/older/3.0.0-beta-1/ktor-server/ktor-server-core/io.ktor.server.engine/index.html)
package.

Ensure that your dependencies are updated accordingly. In most cases, you can simply remove
the `ktor-server-host-common` dependency.

### `Locations` plugin has been removed

The `Locations` plugin for the Ktor server has been removed. To create type-safe routing, use
the [Resources plugin](server-resources.md) instead. This requires the following changes:

* Replace the `io.ktor:ktor-server-locations` artifact with `io.ktor:ktor-server-resources`.

* The `Resources` plugin depends on the Kotlin
  serialization plugin. To add the serialization plugin, see the
  [kotlinx.serialization setup](https://github.com/Kotlin/kotlinx.serialization#setup).

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