[//]: # (title: Logging)

<include src="lib.md" include-id="outdated_warning"/>

This feature adds multiplatform logging for HTTP calls.

## Add Dependencies {id="add_dependencies"}
To enable logging, you need to include the following artifacts in the build script:
* JVM: 
  
   <var name="artifact_name" value="ktor-client-logging-jvm"/>
   <include src="lib.md" include-id="add_ktor_artifact"/>

* JS:

   <var name="artifact_name" value="ktor-client-logging-js"/>
   <include src="lib.md" include-id="add_ktor_artifact"/>

## Installation {id="installation"}

```kotlin
val client = HttpClient() {
    install(Logging) {
        logger = Logger.DEFAULT
        level = LogLevel.HEADERS
    }
}
```
