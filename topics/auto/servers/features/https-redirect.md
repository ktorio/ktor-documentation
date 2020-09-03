[//]: # (title: HttpsRedirect)
[//]: # (caption: Redirect HTTP requests to HTTPS)
[//]: # (category: servers)
[//]: # (permalink: /servers/features/https-redirect.html)
[//]: # (keywords: https ssl)
[//]: # (feature: feature)
[//]: # (artifact: io.ktor)
[//]: # (class: io.ktor.features.HttpsRedirect)
[//]: # (redirect_from: redirect_from)
[//]: # (- /features/https-redirect.html: - /features/https-redirect.html)
[//]: # (ktor_version_review: 1.0.0)

This feature will make all the affected HTTP calls perform a redirect to its
HTTPS counterpart before processing the call.

By default the redirection is a `301 Moved Permanently`,
but it can be configured to be a `302 Found` redirect.

{% include feature.html %}

## Usage

```kotlin
fun Application.main() {
    install(HttpsRedirect)
    // install(XForwardedHeaderSupport) // Required when behind a reverse-proxy
}
```

The code above installs the HttpsRedirect feature with the default configuration.

When behind a reverse-proxy, you will need to install the `ForwardedHeaderSupport` or the `XForwardedHeaderSupport`
feature, for the `HttpsRedirect` feature to properly detect HTTPS requests.
{ .note}

## Configuration

```kotlin
fun Application.main() {
    install(HttpsRedirect) {
        // The port to redirect to. By default 443, the default HTTPS port. 
        sslPort = 443
        // 301 Moved Permanently, or 302 Found redirect.
        permanentRedirect = true
    }
}
```

## Testing
{id="testing "}

Applying this feature changes how [testing](/servers/testing.html) works.
After applying this feature, each `handleRequest` you perform, results in a redirection response.
And probably this is not what you want in most cases, since that behaviour is already tested.

### XForwardedHeaderSupport trick

As shown [in this test](https://github.com/ktorio/ktor/blob/bb0765ce00e5746c954fea70270cf7d802a40648/ktor-server/ktor-server-tests/test/io/ktor/tests/server/features/HttpsRedirectFeatureTest.kt#L31-L49){ target="_blank"},
you can install the `XForwardedHeaderSupport` feature and add a `addHeader(HttpHeaders.XForwardedProto, "https")`
header to the request.

```kotlin
@Test
fun testRedirectHttps() {
    withTestApplication {
        application.install(XForwardedHeaderSupport)
        application.install(HttpsRedirect)
        application.routing {
            get("/") {
                call.respond("ok")
            }
        }

        handleRequest(HttpMethod.Get, "/", {
            addHeader(HttpHeaders.XForwardedProto, "https")
        }).let { call ->
            assertEquals(HttpStatusCode.OK, call.response.status())
        }
    }
}
```

### Do not install the feature when testing or uninstall it

Uninstalling it:

```kotlin
application.uninstall(HttpsRedirect)
```

Prevent installation in the first place:

```kotlin
// The function referenced in the application.conf
fun Application.mymodule() {
    mymoduleConfigured()
}

// The function referenced in the tests
fun Application.mymoduleForTesting() {
    mymoduleConfigured(installHttpsRedirect = false)
}

fun Application.mymoduleConfigured(installHttpsRedirect: Boolean = true) {
    if (installHttpsRedirect) {
        install(HttpsRedirect)
    }
    // ...
}
```

In this case, you can also have a separate test that calls `mymodule` instead of `mymoduleForTesting` to verify
that the `HttpsRedirect` feature is installed and other things that you are not doing in tests.

### I get an infinite redirect when using this feature

Have you installed the `XForwardedHeaderSupport` or the `ForwardedHeaderSupport` feature?
Check [this FAQ entry](/quickstart/faq.html#infinite-redirect) for more details.