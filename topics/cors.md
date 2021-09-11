[//]: # (title: CORS)

<microformat>
<p>
Code examples: 
<a href="https://github.com/ktorio/ktor-documentation/tree/main/codeSnippets/snippets/cors-backend">cors-backend</a>, 
<a href="https://github.com/ktorio/ktor-documentation/tree/main/codeSnippets/snippets/cors-frontend">cors-frontend</a>
</p>
<p>
API configuration: <a href="https://api.ktor.io/ktor-server/ktor-server-core/ktor-server-core/io.ktor.features/-c-o-r-s/-configuration/index.html">CORS.Configuration</a>
</p>
</microformat>

If your server supposes to handle [cross-origin requests](https://developer.mozilla.org/en-US/docs/Web/HTTP/CORS), you need to install and configure the [CORS](https://api.ktor.io/ktor-server/ktor-server-core/ktor-server-core/io.ktor.features/-c-o-r-s/index.html) Ktor plugin. This plugin allows you to configure allowed hosts, headers sent by the client, and so on.

## Install CORS {id="install_feature"}
<var name="feature_name" value="CORS"/>
<include src="lib.xml" include-id="install_feature"/>


## Configure CORS {id="configure"}

### Overview {id="overview"}

Suppose the client with the `0.0.0.0:5000` address makes a `POST` request with JSON data to your server. A [code snippet](https://github.com/ktorio/ktor-documentation/tree/main/codeSnippets/snippets/cors-frontend) below shows such a request made using the Fetch API.

```javascript
```
{src="snippets/cors-frontend/files/js/script.js" initial-collapse-state="collapsed" collapsed-title="fetch('http://0.0.0.0:8080/customer')"}

To allow such a request on the backend side, you need to configure the `CORS` plugin as follows.

```kotlin
```
{src="snippets/cors-backend/src/main/kotlin/com/example/Application.kt" lines="17-20"}

You can find the full example here: [cors-backend](https://github.com/ktorio/ktor-documentation/tree/main/codeSnippets/snippets/cors-backend).


### Hosts {id="hosts"}
Allow requests from the specified domains and schemes

```kotlin
install(CORS) {
    anyHost()
    // host("my-host")
    // host("my-host:8080")
    // host("my-host", subDomains = listOf("en,de,es"))
    // host("my-host", schemes = listOf("http", "https"))
}
```


### HTTP methods {id="methods"}
The default configuration to the CORS plugin handles only `GET`, `POST` and `HEAD` HTTP methods and the following headers:


### HTTP headers {id="headers"}

```kotlin
  HttpHeaders.Accept
  HttpHeaders.AcceptLanguages
  HttpHeaders.ContentLanguage
  HttpHeaders.ContentType
```

### Credentials {id="credentials"}

### Max age {id="max-age"}



