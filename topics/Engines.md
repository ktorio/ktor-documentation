[//]: # (title: Engines)

To run a Ktor server application, you need to create and configure a server at first.
Server configuration can include different settings: a server engine, a host and port values, various engine-specific options, and so on. The following engines are supported:
- Netty
- Jetty
- Tomcat
- CIO (Coroutine-based I/O)

In addition to the engines mentioned above, Ktor provides a special engine type `TestEngine` for testing application logic. You can learn more about it from [](Testing.md).

## Add Dependencies {id="dependencies"}
Before using the desired engine, you need to add a corresponding dependency to your [build.gradle](Gradle.md) or [pom.xml](Maven.md) file:
* `ktor-server-netty`
* `ktor-server-jetty`
* `ktor-server-tomcat`
* `ktor-server-cio`

For example, you can add a Netty artifact in the following ways:

<tabs>
    <tab title="Gradle (Groovy)">
        <code style="block" lang="Groovy" title="Sample">
            implementation "io.ktor:ktor-server-netty:$ktor_version"
        </code>
    </tab>
    <tab title="Gradle (Kotlin)">
        <code style="block" lang="Kotlin" title="Sample">
            implementation("io.ktor:ktor-server-netty:$ktor_version")
        </code>
    </tab>
    <tab title="Maven">
        <code style="block" lang="XML" title="Sample">
        <![CDATA[
        <dependency>
            <groupId>io.ktor</groupId>
            <artifactId>ktor-server-netty</artifactId>
            <version>${ktor_version}</version>
        </dependency>
        ]]>
        </code>
   </tab>
</tabs>


For the [testing](Testing.md), you need to add the `ktor-server-test-host` dependency. 
There is also the `ktor-server-servlet` dependency that allows you to run an application in a servlet container like Jetty or Tomcat. Learn more at [](containers.md).


## Configure Engines {id="configure"}
A Ktor application can be run in two main ways: using the `embeddedServer` or `EngineMain`. `embeddedServer` allows you to run and configure a server in code while `EngineMain` tries to load an `application.conf` configuration file. You can learn more about configuring Ktor from [](Configurations.md).

### embeddedServer {id="embeddedServer"}
The [embeddedServer](https://api.ktor.io/%ktor_version%/io.ktor.server.engine/embedded-server.html) function accepts the required engine factory as a parameter:
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
In this case, you can configure the required server parameters in code. Learn more from the [](Configurations.md) topic.

### EngineMain {id="EngineMain"}
`EngineMain` represents a development engine for running a server. You can use the following engines:
* `io.ktor.server.netty.EngineMain`
* `io.ktor.server.jetty.EngineMain`
* `io.ktor.server.tomcat.EngineMain`
* `io.ktor.server.cio.EngineMain`

The `EngineMain.main` function can be used to start a server with the selected engine:
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

In this case, you need to point your build system to `EngineMain` as a main class:

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





