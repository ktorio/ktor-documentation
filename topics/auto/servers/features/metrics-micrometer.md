[//]: # (title: Metrics with Micrometer metrics)
[//]: # (caption: Metrics with Micrometer metrics)
[//]: # (category: servers)
[//]: # (permalink: /servers/features/metrics-micrometer.html)
[//]: # (feature: feature)
[//]: # (artifact: io.ktor)
[//]: # (class: io.ktor.metrics.micrometer.MicrometerMetrics)
[//]: # (redirect_from: redirect_from)
[//]: # (- /features/metrics.html: - /features/metrics.html)
[//]: # (ktor_version_review: 1.0.0)

The Metrics feature allows you to configure the [Metrics](https://micrometer.io/)
to get useful information about the server and incoming requests. This implementation 
uses Micrometer Metrics which requires a JRE 8 or higher. 

{% include feature.html %}

## Exposed Metrics

Depending on your backing timeline database, the names of this metrics my [vary](
https://micrometer.io/docs/concepts#_naming_meters) to follow the naming conventions.

### `ktor.http.server.requests.active`
The active [gauge](https://micrometer.io/docs/concepts#_gauges) counts the amount
of concurrent http requests to the server. There are no tags for this metric.

### `ktor.http.server.requests`
This [timer](https://micrometer.io/docs/concepts#_timers) measures the time of 
each request. This feature provides the following tags for this timer:
- `address`: `<host>:<port>` of the url requested by the client
- `method`: the http method (e.g. `GET` or `POST`)
- `route`: the ktor route handling the requests path (e.g. 
           `/vehicles/{id}/tires/{tire}` for the path 
           `/vehicles/23847/tires/frontright`).
- `status`: The http status code of the response sent to the client (e.g. `200` 
            or `404`).
- `exception`: When the handler throws an `Exception` or `Throwable` before 
               responding to the client, the class name of the exception or 
               `n/a` otherwise. Exceptions that are thrown by the handler after
               responding to the client are not recognized by this feature.

## Installing

The Metrics feature requires you to specify a `MeterRegistry` at installation. 
For test purposes you can use the `SimpleMeterRegistry`, for more productive 
environments you can choose [any registry depending on your timeline database 
vendor](https://micrometer.io/docs).

```kotlin
install(MicrometerMetrics) {
   registry = SimpleMeterRegistry()
}
```

### Meter Binders

Micrometer provides some low level metrics. These are provided via `MeterBinder`s.
By default this feature installs a list of Metrics but you can add your own or 
provide an empty list if you don't want to install this feature install any 
`MeterBinder`.

```kotlin
install(MicrometerMetrics) {
   registry = SimpleMeterRegistry()
   meterBinders = listOf(
            ClassLoaderMetrics(),
            JvmMemoryMetrics(),
            JvmGcMetrics(),
            ProcessorMetrics(),
            JvmThreadMetrics(),
            FileDescriptorMetrics()
   )
}
```

### Distribution Statistic Configuration

Micrometer provides various ways to configure and expose histograms. You can
expose either (client side) percentile  or the histogram counters (and the 
timeline database 
calculates the percentile on the server side). While percentile are supported
by all backends, they are more expensive in [memory footprint](
https://micrometer.io/docs/concepts#_memory_footprint_estimation)) and cannot be 
aggregated over several dimensions. Histogram counters can be aggregated but 
not all backends support them. The full documentation can be found [here](
    https://micrometer.io/docs/concepts#_histograms_and_percentiles).

By default the timers provided by this feature expose the 50%, 90%, 95% and 
99% percentile. To change this configuration you can configure a 
`DistributionStatisticConfig` that is applied to all timers of this feature. 

```kotlin
install(MicrometerMetrics) {
    registry = PrometheusMeterRegistry(PrometheusConfig.DEFAULT)
    
    distributionStatisticConfig = DistributionStatisticConfig.Builder()
                .percentilesHistogram(true)
                .maximumExpectedValue(Duration.ofSeconds(20).toNanos())
                .sla(
                    Duration.ofMillis(100).toNanos(),
                    Duration.ofMillis(500).toNanos()
                )
                .build()
}
```

### Customizing Timers
To customize the tags for each timer, you can configure a lamda that is called
for each request and can extend the builder for the timer. Note, each unique 
combination of tag values results in an own metric. Therefore it is not recommended
to put properties with high cardinality (e.g. resource ids) into tags.

```kotlin
install(MicrometerMetrics) {
    registry = PrometheusMeterRegistry(PrometheusConfig.DEFAULT)
    
    timers { call, exception ->
        this.tag("tenant", call.request.headers["tenantId"])
    }
}
```