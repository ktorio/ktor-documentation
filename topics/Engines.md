[//]: # (title: Engines)

To run a Ktor server application, you need to create and configure a server first.
Server configuration can include different settings: a server engine, various engine-specific options, host and port values, and so on. The following engines are supported:
- Netty
- Jetty
- Tomcat
- CIO (Coroutine-based I/O)

[//]: # (TODO: update a testing link)
In addition to the engines mentioned above, Ktor provides a special engine type `TestEngine` for testing application logic. You can learn more about it from [](Testing.md).

## Add Dependencies {id="dependencies"}
Before using the desired engine, you need to add a corresponding dependency to your [build.gradle](Gradle.md) or [pom.xml](Maven.md) file:
* `ktor-server-netty`
* `ktor-server-jetty`
* `ktor-server-tomcat`
* `ktor-server-cio`

Below are examples of adding a dependency for Netty:
<var name="artifact_name" value="ktor-server-netty"/>
<include src="lib.md" include-id="add_ktor_artifact"/>

> For [testing](Testing.md), you need to add the `ktor-server-test-host` dependency. 
There is also the `ktor-server-servlet` dependency that allows you to run an application in a servlet container like Jetty or Tomcat. Learn more at [](containers.md).


## Configure Engines {id="configure"}
A Ktor server application can be run in two ways: 
* [embeddedServer](#embeddedServer) is a simple way to configure server parameters in code and quickly run an application.
* [EngineMain](#EngineMain) provides more flexibility to configure a server. You can specify server parameters in an `application.conf` file and change a configuration without recompiling your application. Moreover, you can run your application from a command line and override the required server parameters by passing corresponding command-line arguments.
  
You can learn more about configuring Ktor from [](Configurations.md).

### embeddedServer {id="embeddedServer"}

The [embeddedServer](https://api.ktor.io/%ktor_version%/io.ktor.server.engine/embedded-server.html) function accepts an engine factory used to create an engine of a specific type. In the example below, we pass the [Netty](https://api.ktor.io/%ktor_version%/io.ktor.server.netty/-netty/index.html) factory to run a server with the Netty engine and listen on the `8000` port:

```kotlin
import io.ktor.server.netty.*
    
fun main() {
    embeddedServer(Netty, port = 8000) {
        routing {
            get ("/") {
                call.respondText("Hello, world!")
            }
        }
    }.start(wait = true)
}
```

### EngineMain {id="EngineMain"}
`EngineMain` represents an engine for running a server. You can use the following engines:
* `io.ktor.server.netty.EngineMain`
* `io.ktor.server.jetty.EngineMain`
* `io.ktor.server.tomcat.EngineMain`
* `io.ktor.server.cio.EngineMain`

The `EngineMain.main` function is used to start a server with the selected engine and can accept command-line server parameters. In the example below, we start a server from the application's `main` function:

```kotlin
fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

fun Application.module(testing: Boolean = false) {
    routing {
        get("/") {
            call.respondText("Hello, world!")
        }
    }
}
```
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
            <![CDATA[
        <properties>
            <main.class>io.ktor.server.netty.EngineMain</main.class>
        </properties>
            ]]>
            </code>
       </tab>
</tabs>
