[//]: # (title: Call Logging)
[//]: # (caption: Log the client requests)
[//]: # (category: servers)
[//]: # (permalink: /servers/features/call-logging.html)
[//]: # (feature: feature)
[//]: # (artifact: io.ktor)
[//]: # (class: io.ktor.features.CallLogging)
[//]: # (redirect_from: redirect_from)
[//]: # (- /features/call-logging.html: - /features/call-logging.html)
[//]: # (ktor_version_review: 1.0.0)

You might want to log client requests: and the Call Logging feature does just that.
It uses the `ApplicationEnvironment.log` (`LoggerFactory.getLogger("Application")`)
that uses slf4j so you can easily configure the output. For more information
on logging in Ktor, please check the [logging in the ktor](/servers/logging.html) page.

{% include feature.html %}

## Basic usage

The basic unconfigured feature logs every request using the level TRACE: 

```kotlin
install(CallLogging)
```

## Configuring

This feature allows you to configure the log level and filtering the requests that are being logged:

```kotlin
install(CallLogging) {
    level = Level.INFO
    filter { call -> call.request.path().startsWith("/section1") }
    filter { call -> call.request.path().startsWith("/section2") }
    // ...
}
```

The filter method keeps an allow list of filters. If no filters are defined,
everything is logged. And if there are filters, if any of them returns true,
the call will be logged.

In the example, it will log both: `/section1/*` and `/section2/*` requests.

## MDC
{ #mdc }

The `CallLogging` feature supports `MDC` (Mapped Diagnostic Context) from slf4j
to associate information as part of the request.

When installing the CallLogging, you can configure a parameter to associate to the request with the `mdc` method.
This method requires a key name, and a function provider. The context would be associated
(and the providers will be called) as part of the `Monitoring` pipeline phase.

```kotlin
install(CallLogging) {
    mdc(name) { // call: ApplicationCall -> 
        "value"
    }
    // ...
}
```

MDC works by using ThreadLocals, while Ktor uses coroutines that are not bound to a specific Thread.
This feature uses internally the `kotlinx.coroutines` [ThreadContextElement](https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines.experimental/-thread-context-element/index.html){ target="_blank"}
to address it.
{ .note }