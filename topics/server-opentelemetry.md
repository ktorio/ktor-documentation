[//]: # (title: Distributed tracing with OpenTelemetry in Ktor Server)

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

## Configure OpenTelemetry {id="configure-otel"}

Before installing the `%plugin_name%` plugin in your Ktor application, you need to configure and initialize an
`OpenTelemetry` instance. This instance is responsible for managing telemetry data, including traces and metrics.

### Using `AutoConfiguredOpenTelemetrySdk`

A common approach is to use `AutoConfiguredOpenTelemetrySdk` from the OpenTelemetry SDK, which simplifies setup by
automatically configuring exporters and resources based on system properties and environment variables.

Here is an example of a reusable function that configures OpenTelemetry with a custom service name and disables
metrics exporting (useful when the target backend does not support metrics, such as the Jaeger all-in-one image):

```kotlin
```

{src="snippets/opentelemetry/core/src/main/kotlin/OpenTelemetry.kt"}

## Install %plugin_name% {id="install_plugin"}

To [install](server-plugins.md#install) the `%plugin_name%` plugin to the application, pass it to the `install` function
in the specified [module](server-modules.md) and set the [configured `OpenTelemetry` instance](#configure-otel):

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


## Configure tracing {id="configuration"}

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

## Verify telemetry data with Jaeger

To visualize and verify your telemetry data, you can export traces to a distributed tracing backend, such as Jaeger.

### Using Docker Compose

Create a **docker-compose.yml** file with the following content:

```yaml
```
{src="snippets/opentelemetry/docker/docker-compose.yml"}

To start the Jaeger all-in-one container, run the following command:

```shell
docker-compose up -d
```

### Using Docker CLI

Alternatively, you can run Jaeger directly using the Docker command line:

```shell
docker run -d --name jaeger_instance \
    -p 4317:4317 \
    -p 16686:16686 \
    jaegertracing/all-in-one:latest
```

### Accessing Jaeger UI

Once running, the Jaeger UI will be available at [](http://localhost:16686/search).
From here, you can search for traces by your service name (configured in your OpenTelemetry setup) and inspect
detailed trace information.

