[//]: # (title: CORS)

<var name="artifact_name" value="ktor-server-cors"/>
<var name="plugin_name" value="CORS"/>

<microformat>
<p>
<b>Required dependencies</b>: <code>io.ktor:%artifact_name%</code>
</p>
<p>
<b>Code examples</b>: 
<a href="https://github.com/ktorio/ktor-documentation/tree/%current-branch%/codeSnippets/snippets/cors-backend">cors-backend</a>, 
<a href="https://github.com/ktorio/ktor-documentation/tree/%current-branch%/codeSnippets/snippets/cors-frontend">cors-frontend</a>
</p>
</microformat>

If your server supposes to handle [cross-origin requests](https://developer.mozilla.org/en-US/docs/Web/HTTP/CORS), you need to install and configure the [CORS](https://api.ktor.io/ktor-server/ktor-server-plugins/ktor-server-cors/io.ktor.server.plugins/-c-o-r-s/index.html) Ktor plugin. This plugin allows you to configure allowed hosts, HTTP methods, headers set by the client, and so on.

## Add dependencies {id="add_dependencies"}

<include src="lib.xml" include-id="add_ktor_artifact_intro"/>
<include src="lib.xml" include-id="add_ktor_artifact"/>

## Install %plugin_name% {id="install_plugin"}

<include src="lib.xml" include-id="install_plugin"/>


## Configure CORS {id="configure"}

CORS-specific configuration settings are exposed by the [CORS.Configuration](https://api.ktor.io/ktor-server/ktor-server-plugins/ktor-server-cors/io.ktor.server.plugins/-c-o-r-s/-configuration/index.html) class. Let's see how to configure these settings.

### Overview {id="overview"}

Suppose you have a server listening on the `8080` port, with the `/customer` [route](Routing_in_Ktor.md) responding with [JSON](serialization.md#send_data) data. A [code snippet](https://github.com/ktorio/ktor-documentation/tree/%current-branch%/codeSnippets/snippets/cors-frontend) below shows a sample request made using the Fetch API from the client working on another port to make this request cross-origin.

```javascript
```
{src="snippets/cors-frontend/files/js/script.js" initial-collapse-state="collapsed" collapsed-title="fetch('http://0.0.0.0:8080/customer')"}

To allow such a request on the backend side, you need to configure the `CORS` plugin as follows.

```kotlin
```
{src="snippets/cors-backend/src/main/kotlin/com/example/Application.kt" lines="18-21"}

You can find the full example here: [cors-backend](https://github.com/ktorio/ktor-documentation/tree/%current-branch%/codeSnippets/snippets/cors-backend).


### Hosts {id="hosts"}
To specify the allowed host that can make cross-origin requests, use the `host` function. Apart from the hostname, you can specify a port number, a list of subdomains, or the supported HTTP schemes.

```kotlin
install(CORS) {
     host("client-host")
     host("client-host:5000")
     host("client-host", subDomains = listOf("en", "de", "es"))
     host("client-host", schemes = listOf("http", "https"))
}
```

To allows cross-origin requests from any host, use the `anyHost` function.

```kotlin
install(CORS) {
    anyHost()
}
```


### HTTP methods {id="methods"}

By default, the `%plugin_name%` plugin allows the `GET`, `POST` and `HEAD` HTTP methods. To add additional methods, use the `method` function.

```kotlin
install(CORS) {
    method(HttpMethod.Options)
    method(HttpMethod.Put)
    method(HttpMethod.Patch)
    method(HttpMethod.Delete)
}
```


### Allow headers {id="headers"}

By default, the `%plugin_name%` plugin allows the following client headers managed by `Access-Control-Allow-Headers`:
* `Accept`
* `Accept-Language`
* `Content-Language`

To allow additional headers, use the `header` function.
```kotlin
install(CORS) {
    header(HttpHeaders.ContentType)
    header(HttpHeaders.Authorization)
}
```

To allow custom headers, use the `allowHeaders` or `allowHeadersPrefixed` functions. For instance, the code snippet below shows how to allow headers prefixed with `custom-`.

```kotlin
install(CORS) {
    allowHeadersPrefixed("custom-")
}
```

### Expose headers {id="expose-headers"}
The `Access-Control-Expose-Headers` header adds the specified headers to the allowlist that JavaScript in browsers can access.
To configure such headers, use the `exposeHeader` function.

```kotlin
install(CORS) {
    // ...
    exposeHeader("X-My-Custom-Header")
    exposeHeader("X-Another-Custom-Header")
}
```

### Credentials {id="credentials"}

By default, browsers don't send credential information (such as cookies or authentication information) with cross-origin requests. To allow passing this information, set the `Access-Control-Allow-Credentials` response header to `true` using the `allowCredentials` property.

```kotlin
    install(CORS) {
        allowCredentials = true
    }
```



### Miscellaneous {id="misc"}

The `%plugin_name%` plugin also allows you to specify other CORS-related settings. For example, you can use `maxAgeInSeconds` to specify how long the response to the preflight request can be cached without sending another preflight request.

```kotlin
    install(CORS) {
        maxAgeInSeconds = 3600
    }
```

You can learn about other configuration options from [CORS.Configuration](https://api.ktor.io/ktor-server/ktor-server-plugins/ktor-server-cors/io.ktor.server.plugins/-c-o-r-s/-configuration/index.html).