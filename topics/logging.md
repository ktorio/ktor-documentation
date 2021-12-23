[//]: # (title: Logging)

<microformat>
<var name="example_name" value="logging"/>
<include src="lib.xml" include-id="download_example"/>
</microformat>

Ktor provides the capability to log application events using the [SLF4J](http://www.slf4j.org/) library. You can also install and configure the [CallLogging](call-logging.md) plugin to log client requests.

## Add Logback dependencies {id="add_dependencies"}
To enable logging, you need to include [Logback](https://logback.qos.ch/) artifacts in the build script:

<var name="group_id" value="ch.qos.logback"/>
<var name="artifact_name" value="logback-classic"/>
<var name="version" value="logback_version"/>
<include src="lib.xml" include-id="add_artifact"/>


## Configure Logback {id="configure-logback"}

To configure Logback, you need to put a `logback.xml` file in the root of the classpath (for example, in `src/main/resources`). The example below shows a sample Logback configuration with the `STDOUT` appender, which outputs logs to the console.

```xml
```
{style="block" src="snippets/logging/src/main/resources/logback.xml"}

If you want to output logs to a file, you can use the `FILE` appender.

```xml
```
{style="block" src="snippets/logging/src/main/resources/logback-fileAppender.xml"}

Learn more about configuring Logback from [Logback configuration](http://logback.qos.ch/manual/configuration.html).


## Access the logger {id="access_logger"}
The Logger instance is represented by a class that implements the [Logger](http://www.slf4j.org/api/org/slf4j/Logger.html) interface. You can access the Logger instance inside the `Application` using the [Application.log](https://api.ktor.io/ktor-server/ktor-server-core/io.ktor.server.application/log.html) property. For example, the code snippet below shows how to add a message to a log inside the [module](Modules.md).

```kotlin
```
{src="snippets/logging/src/main/kotlin/com/example/Application.kt" lines="12-13,35"}

You can also access the Logger from [ApplicationCall](https://api.ktor.io/ktor-server/ktor-server-core/io.ktor.server.application/-application-call/index.html) using the `call.application.environment.log` property.

```kotlin
```
{src="snippets/logging/src/main/kotlin/com/example/Application.kt" lines="26-28,30,34"}

To enable logging of client requests, you can use the [CallLogging](call-logging.md) plugin.
