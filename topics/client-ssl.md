[//]: # (title: SSL in Ktor Client)

<show-structure for="chapter" depth="3"/>
<primary-label ref="client-plugin"/>

<tldr>
<var name="example_name" value="client-ssl-config"/>
<include from="lib.topic" element-id="download_example"/>
</tldr>

To configure SSL in the Ktor client, you need to customize the [configuration of an engine](client-engines.md#configure) used by your client.
In this topic, we'll show you how to add an SSL certificate for engines that target [JVM](client-engines.md#jvm) and [Android](client-engines.md#jvm-android).

> To learn how to generate a self-signed certificate using the Ktor API, see [](server-ssl.md#self-signed).


## Load SSL settings {id="ssl-settings"}

In this topic, the Ktor client will be using a certificate loaded from the existing KeyStore file (`keystore.jks`) generated for the server.
Given that different engines use different [JSSE API](https://docs.oracle.com/en/java/javase/17/security/java-secure-socket-extension-jsse-reference-guide.html#GUID-B7AB25FA-7F0C-4EFA-A827-813B2CE7FBDC) to configure SSL (for example, `SSLContext` for Apache or `TrustManager` for Jetty), we need to have the capability to obtain corresponding SSL configurations. The code snippet below creates the `SslSettings` object that loads a certificate from the existing KeyStore file (`keystore.jks`) and provides functions for loading SSL configurations:

```kotlin
```
{src="snippets/client-ssl-config/src/main/kotlin/com/example/Application.kt" include-lines="66-90"}

## Configure SSL in Ktor {id="configure-ssl"}

In this section, we'll see how to configure SSL for different engines.
You can find the full example here: [client-ssl-config](https://github.com/ktorio/ktor-documentation/tree/%ktor_version%/codeSnippets/snippets/client-ssl-config).


### JVM {id="jvm"}

#### Apache {id="apache"}

To enable SSL for Apache, you need to pass `SSLContext`:

```kotlin
```
{src="snippets/client-ssl-config/src/main/kotlin/com/example/Application.kt" include-lines="20-24"}


#### Java {id="java"}

For the Java client, pass `SSLContext` to the `sslContext` function inside the `config` block:

```kotlin
```
{src="snippets/client-ssl-config/src/main/kotlin/com/example/Application.kt" include-lines="25-31"}


#### Jetty {id="jetty"}

For Jetty, you need to create an instance of `SslContextFactory` and pass `SSLContext`:

```kotlin
```
{src="snippets/client-ssl-config/src/main/kotlin/com/example/Application.kt" include-lines="32-38"}


### JVM and Android {id="jvm-android"}

> All engines targeted for Android use [network security configuration](https://developer.android.com/training/articles/security-config).

#### CIO {id="cio"}

The CIO engine allows you to configure HTTPS settings inside the `https` block.
Inside this block, you can access TLS parameters provided by [TLSConfigBuilder](https://api.ktor.io/ktor-network/ktor-network-tls/io.ktor.network.tls/-t-l-s-config-builder/index.html).
In our example, a `TrustManager` instance is used to configure a certificate:

```kotlin
```
{src="snippets/client-ssl-config/src/main/kotlin/com/example/Application.kt" include-lines="39-45"}

> The [sockets-client-tls](https://github.com/ktorio/ktor-documentation/tree/%ktor_version%/codeSnippets/snippets/sockets-client-tls) example shows how to trust all certificates.
> This approach should be used for development purposes only.


#### Android {id="android"}

The Android engine uses the `sslManager` property to configure SSL settings. 
This property accepts `HttpsURLConnection` as a parameter that allows you to pass `SSLSocketFactory`:

```kotlin
```
{src="snippets/client-ssl-config/src/main/kotlin/com/example/Application.kt" include-lines="46-52"}


#### OkHttp {id="okhttp"}

To configure OkHttp for using SSL, you need to pass `SSLSocketFactory` and `X509TrustManager` to the `sslSocketFactory` function:

```kotlin
```
{src="snippets/client-ssl-config/src/main/kotlin/com/example/Application.kt" include-lines="53-59"}

### Darwin {id="darwin"}

To configure trusted certificates for the Darwin engine, use [CertificatePinner](https://api.ktor.io/ktor-client/ktor-client-darwin/io.ktor.client.engine.darwin.certificates/-certificate-pinner/index.html).