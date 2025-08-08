[//]: # (title: OpenTelemetry tracing in Ktor Server)

<show-structure for="chapter" depth="2"/>
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

The `%plugin_name%` plugin enables distributed tracing of incoming HTTP requests in a Ktor server application.  It
automatically creates spans containing route, HTTP method, and status code information, extracts existing trace context
from incoming request headers, and allows customization of span names, attributes, and span kinds.

> On the client, OpenTelemetry provides the [KtorClientTelemetry](client-opentelemetry.md) plugin, which instruments
> outgoing HTTP calls to external services.

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
in the specified [module](server-modules.md) and configure an OpenTelemetry instance.

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

> Ensure that %plugin_name% is installed before any other logging or telemetry-related plugins.
> 
{style="note"}

## Configure instrumentation {id="configuration"}

You can customize how the Ktor server records and exports OpenTelemetry spans. The options below allow you to adjust
which requests are traced, how spans are named, what attributes they contain, and how span kinds are determined.

> For more information on these concepts, see the
> [OpenTelemetry tracing documentation](https://opentelemetry.io/docs/concepts/signals/traces/).

### Trace additional HTTP methods {id="config-known-methods"}

By default, the plugin traces standard HTTP methods (`GET`, `POST`, `PUT`, etc.). To trace additional or custom methods,
configure the `knownMethods` property:

```kotlin
install(%plugin_name%) {
    // ...
    knownMethods(HttpMethod.DefaultMethods + CUSTOM_METHOD)
}
```

### Capture headers {id="config-request-headers"}

To include specific HTTP request headers as span attributes, use the `capturedRequestHeaders` property:

```kotlin
install(%plugin_name%) {
    // ...
    capturedRequestHeaders(HttpHeaders.UserAgent)
}
```

### Select span kind {id="config-span-kind"}

To override the span kind (such as `SERVER`, `CLIENT`, `PRODUCER`, `CONSUMER`) based on request characteristics,
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

### Add custom attributes {id="config-custom-attributes"}

To attach custom attributes at the start and end of a span, use the `attributesExtractor` property:

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

### Additional properties {id="additional-properties"}

To fine-tune tracing behavior across your application, you can also configure additional OpenTelemetry properties
like propagators, attribute limits, and enabling/disabling instrumentation. For more details, see the
[OpenTelemetry Java configuration guide](https://opentelemetry.io/docs/languages/java/configuration/).

