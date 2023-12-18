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

- `start()` and `stop()` methods have been removed from `ApplicationEnvironment`.
- `embeddedServer()`
  returns [`EmbeddedServer`](https://api.ktor.io/older/3.0.0-beta-1/ktor-server/ktor-server-core/io.ktor.server.engine/-embedded-server/index.html)
  instead of `ApplicationEngine`.
- A new
  entity,[`ApplicationPropertiesBuilder`](https://api.ktor.io/older/3.0.0-beta-1/ktor-server/ktor-server-core/io.ktor.server.application/-application-properties-builder/index.html),
  is introduced for configuring `Application` properties.
- `ApplicationEngineEnvironmentBuilder` has been renamed to `ApplicationEnvironmentBuilder`.
- `applicationEngineEnvironment` has been renamed to `applicationEnvironment`.

These changes will impact existing code that relies on the previous model. For example, in the `embeddedServer()`
function, the changes can be illustrated through the following example:

<tabs group="ktor_versions">
<tab title="2.2.x" group-key="2_2">

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
})
```

</tab>
<tab title="3.0.x" group-key="3_0">

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

</tab>
</tabs>

For more details about this change,
see [issue KTOR-3857 on YouTrack](https://youtrack.jetbrains.com/issue/KTOR-3857/Environment-Engine-Application-Design).

### `ktor-server-host-common` module has been removed

Due to `Application` requiring knowledge of `ApplicationEngine`, the contents of `ktor-server-host-common` module have
been merged into `ktor-server-core`, namely
the [`io.ktor.server.engine`](https://api.ktor.io/older/3.0.0-beta-1/ktor-server/ktor-server-core/io.ktor.server.engine/index.html)
package.

Ensure that your dependencies are updated accordingly. In most cases, you can simply remove
the `ktor-server-host-common` dependency.

### `Locations` plugin has been removed

The `Locations` plugin for the Ktor server has been removed. To create type-safe routing, use
the [Resources plugin](type-safe-routing.md) instead. This requires the following changes:

* Replace the `io.ktor:ktor-server-locations` artifact with `io.ktor:ktor-server-resources`.

* The `Resources` plugin depends on the Kotlin
  serialization plugin. To add the serialization plugin, see the
  [kotlinx.serialization setup](https://github.com/Kotlin/kotlinx.serialization#setup).

* Update the plugin import from `io.ktor.server.locations.*` to `io.ktor.server.resources.*`.

* Additionally, import the `Resource` module from `io.ktor.resources`.

The following example shows how to implement these changes:

<tabs group="ktor_versions">
<tab title="2.2.x" group-key="2_2">

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

</tab>
<tab title="3.0.x" group-key="3_0">

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

</tab>
</tabs>

For more information on working with `Resources`, refer to [](type-safe-routing.md).

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

For more information on session encryption in Ktor, see [](sessions.md#sign_encrypt_session).