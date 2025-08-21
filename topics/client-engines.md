[//]: # (title: Client engines)

<show-structure for="chapter" depth="2"/>

<link-summary>
Learn about engines that process network requests.
</link-summary>

The [Ktor HTTP client](client-create-and-configure.md) is multiplatform and runs on JVM,
[Android](https://kotlinlang.org/docs/android-overview.html), [JavaScript](https://kotlinlang.org/docs/js-overview.html)
(including WebAssembly), and [Native](https://kotlinlang.org/docs/native-overview.html) targets. Each platform requires
a specific engine to process network requests.
For example, you can use `Apache` or `Jetty` for JVM applications, `OkHttp` or `Android`
for Android, `Curl` for desktop applications targeting Kotlin/Native. Every engine differs slightly in features and
configuration, so you can choose the one that best meets your platform and use-case needs.

## Supported platforms {id="platforms"}

The table below lists the [platforms](client-supported-platforms.md) supported by each engine:

| Engine  | Platforms                                                                                                         |
|---------|-------------------------------------------------------------------------------------------------------------------|
| Apache  | [JVM](#jvm)                                                                                                       |
| Java    | [JVM](#jvm)                                                                                                       |
| Jetty   | [JVM](#jvm)                                                                                                       |
| Android | [JVM](#jvm), [Android](#jvm-android)                                                                              |
| OkHttp  | [JVM](#jvm), [Android](#jvm-android)                                                                              |
| Darwin  | [Native](#native)                                                                                                 |
| WinHttp | [Native](#native)                                                                                                 |
| Curl    | [Native](#native)                                                                                                 |
| CIO     | [JVM](#jvm), [Android](#jvm-android), [Native](#native), [JavaScript](#js), [WasmJS](#jvm-android-native-wasm-js) |
| Js      | [JavaScript](#js)                                                                                                 |

## Supported Android/Java versions {id="minimum-version"}

Client engines targeting JVM or both JVM and Android support the following Android/Java versions:

| Engine  | Android version   | Java version |
|---------|-------------------|--------------|
| Apache  |                   | 8+           |
| Java    |                   | 11+          |
| Jetty   |                   | 11+          |
| CIO     | 7.0+ <sup>*</sup> | 8+           |
| Android | 1.x+              | 8+           |
| OkHttp  | 5.0+              | 8+           |

_* To use the CIO engine on older Android versions, you need to
enable [Java 8 API desugaring](https://developer.android.com/studio/write/java8-support)._

## Limitations {id="limitations"}

### HTTP/2 and WebSockets

Not all engines support the HTTP/2 protocol. If an engine supports HTTP/2, you can enable it in the
engine's configuration. For example, with the [Java](#java) engine.

The table below shows whether a specific engine supports HTTP/2 and [WebSockets](client-websockets.topic):

| Engine  | HTTP/2             | WebSockets |
|---------|--------------------|------------|
| Apache  | ✅️ _(for Apache5)_ | ✖️         |
| Java    | ✅                  | ✅️         |
| Jetty   | ✅                  | ✖️         |
| CIO     | ✖️                 | ✅          |
| Android | ✖️                 | ✖️         |
| OkHttp  | ✅                  | ✅          |
| Js      | ✅                  | ✅          |
| Darwin  | ✅                  | ✅          |
| WinHttp | ✅                  | ✅          |
| Curl    | ✅                  | ✅          |

### Security

[SSL](client-ssl.md) must be configured per engine. Each engine provides its own SSL configuration options.

### Proxy support

Some engines don't support proxies. For the complete list, see the
[proxy documentation](client-proxy.md#supported_engines).

### Logging

The [Logging](client-logging.md) plugin provides different logger types depending on the target platforms.

### Timeout

The [HttpTimeout](client-timeout.md) plugin has some limitations on certain engines. For the complete list,
see [Timeout limitations](client-timeout.md#limitations).

## Add an engine dependency {id="dependencies"}

In addition to the [`ktor-client-core`](client-dependencies.md) artifact, the Ktor client requires a dependency for a
specific engine. Each supported platform has a set of available engines, described in the corresponding sections:

* [JVM](#jvm)
* [JVM and Android](#jvm-android)
* [JavaScript](#js)
* [Native](#native)

> Ktor provides platform-specific artifacts with suffixes such as `-jvm` or `-js`. For example, `ktor-client-cio-jvm`.
> Dependency resolution differs by build tool. While Gradle resolves artifacts appropriate for a given platform, Maven
> doesn't support this capability. This means that for Maven, you need to specify the platform suffix manually.
>
{type="note"}

## Specify an engine {id="create"}

To use a specific engine, pass the engine class to the [
`HttpClient`](https://api.ktor.io/ktor-client/ktor-client-core/io.ktor.client/-http-client/index.html) constructor. The
following example creates a client with the `CIO` engine:

```kotlin
```

{src="snippets/_misc_client/CioCreate.kt"}

## Default engine {id="default"}

If you omit the engine argument, the client will choose an engine automatically based on the dependencies
[in your build script](#dependencies).

```kotlin
```

{src="snippets/_misc_client/DefaultEngineCreate.kt"}

This is especially useful in multiplatform projects. For example, for a project targeting
both [Android and iOS](client-create-multiplatform-application.md), you can add the [Android](#jvm-android) dependency
to the `androidMain` source set and the [Darwin](#darwin) dependency to the `iosMain` source set. The appropriate
engine is selected at run time upon `HttpClient` creation. 

## Configure an engine {id="configure"}

To configure an engine, use the `engine {}` function. All engines can be configured using the common options from
[
`HttpClientEngineConfig`](https://api.ktor.io/ktor-client/ktor-client-core/io.ktor.client.engine/-http-client-engine-config/index.html):

```kotlin
```

{src="snippets/_misc_client/BasicEngineConfigExample.kt" interpolate-variables="true" disable-links="false"}

In the next sections, you can learn how to configure specific engines for different platforms.

## JVM {id="jvm"}

The JVM target supports several engines: [`Apache`](#apache), [`Apache5`](#apache5), [`Java`](#java), and
[`Jetty`](#jetty).

### Apache {id="apache"}

The `Apache` engine supports HTTP/1.1 and provides multiple configuration options.
If you need HTTP/2 support, use the [`Apache5` engine](#apache5), which enables it by default.

1. Add the `ktor-client-apache` dependency:

   <var name="artifact_name" value="ktor-client-apache"/>
   <include from="lib.topic" element-id="add_ktor_artifact"/>

2. Pass the `Apache` class as an argument to the `HttpClient` constructor:

   ```kotlin
   ```
   {src="snippets/_misc_client/ApacheCreate.kt"}

3. Use the engine block to access and set properties from `ApacheEngineConfig`:

   ```kotlin
   ```
   {src="snippets/_misc_client/ApacheConfig.kt" interpolate-variables="true" disable-links="false"}

### Apache5 {id="apache5"}

The `Apache5` engine supports HTTP/2 and enables it by default.

1. Add the `ktor-client-apache5` dependency:

   <var name="artifact_name" value="ktor-client-apache5"/>
   <include from="lib.topic" element-id="add_ktor_artifact"/>

2. Pass the `Apache5` class as an argument to the `HttpClient` constructor:

   ```kotlin
   ```
   {src="snippets/_misc_client/Apache5Create.kt" include-lines="1-2,4-5"}

3. Use the `engine {}` block to access and set properties from `Apache5EngineConfig`:

   ```kotlin
   ```
   {src="snippets/_misc_client/Apache5Create.kt" include-lines="1-4,7-23"}

### Java {id="java"}

The `Java` engine uses the [Java HTTP Client](https://openjdk.java.net/groups/net/httpclient/intro.html) introduced in
Java 11. To use it, follow the steps below:

1. Add the `ktor-client-java` dependency:

   <var name="artifact_name" value="ktor-client-java"/>
   <include from="lib.topic" element-id="add_ktor_artifact"/>
2. Pass the [Java](https://api.ktor.io/ktor-client/ktor-client-java/io.ktor.client.engine.java/-java/index.html) class
   as an argument to the `HttpClient` constructor:
   ```kotlin
   ```
   {src="snippets/_misc_client/JavaCreate.kt"}
3. To configure the engine, set properties from [
   `JavaHttpConfig`](https://api.ktor.io/ktor-client/ktor-client-java/io.ktor.client.engine.java/-java-http-config/index.html)
   in the `engine {}` block:
   ```kotlin
   ```
   {src="snippets/_misc_client/JavaConfig.kt" interpolate-variables="true" disable-links="false"}

### Jetty {id="jetty"}

The `Jetty` engine supports only HTTP/2 and can be configured in the following way:

1. Add the `ktor-client-jetty-jakarta` dependency:

   <var name="artifact_name" value="ktor-client-jetty-jakarta"/>
   <include from="lib.topic" element-id="add_ktor_artifact"/>
2. Pass the
   [`Jetty`](https://api.ktor.io/ktor-client/ktor-client-jetty-jakarta/io.ktor.client.engine.jetty.jakarta/-jetty/index.html)
   class as an argument to the `HttpClient` constructor:
   ```kotlin
   ```
   {src="snippets/_misc_client/JettyCreate.kt"}
3. To configure the engine, set properties from
   [`JettyEngineConfig`](https://api.ktor.io/ktor-client/ktor-client-jetty-jakarta/io.ktor.client.engine.jetty.jakarta/-jetty-engine-config/index.html)
   in the `engine {}` block:
   ```kotlin
   ```
   {src="snippets/_misc_client/JettyConfig.kt" interpolate-variables="true" disable-links="false"}

## JVM and Android {id="jvm-android"}

In this section, we'll take a look at engines available for JVM/Android and their configurations.

### Android {id="android"}

The `Android` engine targets Android and can be configured in the following way:

1. Add the `ktor-client-android` dependency:

   <var name="artifact_name" value="ktor-client-android"/>
   <include from="lib.topic" element-id="add_ktor_artifact"/>
2. Pass the
   [`Android`](https://api.ktor.io/ktor-client/ktor-client-android/io.ktor.client.engine.android/-android/index.html)
   class as an argument to the `HttpClient` constructor:
   ```kotlin
   ```
   {src="snippets/_misc_client/AndroidCreate.kt"}
3. To configure an engine, set properties from
   [
   `AndroidEngineConfig`](https://api.ktor.io/ktor-client/ktor-client-android/io.ktor.client.engine.android/-android-engine-config/index.html)
   in the `engine {}` block:
   ```kotlin
   ```
   {src="snippets/_misc_client/AndroidConfig.kt" interpolate-variables="true" disable-links="false"}

### OkHttp {id="okhttp"}

The `OkHttp` engine is based on OkHttp can be configured in the following way:

1. Add the `ktor-client-okhttp` dependency:

   <var name="artifact_name" value="ktor-client-okhttp"/>
   <include from="lib.topic" element-id="add_ktor_artifact"/>
2. Pass
   the [`OkHttp`](https://api.ktor.io/ktor-client/ktor-client-okhttp/io.ktor.client.engine.okhttp/-ok-http/index.html)
   class as an argument to the `HttpClient` constructor:
   ```kotlin
   ```
   {src="snippets/_misc_client/OkHttpCreate.kt"}
3. To configure an engine, set properties from
   [
   `OkHttpConfig`](https://api.ktor.io/ktor-client/ktor-client-okhttp/io.ktor.client.engine.okhttp/-ok-http-config/index.html)
   in the `engine {}` block:
   ```kotlin
   ```
   {src="snippets/_misc_client/OkHttpConfig.kt" interpolate-variables="true" disable-links="false"}

## Native {id="native"}

Ktor provides the [`Darwin`](#darwin), [`WinHttp`](#winhttp), and [`Curl`](#curl) engines for
[Kotlin/Native](https://kotlinlang.org/docs/native-overview.html) targets.

> Using Ktor in a Kotlin/Native project requires
> a [new memory manager](https://kotlinlang.org/docs/native-memory-manager.html), which is enabled by default starting
> with Kotlin 1.7.20.
>
{id="newmm-note" style="note"}

### Darwin {id="darwin"}

The `Darwin` engine targets [Darwin-based](https://en.wikipedia.org/wiki/Darwin_(operating_system)) operating systems,
such as macOS, iOS, tvOS, and watchOS. It
uses [`NSURLSession`](https://developer.apple.com/documentation/foundation/nsurlsession) under the hood. To use the
`Darwin` engine, follow the steps below:

1. Add the `ktor-client-darwin` dependency:

   <var name="artifact_name" value="ktor-client-darwin"/>
   <var name="target" value="-macosx64"/>
   <include from="lib.topic" element-id="add_ktor_artifact_mpp"/>
2. Pass the `Darwin` class as an argument to the `HttpClient` constructor:
   ```kotlin
   import io.ktor.client.*
   import io.ktor.client.engine.darwin.*
   
   val client = HttpClient(Darwin)
   ```
3. Configure the engine in the `engine {}` block using
   [
   `DarwinClientEngineConfig`](https://api.ktor.io/ktor-client/ktor-client-darwin/io.ktor.client.engine.darwin/-darwin-client-engine-config/index.html).
   For example, you can customize requests with `configureRequest` or sessions with `configureSession`:
   ```kotlin
   ```
   {src="snippets/client-engine-darwin/src/nativeMain/kotlin/Main.kt" include-lines="8-14"}

   For the full example,
   see [client-engine-darwin](https://github.com/ktorio/ktor-documentation/tree/%ktor_version%/codeSnippets/snippets/client-engine-darwin).

### WinHttp {id="winhttp"}

The `WinHttp` engine targets Windows-based operating systems.
To use the `WinHttp` engine, follow the steps below:

1. Add the `ktor-client-winhttp` dependency:

   <var name="artifact_name" value="ktor-client-winhttp"/>
   <var name="target" value="-mingwx64"/>
   <include from="lib.topic" element-id="add_ktor_artifact_mpp"/>
2. Pass the `WinHttp` class as an argument to the `HttpClient` constructor:
   ```kotlin
   import io.ktor.client.*
   import io.ktor.client.engine.winhttp.*
   
   val client = HttpClient(WinHttp)
   ```
3. Configure the engine in the `engine {}` block using
   [
   `WinHttpClientEngineConfig`](https://api.ktor.io/ktor-client/ktor-client-winhttp/io.ktor.client.engine.winhttp/-winhttp-client-engine-config/index.html).
   For example, you can use the `protocolVersion` property to change the HTTP version:
   ```kotlin
   ```
   {src="snippets/client-engine-winhttp/src/nativeMain/kotlin/Main.kt" include-lines="9-13"}

   For the full example,
   see [client-engine-winhttp](https://github.com/ktorio/ktor-documentation/tree/%ktor_version%/codeSnippets/snippets/client-engine-winhttp).

### Curl {id="curl"}

For desktop platforms, Ktor provides the `Curl` engine. It is supported on `linuxX64`, `linuxArm64`, `macosX64`,
`macosArm64`, and `mingwX64`. To use the `Curl` engine, follow the steps below:

1. Add the `ktor-client-curl` dependency:

   <var name="artifact_name" value="ktor-client-curl"/>
   <var name="target" value="-macosx64"/>
   <include from="lib.topic" element-id="add_ktor_artifact_mpp"/>
2. Pass the `Curl` class as an argument to the `HttpClient` constructor:
   ```kotlin
   import io.ktor.client.*
   import io.ktor.client.engine.curl.*
   
   val client = HttpClient(Curl)
   ```

3. Configure the engine in the `engine {}` block using `CurlClientEngineConfig`.
   For example, disable SSL verification for testing purposes:
   ```kotlin
   ```
   {src="snippets/client-engine-curl/src/nativeMain/kotlin/Main.kt" include-lines="8-12"}

   For the full example,
   see [client-engine-curl](https://github.com/ktorio/ktor-documentation/tree/%ktor_version%/codeSnippets/snippets/client-engine-curl).

## JVM, Android, Native, JS and WasmJS {id="jvm-android-native-wasm-js"}

### CIO {id="cio"}

The CIO engine is a fully asynchronous coroutine-based engine available on JVM, Android, Native, JavaScript and
WebAssembly
JavaScript (WasmJs) platforms. It currently supports HTTP/1.x only. To use it, follow the steps below:

1. Add the `ktor-client-cio` dependency:

   <var name="artifact_name" value="ktor-client-cio"/>
   <include from="lib.topic" element-id="add_ktor_artifact"/>
2. Pass the [`CIO`](https://api.ktor.io/ktor-client/ktor-client-cio/io.ktor.client.engine.cio/-c-i-o/index.html) class
   as
   an argument to the `HttpClient` constructor:
   ```kotlin
   ```
   {src="snippets/_misc_client/CioCreate.kt"}

3. Configure the engine in the `engine {}` block using
   [
   `CIOEngineConfig`](https://api.ktor.io/ktor-client/ktor-client-cio/io.ktor.client.engine.cio/-c-i-o-engine-config/index.html):
   ```kotlin
   ```
   {src="snippets/_misc_client/CioConfig.kt" interpolate-variables="true" disable-links="false"}

## JavaScript {id="js"}

The `Js` engine can be used for [JavaScript projects](https://kotlinlang.org/docs/js-overview.html). It uses
[fetch API](https://developer.mozilla.org/en-US/docs/Web/API/Fetch_API) for browser applications and `node-fetch`
for Node.js. To use it, follow the steps below:

1. Add the `ktor-client-js` dependency:

   <var name="artifact_name" value="ktor-client-js"/>
   <var name="target" value=""/>
   <include from="lib.topic" element-id="add_ktor_artifact_mpp"/>
2. Pass the `Js` class as an argument to the `HttpClient` constructor:
   ```kotlin
   import io.ktor.client.*
   import io.ktor.client.engine.js.*
   
   val client = HttpClient(Js)
   ```

   You can also call the `JsClient()` function to get the `Js` engine singleton:
   ```kotlin
   import io.ktor.client.engine.js.*

   val client = JsClient()
   ```

For the full example,
see [client-engine-js](https://github.com/ktorio/ktor-documentation/tree/%ktor_version%/codeSnippets/snippets/client-engine-js).

## Example: How to configure an engine in a multiplatform mobile project {id="mpp-config"}

When building a multiplatform project, you can use
[expected and actual declarations](https://kotlinlang.org/docs/multiplatform-mobile-connect-to-platform-specific-apis.html)
to select and configure engines for each target platform. This allows you to share most client configuration in common
code while applying engine-specific options in platform code.
We'll demonstrate how to achieve this using a project created in the [](client-create-multiplatform-application.md)
tutorial:

<procedure>

1. Open the **shared/src/commonMain/kotlin/com/example/kmmktor/Platform.kt**
   file and add a top-level `httpClient()` function that accepts a configuration block and returns an `HttpClient`:
   ```kotlin
   expect fun httpClient(config: HttpClientConfig<*>.() -> Unit = {}): HttpClient
   ```

2. Open **shared/src/androidMain/kotlin/com/example/kmmktor/Platform.kt**
   and add an actual declaration of the `httpClient()` function for the Android module:
   ```kotlin
   import io.ktor.client.*
   import io.ktor.client.engine.okhttp.*
   import java.util.concurrent.TimeUnit

   actual fun httpClient(config: HttpClientConfig<*>.() -> Unit) = HttpClient(OkHttp) {
      config(this)

      engine { 
         config {
            retryOnConnectionFailure(true)
            connectTimeout(0, TimeUnit.SECONDS)
         }
      }
   }
   ```

   > This example shows how to configure the [`OkHttp`](#okhttp) engine, but you can also use other
   > engines [supported for Android](#jvm-android).
   >
   {style="tip"}

3. Open **shared/src/iosMain/kotlin/com/example/kmmktor/Platform.kt** and add an actual declaration of the `httpClient()`
   function for the iOS module:
   ```kotlin
   import io.ktor.client.*
   import io.ktor.client.engine.darwin.*
   
   actual fun httpClient(config: HttpClientConfig<*>.() -> Unit) = HttpClient(Darwin) {
      config(this)
      engine {
         configureRequest {
            setAllowsCellularAccess(true)
         }
      }
   }
   ```
   You can now call `httpClient()` in shared code without worrying about which engine is used.

4. To use the client in shared code, open **shared/src/commonMain/kotlin/com/example/kmmktor/Greeting.kt** and replace
   the `HttpClient()` constructor with the `httpClient()` function call:
   ```kotlin
   private val client = httpClient()
   ```

</procedure>
