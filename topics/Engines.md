[//]: # (title: Engines)

To run a Ktor server application, you need to create and configure a server first.
Server configuration can include different settings: a server engine, various engine-specific options, host and port values, and so on. The following engines are supported:
- Netty
- Jetty
- Tomcat
- CIO (Coroutine-based I/O)

[//]: # (TODO: update a testing link)
In addition to the engines mentioned above, Ktor provides a special engine type `TestEngine` for testing application logic. You can learn more about it from [](Testing.md).

>Those are the official engines developed for Ktor, but it is also possible to [create
>your own engines](custom_engines.md) and provide custom configurations for them.
>
{type="note"}

## Add dependencies {id="dependencies"}
Before using the desired engine, you need to add the corresponding dependency to your [build.gradle](Gradle.xml) or [pom.xml](Maven.xml) file:
* `ktor-server-netty`
* `ktor-server-jetty`
* `ktor-server-tomcat`
* `ktor-server-cio`

Below are examples of adding a dependency for Netty:
<var name="artifact_name" value="ktor-server-netty"/>
<include src="lib.xml" include-id="add_ktor_artifact"/>

> For [testing](Testing.md), you need to add the `ktor-server-test-host` dependency. 
There is also the `ktor-server-servlet` dependency that allows you to run an application in a servlet container like Jetty or Tomcat. Learn more at [](war.md).


## Choose how to create a server {id="choose-create-server"}
A Ktor server application can be [created and run in two ways](create_server.xml): using the [embeddedServer](#embeddedServer) to quickly pass server parameters in code, or using [EngineMain](#EngineMain) to load the configuration from the external `application.conf` file.

### embeddedServer {id="embeddedServer"}

The [embeddedServer](https://api.ktor.io/ktor-server/ktor-server-host-common/ktor-server-host-common/io.ktor.server.engine/embedded-server.html) function accepts an engine factory used to create an engine of a specific type. In the example below, we pass the [Netty](https://api.ktor.io/ktor-server/ktor-server-netty/ktor-server-netty/io.ktor.server.netty/-netty/index.html) factory to run a server with the Netty engine and listen on the `8000` port:

```kotlin
```
{src="snippets/embedded-server/src/main/kotlin/com/example/Application.kt"}

### EngineMain {id="EngineMain"}
`EngineMain` represents an engine for running a server. You can use the following engines:
* `io.ktor.server.netty.EngineMain`
* `io.ktor.server.jetty.EngineMain`
* `io.ktor.server.tomcat.EngineMain`
* `io.ktor.server.cio.EngineMain`

The `EngineMain.main` function is used to start a server with the selected engine and loads the [application module](Modules.md) specified in the external [application.conf](Configurations.xml) file. In the example below, we start a server from the application's `main` function:

<tabs>
<tab title="Application.kt">

```kotlin
```
{src="snippets/engine-main/src/main/kotlin/com/example/Application.kt"}

</tab>

<tab title="application.conf">

```kotlin
```
{src="snippets/engine-main/src/main/resources/application.conf"}

</tab>
</tabs>



If you need to start a server using a build system task, you need to configure the required `EngineMain` as the main class:

<tabs>
        <tab title="Gradle (Groovy)">
            <code style="block" lang="Groovy" title="Sample">
                mainClassName = "io.ktor.server.netty.EngineMain"
            </code>
        </tab>
        <tab title="Gradle (Kotlin)">
            <code style="block" lang="Kotlin" title="Sample">
    application {
        mainClassName = "io.ktor.server.netty.EngineMain"
    }
            </code>
        </tab>
        <tab title="Maven">
            <code style="block" lang="XML" title="Sample">
        &lt;properties&gt;
            &lt;main.class&gt;io.ktor.server.netty.EngineMain&lt;/main.class&gt;
        &lt;/properties&gt;
            </code>
       </tab>
</tabs>


## Configure an engine {id="configure-engine"}

In this section, we'll take a look how to specify various engine-specific options.

### embeddedServer {id="embedded-server-configure"}

The `embeddedServer` function allows you to pass engine-specific options using the `configure` optional parameter. This parameter includes options common for all engines and exposed by the [ApplicationEngine.Configuration](https://api.ktor.io/ktor-server/ktor-server-host-common/ktor-server-host-common/io.ktor.server.engine/-application-engine/-configuration/index.html) class.

```kotlin
```
{src="snippets/_misc/EmbeddedServerConfigureEngine.kt"}

In addition to these options, you can configure additional engine-specific properties.

#### Netty
{.no_toc}

Netty-specific options are exposed by the [NettyApplicationEngine.Configuration](https://api.ktor.io/ktor-server/ktor-server-netty/ktor-server-netty/io.ktor.server.netty/-netty-application-engine/-configuration/index.html) class.

```kotlin
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
```

#### Jetty
{.no_toc}

If you use Jetty as the engine, you can configure the Jetty server inside the [configureServer](https://api.ktor.io/ktor-server/ktor-server-jetty/ktor-server-jetty/io.ktor.server.jetty/-jetty-application-engine-base/-configuration/configure-server.html) block, which provides access to a
[Server](https://www.eclipse.org/jetty/javadoc/jetty-11/org/eclipse/jetty/server/Server.html) instance.

```kotlin
embeddedServer(Jetty, configure = {
    configureServer = { // this: Server ->
        // ...
    } 
}) {
    // ...
}.start(true)
```

#### CIO
{.no_toc}

CIO-specific options are exposed by the [CIOApplicationEngine.Configuration](https://api.ktor.io/ktor-server/ktor-server-cio/ktor-server-cio/io.ktor.server.cio/-c-i-o-application-engine/-configuration/index.html) class.

```kotlin
embeddedServer(CIO, configure = {
    connectionIdleTimeoutSeconds = 45
}) {
    // ...
}.start(true)
```

#### Tomcat
{.no_toc}

If you use Tomcat as the engine, you can configure it using the [configureTomcat](https://api.ktor.io/ktor-server/ktor-server-tomcat/ktor-server-tomcat/io.ktor.server.tomcat/-tomcat-application-engine/-configuration/configure-tomcat.html) property, which provides access to a
[Tomcat](https://tomcat.apache.org/tomcat-9.0-doc/api/org/apache/catalina/startup/Tomcat.html) instance.

```kotlin
embeddedServer(Tomcat, configure = {
    configureTomcat = { // this: Tomcat ->
        // ...
    }
}) {
    // ...
}.start(true)
```



### EngineMain {id="engine-main-configure"}

If you use `EngineMain`, you can specify options common for all engines in the [application.conf](Configurations.xml#hocon-file) file within the `ktor.deployment` group.

```shell
ktor {
    deployment {
        connectionGroupSize = 2
        workerGroupSize = 5
        callGroupSize = 10
    }
}
```
