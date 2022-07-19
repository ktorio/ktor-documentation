[//]: # (title: Micrometer metrics)

<show-structure for="chapter" depth="2"/>

[micrometer_jvm_metrics]: https://micrometer.io/docs/ref/jvm

<var name="package_name" value="io.ktor.server.metrics.micrometer"/>

<tldr>
<p>
<b>Required dependencies</b>: <code>io.ktor:ktor-server-metrics-micrometer</code>
</p>
<var name="example_name" value="micrometer-metrics"/>
<include from="lib.topic" element-id="download_example"/>
</tldr>

<link-summary>The MicrometerMetrics plugin enables Micrometer metrics in your Ktor server application and allows you to choose the required monitoring system, such as Prometheus, JMX, Elastic, and so on.</link-summary>

The [MicrometerMetrics](https://api.ktor.io/ktor-server/ktor-server-plugins/ktor-server-metrics-micrometer/io.ktor.server.metrics.micrometer/-micrometer-metrics) plugin enables [Micrometer](https://micrometer.io/docs) metrics in your Ktor server application and allows you to choose the required monitoring system, such as Prometheus, JMX, Elastic, and so on. By default, Ktor exposes metrics for monitoring HTTP requests and a set of low-level metrics for [monitoring the JVM][micrometer_jvm_metrics]. You can customize these metrics or create new ones.

## Add dependencies {id="add_dependencies"}
To enable `MicrometerMetrics`, you need to include the following artifacts in the build script:
* Add the `ktor-server-metrics-micrometer` dependency:
    <var name="artifact_name" value="ktor-server-metrics-micrometer"/>
    <include from="lib.topic" element-id="add_ktor_artifact"/>
  
* Add a dependency required for a monitoring system. The example below shows how to add an artifact for Prometheus:
    <var name="group_id" value="io.micrometer"/>
    <var name="artifact_name" value="micrometer-registry-prometheus"/>
    <var name="version" value="prometheus_version"/>
    <include from="lib.topic" element-id="add_artifact"/>
    You can replace `$prometheus_version` with the required version of the `micrometer-registry-prometheus` artifact, for example, `%prometheus_version%`.

## Install MicrometerMetrics {id="install_plugin"}

<var name="plugin_name" value="MicrometerMetrics"/>
<include from="lib.topic" element-id="install_plugin"/>

### Exposed metrics {id="ktor_metrics"}
Ktor exposes the following metrics for monitoring HTTP requests:
* `ktor.http.server.requests.active`: a [gauge](https://micrometer.io/docs/concepts#_gauges) that counts the amount of concurrent HTTP requests. This metric doesn't provide any tags.
* `ktor.http.server.requests`: a [timer](https://micrometer.io/docs/concepts#_timers) for measuring the time of each request. This metric provides a set of tags for monitoring request data, including `address` for a requested URL, `method` for an HTTP method, `route` for a Ktor route handling requests, and so on.

You can customize the default `ktor.http.server.requests` prefix using the `metricName` [configuration](#configure_metrics) property.

> The metric names may be [different](https://micrometer.io/docs/concepts#_naming_meters) depending on the configured monitoring system.

In addition to HTTP metrics, Ktor exposes a set of metrics for [monitoring the JVM](#jvm_metrics).

## Create a registry {id="create_registry"}

After installing `MicrometerMetrics`, you need to create a [registry for your monitoring system](https://micrometer.io/docs/concepts#_registry) and assign it to the `registry` property. In the example below, the `PrometheusMeterRegistry` is created outside the `install` block to have the capability to reuse this registry in different [route handlers](Routing_in_Ktor.md):

```kotlin
```
{src="snippets/micrometer-metrics/src/main/kotlin/com/example/Application.kt" include-lines="15-18,32,42"}



## Configure metrics {id="configure_metrics"}

The `MicrometerMetrics` plugin provides various configuration options that can be accessed using [MicrometerMetricsConfig](https://api.ktor.io/ktor-server/ktor-server-plugins/ktor-server-metrics-micrometer/io.ktor.server.metrics.micrometer/-micrometer-metrics-config/index.html).

### Timers {id="timers"}
To customize tags for each timer, you can use the `timers` function that is called for each request:
```kotlin
install(MicrometerMetrics) {
    // ...
    timers { call, exception ->
        tag("region", call.request.headers["regionId"])
    }
}
```

### Distribution statistics {id="distribution_statistics"}
You configure [distribution statistics](https://micrometer.io/docs/concepts#_configuring_distribution_statistics) using the `distributionStatisticConfig` property, for example:

```kotlin
```
{src="snippets/micrometer-metrics/src/main/kotlin/com/example/Application.kt" include-lines="17,19-26,32"}


### JVM and system metrics {id="jvm_metrics"}
In addition to [HTTP metrics](#ktor_metrics), Ktor exposes a set of metrics for [monitoring the JVM][micrometer_jvm_metrics]. You can customize a list of these metrics using the `meterBinders` property, for example:

```kotlin
```
{src="snippets/micrometer-metrics/src/main/kotlin/com/example/Application.kt" include-lines="17,27-32"}

You can also assign an empty list to disable these metrics at all.


## Prometheus: expose a scrape endpoint {id="prometheus_endpoint"}
If you use Prometheus as a monitoring system, you need to expose an HTTP endpoint to the Prometheus scraper. In Ktor, you can do this in the following way:
1. Create a dedicated [route](Routing_in_Ktor.md) that accepts GET requests by the required address (`/metrics` in the example below).
2. Use `call.respond` to send scraping data to Prometheus.

   ```kotlin
   ```
   {src="snippets/micrometer-metrics/src/main/kotlin/com/example/Application.kt" include-lines="15-18,32-33,38-42"}

   You can find the full example here: [micrometer-metrics](https://github.com/ktorio/ktor-documentation/tree/%ktor_version%/codeSnippets/snippets/micrometer-metrics).
