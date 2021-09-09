[//]: # (title: Call logging)

<microformat>
<var name="example_name" value="logging"/>
<include src="lib.xml" include-id="download_example"/>
</microformat>

<var name="feature_name" value="CallLogging"/>

Ktor provides the capability to log application events using the [SLF4J](http://www.slf4j.org/) library. You can learn about general logging configuration from the [](logging.md) topic.

The `%feature_name%` plugin (previously known as feature) allows you to log incoming client requests.


## Install %feature_name% {id="install_feature"}
<include src="lib.xml" include-id="install_feature"/>


## Configure logging settings {id="configure"}
You can configure `%feature_name%` in multiple ways: specify a logging level, filter requests based on a specified condition, customize log messages, and so on. You can see the available configuration settings at [CallLogging.Configuration](https://api.ktor.io/ktor-server/ktor-server-core/ktor-server-core/io.ktor.features/-call-logging/-configuration/index.html).
### Set the logging level {id="logging_level"}
By default, Ktor uses the `Level.TRACE` logging level. To change it, use the `level` property:
```kotlin
```
{src="snippets/logging/src/main/kotlin/com/example/Application.kt" lines="14-15,25"}

### Filter log requests {id="filter"}
The `filter` property allows you to add conditions for filtering requests. In the example below, only requests made to `/api/v1` get into a log:
```kotlin
```
{src="snippets/logging/src/main/kotlin/com/example/Application.kt" lines="14,16-18,25"}

### Customize a log message format {id="format"}
By using the `format` function, you can put any data related to a request/response into a log. The example below shows how to log a response status, a request HTTP method, and the `User-Agent` header value for each request.

```kotlin
```
{src="snippets/logging/src/main/kotlin/com/example/Application.kt" lines="14,19-25"}

You can find the full example here: [logging](https://github.com/ktorio/ktor-documentation/tree/main/codeSnippets/snippets/logging).


### Put call parameters in MDC {id="mdc"}
The `%feature_name%` plugin supports MDC (Mapped Diagnostic Context). You can put a desired context value with the specified name to MDC using the `mdc` function. For example, in the code snippet below, a `name` query parameter is added to MDC:

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
