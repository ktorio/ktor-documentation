[//]: # (title: Jackson)
[//]: # (caption: JSON support using Jackson)
[//]: # (category: servers)
[//]: # (feature: feature)
[//]: # (artifact: io.ktor)
[//]: # (class: io.ktor.jackson.JacksonConverter)
[//]: # (redirect_from: redirect_from)
[//]: # (- /features/jackson.html: - /features/jackson.html)
[//]: # (- /features/content-negotiation/jackson.html: - /features/content-negotiation/jackson.html)
[//]: # (ktor_version_review: 1.0.0)

The Jackson feature allows you to handle JSON content in your application easily using
the [jackson](https://github.com/FasterXML/jackson) library.

This feature is a [ContentNegotiation](/servers/features/content-negotiation.html) converter.

{% include feature.html %}

## Basic usage

To install the feature by registering a JSON content convertor using Jackson:

```kotlin
install(ContentNegotiation) {
    jackson {
        // Configure Jackson's ObjectMapper here
    }
}
```

The `jackson` block is a convenient method for:

```kotlin
register(ContentType.Application.Json, JacksonConverter(ObjectMapper().apply {
    // ...
}.create()))
```

## Configuration

Inside the `jackson` block, you have access to the [ObjectMapper](https://fasterxml.github.io/jackson-databind/javadoc/2.9/com/fasterxml/jackson/databind/ObjectMapper.html)
used to install the ContentNegotiation. To give you an idea of what is available:

```kotlin
install(ContentNegotiation) {
    jackson {
        enable(SerializationFeature.INDENT_OUTPUT)
        enable(...)
        dateFormat = DateFormat.getDateInstance()
        disableDefaultTyping()
        convertValue(..., ...)
        ...
    }
}
```