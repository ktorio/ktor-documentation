[//]: # (title: Cookies)

<include src="lib.xml" include-id="outdated_warning"/>

<microformat>
<var name="example_name" value="client-cookies"/>
<include src="lib.xml" include-id="download_example"/>
</microformat>

This plugin keeps cookies between calls or forces specific cookies.



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