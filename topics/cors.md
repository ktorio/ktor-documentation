[//]: # (title: CORS)

<include src="lib.md" include-id="outdated_warning"/>

Ktor by default provides an interceptor for implementing proper support for Cross-Origin Resource Sharing (CORS).

> Cross-Origin Resource Sharing (CORS) is a specification that enables truly open access across domain-boundaries. If you serve public content, please consider using CORS to open it up for universal JavaScript / browser access.
*From [enable-cors.org](http://enable-cors.org/)*



## Basic

First of all, install the CORS plugin (previously known as feature) into your application.

```kotlin
fun Application.main() {
  ...
  install(CORS)
  ...
}
```

The default configuration to the CORS plugin handles only `GET`, `POST` and `HEAD` HTTP methods and the following headers:

```kotlin
  HttpHeaders.Accept
  HttpHeaders.AcceptLanguages
  HttpHeaders.ContentLanguage
  HttpHeaders.ContentType
```

## Advanced

- [source code](https://github.com/ktorio/ktor/blob/main/ktor-server/ktor-server-core/jvm/src/io/ktor/features/CORS.kt)
- [tests](https://github.com/ktorio/ktor/blob/main/ktor-server/ktor-server-tests/jvm/test/io/ktor/tests/server/features/CORSTest.kt)

Here is an advanced example that demonstrates most of CORS-related API functions

```kotlin
fun Application.main() {
  ...
  install(CORS)
  {
    method(HttpMethod.Options)
    header(HttpHeaders.XForwardedProto)
    anyHost()
    host("my-host")
    // host("my-host:80")
    // host("my-host", subDomains = listOf("www"))
    // host("my-host", schemes = listOf("http", "https"))
    allowCredentials = true
    allowNonSimpleContentTypes = true
    maxAge = Duration.ofDays(1)
  }
  ...
}
```

## Configuration

- `method("HTTP_METHOD")` : Includes this method to the white list of Http methods to use CORS.
- `header("header-name")` : Includes this header to the white list of headers to use CORS.
- `exposeHeader("header-name")` : Exposes this header in the response.
- `exposeXHttpMethodOverride()` : Exposes `X-Http-Method-Override` header in the response
- `anyHost()` : Allows any host to access the resources
- `host("hostname")` : Allows only the specified host to use CORS, it can have the port number, a list of subDomains or the supported schemes.
- `allowCredentials` : Includes `Access-Control-Allow-Credentials` header in the response
- `allowNonSimpleContentTypes`: Inclues `Content-Type` request header to the white list for values other than [simple content types](https://www.w3.org/TR/cors/#simple-header).
- `maxAge`: Includes `Access-Control-Max-Age` header in the response with the given max age


