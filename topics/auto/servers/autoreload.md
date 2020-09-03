[//]: # (title: Autoreload)
[//]: # (caption: Saving Time with Automatic Reloading)
[//]: # (category: servers)
[//]: # (permalink: /servers/autoreload.html)
[//]: # (keywords: autoreload watchpaths)
[//]: # (ktor_version_review: 1.0.0)

During development, it is important to have a fast feedback loop cycle. 
Often, restarting the server can take some time, so Ktor provides a basic auto-reload facility that
reloads your Application classes.

>Autoreload is experimental under JDK9+. If it doesn't work, feel free to [open an issue](https://github.com/ktorio/ktor/issues/new/choose) and 
>please stick to **JDK 8** in this case.
>There is a performance penalty when using auto-reloading. So keep in mind that you should not use it in production or when doing benchmarks.
>
{type="note" id="java9"}

## Automatic reloading on class changes
{id="basics"}

In both cases, when using the [embeddedServer](#embedded-server) or a [configuration file](#configuration-file), you will have to provide a list of watch substrings
that should match the classloaders you want to watch.

So for example, a typical class loader when using gradle would look like: \\
`/Users/user/projects/ktor-exercises/solutions/exercise4/build/classes/kotlin/main`

In this case, you can use the `solutions/exercise4` string or just `exercise4` when watching, so it will match that classloader.

## Using embeddedServer
{id="embedded-server"}

When using a custom main and `embeddedServer`,
you can use the optional parameter `watchPaths` to provide
a list of sub-paths that will be watched and reloaded.

```kotlin
fun main(args: Array<String>) {
    embeddedServer(
        Netty,
        watchPaths = listOf("solutions/exercise4"),
        port = 8080,
        module = Application::mymodule
    ).apply { start(wait = true) }
}

fun Application.mymodule() {
    routing {
        get("/plain") {
            call.respondText("Hello World!")
        }
    }
}
```

When using `watchPaths` you should *not* use a lambda to configure the server, but to provide a method reference to your
Application module.
{ .note}

If you try to use a lambda instead of a method reference, you will get the following error:
```
Exception in thread "main" java.lang.RuntimeException: Module function provided as lambda cannot be unlinked for reload
```

To fix this error, you just have to extract your lambda body to an Application extension method (module) just like this:


❌ Code that *won't* work:
```kotlin
fun main(args: Array<String>) {
    // ERROR! Module function provided as lambda cannot be unlinked for reload
    embeddedServer(Netty, watchPaths = listOf("solutions/exercise4"), port = 8080) {
        routing {
            get("/") {
                call.respondText("Hello World!")
            }
        }
    }.start(true)
}
```
{ .error }



✅ Code that will work:
```kotlin
fun main(args: Array<String>) {
    embeddedServer(
        Netty, watchPaths = listOf("solutions/exercise4"), port = 8080,
        // GOOD!, it will work 
        module = Application::mymodule
    ).start(true)
}

// Body extracted to a function acting as a module
fun Application.mymodule() {
    routing {
        get("/") {
            call.respondText("Hello World!")
        }
    }
}
```
{ .success }


{% include two-column.html left=left right=right %}

## Using the `application.conf`
{id="configuration-file"}

When using a configuration file, for example with an [`EngineMain`](/servers/engine.html) to either run
from the command line or hosted within a server container: 

To enable this feature, add `watch` keys to `ktor.deployment` configuration. 

`watch` - Array of classpath entries that should be watched and automatically reloaded.

```
ktor {
    deployment {
        port = 8080
        watch = [ module1, module2 ]
    }
    
    …
}
```

For now watch keys are just strings that are matched with `contains`, against the classpath entries in the loaded 
application, such as a jar name or a project directory name. 
These classes are then loaded with a special `ClassLoader` that is recycled when a change is detected.

`ktor-server-core` classes are specifically excluded from auto-reloading, so if you are working on something in ktor itself, 
don't expect it to be reloaded automatically. It cannot work because core classes are loaded before the auto-reload kicks in. 
The exclusion can potentially be smaller, but it is hard to analyze all the transitive closure of types loaded during
startup.
{ .note}

Classpath entries look like `file:///path/to/project/build/classes/myproject.jar`, so `to/project` would match, but `com.mydomain` would not.
{ .note}

## Recompiling automatically on source changes

Since the Autoreload feature only detects changes in class files, you have to compile the application by yourself.
You can do it using IntelliJ IDEA with `Build -> Build Project` while running.

However, you can also use gradle to automatically detect source changes and compile it for you. You can just open
another terminal in your project folder and run: `gradle -t installDist`.

It will compile the application, and after doing so,
it will listen for additional source changes and recompile when necessary. And thus, triggering Automatic class reloading.

You can then use another terminal to run the application with `gradle run`. If you use IntelliJ IDEA to run the application, you should properly configure its [compilation output locations](https://www.jetbrains.com/help/idea/specifying-compilation-settings.html) because it uses a different output location from that gradle uses.

## Example
{id="example"}

Consider the following example:

You can run the application by using either a `build.gradle` or directly within your IDE.
Executing the main method in the example file, or by executing: `io.ktor.server.netty.EngineMain.main`.
EngineMain using `commandLineEnvironment` will be in charge of loading the `application.conf` file (that is in HOCON format).


```kotlin
package io.ktor.exercise.autoreload

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*

// Exposed as: `static void io.ktor.exercise.autoreload.MainKt.main(String[] args)`
fun main(args: Array<String>) {
    //io.ktor.server.netty.main(args) // Manually using Netty's EngineMain
    embeddedServer(
        Netty, watchPaths = listOf("solutions/exercise4"), port = 8080,
        module = Application::module
    ).apply { start(wait = true) 
}

// Exposed as: `static void io.ktor.exercise.autoreload.MainKt.module(Application receiver)`
fun Application.module() {
    routing {
        get("/plain") {
            call.respondText("Hello World!")
        }
    }
}
```



```kotlin
ktor {
    deployment {
        port = 8080
        watch = [ solutions/exercise4 ]
    }

    application {
        modules = [ io.ktor.exercise.autoreload.MainKt.module ]
    }
}
```


{% include tabbed-code.html
    tab1-title="main.kt" tab1-content=main-kt
    tab2-title="application.conf" tab2-content=application-conf
%}

As you can see, you need to specify a list of strings to match the classloaders you want to watch –in this case only `solutions/exercise4`– which should then be reloaded upon modification.