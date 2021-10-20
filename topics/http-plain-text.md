[//]: # (title: Text and charsets)

<include src="lib.xml" include-id="outdated_warning"/>

This plugin allows you to process the plain text content in request and response: fills `Accept` header with registered charsets, encode request body and decode response body according to `ContentType` charset.


## Configuration

If no configuration specified in configuration or HTTP call properties, `Charsets.UTF_8` is used by default.

```kotlin
val client = HttpClient(HttpClientEngine) {
    Charsets {
        // Allow using `UTF_8`.
        register(Charsets.UTF_8)

        // Allow using `ISO_8859_1` with quality 0.1.
        register(Charsets.ISO_8859_1, quality=0.1f)
        
        // Specify Charset to send request(if no charset in request headers).
        sendCharset = ...

        // Specify Charset to receive response(if no charset in response headers).
        responseCharsetFallback = ...
    }
}
```