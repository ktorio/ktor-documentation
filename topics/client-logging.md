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
  <var name="ktor_version_catalog_name" value="ktorLibs.client.logging"/>
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
the [LoggingConfig](https://api.ktor.io/ktor-client-logging/io.ktor.client.plugins.logging/-logging-config)
class. The example below shows a sample configuration:

`logger`
: Specifies a Logger instance. `Logger.DEFAULT` uses an SLF4J logging framework. For Native targets, set this
property to `Logger.SIMPLE`.

`level`
: Specifies which HTTP request and response details to log. The default is `LogLevel.HEADERS`,
which includes basic request and response information as well as headers.

`filter()`
: Allows you to filter log messages for requests matching the specified predicate. In the example
below, only requests made to `ktor.io` get into the log.

`sanitizeHeader()`
: Allows you to sanitize sensitive headers to avoid their values appearing in the logs. In
the example below, the `Authorization` header value will be replaced with '***' when logged.

```kotlin
```

{src="snippets/client-logging/src/main/kotlin/com/example/Application.kt" include-symbol="createClient"}

For the full example,
see [client-logging](https://github.com/ktorio/ktor-documentation/tree/main/codeSnippets/snippets/client-logging).

### Logging levels {id="logging_levels"}

For the default logging format, the available levels include the following details:

| Level | Basic information | Headers | Bodies |
|-------|-------------------|---------|--------|
| `NONE` | No | No | No |
| `INFO` | Yes | No | No |
| `HEADERS` | Yes | Yes | No |
| `BODY` | Yes | No | Yes |
| `ALL` | Yes | Yes | Yes |

Basic information includes the request URL and method, the response status, and information about failures.
Body logging also includes body-related metadata, such as content type. Bodies that cannot be read as text may be omitted.

On JVM, `Logger.DEFAULT` sends messages to SLF4J at the `INFO` severity using the
`io.ktor.client.HttpClient` logger. Ktor's `LogLevel` selects the HTTP details in those messages;
the logging backend controls whether they are displayed. For example, a backend threshold of `WARN`
suppresses these messages even when Ktor's level is `LogLevel.ALL`.

### Development and production logging {id="development_production_logging"}

Choose the logging level when creating the client. The `Logging` plugin does not automatically detect
whether the application is running in development or production.

The `createClient` function above accepts an `isDevelopment` setting from the application. This example
uses `HEADERS` during development and `NONE` otherwise. `NONE` disables this plugin's HTTP logging;
other application logging is unaffected. This is one possible production policy. Choose a level that
meets your application's diagnostic and data-handling requirements.

The runnable sample supplies this setting through a `--development` command-line argument:

```kotlin
```

{src="snippets/client-logging/src/main/kotlin/com/example/Application.kt" include-symbol="main"}

From the documentation repository's `codeSnippets` directory, run the sample with development logging enabled:

```bash
./gradlew :client-logging:run --args="--development"
```

To run the same requests with HTTP logging disabled, omit the argument:

```bash
./gradlew :client-logging:run
```

With `Logger.DEFAULT` on JVM, the logging backend must allow `INFO` messages for the development logs to appear.
The command-line argument is specific to this sample; applications can obtain their environment setting from
their own configuration.

### Sensitive data in logs {id="sensitive_data"}

`sanitizeHeader()` replaces matching header values in request and response logs. It does not remove sensitive
values from URLs, query parameters, or bodies. For example, `INFO` can expose a token in a request URL even
though it excludes header listings and bodies. `BODY` and `ALL` can also expose credentials or tokens in
request and response bodies. Review the data your application sends and receives before enabling HTTP logging.

### Provide a custom logger {id="custom_logger"}

To use a custom logger in your client application, you need to create a `Logger` instance and override the `log`
function.
The example below shows how to use the [Napier](https://github.com/AAkira/Napier) library to log HTTP calls:

```kotlin
```

{src="snippets/client-logging-napier/src/main/kotlin/com/example/Application.kt" include-symbol="main"}

For the full example,
see [client-logging-napier](https://github.com/ktorio/ktor-documentation/tree/main/codeSnippets/snippets/client-logging-napier).
