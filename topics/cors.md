[//]: # (title: CORS)

<show-structure for="chapter" depth="2"/>

<var name="artifact_name" value="ktor-server-cors"/>
<var name="package_name" value="io.ktor.server.plugins.cors"/>
<var name="plugin_name" value="CORS"/>

<tldr>
<p>
<b>Required dependencies</b>: <code>io.ktor:%artifact_name%</code>
</p>
<var name="example_name" value="cors"/>
<include from="lib.topic" element-id="download_example"/>
<include from="lib.topic" element-id="native_server_supported"/>
</tldr>

If your server is supposed to handle [cross-origin requests](https://developer.mozilla.org/en-US/docs/Web/HTTP/CORS),
you need to install and configure
the [CORS](https://api.ktor.io/ktor-server/ktor-server-plugins/ktor-server-cors/io.ktor.server.plugins.cors.routing/-c-o-r-s.html)
Ktor plugin. This plugin allows you to configure allowed hosts, HTTP methods, headers set by the client, and so on.

## Add dependencies {id="add_dependencies"}

<include from="lib.topic" element-id="add_ktor_artifact_intro"/>
<include from="lib.topic" element-id="add_ktor_artifact"/>

## Install %plugin_name% {id="install_plugin"}

<include from="lib.topic" element-id="install_plugin"/>
<include from="lib.topic" element-id="install_plugin_route"/>

> If you install the `CORS` plugin to a specific route, you need to add
the `options` [handler](Routing_in_Ktor.md#define_route) to this route. This allows Ktor to respond correctly to a CORS
preflight request.

## Configure CORS {id="configure"}

CORS-specific configuration settings are exposed by
the [CORSConfig](https://api.ktor.io/ktor-server/ktor-server-plugins/ktor-server-cors/io.ktor.server.plugins.cors/-c-o-r-s-config/index.html)
class. Let's see how to configure these settings.

### Overview {id="overview"}

Suppose you have a server listening on the `8080` port, with the `/customer` [route](Routing_in_Ktor.md) responding
with [JSON](serialization.md#send_data) data. A code snippet below shows a sample request made using the Fetch API from
the client working on another port to make this request cross-origin:

```javascript
```

{src="snippets/cors/files/js/script.js" initial-collapse-state="collapsed" collapsed-title="
fetch('http://0.0.0.0:8080/customer')"}

To allow such a request on the backend side, you need to configure the `CORS` plugin as follows:

```kotlin
```

{src="snippets/cors/src/main/kotlin/com/example/Application.kt" include-lines="47-50"}

You can find the full example
here: [cors](https://github.com/ktorio/ktor-documentation/tree/%ktor_version%/codeSnippets/snippets/cors).

### Hosts {id="hosts"}

To specify the allowed host that can make cross-origin requests, use the `allowHost` function. Apart from the hostname,
you can specify a port number, a list of subdomains, or the supported HTTP schemes.

```kotlin
install(CORS) {
    allowHost("client-host")
    allowHost("client-host:8081")
    allowHost("client-host", subDomains = listOf("en", "de", "es"))
    allowHost("client-host", schemes = listOf("http", "https"))
}
```

To allow cross-origin requests from any host, use the `anyHost` function.

```kotlin
install(CORS) {
    anyHost()
}
```

### HTTP methods {id="methods"}

By default, the `%plugin_name%` plugin allows the `GET`, `POST` and `HEAD` HTTP methods. To add additional methods, use
the `allowMethod` function.

```kotlin
install(CORS) {
    allowMethod(HttpMethod.Options)
    allowMethod(HttpMethod.Put)
    allowMethod(HttpMethod.Patch)
    allowMethod(HttpMethod.Delete)
}
```

### Allow headers {id="headers"}

By default, the `%plugin_name%` plugin allows the following client headers managed by `Access-Control-Allow-Headers`:

* `Accept`
* `Accept-Language`
* `Content-Language`

To allow additional headers, use the `allowHeader` function.

```kotlin
install(CORS) {
    allowHeader(HttpHeaders.ContentType)
    allowHeader(HttpHeaders.Authorization)
}
```

To allow custom headers, use the `allowHeaders` or `allowHeadersPrefixed` functions. For instance, the code snippet
below shows how to allow headers prefixed with `custom-`.

```kotlin
install(CORS) {
    allowHeadersPrefixed("custom-")
}
```

> Note that `allowHeaders` or `allowHeadersPrefixed` require setting
the [allowNonSimpleContentTypes](https://api.ktor.io/ktor-server/ktor-server-plugins/ktor-server-cors/io.ktor.server.plugins.cors/-c-o-r-s-config/allow-non-simple-content-types.html)
property to `true` for non-simple content types.

### Expose headers {id="expose-headers"}

The `Access-Control-Expose-Headers` header adds the specified headers to the allowlist that JavaScript in browsers can
access.
To configure such headers, use the `exposeHeader` function.

```kotlin
install(CORS) {
    // ...
    exposeHeader("X-My-Custom-Header")
    exposeHeader("X-Another-Custom-Header")
}
```

### Credentials {id="credentials"}

By default, browsers don't send credential information (such as cookies or authentication information) with cross-origin
requests. To allow passing this information, set the `Access-Control-Allow-Credentials` response header to `true` using
the `allowCredentials` property.

```kotlin
install(CORS) {
    allowCredentials = true
}
```

### Miscellaneous {id="misc"}

The `%plugin_name%` plugin also allows you to specify other CORS-related settings. For example, you can
use `maxAgeInSeconds` to specify how long the response to the preflight request can be cached without sending another
preflight request.

```kotlin
install(CORS) {
    maxAgeInSeconds = 3600
}
```

You can learn about other configuration options
from [CORSConfig](https://api.ktor.io/ktor-server/ktor-server-plugins/ktor-server-cors/io.ktor.server.plugins.cors/-c-o-r-s-config/index.html).