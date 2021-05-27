[//]: # (title: Compression)

Ktor provides the capability to compress outgoing content by using the [Compression](https://api.ktor.io/%ktor_version%/io.ktor.features/-compression/index.html) feature. You can use different compression algorithms, including `gzip` and `deflate`, 
specify the required conditions for compressing data (such as a content type or response size), or even compress data based on specific request parameters.

> To learn how to serve pre-compressed static files in Ktor, see [](Serving_Static_Content.md#precompressed).

## Install Compression {id="install_feature"}

<var name="feature_name" value="Compression"/>
<include src="lib.xml" include-id="install_feature"/>

This enables the `gzip`, `deflate`, and `identity` encoders on a server. In the next chapter, we'll see how to enable only specific encoders and configure conditions for compressing data.


## Configure compression settings {id="configure"}
You can configure compression in multiple ways: enable only specific encoders, specify their priorities, compress only specific content types, and so on.

### Add specific encoders {id="add_specific_encoders"}
To enable only specific encoders, call the corresponding extension functions, for example:
```kotlin
install(Compression) {
    gzip()
    deflate()
}
```
You can specify the priority for each compression algorithm by establishing the `priority` property:
```kotlin
install(Compression) {
    gzip {
        priority = 0.9
    }
    deflate {
        priority = 1.0
    }
}
```
In the example above, `deflate` has a higher priority value and takes precedence over `gzip`. Note that the server first looks at the [quality](https://developer.mozilla.org/en-US/docs/Glossary/Quality_Values) values within the [Accept-Encoding](https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/Accept-Encoding) header and then takes into account the specified priorities.

### Configure content type {id="configure_content_type"}
By default, Ktor doesn't compress specific content types, such as `audio`, `video`, `image`, and `text/event-stream`. 
You can choose the content types to compress by calling [matchContentType](https://api.ktor.io/%ktor_version%/io.ktor.features/match-content-type.html) or exclude the desired media types from compression by using [excludeContentType](https://api.ktor.io/%ktor_version%/io.ktor.features/exclude-content-type.html). The code snippet below shows how to compress all text subtypes and JavaScript code using `gzip`:
```kotlin
install(Compression) {
    gzip {
        matchContentType(
            ContentType.Text.Any,
            ContentType.Application.JavaScript
        )
    }
}
```

### Configure response size {id="configure_response_size"}
The `Compression` feature allows you to disable compression for responses whose size doesn't exceed the specified value. To do this, pass the desired value (in bytes) to the [minimumSize](https://api.ktor.io/%ktor_version%/io.ktor.features/minimum-size.html) function:
```kotlin
    install(Compression) {
        deflate {
            minimumSize(1024)
        }
    }

```

### Specify custom conditions {id="specify_custom_conditions"}
If necessary, you can provide a custom condition using the [condition](https://api.ktor.io/%ktor_version%/io.ktor.features/condition.html) function and compress data depending on the specific request parameters. The code snippet below shows how to compress requests for the specified URI:
```kotlin
install(Compression) {
    gzip {
        condition {
            request.uri == "/orders"
        }
    }
}
```


## HTTPS security {id="security"}
HTTPS with the enabled compression is vulnerable to the [BREACH](https://en.wikipedia.org/wiki/BREACH) attack. You can use various ways to mitigate this attack. For example, you can disable compression whenever the referrer header indicates a cross-site request. In Ktor, this can be done by checking the referrer header value:
```kotlin
install(Compression) {
    gzip {
        condition {
            request.headers[HttpHeaders.Referrer]?.startsWith("https://my.domain/") == true
        }
    }
}
```

## Implement custom encoder {id="custom_encoder"}
If necessary, you can provide your own encoder by implementing the [CompressionEncoder](https://api.ktor.io/%ktor_version%/io.ktor.features/-compression-encoder/index.html) interface. See [GzipEncoder](https://github.com/ktorio/ktor/blob/main/ktor-server/ktor-server-core/jvm/src/io/ktor/features/Compression.kt) as an example implementation.
