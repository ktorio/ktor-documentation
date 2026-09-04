[//]: # (title: HTTP/2)

<show-structure for="chapter" depth="2"/>

<tldr>
<p>
<b>Code examples</b>: <a href="https://github.com/ktorio/ktor-documentation/tree/main/codeSnippets/snippets/http2-netty">http2-netty</a>, <a href="https://github.com/ktorio/ktor-documentation/tree/main/codeSnippets/snippets/http2-jetty">http2-jetty</a>
</p>
</tldr>

[HTTP/2](https://en.wikipedia.org/wiki/HTTP/2) is a modern binary multiplexing protocol designed as a replacement for
HTTP/1.x.

Ktor supports HTTP/2 with the Jetty and Netty server engines. However, there are significant differences, and each engine
requires additional configuration. Once your host is configured, HTTP/2 support is activated automatically.

For HTTP/2 over TLS, you typically need:

* [An SSL certificate](#ssl_certificate), which can be self-signed.
* [An ALPN implementation](#apln_implementation) supported by the selected engine.

[HTTP/2 over cleartext (h2c)](#http2-without-tls) is available with the Netty engine and doesn't require SSL or ALPN configuration.

## Configure an SSL certificate {id="ssl_certificate"}

HTTP/2 doesn't require TLS, but browsers typically support HTTP/2 only over encrypted connections. To use HTTP/2 over
TLS, you need to configure an SSL certificate for your server.

For testing, you can generate a self-signed certificate using the JDK `keytool` utility:

```bash
keytool -keystore test.jks -genkeypair -alias testkey -keyalg RSA -keysize 4096 -validity 5000 -dname 'CN=localhost, OU=ktor, O=ktor, L=Unspecified, ST=Unspecified, C=US'
```

You can also create a keystore programmatically using the [`buildKeyStore()`](server-ssl.md) function.

Then, configure Ktor to use the keystore in your
<path>application.conf</path> or <path>application.yaml</path> [configuration file](server-configuration-file.topic):

<tabs group="config">
<tab title="application.conf" group-key="hocon">

```shell
```
{src="snippets/http2-netty/src/main/resources/application.conf"}

</tab>
<tab title="application.yaml" group-key="yaml">

```yaml
```
{src="snippets/http2-netty/src/main/resources/_application.yaml"}

</tab>
</tabs>

## Configure ALPN {id="apln_implementation"}

HTTP/2 over TLS uses [Application-Layer Protocol Negotiation (ALPN)](https://en.wikipedia.org/wiki/Application-Layer_Protocol_Negotiation) to negotiate the protocol between the client
and server. ALPN configuration depends on the server engine.

### Jetty {id="jetty"}

The Jetty engine handles ALPN without additional Ktor configuration.
To use HTTP/2 over TLS with Jetty:
1. [Create a server](server-engines.md#choose-create-server) with the Jetty engine.
2. [](#ssl_certificate).
3. Configure `sslPort`.

> For a complete runnable example of HTTP/2 with Jetty, see [http2-jetty](https://github.com/ktorio/ktor-documentation/tree/main/codeSnippets/snippets/http2-jetty) 
>
{style="tip"}

### Netty {id="netty"}

To use HTTP/2 over TLS with Netty, add the [Netty `tcnative`](https://netty.io/wiki/forked-tomcat-native.html) OpenSSL bindings.

The following example adds the statically linked BoringSSL implementation to the
<path>build.gradle.kts</path> file:

```kotlin
```
{src="snippets/http2-netty/build.gradle.kts" include-lines="20-28,34-39"}

The `tc.native.classifier` can be `linux-x86_64`, `osx-x86_64`, or `windows-x86_64`.

> For a complete runnable example of HTTP/2 with Netty, see [http2-netty](https://github.com/ktorio/ktor-documentation/tree/main/codeSnippets/snippets/http2-netty).
> 
{style="tip"}

## HTTP/2 without TLS {id="http2-without-tls"}

The Netty engine supports [HTTP/2 over cleartext (h2c)](https://httpwg.org/specs/rfc7540.html#discover-http), which allows HTTP/2 communication without TLS.
This can be useful within private networks where encryption is not required. 

Clients can connect using h2c directly or upgrade an HTTP/1.1 connection to HTTP/2.

To enable h2c, set both `enableH2c` and `enableHttp2` options to `true` in the engine configuration:

```kotlin
embeddedServer(Netty, configure = {
    connector {
        port = 8080
    }
    enableHttp2 = true
    enableH2c = true
})
```

You can enable h2c and HTTP/2 over TLS on the same server. Cleartext connectors accept h2c connections, while SSL
connectors use HTTP/2 over TLS.