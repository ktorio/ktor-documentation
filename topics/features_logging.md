[//]: # (title: Logging)

<microformat>
<var name="example_name" value="client-logging"/>
<include src="lib.xml" include-id="download_example"/>
</microformat>

Ktor client provides the capability to log HTTP calls using the `Logging` feature. This feature uses the [SLF4J](http://www.slf4j.org/) library for logging.


## Add dependencies {id="add_dependencies"}
To enable logging, you need to include the following artifacts in the build script:
* An artifact with the required SLF4J implementation, for example, [Logback](https://logback.qos.ch/):
  <var name="group_id" value="ch.qos.logback"/>
  <var name="artifact_name" value="logback-classic"/>
  <var name="version" value="logback_version"/>
  <include src="lib.xml" include-id="add_artifact"/>
  
* The `ktor-client-logging` artifact:
  <var name="artifact_name" value="ktor-client-logging"/>
  <include src="lib.xml" include-id="add_ktor_artifact"/>
  

## Install Logging {id="install_feature"}
To install `Logging`, pass it to the `install` function inside a [client configuration block](client.md#configure-client):
```kotlin
val client = HttpClient(CIO) {
    install(Logging)
}
```

## Configure Logging {id="configure_feature"}
The `Logging` feature allows you to configure the desired logger using the `logger` property and the level of logging using the `level` property:
```kotlin
```
{src="/snippets/client-logging/src/main/kotlin/com/example/Application.kt"}