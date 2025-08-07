[//]: # (title: OpenTelemetry tracing in Ktor Client)

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
URL, and status code, and propagates trace context across services. You can also customize span attributes or use your
own OpenTelemetry configuration.

> On the server, OpenTelemetry provides the [KtorServerTelemetry](server-opentelemetry.md) plugin for monitoring
> incoming HTTP requests.

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
## Configuration

You can fine-tune how Ktor client emits OpenTelemetry spans. Below are common configuration options.


### Trace custom and known HTTP methods

By default, common HTTP methods are traced. You can extend this list to include custom methods used by your API.

```kotlin
install(%plugin_name%) {
    // ...
    knownMethods(HttpMethod.DefaultMethods + CUSTOM_METHOD)
}
```

### Capture request headers

To capture specific HTTP request headers as span attributes, use the `capturedRequestHeaders` property:

```kotlin
install(%plugin_name%) {
    // ...
    capturedRequestHeaders(HttpHeaders.Accept)
}
```

### Capture response headers

To capture specific HTTP response headers as span attributes, use the `capturedResponseHeaders` property:

```kotlin
install(%plugin_name%) {
    // ...
    capturedResponseHeaders(HttpHeaders.ContentType, CUSTOM_HEADER)
}
```

### Add custom attributes

Add custom attributes at span start/end or adjust how status is determined. 

```kotlin
install(%plugin_name%) {
    // ...
    attributesExtractor {
        onStart {
            attributes.put("start-time", System.currentTimeMillis())
        }
        onEnd {
            attributes.put("end-time", System.currentTimeMillis())
        }
    }
}
```