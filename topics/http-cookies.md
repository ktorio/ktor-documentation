[//]: # (title: Cookies)

<include src="lib.md" include-id="outdated_warning"/>

This plugin (previously known as feature) keeps cookies between calls or forces specific cookies.



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