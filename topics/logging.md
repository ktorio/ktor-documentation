[//]: # (title: Logging)

<microformat>
<var name="example_name" value="logging"/>
<include src="lib.xml" include-id="download_example"/>
</microformat>

Ktor uses [SLF4J API](http://www.slf4j.org/) as a facade for various logging frameworks (for example, [Logback](https://logback.qos.ch/) or [Log4j](https://logging.apache.org/log4j)) and allows you to log application events. 
To enable logging, you need to add [dependencies](#add_dependencies) for the desired framework and provide [configuration](#configure-logger) specific for this framework.

> You can also install and configure the [CallLogging](call-logging.md) plugin to log client requests.


## Add logger dependencies {id="add_dependencies"}
To enable logging, you need to include artifacts for the desired logging framework.
For example, Logback requires the following dependency:

<var name="group_id" value="ch.qos.logback"/>
<var name="artifact_name" value="logback-classic"/>
<var name="version" value="logback_version"/>
<include src="lib.xml" include-id="add_artifact"/>

To use Log4j, you need to add the `org.apache.logging.log4j:log4j-core` and `org.apache.logging.log4j:log4j-slf4j-impl` artifacts.


## Configure logger {id="configure-logger"}

To learn how to configure the selected logging framework, see its documentation, for example:
- [Logback configuration](http://logback.qos.ch/manual/configuration.html)
- [Log4j configuration](https://logging.apache.org/log4j/2.x/manual/configuration.html)

For instance, to configure Logback, you need to put a `logback.xml` file in the root of the classpath (for example, in `src/main/resources`). 
The example below shows a sample Logback configuration with the `STDOUT` appender, which outputs logs to the console.

```xml
```
{style="block" src="snippets/logging/src/main/resources/logback.xml"}

If you want to output logs to a file, you can use the `FILE` appender.

```xml
```
{style="block" src="snippets/logging/src/main/resources/logback-fileAppender.xml"}

You can find the full example here: [logging](https://github.com/ktorio/ktor-documentation/tree/%current-branch%/codeSnippets/snippets/logging).



## Access logger in code {id="access_logger"}
The Logger instance is represented by a class that implements the [Logger](http://www.slf4j.org/api/org/slf4j/Logger.html) interface. You can access the Logger instance inside the `Application` using the [Application.log](https://api.ktor.io/ktor-server/ktor-server-core/io.ktor.server.application/log.html) property. For example, the code snippet below shows how to add a message to a log inside the [module](Modules.md).

```kotlin
```
{src="snippets/logging/src/main/kotlin/com/example/Application.kt" lines="12-13,35"}

You can also access the Logger from [ApplicationCall](https://api.ktor.io/ktor-server/ktor-server-core/io.ktor.server.application/-application-call/index.html) using the `call.application.environment.log` property.

```kotlin
```
{src="snippets/logging/src/main/kotlin/com/example/Application.kt" lines="26-28,30,34"}

To enable logging of client requests, you can use the [CallLogging](call-logging.md) plugin.
