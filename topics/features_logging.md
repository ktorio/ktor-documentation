[//]: # (title: Logging)

<include src="lib.md" include-id="outdated_warning"/>

This feature adds multiplatform logging for HTTP calls.

## Installation

```kotlin
val client = HttpClient() {
    install(Logging) {
        logger = Logger.DEFAULT
        level = LogLevel.HEADERS
    }
}
```

>To use this feature, you need to include `io.ktor:ktor-client-logging-jvm` artifact on the JVM and `ktor-client-logging-native` on iOS.
>
{type="note"}