[//]: # (title: OpenTelemetry tracing in Ktor Server)

<primary-label ref="server-plugin"/>
<var name="plugin_name" value="KtorServerTelemetry"/>
<var name="package_name" value="io.opentelemetry.instrumentation"/>
<var name="artifact_name" value="opentelemetry-ktor-3.0"/>

<snippet id="opentelemtry-description">

Ktor integrates with [OpenTelemetry](https://opentelemetry.io/) — an open-source observability framework for collecting
telemetry data such as traces, metrics, and logs. It provides a standard way to instrument applications and export data
to monitoring and observability tools like Jaeger or Prometheus.

</snippet>

The `%plugin_name%` plugin allows you to trace incoming HTTP requests. It creates spans with route and status information,
extracts trace context from headers, and supports customization of span names and attributes.

## Add dependencies {id="add_dependencies"}

<include from="lib.topic" element-id="add_ktor_artifact_intro"/>

<tabs group="languages">
    <tab title="Gradle (Kotlin)" group-key="kotlin">
        <code-block lang="Kotlin">
            implementation("io.opentelemetry.instrumentation:opentelemetry-ktor-3.0:$opentelemetry_version")
        </code-block>
    </tab>
    <tab title="Gradle (Groovy)" group-key="groovy">
        <code-block lang="Groovy">
            implementation "io.opentelemetry.instrumentation:opentelemetry-ktor-3.0:$opentelemetry_version"
        </code-block>
    </tab>
    <tab title="Maven" group-key="maven">
        <code-block lang="XML">
        <![CDATA[
             <dependencies>
              <dependency>
                <groupId>io.opentelemetry.instrumentation</groupId>
                <artifactId>opentelemetry-ktor-3.0</artifactId>
                <version>$opentelemetry_version</version>
              </dependency>
            </dependencies>
            ]]>
        </code-block>
    </tab>
</tabs>

## Install %plugin_name% {id="install_plugin"}

<include from="lib.topic" element-id="install_plugin"/>

Make sure that no other logging plugin is installed before this.

## Configuration

```kotlin
val openTelemetry = getOpenTelemetry("opentelemetry-ktor-sample-server")

install(KtorServerTelemetry) {
    setOpenTelemetry(openTelemetry)

    // Add custom and known HTTP methods to be traced
    knownMethods(HttpMethod.DefaultMethods + CUSTOM_METHOD)
    
    // Capture specific request headers
    capturedRequestHeaders(HttpHeaders.UserAgent)
    
    // Capture specific response headers
    capturedResponseHeaders(HttpHeaders.ContentType, CUSTOM_HEADER)

    // Customize how span status is determined
    spanStatusExtractor {
        val path = response?.call?.request?.path() ?: ""
        if (path.contains("/span-status-extractor") || error != null) {
            spanStatusBuilder.setStatus(StatusCode.ERROR)
        }
    }

    // Define the kind of span based on HTTP method
    spanKindExtractor {
        if (httpMethod == HttpMethod.Post) {
            SpanKind.PRODUCER
        } else {
            SpanKind.CLIENT
        }
    }

    // Add custom attributes to spans
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

