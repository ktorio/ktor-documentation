[//]: # (title: Migrating from 2.2.x to 3.0.x)

<show-structure for="chapter" depth="2"/>

This guide provides instructions on how to migrate your Ktor application from the 2.2.x version to 3.0.x.

## Ktor Server {id="server"}

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
<tab title="2.2.x" group-key="1_6">

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
<tab title="3.0.x" group-key="2_0">

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