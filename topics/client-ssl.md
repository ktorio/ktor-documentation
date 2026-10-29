[//]: # (title: SSL in Ktor Client)

<show-structure for="chapter" depth="3"/>
<primary-label ref="client-plugin"/>

<tldr>
<var name="example_name" value="client-ssl-config"/>
<include from="lib.topic" element-id="download_example"/>
</tldr>

To configure SSL in the Ktor client, you need to customize the [configuration of an engine](client-engines.md#configure) used by your client.

In this topic, you'll learn how to add an SSL certificate for engines that target [JVM](client-engines.md#jvm) and [Android](client-engines.md#jvm-android).

> To learn how to generate a self-signed certificate with Ktor, see [](server-ssl.md#self-signed).
> 
{style="tip"}

## Load SSL settings {id="ssl-settings"}

In this topic, the Ktor client is using a certificate loaded from an existing KeyStore file (`keystore.jks`) generated
for the server.

Given that every engine uses different [JSSE API](https://docs.oracle.com/en/java/javase/17/security/java-secure-socket-extension-jsse-reference-guide.html#GUID-B7AB25FA-7F0C-4EFA-A827-813B2CE7FBDC) to configure SSL (for example, `SSLContext` for Apache or
`TrustManager` for Jetty), you need to have the capability to obtain corresponding SSL configurations.

The code snippet below creates the `SslSettings` object that loads a certificate from an existing KeyStore file (`keystore.jks`)
and provides functions for loading SSL configurations.

The same KeyStore is used to trust the server certificate and, through `KeyManagerFactory`, to present a client certificate,
which is required by a server that has a trust store configured:

```kotlin
```
{src="snippets/client-ssl-config/src/main/kotlin/com/example/Application.kt" include-lines="68-98"}

## Configure SSL in Ktor {id="configure-ssl"}

SSL configuration differs depending on the engine. The following sections describe how to configure SSL for
each engine.

### JVM {id="jvm"}

#### Apache {id="apache"}

To enable SSL for the Apache client, configure an `SSLContext` using the `sslContext` parameter:

```kotlin
```
{src="snippets/client-ssl-config/src/main/kotlin/com/example/Application.kt" include-lines="21-25"}

#### Java {id="java"}

For the Java client, configure an `SSLContext` using the `sslContext()` function inside the `config {}` block:

```kotlin
```
{src="snippets/client-ssl-config/src/main/kotlin/com/example/Application.kt" include-lines="26-32"}

#### Jetty {id="jetty"}

For the Jetty client, create an instance of `SslContextFactory` and pass `SSLContext`:

```kotlin
```
{src="snippets/client-ssl-config/src/main/kotlin/com/example/Application.kt" include-lines="33-39"}

### JVM and Android {id="jvm-android"}

> All engines targeted for Android use [network security configuration](https://developer.android.com/training/articles/security-config).
> 
{style="note"}

#### CIO {id="cio"}

The CIO engine allows you to configure HTTPS settings inside the `https {}` block.
Inside this block, you can access TLS parameters provided by [`TLSConfigBuilder`](https://api.ktor.io/ktor-network-tls/io.ktor.network.tls/-t-l-s-config-builder/index.html).

In the following example, a `TrustManager` instance is used to trust the server certificate, and the `addKeyStore()` 
function adds a client certificate:

```kotlin
```
{src="snippets/client-ssl-config/src/main/kotlin/com/example/Application.kt" include-lines="40-47"}

> The [sockets-client-tls](https://github.com/ktorio/ktor-documentation/tree/main/codeSnippets/snippets/sockets-client-tls) example shows how to trust all certificates.
> This approach should be used for development purposes only.
>
{style="tip"}

#### Android {id="android"}

The Android engine uses the `sslManager` property to configure SSL settings. 
This property accepts `HttpsURLConnection` as a parameter that allows you to pass `SSLSocketFactory`:

```kotlin
```
{src="snippets/client-ssl-config/src/main/kotlin/com/example/Application.kt" include-lines="48-54"}

#### OkHttp {id="okhttp"}

To configure OkHttp for using SSL, pass a `SSLSocketFactory` and `X509TrustManager` to the `sslSocketFactory()` function:

```kotlin
```
{src="snippets/client-ssl-config/src/main/kotlin/com/example/Application.kt" include-lines="55-61"}

### Darwin {id="darwin"}

To configure trusted certificates for the Darwin engine, use [`CertificatePinner`](https://api.ktor.io/ktor-client-darwin/io.ktor.client.engine.darwin.certificates/-certificate-pinner/index.html).