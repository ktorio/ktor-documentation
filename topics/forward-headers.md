[//]: # (title: Forwarded headers)

<show-structure for="chapter" depth="2"/>

<var name="artifact_name" value="ktor-server-forwarded-header"/>
<var name="package_name" value="io.ktor.server.plugins.forwardedheaders"/>

<tldr>
<p>
<b>Required dependencies</b>: <code>io.ktor:%artifact_name%</code>
</p>
<var name="example_name" value="forwarded-header"/>
<include from="lib.topic" element-id="download_example"/>
<include from="lib.topic" element-id="native_server_supported"/>
</tldr>

The [ForwardedHeaders](https://api.ktor.io/ktor-server/ktor-server-plugins/ktor-server-forwarded-header/io.ktor.server.plugins.forwardedheaders/-forwarded-headers.html) and [XForwardedHeaders](https://api.ktor.io/ktor-server/ktor-server-plugins/ktor-server-forwarded-header/io.ktor.server.plugins.forwardedheaders/-x-forwarded-headers.html) plugins allow you to handle reverse proxy headers to get information about the original [request](requests.md) when a Ktor server is placed behind a reverse proxy. This might be useful for [logging](logging.md) purposes.

* `ForwardedHeaders` handles the `Forwarded` header ([RFC 7239](https://tools.ietf.org/html/rfc7239))
* `XForwardedHeaders` handles the following `X-Forwarded-` headers:
   - `X-Forwarded-Host`/`X-Forwarded-Server` 
   - `X-Forwarded-For` 
   - `X-Forwarded-By`
   - `X-Forwarded-Proto`/`X-Forwarded-Protocol`
   - `X-Forwarded-SSL`/`Front-End-Https`

> To prevent manipulating the `Forwarded` headers, install these plugins if your application only accepts the reverse proxy connections.
> 
{type="note"}


## Add dependencies {id="add_dependencies"}
To use the `ForwardedHeaders`/`XForwardedHeaders` plugins, you need to include the `%artifact_name%` artifact in the build script:

<include from="lib.topic" element-id="add_ktor_artifact"/>


## Install plugins {id="install_plugin"}

<tabs>
<tab title="ForwardedHeader">

<var name="plugin_name" value="ForwardedHeaders"/>
<include from="lib.topic" element-id="install_plugin"/>

</tab>

<tab title="XForwardedHeader">

<var name="plugin_name" value="XForwardedHeaders"/>
<include from="lib.topic" element-id="install_plugin"/>

</tab>
</tabs>

After installing `ForwardedHeaders`/`XForwardedHeaders`, you can get information about the original request using the 
[call.request.origin](#request_info) property.



## Get request information {id="request_info"}

### Proxy request information {id="proxy_request_info"}

To get information about the proxy request, use the [call.request.local](https://api.ktor.io/ktor-server/ktor-server-core/io.ktor.server.request/-application-request/local.html) property inside the [route handler](Routing_in_Ktor.md#define_route).
The code snippet below shows how to obtain information about the proxy address and the host to which the request was made:

```kotlin
```
{src="snippets/forwarded-header/src/main/kotlin/com/example/Application.kt" include-lines="17-19,25"}



### Original request information

To read information about the original request, use the [call.request.origin](https://api.ktor.io/ktor-server/ktor-server-core/io.ktor.server.plugins/origin.html) property:

```kotlin
```
{src="snippets/forwarded-header/src/main/kotlin/com/example/Application.kt" include-lines="17,20-21,25"}

The table below shows the values of different properties exposed by `call.request.origin` depending on whether `ForwardedHeaders`/`XForwardedHeaders` is installed or not.

![Request diagram](forwarded-headers.png){width="706"}

| Property               | Without ForwardedHeaders | With ForwarderHeaders |
|------------------------|--------------------------|-----------------------|
| `origin.localHost`     | _web-server_             | _web-server_          |
| `origin.localPort`     | _8080_                   | _8080_                |
| `origin.serverHost`    | _web-server_             | _proxy_               |
| `origin.serverPort`    | _8080_                   | _80_                  |
| `origin.remoteHost`    | _proxy_                  | _client_              |
| `origin.remotePort`    | _32864_                  | _32864_               |

> You can find the full example here: [forwarded-header](https://github.com/ktorio/ktor-documentation/tree/%ktor_version%/codeSnippets/snippets/forwarded-header).


## Configure ForwardedHeaders {id="configure"}

You may need to configure `ForwardedHeaders`/`XForwardedHeaders` if a request goes through multiple proxies.
In this case, `X-Forwarded-For` contains all the IP addresses of each successive proxy, for example:

```HTTP
X-Forwarded-For: <client>, <proxy1>, <proxy2>
```

By default, `XForwardedHeader` assigns the first entry in `X-Forwarded-For` to the `call.request.origin.remoteHost` property.
You can also supply custom logic for [selecting an IP address](https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/X-Forwarded-For#selecting_an_ip_address). 
[XForwardedHeadersConfig](https://api.ktor.io/ktor-server/ktor-server-plugins/ktor-server-forwarded-header/io.ktor.server.plugins.forwardedheaders/-x-forwarded-headers-config/index.html) exposes the following API for this:

- `useFirstProxy` and `useLastProxy` allow you to take the first or last value from the list of IP addresses, respectively.
- `skipLastProxies` skips the specified number of entries starting from the right and takes the next entry.
   For example, if the `proxiesCount` parameter is equal to `3`, `origin.remoteHost` will return `10.0.0.123` for the header below:
   ```HTTP
   X-Forwarded-For: 10.0.0.123, proxy-1, proxy-2, proxy-3
   ```
- `skipKnownProxies` removes the specified entries from the list and takes the last entry.
   For example, if you pass `listOf("proxy-1", "proxy-3")` to this function, `origin.remoteHost` will return `proxy-2` for the header below:
   ```HTTP
   X-Forwarded-For: 10.0.0.123, proxy-1, proxy-2, proxy-3
   ```
- `extractEdgeProxy` allows you to provide custom logic for extracting the value from the `X-Forward-*` headers.
