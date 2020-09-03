[//]: # (title: User Agent)
[//]: # (category: clients)
[//]: # (caption: User Agent)
[//]: # (feature: feature)
[//]: # (artifact: io.ktor)
[//]: # (class: io.ktor.client.features.UserAgent)
[//]: # (ktor_version_review: 1.2.0)

This feature adds a User-Agent header to requests.

{% include feature.html %}

## Install

```kotlin
val client = HttpClient() {

    // Full configuration.
    install(UserAgent) {
        agent = "ktor"
    }

    // Shortcut for the browser-like user agent.
    BrowserUserAgent()

    // Shortcut for the curl-like user agent.
    CurlUserAgent()
}

```