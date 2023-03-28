[//]: # (title: Choosing an engine)

<show-structure for="chapter" depth="3"/>

<link-summary>
Learn about engines that process network requests.
</link-summary>

To run a Ktor server application, you need to [create](create_server.topic) and configure a server first.
Server configuration includes different settings:
- an [engine](#supported-engines) for processing network requests;
- host and port values used to access a server;
- SSL settings;
- ... and so on.

## Supported engines {id="supported-engines"}

The table below lists engines supported by Ktor, along with the supported platforms:

| Engine                             | Platforms                                              | HTTP/2 |
|------------------------------------|--------------------------------------------------------|--------|
| `Netty`                            | JVM                                                    | ✅      |
| `Jetty`                            | JVM                                                    | ✅      |
| `Tomcat`                           | JVM                                                    | ✅      |
| `CIO` (Coroutine-based I/O)        | JVM, [Native](native_server.md), [GraalVM](Graalvm.md) | ✖️     |
| [ServletApplicationEngine](war.md) | JVM                                                    | ✅      |


## Add dependencies {id="dependencies"}
Before using the desired engine, you need to add the corresponding dependency to your [build script](server-dependencies.topic):
* `ktor-server-netty`
* `ktor-server-jetty`
* `ktor-server-tomcat`
* `ktor-server-cio`

Below are examples of adding a dependency for Netty:

<var name="artifact_name" value="ktor-server-netty"/>
<include from="lib.topic" element-id="add_ktor_artifact"/>


## Choose how to create a server {id="choose-create-server"}
A Ktor server application can be [created and run in two ways](create_server.topic#embedded): using the [embeddedServer](#embeddedServer) to quickly pass server parameters in code, or using [EngineMain](#EngineMain) to load the configuration from the external `application.conf` or `application.yaml` file.

### embeddedServer {id="embeddedServer"}

The [embeddedServer](https://api.ktor.io/ktor-server/ktor-server-host-common/io.ktor.server.engine/embedded-server.html) function accepts an engine factory used to create an engine of a specific type. In the example below, we pass the [Netty](https://api.ktor.io/ktor-server/ktor-server-netty/io.ktor.server.netty/-netty/index.html) factory to run a server with the Netty engine and listen on the `8080` port:

```kotlin
```
{src="snippets/embedded-server/src/main/kotlin/com/example/Application.kt"}

### EngineMain {id="EngineMain"}
`EngineMain` represents an engine for running a server. You can use the following engines:
* `io.ktor.server.netty.EngineMain`
* `io.ktor.server.jetty.EngineMain`
* `io.ktor.server.tomcat.EngineMain`
* `io.ktor.server.cio.EngineMain`

The `EngineMain.main` function is used to start a server with the selected engine and loads the [application module](Modules.md) specified in the external [configuration file](Configuration-file.topic). In the example below, we start a server from the application's `main` function:

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



If you need to start a server using a build system task, you need to configure the required `EngineMain` as the main class:

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

The `embeddedServer` function allows you to pass engine-specific options using the `configure` optional parameter. This parameter includes options common for all engines and exposed by the [ApplicationEngine.Configuration](https://api.ktor.io/ktor-server/ktor-server-host-common/io.ktor.server.engine/-application-engine/-configuration/index.html) class.

```kotlin
```
{src="snippets/_misc/EmbeddedServerConfigureEngine.kt"}

In addition to these options, you can configure additional engine-specific properties.

#### Netty {id="netty-code"}

Netty-specific options are exposed by the [NettyApplicationEngine.Configuration](https://api.ktor.io/ktor-server/ktor-server-netty/io.ktor.server.netty/-netty-application-engine/-configuration/index.html) class.

```kotlin
import io.ktor.server.engine.*
import io.ktor.server.netty.*

fun main() {
    embeddedServer(Netty, configure = {
        requestQueueLimit = 16
        shareWorkGroup = false
        configureBootstrap = {
            // ...
        }
        responseWriteTimeoutSeconds = 10
    }) {
        // ...
    }.start(true)
}
```

#### Jetty {id="jetty-code"}

If you use Jetty as the engine, you can configure the Jetty server inside the [configureServer](https://api.ktor.io/ktor-server/ktor-server-jetty/io.ktor.server.jetty/-jetty-application-engine-base/-configuration/configure-server.html) block, which provides access to a
[Server](https://www.eclipse.org/jetty/javadoc/jetty-11/org/eclipse/jetty/server/Server.html) instance.

```kotlin
import io.ktor.server.engine.*
import io.ktor.server.jetty.*

fun main() {
    embeddedServer(Jetty, configure = {
        configureServer = { // this: Server ->
            // ...
        }
    }) {
        // ...
    }.start(true)
}
```

#### CIO {id="cio-code"}

CIO-specific options are exposed by the [CIOApplicationEngine.Configuration](https://api.ktor.io/ktor-server/ktor-server-cio/io.ktor.server.cio/-c-i-o-application-engine/-configuration/index.html) class.

```kotlin
import io.ktor.server.engine.*
import io.ktor.server.cio.*

fun main() {
    embeddedServer(CIO, configure = {
        connectionIdleTimeoutSeconds = 45
    }) {
        // ...
    }.start(true)
}
```

#### Tomcat {id="tomcat-code"}

If you use Tomcat as the engine, you can configure it using the [configureTomcat](https://api.ktor.io/ktor-server/ktor-server-tomcat/io.ktor.server.tomcat/-tomcat-application-engine/-configuration/configure-tomcat.html) property, which provides access to a
[Tomcat](https://tomcat.apache.org/tomcat-9.0-doc/api/org/apache/catalina/startup/Tomcat.html) instance.

```kotlin
import io.ktor.server.engine.*
import io.ktor.server.tomcat.*

fun main() {
    embeddedServer(Tomcat, configure = {
        configureTomcat = { // this: Tomcat ->
            // ...
        }
    }) {
        // ...
    }.start(true)
}
```



### In a configuration file {id="engine-main-configure"}

If you use `EngineMain`, you can specify options common for all engines in a [configuration file](Configuration-file.topic) within the `ktor.deployment` group.

<tabs group="config">
<tab title="application.conf" group-key="hocon">

```shell
ktor {
    deployment {
        connectionGroupSize = 2
        workerGroupSize = 5
        callGroupSize = 10
        shutdownGracePeriod = 2000
        shutdownTimeout = 3000
    }
}
```

</tab>
<tab title="application.yaml" group-key="yaml">

```yaml
ktor:
    deployment:
        connectionGroupSize: 2
        workerGroupSize: 5
        callGroupSize: 10
        shutdownGracePeriod: 2000
        shutdownTimeout: 3000
```

</tab>
</tabs>


#### Netty {id="netty-file"}

You can also configure Netty-specific options in a configuration file within the `ktor.deployment` group:

<tabs group="config">
<tab title="application.conf" group-key="hocon">

```shell
ktor {
    deployment {
        maxInitialLineLength = 2048
        maxHeaderSize = 1024
        maxChunkSize = 42
    }
}
```

</tab>
<tab title="application.yaml" group-key="yaml">

```yaml
ktor:
    deployment:
        maxInitialLineLength: 2048
        maxHeaderSize: 1024
        maxChunkSize: 42
```

</tab>
</tabs>




