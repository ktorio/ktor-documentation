[//]: # (title: Compression)
[//]: # (caption: Enable HTTP Compression Facilities)
[//]: # (category: servers)
[//]: # (permalink: /servers/features/compression.html)
[//]: # (feature: feature)
[//]: # (artifact: io.ktor)
[//]: # (class: io.ktor.features.Compression)
[//]: # (redirect_from: redirect_from)
[//]: # (- /features/compression.html: - /features/compression.html)
[//]: # (ktor_version_review: 1.0.0)

Compression feature adds the ability to compress outgoing content using gzip, deflate or custom encoder and thus reduce the
size of the response.

```kotlin
install(Compression)
```



## Configuration

When the configuration block is omitted, the default configuration is used. It includes
 the following encoders:
 
 * gzip
 * deflate
 * identity
 
If you want to select specific encoders you need to provide a configuration block:

```kotlin
install(Compression) {
    gzip()
}
```

Each encoder can be configured with a priority and some conditions: 

```kotlin
install(Compression) {
    gzip {
        priority = 1.0
    }
    deflate {
        priority = 10.0 
        minimumSize(1024) // condition
    }
}
```

Encoders are sorted by specified quality in an `Accept-Encoding` header in the HTTP request, and
then by specified priority. First encoder that satisfies all conditions wins.

In the example above when `Accept-Encoding` doesn't specify quality, `gzip` will be selected for all contents 
less than 1K in size, and all the rest will be encoded with `deflate` encoder. 

Some typical conditions are readily available:

* `minimumSize` – minimum size of the response to compress
* `matchContentType` – one or more content types that should be compressed
* `excludeContentType` – do not compress these content types

You can also use a custom condition by providing a predicate:

```kotlin
gzip {
    condition {
        parameters["e"] == "1"
    }
}
```

## Security with HTTPS

HTTPS with any kind of compression is vulnerable to the [BREACH](https://en.wikipedia.org/wiki/BREACH){target="_blank"} attack.
This kind of attack allows a malicious attacker to guess a secret (like a session, an auth token, a password,
or a credit card) from an encrypted HTTPS page in less than a minute.

You can mitigate this attack by:

* Completely turn off HTTP compression (which might affect performance).
* Not placing user input (GET, POST or Header/Cookies parameters) as part of the response (either Headers or Bodies) mixed with secrets (including a `Set-Cookie` with a session_id).
* Add a random amount of bytes to the output for example in an html page, you can just add `<!-- 100~500 random_bytes !-->` making it much harder to guess the secret for an attacker in a reasonable time.
* Ensure that your website is **completely HTTPS and has HSTS enabled**, and adding a conditional header checking the Referrer page. (If you have a single page without HTTPS, the malicious attacker can use that page to inject code using the same domain as Referrer).
* Adding [CSRF](https://en.wikipedia.org/wiki/Cross-site_request_forgery){target="_blank"} protection to your pages.

```kotlin
application.install(Compression) {
    gzip {
        condition {
            // @TODO: Check: this is only effective if your website is completely HTTPS and has HSTS enabled. 
            request.headers[HttpHeaders.Referrer]?.startsWith("https://my.domain/") == true
        }
    }
}
```

>TL;DR; Even when HTTPS prevents an eavesdropper to know the content of a request, it does not hide the response length.
>So one of your users could be connecting to an evil access point, for example by connecting to a public network
>or one with a well-known password, or a private network with an Evil Twin. That access point can intercept all the
>encrypted messages and measure the length. Then can modify any non-https connection (or social engineer the user to
>access an https page controlled by the attacker) to inject a javascript or place images pointing to the vulnerable
>page mutating an input (get, post or header parameters) that are reflected in either the headers or the response body,
>then the access point can measure the length of the responses to guess a secret with as little as 100 to 10000 requests
>that are forced to be done by your browser with either the javascript or image requests without the user ever noticing.
>
{type="note"}

## Extensibility

You can provide your own encoder by implementing the `CompressionEncoder` interface and providing a configuration function. 
Since content can be provided as a `ReadChannel` or `WriteChannel`, it should be able to compress in both ways. 
See `GzipEncoder` as an example of an encoder. 