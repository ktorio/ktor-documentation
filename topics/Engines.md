[//]: # (title: Engines)

Ktor can run in many environments, such as Netty, Jetty or any other Servlet-compatible Application Container such as Tomcat.
Running a Ktor server application requires creating a server.
A server can be run using different engines.

- Netty
- Jetty
- Tomcat
- CIO (Coroutine-based I/O)

In addition to the mentioned frameworks, Ktor provides a special engine type `TestEngine`for testing application logic. You can learn more from [](Testing.md).

## Add Dependencies {id="dependencies"}
To use the desired engine, you need to add and install a corresponding dependency at first. For example, if you are using [Gradle](Gradle.md) in you project:

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

- `ktor-server-jetty`
- `ktor-server-tomcat`
- `ktor-server-cio`
  For [](Testing.md), you need `ktor-server-test-host`.

For [deploying](#deployment), `ktor-server-servlet`.


## Configure Engines {id="configure"}
A Ktor application can be run in two main ways:

- Using the [embeddedServer](https://api.ktor.io/%ktor_version%/io.ktor.server.engine/embedded-server.html). Configure a server in code. Pass the required engine factory as a parameter.
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

- Using `EngineMain.main` for the required engine. In this case, you configure the server engine to be used with the `mainClassName` pointing to a particular `EngineMain`.
```kotlin
fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

fun Application.module(testing: Boolean = false) {
    routing {
        get ("/") {
            call.respondText("Hello, world!")
        }
    }
}
```

Learn more at [](Configurations.md).



## Deployment {id="deployment"}



