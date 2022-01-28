[//]: # (title: SSL)

<microformat>
<var name="example_name" value="client-ssl-config"/>
<include src="lib.xml" include-id="download_example"/>
</microformat>

To configure SSL in the Ktor client, you need to customize the [configuration of an engine](http-client_engines.md#configure) used by your client.
In this topic, we'll show you how to add an SSL certificate for different [JVM engines](http-client_engines.md#jvm).

> To learn how to generate a self-signed certificate using the Ktor API, see [](ssl.md#self-signed).


## Load SSL settings {id="ssl-settings"}

In this topic, the Ktor client will be using a certificate loaded from the existing KeyStore file (`keystore.jks`) generated for the server.
Given that different engines use different [JSSE API](https://docs.oracle.com/en/java/javase/17/security/java-secure-socket-extension-jsse-reference-guide.html#GUID-B7AB25FA-7F0C-4EFA-A827-813B2CE7FBDC) to configure SSL (for example, `SSLContext` for Apache or `TrustManager` for Jetty), we need to have the capability to obtain corresponding SSL configurations. The code snippet below creates the `SslSettings` object that loads a certificate from the existing KeyStore file (`keystore.jks`) and provides functions for loading SSL configurations:

```kotlin
```
{src="snippets/client-ssl-config/src/main/kotlin/com/example/Application.kt" lines="66-90"}

## Configure SSL in Ktor {id="configure-ssl"}

In this section, we'll see how to configure SSL for different engines.
You can find the full example here: [client-ssl-config](https://github.com/ktorio/ktor-documentation/tree/%current-branch%/codeSnippets/snippets/client-ssl-config).

### Apache {id="apache"}

To enable SSL for Apache, you need to pass `SSLContext`:

```kotlin
```
{src="snippets/client-ssl-config/src/main/kotlin/com/example/Application.kt" lines="20-24"}


### Java {id="java"}

For the Java client, pass `SSLContext` to the `sslContext` function inside the `config` block:

```kotlin
```
{src="snippets/client-ssl-config/src/main/kotlin/com/example/Application.kt" lines="25-31"}


### Jetty {id="jetty"}

For Jetty, you need to create an instance of `SslContextFactory` and pass `SSLContext`:

```kotlin
```
{src="snippets/client-ssl-config/src/main/kotlin/com/example/Application.kt" lines="32-38"}


### CIO {id="cio"}

The CIO engine allows you to configure HTTPS settings inside the `https` block.
Inside this block, you can access TLS parameters provided by [TLSConfigBuilder](https://api.ktor.io/ktor-network/ktor-network-tls/io.ktor.network.tls/-t-l-s-config-builder/index.html).
In our example, a `TrustManager` instance is used to configure a certificate:

```kotlin
```
{src="snippets/client-ssl-config/src/main/kotlin/com/example/Application.kt" lines="39-45"}


### Android {id="android"}

The Android engine uses the `sslManager` property to configure SSL settings. 
This property accepts `HttpsURLConnection` as a parameter that allows you to pass `SSLSocketFactory`:

```kotlin
```
{src="snippets/client-ssl-config/src/main/kotlin/com/example/Application.kt" lines="46-52"}


### OkHttp {id="okhttp"}

To configure OkHttp for using SSL, you need to pass `SSLSocketFactory` and `X509TrustManager` to the `sslSocketFactory` function:

```kotlin
```
{src="snippets/client-ssl-config/src/main/kotlin/com/example/Application.kt" lines="53-59"}
