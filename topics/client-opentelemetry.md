[//]: # (title: OpenTelemetry tracing in Ktor Client)

<primary-label ref="client-plugin"/>
<var name="plugin_name" value="KtorClientTelemetry"/>

<include from="server-opentelemetry.md" element-id="opentelemtry-description"/>

The `%plugin_name%` plugin allows you to automatically trace outgoing HTTP requests. It captures metadata like method,
URL, and status code, and propagates trace context across services. You can also customize span attributes or use your
own OpenTelemetry configuration.

<include from="server-opentelemetry.md" element-id="add_dependencies"/>
<include from="server-opentelemetry.md" element-id="install_plugin"/>

## Configuration

```kotlin
val openTelemetry = getOpenTelemetry(serviceName = "opentelemetry-ktor-sample-client")

install(KtorClientTelemetry) {
    setOpenTelemetry(openTelemetry)

    Experimental.emitExperimentalTelemetry(this)

    // Add custom and known HTTP methods to be traced
    knownMethods(HttpMethod.DefaultMethods + CUSTOM_METHOD)

    // Capture specific request headers
    capturedRequestHeaders(HttpHeaders.Accept)

    // Capture specific response headers
    capturedResponseHeaders(HttpHeaders.ContentType, CUSTOM_HEADER)

    // Customize how span status is determined
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