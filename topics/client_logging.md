[//]: # (title: Logging)

<tldr>
<p>
<b>Required dependencies</b>: <code>io.ktor:ktor-client-logging</code>
</p>
<var name="example_name" value="client-logging"/>
<include src="lib.topic" element-id="download_example"/>
</tldr>

Ktor client provides the capability to log HTTP calls using the [Logging](https://api.ktor.io/ktor-client/ktor-client-plugins/ktor-client-logging/io.ktor.client.plugins.logging/-logging/index.html) plugin.
This plugin provides different logger types for different platforms:
- On [JVM](http-client_engines.md#jvm), Ktor uses [SLF4J API](http://www.slf4j.org/) as a facade for various logging frameworks (for example, [Logback](https://logback.qos.ch/) or [Log4j](https://logging.apache.org/log4j)).
- For [Native targets](http-client_engines.md#native), the `Logging` plugin provides a logger that prints everything to `STDOUT`.
- For [multiplatform projects](getting_started_ktor_client_multiplatform_mobile.md), you can specify a [custom logger](#custom_logger) (for example, [Napier](https://github.com/AAkira/Napier)).


## Add dependencies {id="add_dependencies"}
To enable logging, you need to include the following artifacts in the build script:
* (Optional) An artifact with the required SLF4J implementation, for example, [Logback](https://logback.qos.ch/):
  <var name="group_id" value="ch.qos.logback"/>
  <var name="artifact_name" value="logback-classic"/>
  <var name="version" value="logback_version"/>
  <include src="lib.topic" element-id="add_artifact"/>
  
* The `ktor-client-logging` artifact:
  <var name="artifact_name" value="ktor-client-logging"/>
  <include src="lib.topic" element-id="add_ktor_artifact"/>
  <include src="lib.topic" element-id="add_ktor_client_artifact_tip"/>
  

## Install Logging {id="install_plugin"}
To install `Logging`, pass it to the `install` function inside a [client configuration block](create-client.md#configure-client):
```kotlin
val client = HttpClient(CIO) {
    install(Logging)
}
```

## Configure Logging {id="configure_plugin"}

The `Logging` plugin configuration is provided by the [Logging.Config](https://api.ktor.io/ktor-client/ktor-client-plugins/ktor-client-logging/io.ktor.client.plugins.logging/-logging/-config/index.html) class.
The example below shows a sample configuration:
- The `logger` property is set to `Logger.DEFAULT`, which uses an SLF4J logging framework. For Native targets, set this property to `Logger.SIMPLE`.
- The `level` property specifies the logging level. 
   For instance, you can log only request/response headers or include their bodies.
- The `filter` function allows you to filter log messages for requests matching the specified predicate. In the example below, only requests made to `ktor.io` get into the log.

```kotlin
```
{src="snippets/client-logging/src/main/kotlin/com/example/Application.kt"}

You can find the full example here: [client-logging](https://github.com/ktorio/ktor-documentation/tree/%current-branch%/codeSnippets/snippets/client-logging).


### Provide a custom logger {id="custom_logger"}

To use a custom logger in your client application, you need to create a `Logger` instance and override the `log` function.
The example below shows how to use the [Napier](https://github.com/AAkira/Napier) library to log HTTP calls:

```kotlin
```
{src="snippets/client-logging-napier/src/main/kotlin/com/example/Application.kt" include-symbol="main"}

You can find the full example here: [client-logging-napier](https://github.com/ktorio/ktor-documentation/tree/%current-branch%/codeSnippets/snippets/client-logging-napier).
