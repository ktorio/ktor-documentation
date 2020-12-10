[//]: # (title: User Agent)

<include src="lib.md" include-id="outdated_warning"/>

This feature adds a User-Agent header to requests.



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