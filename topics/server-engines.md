[//]: # (title: Server engines)

<show-structure for="chapter" depth="3"/>

<link-summary>
Learn about engines that process network requests.
</link-summary>

## Add dependencies {id="dependencies"}

Before using the desired engine, you need to add the corresponding dependency to your [build script](server-dependencies.topic):

* `ktor-server-netty`
* `ktor-server-jetty-jakarta`
* `ktor-server-tomcat-jakarta`
* `ktor-server-cio`

<tip>
Below are examples of adding a dependency for Netty:
Which is the most popular engine, and recommended JVM engine for Ktor.
</tip>

<var name="artifact_name" value="ktor-server-netty"/>
<include from="lib.topic" element-id="add_ktor_artifact"/>

To run a Ktor server application, you need to [create](server-create-and-configure.topic) and configure a server first.
Server configuration includes different settings:
- an [engine](#supported-engines) for processing network requests;
- host and port values used to access a server;
- SSL settings;
- ... and so on.

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
* `io.ktor.server.jetty.jakarta.EngineMain`
* `io.ktor.server.tomcat.jakarta.EngineMain`
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

### Common Configuration

Depending on how you [create your server](#choose-create-server) you can pass engine-specific options.
This can be done through the `configure` DSL for `embeddedServer`, which includes common options for all engines exposed by the [ApplicationEngine.Configuration](https://api.ktor.io/ktor-server/ktor-server-core/io.ktor.server.engine/-application-engine/-configuration/index.html) class.
`EngineMain` defines its configuration in `application.yaml` as shown below.

<tabs group="config">
<tab title="Application.kt" group-key="embeddedServer">

```kotlin
```

{src="snippets/embedded-server/src/main/kotlin/com/example/Application.kt" include-lines="75-88"}

</tab>
<tab title="application.conf" group-key="hocon">

```
ktor {
    deployment {
        port = 8080
        connectionGroupSize = 2
        workerGroupSize = 5
        callGroupSize = 10
        shutdownGracePeriod = 2000
        shutdownTimeout = 3000
    }
    application {
        modules = [ com.example.ApplicationKt.module ]
    }
}
```

</tab>
<tab title="application.yaml" group-key="yaml">

```yaml
ktor:
  deployment:
    port: 8080
    connectionGroupSize: 2
    workerGroupSize: 5
    callGroupSize: 10
    shutdownGracePeriod: 2000
    shutdownTimeout: 3000
  application:
    modules:
      - com.example.ApplicationKt.module
```

</tab>
</tabs>

In addition to these options, you can configure other engine-specific properties.

### Netty {id="netty-code"}

Netty-specific options are exposed by the [NettyApplicationEngine.Configuration](https://api.ktor.io/ktor-server/ktor-server-netty/io.ktor.server.netty/-netty-application-engine/-configuration/index.html) class.
The [NettyApplicationEngine.Configuration](https://api.ktor.io/ktor-server/ktor-server-netty/io.ktor.server.netty/-netty-application-engine/-configuration/index.html) properties are also available under `ktor.deployment` for file-based configuration.

<tabs group="config">
<tab title="Application.kt" group-key="embeddedServer">

```kotlin
```

{src="snippets/embedded-server/src/main/kotlin/com/example/Application.kt" include-lines="99-116"}

</tab>
<tab title="application.conf" group-key="hocon">

```
ktor {
    deployment {
        host = "0.0.0.0"
        port = 8080
        runningLimit = 16
        shareWorkGroup = false
        responseWriteTimeoutSeconds = 10
        requestReadTimeoutSeconds = 0
        tcpKeepAlive = false
        maxInitialLineLength = 4096
        maxHeaderSize = 8192
        maxChunkSize = 8192
    }
    application {
        modules = [ com.example.ApplicationKt.module ]
    }
}
```

</tab>
<tab title="application.yaml" group-key="yaml">

```yaml
ktor:
  application.modules:
    - org.jetbrains.Application.module
  deployment:
    host: 0.0.0.0
    port: 8080
    runningLimit: 16
    shareWorkGroup: false
    responseWriteTimeoutSeconds: 10
    requestReadTimeoutSeconds: 0
    tcpKeepAlive: false
    maxInitialLineLength: 4096
    maxHeaderSize: 8192
    maxChunkSize: 8192
```
</tab>
</tabs>

### CIO {id="cio-code"}

CIO-specific options are exposed by the [CIOApplicationEngine.Configuration](https://api.ktor.io/ktor-server/ktor-server-cio/io.ktor.server.cio/-c-i-o-application-engine/-configuration/index.html) class.
The [CIOApplicationEngine.Configuration](https://api.ktor.io/ktor-server/ktor-server-cio/io.ktor.server.cio/-c-i-o-application-engine/-configuration/index.html) properties are also available under `ktor.deployment` for file-based configuration.

<tabs group="config">
<tab title="Application.kt" group-key="embeddedServer">

```kotlin
```
{src="snippets/embedded-server/src/main/kotlin/com/example/CIO.kt" include-lines="7-12"}

</tab>
<tab title="application.conf" group-key="hocon">

```
ktor {
    deployment {
        host = "0.0.0.0"
        port = 8080
        connectionIdleTimeoutSeconds: 45
    }
    application {
        modules = [ com.example.ApplicationKt.module ]
    }
}
```

</tab>
<tab title="application.yaml" group-key="yaml">

```yaml
ktor:
  application.modules:
    - org.jetbrains.Application.module
  deployment:
    host: 0.0.0.0
    port: 8080
    connectionIdleTimeoutSeconds: 45
```
</tab>
</tabs>

### Jetty {id="jetty-code"}

Jetty-specific options are exposed by the [JettyApplicationEngineBase.Configuration](https://api.ktor.io/ktor-server/ktor-server-jetty-jakarta/io.ktor.server.jetty.jakarta/-jetty-application-engine-base/-configuration/index.html) class.
You can configure the Jetty server inside the [configureServer](https://api.ktor.io/ktor-server/ktor-server-jetty-jakarta/io.ktor.server.jetty.jakarta/-jetty-application-engine-base/-configuration/configure-server.html) block, which provides access to a [Server](https://www.eclipse.org/jetty/javadoc/jetty-11/org/eclipse/jetty/server/Server.html) instance.
Use the `idleTimeout` property to specify the duration of time a connection can be idle before it gets closed.

There is no special support for configuring Jetty from file-based configurations.

```kotlin
```
{src="snippets/embedded-server/src/main/kotlin/com/example/Jetty.kt" include-lines="9-16"}

### Tomcat {id="tomcat-code"}

If you use Tomcat as the engine, you can configure it using the [configureTomcat](https://api.ktor.io/ktor-server/ktor-server-tomcat-jakarta/io.ktor.server.tomcat.jakarta/-tomcat-application-engine/-configuration/configure-tomcat.html) property, which provides access to a [Tomcat](https://tomcat.apache.org/tomcat-10.1-doc/api/org/apache/catalina/startup/Tomcat.html) instance.
There is no special support for configuring Tomcat from file-based configurations.

```kotlin
```
{src="snippets/embedded-server/src/main/kotlin/com/example/Tomcat.kt" include-lines="7-16"}

## Supported engines {id="supported-engines"}

The table below lists engines supported by Ktor, along with the supported platforms:

| Engine                                  | Platforms                                            | HTTP/2 |
|-----------------------------------------|------------------------------------------------------|--------|
| `Netty`                                 | JVM                                                  | ✅      |
| `Jetty`                                 | JVM                                                  | ✅      |
| `Tomcat`                                | JVM                                                  | ✅      |
| `CIO` (Coroutine-based I/O)             | JVM, [Native](server-native.md), [GraalVM](graalvm.md) | ✖️     |
| [ServletApplicationEngine](server-war.md) | JVM                                                  | ✅      |

