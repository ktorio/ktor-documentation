[//]: # (title: Caching Headers)
[//]: # (caption: Controlling cache headers)
[//]: # (category: servers)
[//]: # (permalink: /servers/features/caching-headers.html)
[//]: # (feature: feature)
[//]: # (artifact: io.ktor)
[//]: # (class: io.ktor.features.CachingHeaders)
[//]: # (redirect_from: redirect_from)
[//]: # (- /features/caching-headers.html: - /features/caching-headers.html)
[//]: # (ktor_version_review: 1.0.0)

The CachingOptions feature adds the ability to send the headers `Cache-Control` and `Expires`
used by clients and proxies to cache requests in an easy way.

{% include feature.html %}

The basic feature is installed just like many others, but for it to do something, you have to define
`options` blocks transforming outputContent to CachingOptions using for example:

```kotlin
install(CachingHeaders) {
    options { outgoingContent ->
        when (outgoingContent.contentType?.withoutParameters()) {
            ContentType.Text.CSS -> CachingOptions(CacheControl.MaxAge(maxAgeSeconds = 24 * 60 * 60))
            else -> null
        }
    }
}
```

The options configuration method, allows you to define code to optionally select a `CachingOptions`
from a provided `outgoingContent: OutgoingContent`.
You can, for example, use the `Content-Type` of the outgoing message to determine which Cache-Control to use.

## CachingOptions and CacheControl

The `options` high order function requires you to return a `CachingOption` that describes a `CacheControl`
plus an optional expiring time:

```kotlin
data class CachingOptions(val cacheControl: CacheControl? = null, val expires: ZonedDateTime? = null)

sealed class CacheControl(val visibility: Visibility?) {
    enum class Visibility { Public, Private }
    
    class NoCache(visibility: Visibility?) : CacheControl(visibility)
    class NoStore(visibility: Visibility?) : CacheControl(visibility)
    class MaxAge(val maxAgeSeconds: Int, val proxyMaxAgeSeconds: Int? = null, val mustRevalidate: Boolean = false, val proxyRevalidate: Boolean = false, visibility: Visibility? = null) : CacheControl(visibility)
}
```

If you have several options, that would append several `Cache-Control` headers per each matching option.