[//]: # (title: Cookies)
[//]: # (category: clients)
[//]: # (caption: Cookies)
[//]: # (feature: feature)
[//]: # (artifact: io.ktor)
[//]: # (class: io.ktor.client.features.Cookies`)
[//]: # (method: io.ktor.client.features.cookies)
[//]: # (ktor_version_review: 1.2.0)

This feature keeps cookies between calls or forces specific cookies.

{% include feature.html %}

## Installation

```kotlin
val client = HttpClient() {
    install(HttpCookies) {
        // Will keep an in-memory map with all the cookies from previous requests.
        storage = AcceptAllCookiesStorage()

        // Will ignore Set-Cookie and will send the specified cookies.
        storage = ConstantCookiesStorage(Cookie("mycookie1", "value"), Cookie("mycookie2", "value"))
    }
}

client.cookies("mydomain.com")
```