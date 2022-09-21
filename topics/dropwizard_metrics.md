[//]: # (title: Dropwizard metrics)

<show-structure for="chapter" depth="2"/>

<var name="plugin_name" value="DropwizardMetrics"/>
<var name="package_name" value="io.ktor.server.metrics.dropwizard"/>
<var name="artifact_name" value="ktor-server-metrics"/>

<tldr>
<p>
<b>Required dependencies</b>: <code>io.ktor:%artifact_name%</code>
</p>
<var name="example_name" value="dropwizard-metrics"/>
<include from="lib.topic" element-id="download_example"/>
<include from="lib.topic" element-id="native_server_not_supported"/>
</tldr>

<link-summary>The %plugin_name% plugin lets you configure the Metrics library to get useful information about the server and incoming requests.</link-summary>

The [%plugin_name%](https://api.ktor.io/ktor-server/ktor-server-plugins/ktor-server-metrics/io.ktor.server.metrics.dropwizard/-dropwizard-metrics.html) plugin lets you configure the [Metrics](http://metrics.dropwizard.io/) library to get useful information about the server and incoming requests.

## Add dependencies {id="add_dependencies"}
To enable `%plugin_name%`, you need to include the following artifacts in the build script:
* Add the `%artifact_name%` dependency:

  <include from="lib.topic" element-id="add_ktor_artifact"/>

* Optionally, add a dependency required for a specific reporter. The example below shows how to add an artifact required to report metrics via JMX:

  <var name="group_id" value="io.dropwizard.metrics"/>
  <var name="artifact_name" value="metrics-jmx"/>
  <var name="version" value="dropwizard_version"/>
  <include from="lib.topic" element-id="add_artifact"/>
  
  You can replace `dropwizard_version` with the required version of the `metrics-jmx` artifact, for example, `%dropwizard_version%`.

## Install %plugin_name% {id="install_plugin"}

<include from="lib.topic" element-id="install_plugin"/>

## Configure %plugin_name% {id="configure_plugin"}

`%plugin_name%` allows you to use any supported [Metric reporter](http://metrics.dropwizard.io/) using the `registry` property. Let's take a look at how to configure the SLF4J and JMX reporters.

### SLF4J reporter {id="slf4j"}

The SLF4J reporter allows you to periodically emit reports to any output supported by SLF4J.
For example, to output the metrics every 10 seconds, you would:

```kotlin
```
{src="snippets/dropwizard-metrics/src/main/kotlin/com/example/MetricsApplication.kt" include-lines="12-18,25"}

You can find the full example here: [dropwizard-metrics](https://github.com/ktorio/ktor-documentation/tree/%ktor_version%/codeSnippets/snippets/dropwizard-metrics).

If you run the application and open [http://0.0.0.0:8080](http://0.0.0.0:8080), the output will look like this:

```Bash
[DefaultDispatcher-worker-1] INFO  Application - Responding at http://0.0.0.0:8080
... type=COUNTER, name=ktor.calls.active, count=0
... type=METER, name=ktor.calls./(method:GET).200, count=6, m1_rate=1.2, m5_rate=1.2, m15_rate=1.2, mean_rate=0.98655785084844, rate_unit=events/second
... type=METER, name=ktor.calls./(method:GET).meter, count=6, m1_rate=1.2, m5_rate=1.2, m15_rate=1.2, mean_rate=0.9841134429134598, rate_unit=events/second
... type=METER, name=ktor.calls.exceptions, count=0, m1_rate=0.0, m5_rate=0.0, m15_rate=0.0, mean_rate=0.0, rate_unit=events/second
... type=METER, name=ktor.calls.status.200, count=6, m1_rate=1.2, m5_rate=1.2, m15_rate=1.2, mean_rate=0.9866015088545449, rate_unit=events/second
... type=TIMER, name=ktor.calls./(method:GET).timer, count=6, min=0.359683, max=14.213046, mean=2.691307542732234, stddev=5.099546889849414, p50=0.400967, p75=0.618972, p95=14.213046, p98=14.213046, p99=14.213046, p999=14.213046, m1_rate=1.2, m5_rate=1.2, m15_rate=1.2, mean_rate=0.9830677128229028, rate_unit=events/second, duration_unit=milliseconds
... type=TIMER, name=ktor.calls.duration, count=6, min=0.732149, max=33.735719, mean=6.238046092985701, stddev=12.169258340009847, p50=0.778864, p75=1.050454, p95=33.735719, p98=33.735719, p99=33.735719, p999=33.735719, m1_rate=0.2, m5_rate=0.2, m15_rate=0.2, mean_rate=0.6040311229887146, rate_unit=events/second, duration_unit=milliseconds
```

### JMX reporter {id="jmx"}

The JMX reporter allows you to expose all the metrics to JMX, allowing you to view those metrics using `jconsole`.

```kotlin
```
{src="snippets/dropwizard-metrics/src/main/kotlin/com/example/MetricsApplication.kt" include-lines="12,19-23,25"}

You can find the full example here: [dropwizard-metrics](https://github.com/ktorio/ktor-documentation/tree/%ktor_version%/codeSnippets/snippets/dropwizard-metrics).

If you run the application and connect to its process using the [JConsole](https://docs.oracle.com/en/java/javase/17/management/using-jconsole.html), metrics will look like this:

![Ktor Metrics: JMX](jmx.png){width="758"}



## Exposed metrics {id="exposed-metrics"}

`%plugin_name%` exposes the following metrics:

- [Global metrics](#global-metrics) that include Ktor-specific and [JVM metrics](#jvm-metrics).
- [Metrics for endpoints](#endpoint-metrics).

### Global metrics {id="global-metrics"}

Global metrics include the following Ktor-specific metrics:

* `ktor.calls.active`:`Count` - The number of unfinished active requests.
* `ktor.calls.duration` - Information about the duration of the calls.
* `ktor.calls.exceptions` - Information about the number of exceptions.
* `ktor.calls.status.NNN` - Information about the number of times that happened a specific HTTP status code `NNN`.

Note that a metric name starts with the `ktor.calls` prefix. You can customize it using the `baseName` property:

```kotlin
install(DropwizardMetrics) {
    baseName = "my.prefix"
}
```

### Metrics per endpoint {id="endpoint-metrics"}

* `"/uri(method:VERB).NNN"` - Information about the number of times that happened a specific HTTP status code `NNN`, for this path and verb.
* `"/uri(method:VERB).meter"` - Information about the number of calls for this path and verb.
* `"/uri(method:VERB).timer"` - Information about the duration for this endpoint.


### JVM metrics {id="jvm-metrics"}

In addition to HTTP metrics, Ktor exposes a set of metrics for monitoring the JVM. You can disable these metrics using the `registerJvmMetricSets` property:

```kotlin
```
{src="snippets/dropwizard-metrics/src/main/kotlin/com/example/MetricsApplication.kt" include-lines="12,24-25"}

You can find the full example here: [dropwizard-metrics](https://github.com/ktorio/ktor-documentation/tree/%ktor_version%/codeSnippets/snippets/dropwizard-metrics).
