[//]: # (title: Distributed tracing with OpenTelemetry in Ktor Client)

<show-structure for="chapter" depth="2"/>
<primary-label ref="client-plugin"/>
<var name="plugin_name" value="KtorClientTelemetry"/>

<tldr>
<p>
<b>Required dependencies</b>: <code>io.opentelemetry.instrumentation:opentelemetry-ktor-3.0</code>
</p>
<var name="example_name" value="opentelemetry"/>
<include from="lib.topic" element-id="download_example"/>
</tldr>

<include from="server-opentelemetry.md" element-id="opentelemetry-description"/>

The `%plugin_name%` plugin allows you to automatically trace outgoing HTTP requests. It captures metadata like method,
URL, and status code and propagates trace context across services. You can also customize span attributes or use your
own OpenTelemetry configuration.

> On the server, OpenTelemetry provides the [KtorServerTelemetry](server-opentelemetry.md) plugin for instrumenting
> incoming HTTP requests to your server.

<include from="server-opentelemetry.md" element-id="add_dependencies"/>

## Install %plugin_name% {id="install_plugin"}

To install the `%plugin_name%` plugin, pass it to the `install` function inside a
[client configuration block](client-create-and-configure.md#configure-client) and provide an `OpenTelemetry` instance.
This enables automatic span creation for outgoing requests:

```kotlin
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.auth.*
//...

val client = HttpClient(CIO) {
    val openTelemetry = getOpenTelemetry(serviceName = "opentelemetry-ktor-client")

    install(%plugin_name%) {
        setOpenTelemetry(openTelemetry)
    }
}
```

## Configure instrumentation

You can customize how the Ktor client records and exports OpenTelemetry spans for outgoing HTTP calls. The options below
allow you to adjust which requests are traced, how spans are named, what attributes they contain, which headers are
captured, and how span kinds are determined.

> For more information on these concepts, see the
> [OpenTelemetry tracing documentation](https://opentelemetry.io/docs/concepts/signals/traces/).

<include from="server-opentelemetry.md" element-id="config-known-methods"/>
<include from="server-opentelemetry.md" element-id="config-request-headers"/>

### Capture response headers

To capture specific HTTP response headers as span attributes, use the `capturedResponseHeaders` property:

```kotlin
install(%plugin_name%) {
    // ...
    capturedResponseHeaders(HttpHeaders.ContentType, CUSTOM_HEADER)
}
```

<include from="server-opentelemetry.md" element-id="config-custom-attributes"/>

## Next steps

Once you have %plugin_name% installed and configured, you can verify that spans are being created and propagated by
sending requests to a service that also has telemetry enabled—such as one using
[`KtorServerTelemetry`](server-opentelemetry.md). Viewing both sides of the trace in an observability backend like
Jaeger, Zipkin, or Grafana Tempo will confirm that distributed tracing is working end-to-end.