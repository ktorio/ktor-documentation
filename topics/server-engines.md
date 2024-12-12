[//]: # (title: Choosing an engine)

<show-structure for="chapter" depth="3"/>

<link-summary>
Learn about engines that process network requests.
</link-summary>

To run a Ktor server application, you need to [create](server-create-and-configure.topic) and configure a server first.
Server configuration includes different settings:
- an [engine](#supported-engines) for processing network requests;
- host and port values used to access a server;
- SSL settings;
- ... and so on.

## Supported engines {id="supported-engines"}

The table below lists engines supported by Ktor, along with the supported platforms:

| Engine                                  | Platforms                                            | HTTP/2 |
|-----------------------------------------|------------------------------------------------------|--------|
| `Netty`                                 | JVM                                                  | ✅      |
| `Jetty`                                 | JVM                                                  | ✅      |
| `Tomcat`                                | JVM                                                  | ✅      |
| `CIO` (Coroutine-based I/O)             | JVM, [Native](server-native.md), [GraalVM](graalvm.md) | ✖️     |
| [ServletApplicationEngine](server-war.md) | JVM                                                  | ✅      |

## Add dependencies {id="dependencies"}

Before using the desired engine, you need to add the corresponding dependency to
your [build script](server-dependencies.topic):

* `ktor-server-netty`
* `ktor-server-jetty`
* `ktor-server-tomcat`
* `ktor-server-cio`

Below are examples of adding a dependency for Netty:

<var name="artifact_name" value="ktor-server-netty"/>
<include from="lib.topic" element-id="add_ktor_artifact"/>

## Choose how to create a server {id="choose-create-server"}
A Ktor server application can be [created and run in two ways](server-create-and-configure.topic#embedded): using
the [embeddedServer](#embeddedServer) to quickly pass server parameters in code, or using [EngineMain](#EngineMain) to
load the configuration from the external `application.conf` or `application.yaml` file.

### embeddedServer {id="embeddedServer"}

The [embeddedServer](https://api.ktor.io/ktor-server/ktor-server-core/io.ktor.server.engine/embedded-server.html)
function accepts an engine factory used to create an engine of a specific type. In the example below, we pass
the [Netty](https://api.ktor.io/ktor-server/ktor-server-netty/io.ktor.server.netty/-netty/index.html) factory to run a
server with the Netty engine and listen on the `8080` port:

```kotlin
```

{src="snippets/embedded-server/src/main/kotlin/com/example/Application.kt" include-lines="3-7,13,28-35"}

### EngineMain {id="EngineMain"}

`EngineMain` represents an engine for running a server. You can use the following engines:

* `io.ktor.server.netty.EngineMain`
* `io.ktor.server.jetty.EngineMain`
* `io.ktor.server.tomcat.EngineMain`
* `io.ktor.server.cio.EngineMain`

The `EngineMain.main` function is used to start a server with the selected engine and loads
the [application module](server-modules.md) specified in the external [configuration file](server-configuration-file.topic). In the
example below, we start a server from the application's `main` function:

<tabs>
<tab title="Application.kt">

```kotlin
```

{src="snippets/engine-main/src/main/kotlin/com/example/Application.kt"}

</tab>

<tab title="application.conf">

```shell
```

{src="snippets/engine-main/src/main/resources/application.conf"}

</tab>

<tab title="application.yaml">

```yaml
```

{src="snippets/engine-main-yaml/src/main/resources/application.yaml"}

</tab>
</tabs>



If you need to start a server using a build system task, you need to configure the required `EngineMain` as the main
class:

<tabs group="languages" id="main-class-set-engine-main">
<tab title="Gradle (Kotlin)" group-key="kotlin">

```kotlin
application {
    mainClass.set("io.ktor.server.netty.EngineMain")
}
```

</tab>
<tab title="Gradle (Groovy)" group-key="groovy">

```groovy
mainClassName = "io.ktor.server.netty.EngineMain"
```

</tab>
<tab title="Maven" group-key="maven">

```xml
<properties>
    <main.class>io.ktor.server.netty.EngineMain</main.class>
</properties>
```

</tab>
</tabs>

## Configure an engine {id="configure-engine"}

In this section, we'll take a look at how to specify various engine-specific options.

### In code {id="embedded-server-configure"}

<include from="server-configuration-code.topic" element-id="embedded-engine-configuration"/>

### In a configuration file {id="engine-main-configure"}

<include from="server-configuration-file.topic" element-id="engine-main-configuration"/>