[//]: # (title: Logging)
[//]: # (category: clients)
[//]: # (caption: Logging)
[//]: # (feature: feature)
[//]: # (artifact: io.ktor)
[//]: # (class: io.ktor.client.features.logging.Logging)
[//]: # (ktor_version_review: 1.2.0)

This feature adds multiplatform logging for HTTP calls.

{% include 
    mpp_feature.html
    targets="common,jvm,native,js"
    base="ktor-client-logging"
    classifiers=",-jvm,-native,-js"
%}

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