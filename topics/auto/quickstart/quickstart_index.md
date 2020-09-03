[//]: # (title: Quick Start)
[//]: # (caption: QuickStart)
[//]: # (category: quickstart)
[//]: # (toc: true)
[//]: # (permalink: /quickstart/index.html)
[//]: # (children: /quickstart/quickstart/)
[//]: # (ktor_version_review: 1.0.0)

![Ktor logo](ktor_logo.svg){style="width:134px;height:56px;"}
 
Ktor is a framework to easily build connected applications – web applications, HTTP services, mobile and browser applications.
Modern connected applications need to be asynchronous to provide the best experience to users, and Kotlin coroutines provide
awesome facilities to do it in an easy and straightforward way. 

While not yet entirely there, the goal of Ktor is to provide an end-to-end multiplatform application framework for connected applications. 
Currently, JVM client and server scenarios are supported, as well as JavaScript, iOS and Android clients, and we are working on bringing server facilities to native
environments, and client facilities to other native targets.





## Set up a Ktor project

You can set up a Ktor project using [Maven](/quickstart/quickstart/maven.html), [Gradle](/quickstart/quickstart/gradle.html), [start.ktor.io](/quickstart/generator.html#) and the [IntelliJ Plugin](/quickstart/quickstart/intellij-idea.html).

The plugin allows you to create a Ktor project as well as [start.ktor.io](/quickstart/generator.html#), but with the additional convenience of being fully integrated in the IDE.

### 1) In a first step, you can configure the project to generate and select features to install:
![](ktor-plugin-1.png){ width="100" }

### 2) In a second step, you can configure the project artifacts:
![](ktor-plugin-2.png){ width="100" }

And that's it. A new project will be created and opened inside your IDE.

## Hello World

A simple hello world in Ktor looks like this:

![Ktor Hello World](ktor_hello_world_main.png){ width="100" }

1. Here you define a plain callable *main method*.
2. Then you create an embedded *server using Netty* as the back-end that will listen on *port 8080*.
3. Installs the *routing feature* with a block where you can define routes for specific paths and HTTP methods.
4. Actual routes: In this case, it will handle a *GET request* for the path `/demo`, and will reply with a `HELLO WORLD!` message.
5. Actually *start the server* and wait for connections.


```kotlin
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*

fun main(args: Array<String>) {
    val server = embeddedServer(Netty, port = 8080) {
        routing {
            get("/") {
                call.respondText("Hello World!", ContentType.Text.Plain)
            }
            get("/demo") {
                call.respondText("HELLO WORLD!")
            }
        }
    }
    server.start(wait = true)
}
```


{% include tabbed-code.html
    tab1-title="Main.kt" tab1-content=main-kt
    no-height="true"
%}

## Accessing your application

Since you have a main method, you can execute it with your IDE. That will open a HTTP server,
listening on [http://127.0.0.1:8080](http://127.0.0.1:8080/), You can try opening it with your favorite web browser.

If that doesn't work, maybe your computer is using that port already. You can try changing the
port 8080 (in line 10) and adjust it as needed.
{ .note}

![Ktor Hello World Browser](screenshot.png){ width="50""}

At this point you should have a very simple Web Back-end running, so you can make changes,
and see the results in your browser.

Since you have configured a Gradle project with the application plugin and the `mainClassName`,
you can also run it from a terminal using `./gradlew run` on Linux/Mac, or `gradlew run` on a Windows machine.
{.note}

{:comment}
## Next step

Now we are ready for the next step. *What kind of application are you developing?*

1. [RESTful API: Let's serve a *data class* as JSON](/quickstart/restful.html)
2. Web Application:
    * [Let's describe and serve some HTML, fully typed, using kotlinx.html the DSL way](/quickstart/html-dsl.html)
    * [Let's serve some HTML using FreeMarker template engine](/quickstart/html-freemarker.html)
    
{/comment}

## Walkthroughs

{% include category-list.html %}