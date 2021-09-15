[//]: # (title: User agent)

<include src="lib.xml" include-id="outdated_warning"/>

This plugin (previously known as feature) adds a User-Agent header to requests.



## Install

```kotlin
import io.ktor.client.plugins.*

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