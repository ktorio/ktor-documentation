[//]: # (title: Conditional Headers)
[//]: # (caption: Easy '304 Not Modified' Responses)
[//]: # (category: servers)
[//]: # (permalink: /servers/features/conditional-headers.html)
[//]: # (keywords: etag last-modified)
[//]: # (feature: feature)
[//]: # (artifact: io.ktor)
[//]: # (class: io.ktor.features.ConditionalHeaders)
[//]: # (redirect_from: redirect_from)
[//]: # (- /features/conditional-headers.html: - /features/conditional-headers.html)
[//]: # (ktor_version_review: 1.0.0)

ConditionalHeaders feature adds the ability to avoid sending content if the client already has the same content. It does so by
checking the `ETag` or `LastModified` properties of the `Resource` or `FinalContent` that are sent and comparing these 
properties to what client indicates it is having. If the conditions allow it, the entire content is not sent and a
"304 Not Modified" response is sent instead. 

{% include feature.html %}

## Configuration

You can install and use `ConditionalHeaders` without additional configuration:

```kotlin
install(ConditionalHeaders)
```

It also allows to configure a lambda to fetch a version list from the generated `OutgoingContent` passed as parameter of the lambda:

```kotlin
install(ConditionalHeaders) {
    version { content -> listOf(EntityTagVersion("tag1")) }
}
```

## Extensibility

`Version` interface implementations are attached to the `Resource` instances, and you can return custom implementations
with your own logic. Please note that `FinalContent` is only checked for `ETag` and `LastModified` headers.