[//]: # (title: First App)

<include src="lib.md" include-id="outdated_warning"/>

In this tutorial you will learn how to create a simple self-hosted Ktor server application that responds to HTTP requests with `Hello, World!`.
Ktor applications can be built using common build systems such as [Maven](Maven.md) or [Gradle](Gradle.md).





## Including the right dependencies
{id="dependencies"}

Ktor is split up into several groups of artifacts,
allowing you to include only the functionality that you will need. And thus reducing the size of a fat-jar containing all the code, and the startup time.

In this case, you only need to include the artifact `ktor-server-netty`.
For a list of all the artifacts available, please check the [Artifacts](artifacts.md) page.  

Release versions of these dependencies are available at jcenter and maven central.
For pre-releases we host them on [Bintray kotlin/ktor](https://bintray.com/kotlin/ktor).

For a more detailed guide on setting up build files with different build systems check:

* [Setting up Gradle Build](Gradle.md)
* [Setting up Maven Build](Maven.md)

## Creating a self-hosted Application
{id="self-hosted"}

Ktor allows applications to run within an Application Server compatible with Servlets, such as Tomcat,
or as an embedded application, using Jetty, Netty or CIO.

In this tutorial, we are going to create a self-hosted application using Netty.

You can start by calling the `embeddedServer` function, passing in the engine factory as the first argument,
the port as the second argument, and the actual application code as the fourth argument (third argument
is the host which is `0.0.0.0` by default).

The code below defines a single route that responds to the `GET` verb on the URL `/` with
the text `Hello, world!`

After defining the routes, you have to start the server by calling the `server.start` method,
passing as argument a boolean to indicate whether you want the main thread of the application to block.


```kotlin
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*

fun main(args: Array<String>) {
    val server = embeddedServer(Netty, 8080) {
        routing {
            get("/") {
                call.respondText("Hello, world!", ContentType.Text.Html)
            }
        }
    }
    server.start(wait = true)
}
```


>If your server is just listening for HTTP requests and do not want to do anything else after that in the setup,
>you will normally call the server.start with `wait = true`.
>
{type="note"}

## Running the Application
{id="running"}

Given that the entry point of your application is the standard Kotlin `main` function, 
you can simply run it, effectively starting the server and listening on the specified port.

Checking the `localhost:8080` page in your browser, you should see the `Hello, world!` text. 

## Next Steps
{id="next-steps"}

This was the simplest example of getting a self-hosted Ktor application up and running. 
