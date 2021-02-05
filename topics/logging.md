[//]: # (title: Logging)

Ktor provides the capability to log application events using the [SLF4J](http://www.slf4j.org/) library. You can also install and configure the `CallLogging` feature to log client requests.

## Add Dependencies {id="add_dependencies"}
To enable logging, you need to include [Logback](https://logback.qos.ch/) artifacts in the build script:

<var name="group_id" value="ch.qos.logback"/>
<var name="artifact_name" value="logback-classic"/>
<var name="version" value="logback_version"/>
<include src="lib.md" include-id="add_artifact"/>


## Access the Logger {id="access_logger"}
The Logger instance is represented by a class that implements the [Logger](http://www.slf4j.org/api/org/slf4j/Logger.html) interface. The Logger instance in a Ktor application is created when building the [application environment](Configurations.xml), and this instance is assigned to the [ApplicationEnvironment.log](https://api.ktor.io/%ktor_version%/io.ktor.application/-application-environment/log.html) property. You can access the Logger from [ApplicationCall](https://api.ktor.io/%ktor_version%/io.ktor.application/-application-call/index.html) using the `call.application.environment.log` property:
```kotlin
    routing {
        get("/api/v1") {
            call.application.environment.log.info("Hello from /api/v1!")
        }
    }
```

You can also get access to the Logger using [Application.log](https://api.ktor.io/%ktor_version%/io.ktor.application/log.html).



## Call Logging {id="call_logging"}
<var name="feature_name" value="CallLogging"/>

The `%feature_name%` feature allows you to log incoming client requests.
<include src="lib.md" include-id="install_feature"/>

You can configure `%feature_name%` in multiple ways: specify a logging level, filter requests based on a specified condition, customize log messages, and so on. You can see the available configuration settings at [CallLogging.Configuration](https://api.ktor.io/%ktor_version%/io.ktor.features/-call-logging/-configuration/index.html).

### Set the Logging Level {id="logging_level"}
By default, Ktor uses the `Level.TRACE` logging level. To change it, use the `level` property:
```kotlin
import org.slf4j.event.Level
// ...
install(CallLogging) {
    level = Level.INFO
}
```

### Filter Log Requests {id="filter"}
The `filter` property allows you to add conditions for filtering requests. In the example below, only requests made to `/api/v1` get into a log:
```kotlin
install(CallLogging) {
    filter { call -> call.request.path().startsWith("/api/v1") }
}
```

### Customize a Log Message Format {id="format"}
By using the `format` function, you can put any data related to a request into a log. The example below shows how to show a `User-Agent` header value in a log:

```kotlin
    install(CallLogging) {
        format { call -> call.request.headers["User-Agent"].toString() }
    }
```


### Put Call Parameters in MDC {id="mdc"}
The `%feature_name%` feature supports MDC (Mapped Diagnostic Context). You can put a desired context value with the specified name to MDC using the `mdc` function. For example, in the code snippet below, a `name` query parameter is added to MDC:

```kotlin
install(CallLogging) {
    mdc("name-parameter") { call ->
        call.request.queryParameters["name"]
    }
}
```
You can access the added value during an `ApplicationCall` lifetime:
```kotlin
import org.slf4j.MDC
// ...
MDC.get("name-parameter")
```
