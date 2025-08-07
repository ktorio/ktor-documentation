[//]: # (title: OpenTelemetry tracing in Ktor Server)

<primary-label ref="server-plugin"/>
<var name="plugin_name" value="KtorServerTelemetry"/>
<var name="package_name" value="io.opentelemetry.instrumentation"/>
<var name="artifact_name" value="opentelemetry-ktor-3.0"/>

<tldr>
<p>
<b>Required dependencies</b>: <code>io.opentelemetry.instrumentation:opentelemetry-ktor-3.0</code>
</p>
<var name="example_name" value="opentelemetry"/>
<include from="lib.topic" element-id="download_example"/>
</tldr>

<snippet id="opentelemetry-description">

Ktor integrates with [OpenTelemetry](https://opentelemetry.io/) — an open-source observability framework for collecting
telemetry data such as traces, metrics, and logs. It provides a standard way to instrument applications and export data
to monitoring and observability tools like Jaeger or Prometheus.

</snippet>

The `%plugin_name%` plugin allows you to trace incoming HTTP requests. It creates spans with route and status
information, extracts trace context from headers, and supports customization of span names and attributes.

> On the client, OpenTelemetry provides the [KtorClientTelemetry](client-opentelemetry.md) plugin for tracing outgoing
> HTTP calls to external services.

## Add dependencies {id="add_dependencies"}

<include from="lib.topic" element-id="add_ktor_artifact_intro"/>

<tabs group="languages">
    <tab title="Gradle (Kotlin)" group-key="kotlin">
        <code-block lang="Kotlin">
            implementation("io.opentelemetry.instrumentation:opentelemetry-ktor-3.0:%opentelemetry_version%")
        </code-block>
    </tab>
    <tab title="Gradle (Groovy)" group-key="groovy">
        <code-block lang="Groovy">
            implementation "io.opentelemetry.instrumentation:opentelemetry-ktor-3.0:%opentelemetry_version%"
        </code-block>
    </tab>
    <tab title="Maven" group-key="maven">
        <code-block lang="XML">
        <![CDATA[
             <dependencies>
              <dependency>
                <groupId>io.opentelemetry.instrumentation</groupId>
                <artifactId>opentelemetry-ktor-3.0</artifactId>
                <version>%opentelemetry_version%</version>
              </dependency>
            </dependencies>
            ]]>
        </code-block>
    </tab>
</tabs>

## Install %plugin_name% {id="install_plugin"}

To [install](server-plugins.md#install) the `%plugin_name%` plugin to the application, pass it to the `install` function
in the specified [module](server-modules.md).

<tabs>
<tab title="embeddedServer">

```kotlin
    import io.ktor.server.engine.*
    import io.ktor.server.netty.*
    import io.ktor.server.application.*
    import %package_name%.*

    fun main() {
        embeddedServer(Netty, port = 8080) {
            val openTelemetry = getOpenTelemetry(serviceName = "opentelemetry-ktor-server")

            install(%plugin_name%){
                setOpenTelemetry(openTelemetry)
            }
            // ...
        }.start(wait = true)
    }
```
</tab>
<tab title="module">

```kotlin

    import io.ktor.server.application.*
    import %package_name%.*
    // ...

    fun Application.module() {
        val openTelemetry = getOpenTelemetry(serviceName = "opentelemetry-ktor-server")

        install(%plugin_name%){
            setOpenTelemetry(openTelemetry)
        }
        // ...
    }
```
</tab>
</tabs>

Make sure that no other logging plugin is installed before this.

## Configuration {id="configuration"}

### Trace additional HTTP methods {id="config-known-methods"}

By default, standard HTTP methods are traced. To extend this list with custom methods, use the `knownMethods` property:

```kotlin
install(%plugin_name%) {
    // ...
    knownMethods(HttpMethod.DefaultMethods + CUSTOM_METHOD)
}
```

### Capture headers {id="config-headers"}

To capture specific HTTP request headers as span attributes, use the `capturedRequestHeaders` property:

```kotlin
install(%plugin_name%) {
    // ...
    capturedRequestHeaders(HttpHeaders.UserAgent)
}
```

### Select span kind {id="config-span-kind"}

You can override the span kind depending on request characteristics to reflect producer/consumer semantics. To do that,
use the `spanKindExtractor` property:

```kotlin
install(%plugin_name%) {
    // ...
    spanKindExtractor {
        if (httpMethod == HttpMethod.Post) {
            SpanKind.PRODUCER
        } else {
            SpanKind.CLIENT
        }
    }
}
```

### Add custom attributes {id="config-attributes"}

To add custom attributes when a span starts and when it ends, use `attributesExtractor`:

```kotlin
install(%plugin_name%) {
    // ...
    attributesExtractor {
        onStart {
            attributes.put("start-time", System.currentTimeMillis())
        }
        onEnd {
            attributes.put("end-time", Instant.now().toEpochMilli())
        }
    }
}
```

