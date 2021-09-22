[//]: # (title: Shutdown URL)

<include src="lib.xml" include-id="outdated_warning"/>

This plugin enables a URL that when accessed, shutdowns the server.

There are two ways to use it: [Automatically using HOCON](#hocon) and [Installing the plugin](#install)



## Automatically using HOCON
{id="hocon"}

You can configure a shutdown URL using HOCON with the 
[ktor.deployment.shutdown.url](Configurations.xml#predefined-properties) property.

```kotlin
ktor {
    deployment {
        shutdown.url = "/my/shutdown/path"
    }
}
```

## Installing the plugin
{id="install"}

You can manually install the plugin, with `ShutDownUrl.ApplicationCallPlugin` and set the `shutDownUrl` and an `exitCodeSupplier`:

```kotlin
install(ShutDownUrl.ApplicationCallPlugin) {
    // The URL that will be intercepted
    shutDownUrl = "/ktor/application/shutdown"
    // A function that will be executed to get the exit code of the process
    exitCodeSupplier = { 0 } // ApplicationCall.() -> Int
}
```