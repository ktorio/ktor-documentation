[//]: # (title: HTTP/2)

<include src="lib.xml" include-id="outdated_warning"/>

<microformat>
<p>
Code examples: <a href="https://github.com/ktorio/ktor-documentation/tree/main/codeSnippets/snippets/http2-netty">http2-netty</a>, <a href="https://github.com/ktorio/ktor-documentation/tree/main/codeSnippets/snippets/http2-jetty">http2-jetty</a>
</p>
</microformat>

[HTTP/2](https://en.wikipedia.org/wiki/HTTP/2) is a modern binary duplex multiplexing protocol designed as a replacement for HTTP/1.x.

Jetty, Netty, and Tomcat engines provide HTTP/2 implementations that Ktor can use. However, there are significant differences,
and each engine requires additional configuration. Once your host is configured properly for Ktor, HTTP/2 support will be activated automatically.

Key requirements:

* SSL certificate (can be self-signed)
* ALPN implementation suitable for a particular engine (see corresponding sections for Netty, Jetty, and Tomcat)

## SSL certificate {id="ssl_certificate"}

As per the specification, HTTP/2 does not require encryption, but all browsers will require encrypted connections to be used with HTTP/2.
That's why a working TLS environment is a prerequisite for enabling HTTP/2. Therefore, a certificate is required to enable encryption.
For testing purposes, it can be generated with `keytool` from the JDK ...

```bash
keytool -keystore test.jks -genkeypair -alias testkey -keyalg RSA -keysize 4096 -validity 5000 -dname 'CN=localhost, OU=ktor, O=ktor, L=Unspecified, ST=Unspecified, C=US'
```

... or by using the [generateCertificate](self-signed-certificate.md) function.

The next step is configuring Ktor to use your keystore. See the example `application.conf`:


```kotlin
```
{src="snippets/http2-netty/src/main/resources/application.conf"}

## ALPN implementation

HTTP/2 requires ALPN ([Application-Layer Protocol Negotiation](https://en.wikipedia.org/wiki/Application-Layer_Protocol_Negotiation)) to be enabled. The first option is to use an external ALPN implementation that needs to be added to the boot classpath.
Another option is to use OpenSSL native bindings and precompiled native binaries. Also, each particular engine can support only one of these methods.

### Jetty

Since ALPN APIs are supported starting with Java 8, the Jetty engine doesn't require any specific configurations for using HTTP/2. So, you only need to:
1. [Create a server](Engines.md#choose-create-server) with the Jetty engine.
2. Add an SSL configuration as described in [](#ssl_certificate).
3. Configure `sslPort`.

The [http2-jetty](https://github.com/ktorio/ktor-documentation/tree/main/codeSnippets/snippets/http2-jetty) runnable example demonstrates HTTP/2 support for Jetty.

### Netty

The easiest way to enable HTTP/2 in Netty is to use OpenSSL bindings ([tcnative netty port](https://netty.io/wiki/forked-tomcat-native.html)). 
Add an API jar to dependencies:

```groovy
```
{src="snippets/http2-netty/build.gradle" lines="39"}

and then  native implementation (statically linked BoringSSL library, a fork of OpenSSL):

```groovy
```
{src="snippets/http2-netty/build.gradle" lines="40-41"}

where `tc.native.classifier` should be one of the following: `linux-x86_64`, `osx-x86_64` or `windows-x86_64`. The [http2-netty](https://github.com/ktorio/ktor-documentation/tree/main/codeSnippets/snippets/http2-netty) runnable example demonstrates how to enable HTTP/2 support for Netty.

### Tomcat and other servlet containers

Similar to Netty, to get HTTP/2 working in Tomcat you need native OpenSSL bindings. Unfortunately, Tomcat's tcnative is not completely compatible with the Netty one.
This is why you need a slightly different binary. You can get it here (<https://tomcat.apache.org/native-doc/>), or you can try Netty's tcnative. However,
you'll have to guess which exact version is compatible with your specific Tomcat version.

If you are deploying your Ktor application as a war package into the server (servlet container), then you will have to configure your Tomcat server properly:

* <https://tomcat.apache.org/tomcat-8.5-doc/config/http.html#HTTP/2_Support>
* <https://tomcat.apache.org/tomcat-8.5-doc/config/http2.html>
* <https://tomcat.apache.org/tomcat-8.5-doc/ssl-howto.html>
* <https://tomcat.apache.org/native-doc/>