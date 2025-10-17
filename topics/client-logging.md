[//]: # (title: Logging in Ktor Client)

<show-structure for="chapter" depth="2"/>
<primary-label ref="client-plugin"/>

<tldr>
<p>
<b>Required dependencies</b>: <code>io.ktor:ktor-client-logging</code>
</p>
<var name="example_name" value="client-logging"/>
<include from="lib.topic" element-id="download_example"/>
</tldr>

Logging is a way to keep track of what your program is doing and diagnose problems by recording important events,
errors, or informational messages.

Ktor provides the capability to log HTTP calls using
the [Logging](https://api.ktor.io/ktor-client-logging/io.ktor.client.plugins.logging/-logging)
plugin.
This plugin provides different logger types for different platforms.

> On the server, Ktor provides the [Logging](server-logging.md) plugin for application logging and
> the [CallLogging](server-call-logging.md) plugin for logging client requests.

## JVM

<snippet id="jvm-logging">
  <p>
    On <a href="client-engines.md" anchor="jvm">JVM</a>, Ktor uses the Simple Logging Facade for Java
    (<a href="http://www.slf4j.org/">SLF4J</a>) as an
    abstraction layer for logging. SLF4J decouples the logging API from the underlying logging implementation, 
    allowing you to integrate the logging framework that best suits your application's requirements.
    Common choices include <a href="https://logback.qos.ch/">Logback</a> or 
    <a href="https://logging.apache.org/log4j">Log4j</a>. If no framework is provided, SLF4J will default to a 
    no-operation (NOP) implementation, which essentially disables
    logging.
  </p>

  <p>
    To enable logging, include an artifact with the required SLF4J implementation, such
    as <a href="https://logback.qos.ch/">Logback</a>:
  </p>
  <var name="group_id" value="ch.qos.logback"/>
  <var name="artifact_name" value="logback-classic"/>
  <var name="version" value="logback_version"/>
  <include from="lib.topic" element-id="add_artifact"/>
</snippet>

### Android

<p>
    On Android, we recommend to use the SLF4J Android library:
</p>
 <var name="group_id" value="org.slf4j"/>
  <var name="artifact_name" value="slf4j-android"/>
  <var name="version" value="slf4j_version"/>
<tabs group="languages">
    <tab title="Gradle (Kotlin)" group-key="kotlin">
        <code-block lang="Kotlin">
            implementation("%group_id%:%artifact_name%:$%version%")
        </code-block>
    </tab>
    <tab title="Gradle (Groovy)" group-key="groovy">
        <code-block lang="Groovy">
            implementation "%group_id%:%artifact_name%:$%version%"
        </code-block>
    </tab>
</tabs>

## Native

For [Native targets](client-engines.md#native), the `Logging` plugin provides a logger that prints everything
to the standard output stream (`STDOUT`).

## Multiplatform

In [multiplatform projects](client-create-multiplatform-application.md), you can specify
a [custom logger](#custom_logger), such as [Napier](https://github.com/AAkira/Napier).

## Add dependencies {id="add_dependencies"}

To add the `Logging` plugin, include the following artifact to your build script:

  <var name="artifact_name" value="ktor-client-logging"/>
  <include from="lib.topic" element-id="add_ktor_artifact"/>
  <include from="lib.topic" element-id="add_ktor_client_artifact_tip"/>

## Install Logging {id="install_plugin"}

To install `Logging`, pass it to the `install` function inside
a [client configuration block](client-create-and-configure.md#configure-client):

```kotlin
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.logging.*
//...
val client = HttpClient(CIO) {
    install(Logging)
}
```

## Configure Logging {id="configure_plugin"}

The `Logging` plugin configuration is provided by
the [Logging.Config](https://api.ktor.io/ktor-client-logging/io.ktor.client.plugins.logging/-logging-config)
class. The example below shows a sample configuration:

`logger`
: Specifies a Logger instance. `Logger.DEFAULT` uses an SLF4J logging framework. For Native targets, set this
property to `Logger.SIMPLE`.

`level`
: Specifies the logging level. `LogLevel.HEADERS` will log only request/response headers.

`filter()`
: Allows you to filter log messages for requests matching the specified predicate. In the example
below, only requests made to `ktor.io` get into the log.

`sanitizeHeader()`
: Allows you to sanitize sensitive headers to avoid their values appearing in the logs. In
the example below, the `Authorization` header value will be replaced with '***' when logged.

```kotlin
```

{src="snippets/client-logging/src/main/kotlin/com/example/Application.kt"}

For the full example,
see [client-logging](https://github.com/ktorio/ktor-documentation/tree/%ktor_version%/codeSnippets/snippets/client-logging).

### Provide a custom logger {id="custom_logger"}

To use a custom logger in your client application, you need to create a `Logger` instance and override the `log`
function.
The example below shows how to use the [Napier](https://github.com/AAkira/Napier) library to log HTTP calls:

```kotlin
```

{src="snippets/client-logging-napier/src/main/kotlin/com/example/Application.kt" include-symbol="main"}

For the full example,
see [client-logging-napier](https://github.com/ktorio/ktor-documentation/tree/%ktor_version%/codeSnippets/snippets/client-logging-napier).
