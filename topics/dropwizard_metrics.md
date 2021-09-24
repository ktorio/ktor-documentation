[//]: # (title: Dropwizard metrics)

<var name="plugin_name" value="Metrics"/>
<var name="artifact_name" value="ktor-server-metrics"/>

<microformat>
<p>
Required dependencies: <code>io.ktor:%artifact_name%</code>
</p>
<var name="example_name" value="metrics"/>
<include src="lib.xml" include-id="download_example"/>
</microformat>

<include src="lib.xml" include-id="outdated_warning"/>

The `%plugin_name%` plugin allows you to configure the [Metrics](http://metrics.dropwizard.io/4.2.0/)
to get useful information about the server and incoming requests.

## Add dependencies {id="add_dependencies"}

<include src="lib.xml" include-id="add_ktor_artifact_intro"/>
<include src="lib.xml" include-id="add_ktor_artifact"/>

## Install Metrics

The `%plugin_name%` plugin exposes a `registry` property, that can be used to build and start
metric reporters.

### JMX Reporter

The JMX Reporter allows you to expose all the metrics to JMX,
allowing you to view those metrics with `jconsole` or `jvisualvm` with the MBeans plugin.

```kotlin
install(DropwizardMetrics) {
    JmxReporter.forRegistry(registry)
        .convertRatesTo(TimeUnit.SECONDS)
        .convertDurationsTo(TimeUnit.MILLISECONDS)
        .build()
        .start()
}
```

![Ktor Metrics: JMX](jmx.png)

### SLF4J Reporter

The SLF4J Reporter allows you to periodically emit reports to any output supported by SLF4J.
For example, to output the metrics every 10 seconds, you would:

```kotlin
install(DropwizardMetrics) {
    Slf4jReporter.forRegistry(registry)
        .outputTo(log)
        .convertRatesTo(TimeUnit.SECONDS)
        .convertDurationsTo(TimeUnit.MILLISECONDS)
        .build()
        .start(10, TimeUnit.SECONDS)
}
```

![Ktor Metrics: SLF4J](slf4j.png)

### Other reporters

You can use any of the available [Metric reporters](http://metrics.dropwizard.io/4.2.0/).

## Exposed reports

This plugin exposes many JVM properties relating to memory usage and thread behavior.

### Global:

Specifically to Ktor, it exposes:

* `ktor.calls.active`:`Count` - The number of unfinished active requests
* `ktor.calls.duration` - Information about the duration of the calls
* `ktor.calls.exceptions` - Information about the number of exceptions
* `ktor.calls.status.NNN` - Information about the number of times that happened a specific HTTP Status Code NNN

### Per endpoint:

* `"/uri(method:VERB).NNN"` - Information about the number of times that happened a specific HTTP Status Code NNN, for this path, for this verb
* `"/uri(method:VERB).meter"` - Information about the number of calls for this path, for this verb
* `"/uri(method:VERB).timer"` - Information about the durations for this endpoint

## Information per report

### Durations

`"/uri(method:VERB).timer"` and `ktor.calls.duration` are durations and expose:

* 50thPercentile
* 75thPercentile
* 95thPercentile
* 98thPercentile
* 99thPercentile
* 999thPercentile
* Count
* DurationUnit
* OneMinuteRate
* FifteenMinuteRate
* FiveMinuteRate
* Max
* Mean
* MeanRate
* Min
* RateUnit
* StdDev

### Counts

The other properties are exposed as counts:

* Count
* FifteenMinuteRate
* FiveMinuteRate
* OneMinuteRate
* MeanRate
* RateUnit