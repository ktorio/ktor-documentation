[//]: # (title: HttpsRedirect)

<include src="lib.xml" include-id="outdated_warning"/>

<var name="plugin_name" value="HttpsRedirect"/>
<var name="artifact_name" value="ktor-server-http-redirect"/>

<microformat>
<p>
Required dependencies: <code>io.ktor:%artifact_name%</code>
</p>
</microformat>

The `%plugin_name%` plugin makes all the affected HTTP calls perform a redirect to its
HTTPS counterpart before processing the call.

By default, the redirection is a `301 Moved Permanently`,
but it can be configured to be a `302 Found` redirect.

## Add dependencies {id="add_dependencies"}

<include src="lib.xml" include-id="add_ktor_artifact_intro"/>
<include src="lib.xml" include-id="add_ktor_artifact"/>


## Install %plugin_name% {id="install_plugin"}

<include src="lib.xml" include-id="install_plugin"/>

The code above installs the `%plugin_name%` plugin with the default configuration.

>When behind a reverse-proxy, you will need to install the `ForwardedHeaderSupport` or the `XForwardedHeaderSupport`
>plugin, for the `%plugin_name%` plugin to properly detect HTTPS requests.
>
{type="note"}

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
{id="testing"}

Applying this plugin changes how [testing](Testing.md) works.
After applying this plugin, each `handleRequest` you perform, results in a redirection response.
And probably this is not what you want in most cases, since that behaviour is already tested.

### XForwardedHeaderSupport trick

As shown [in this test](https://github.com/ktorio/ktor/blob/main/ktor-server/ktor-server-tests/jvm/test/io/ktor/tests/server/plugins/HttpsRedirectPluginTest.kt#L33-L50){ target="_blank"},
you can install the `XForwardedHeaderSupport` plugin and add a `addHeader(HttpHeaders.XForwardedProto, "https")`
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

### Do not install the plugin when testing or uninstall it

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
that the `%plugin_name%` plugin is installed and other things that you are not doing in tests.

### I get an infinite redirect when using this plugin

Have you installed the `XForwardedHeaderSupport` or the `ForwardedHeaderSupport` plugin?
Check [this FAQ entry](FAQ.xml#infinite-redirect) for more details.