[//]: # (title: Modules)

<include src="lib.xml" include-id="outdated_warning"/>

A Ktor Application consists of a series of one or more modules, each of which can house any kind of functionality. 

![App Diagram](app-diagram.svg)
 
 
[comment]: <> (Each module consists of....)

![Module Diagram](module-diagram.svg)

A Ktor module is just a user-defined function receiving the `Application` class that is in charge of configuring
the server pipeline, install plugins, registering routes, handling requests, etc.

You have to specify the modules to load when the server starts in [the `application.conf` file](Configurations.xml#hocon-file).

A simple module function would look like this:


```kotlin
package com.example.myapp

fun Application.mymodule() {
    routing {
        get("/") {
            call.respondText("Hello World!")
        }
    }
}
```

Of course, you can split the module function in several smaller functions or classes.

Modules are referenced by their fully qualified name: the fully qualified name of the class and the method name,
separated by a dot (`.`).

So for the example, the module's fully qualified name would be:

```kotlin
com.example.myapp.MainKt.mymodule
```

>`mymodule` is an extension method of the class `Application` (where `Application` is the *receiver*).
>Since it is defined as a top-level function, Kotlin creates a JVM class with a `Kt` suffix (`FileNameKt`),
>and adds the extension method as a static method with the receiver as its first parameter.
>In this case, the class name is `MainKt` in the `com.example.myapp` package, and the Java method signature would be
>`static public void mymodule(Application app)`.
>
{type="note"}

