[//]: # (title: Engines)

The [Ktor HTTP client](client.md) can be used on different platforms, including JVM, [Android](https://kotlinlang.org/docs/android-overview.html), [JavaScript](https://kotlinlang.org/docs/js-overview.html), and [Native](https://kotlinlang.org/docs/native-overview.html) (iOS and desktop). A specific platform may require a specific engine that processes network requests. For example, you can use `Apache`, `Jetty`, or `CIO` for JVM, `OkHttp` for Android, and so on. Different engines may have specific features and provide different configuration options.

## Add an Engine Dependency {id="dependencies"}

Besides the [ktor-client-core](client.md#client-dependency) artifact, the Ktor client requires adding a specific dependency for each engine. For each of the supported platform, you can see the available engines and required dependencies in a corresponding section:
* [JVM and Android](#jvm-android)
* [JavaScript](#js)
* [Native](#desktop)
* [Testing](#test)

## Create a Client with a Specified Engine {id="create"}
To create the HTTP client with a specific engine, pass an engine class as an argument to the [HttpClient](https://api.ktor.io/%ktor_version%/io.ktor.client/-http-client.html) constructor. For example, you can create a client with the `CIO` engine as follows:
```kotlin
```
{src="snippets/_misc_client/CioCreate.kt"}

### Default engine {id="default"}
If you call the `HttpClient` constructor without an argument, the client will choose an engine automatically depending on the artifacts [added in a build script](#dependencies).
```kotlin
```
{src="snippets/_misc_client/DefaultEngineCreate.kt"}

This can be useful for [multiplatform projects](http-client_multiplatform.md). For example, for a project targeting both [Android and iOS](https://kotlinlang.org/docs/mobile/create-first-app.html), you can add the [Android](#android) dependency to the `androidMain` source set and the [Ios](#ios) dependency to the `iosMain` source set. The necessary dependency will be selected at compile time.


## Configure an Engine {id="configure"}
You can configure an engine using the `engine` method. All engines share several common properties exposed by [HttpClientEngineConfig](https://api.ktor.io/%ktor_version%/io.ktor.client.engine/-http-client-engine-config/index.html), for example:

```kotlin
```
{src="snippets/_misc_client/BasicEngineConfigExample.kt" interpolate-variables="true" disable-links="false"}

To learn how to configure a specific engine, see a corresponding section below.


## JVM and Android {id="jvm-android"}
In this section, we'll take a look on engines available for JVM/Android and their configurations.

### Apache (JVM) {id="apache"}
The `Apache` engine supports HTTP/1.1 and provides multiple configuration options. To use it, follow the steps below:
1. Add the `ktor-client-apache` dependency:
   <var name="artifact_name" value="ktor-client-apache"/>
   <include src="lib.md" include-id="add_ktor_artifact"/>
1. Pass the [Apache](https://api.ktor.io/%ktor_version%/io.ktor.client.engine.apache/-apache/index.html) class as an argument to the `HttpClient` constructor:
   ```kotlin
   ```
   {src="snippets/_misc_client/ApacheCreate.kt"}
1. To configure an engine, pass settings exposed by [ApacheEngineConfig](https://api.ktor.io/%ktor_version%/io.ktor.client.engine.apache/-apache-engine-config/index.html) to the `engine` method:
   ```kotlin
   ```
   {src="snippets/_misc_client/ApacheConfig.kt" interpolate-variables="true" disable-links="false"}


### Java (JVM) {id="java"}
The `Java` engine uses the [Java HTTP Client](https://openjdk.java.net/groups/net/httpclient/intro.html) introduced in Java 11. To use it, follow the steps below:
1. Add the `ktor-client-java` dependency:
   <var name="artifact_name" value="ktor-client-java"/>
   <include src="lib.md" include-id="add_ktor_artifact"/>
1. Pass the [Java](https://api.ktor.io/%ktor_version%/io.ktor.client.engine.java/-java/index.html) class as an argument to the `HttpClient` constructor:
   ```kotlin
   ```
   {src="snippets/_misc_client/JavaCreate.kt"}
1. To configure an engine, pass settings exposed by [JavaHttpConfig](https://api.ktor.io/%ktor_version%/io.ktor.client.engine.java/-java-http-config/index.html) to the `engine` method:
   ```kotlin
   ```
   {src="snippets/_misc_client/JavaConfig.kt" interpolate-variables="true" disable-links="false"}

### Jetty (JVM) {id="jetty"}
The `Jetty` engine supports only HTTP/2 and can be configured in the following way:
1. Add the `ktor-client-jetty` dependency:
   <var name="artifact_name" value="ktor-client-jetty"/>
   <include src="lib.md" include-id="add_ktor_artifact"/>
1. Pass the [Jetty](https://api.ktor.io/%ktor_version%/io.ktor.client.engine.jetty/-jetty/index.html) class as an argument to the `HttpClient` constructor:
   ```kotlin
   ```
   {src="snippets/_misc_client/JettyCreate.kt"}
1. To configure an engine, pass settings exposed by [JettyEngineConfig](https://api.ktor.io/%ktor_version%/io.ktor.client.engine.jetty/-jetty-engine-config/index.html) to the `engine` method:
   ```kotlin
   ```
   {src="snippets/_misc_client/JettyConfig.kt" interpolate-variables="true" disable-links="false"}

### CIO (JVM and Android) {id="cio"}
CIO is a fully asynchronous coroutine-based engine that can be used for both JVM and Android platforms. It supports only HTTP/1.x for now. To use it, follow the steps below:
1. Add the `ktor-client-cio` dependency:
   <var name="artifact_name" value="ktor-client-cio"/>
   <include src="lib.md" include-id="add_ktor_artifact_maven_jvm"/>
1. Pass the [CIO](https://api.ktor.io/%ktor_version%/io.ktor.client.engine.cio/-c-i-o/index.html) class as an argument to the `HttpClient` constructor:
   ```kotlin
   ```
   {src="snippets/_misc_client/CioCreate.kt"}
   
1. To configure an engine, pass settings exposed by [CIOEngineConfig](https://api.ktor.io/%ktor_version%/io.ktor.client.engine.cio/-c-i-o-engine-config/index.html) to the `engine` method:
   ```kotlin
   ```
   {src="snippets/_misc_client/CioConfig.kt" interpolate-variables="true" disable-links="false"}

### Android (Android) {id="android"}
The `Android` engine targets Android and can be configured in the following way:
1. Add the `ktor-client-android` dependency:
   <var name="artifact_name" value="ktor-client-android"/>
   <include src="lib.md" include-id="add_ktor_artifact"/>
1. Pass the [Android](https://api.ktor.io/%ktor_version%/io.ktor.client.engine.android/-android/index.html) class as an argument to the `HttpClient` constructor:
   ```kotlin
   ```
   {src="snippets/_misc_client/AndroidCreate.kt"}
1. To configure an engine, pass settings exposed by [AndroidEngineConfig](https://api.ktor.io/%ktor_version%/io.ktor.client.engine.android/-android-engine-config/index.html) to the `engine` method:
   ```kotlin
   ```
   {src="snippets/_misc_client/AndroidConfig.kt" interpolate-variables="true" disable-links="false"}

### OkHttp (Android) {id="okhttp"}
The `OkHttp` engine is based on OkHttp can be configured in the following way:
1. Add the `ktor-client-okhttp` dependency:
   <var name="artifact_name" value="ktor-client-okhttp"/>
   <include src="lib.md" include-id="add_ktor_artifact"/>
1. Pass the [OkHttp](https://api.ktor.io/%ktor_version%/io.ktor.client.engine.okhttp/-ok-http/index.html) class as an argument to the `HttpClient` constructor:
   ```kotlin
   ```
   {src="snippets/_misc_client/OkHttpCreate.kt"}
1. To configure an engine, pass settings exposed by [OkHttpConfig](https://api.ktor.io/%ktor_version%/io.ktor.client.engine.okhttp/-ok-http-config/index.html) to the `engine` method:
   ```kotlin
   ```
   {src="snippets/_misc_client/OkHttpConfig.kt" interpolate-variables="true" disable-links="false"}


## JavaScript {id="js"}

The `Js` engine can be used for [JavaScript projects](https://kotlinlang.org/docs/js-overview.html). This engine uses the [fetch API](https://developer.mozilla.org/en-US/docs/Web/API/Fetch_API) for browser applications and `node-fetch` for Node.js. To use it, follow the steps below:

1. Add the `ktor-client-js` dependency:
   <var name="artifact_name" value="ktor-client-js"/>
   <include src="lib.md" include-id="add_ktor_artifact"/>
1. Pass the `Js` class as an argument to the `HttpClient` constructor:
   ```kotlin
   ```
   {src="snippets/_misc_client/JsCreate.kt"}
   
   You can also call the `JsClient` function to get the `Js` engine singleton:
   ```kotlin
   import io.ktor.client.engine.js.*

   val client = JsClient()
   ```

## Native {id="native"}
In this section, we'll have a look on how to configure engines targeted for [Kotlin/Native](https://kotlinlang.org/docs/native-overview.html).
> Note that engines targeting Kotlin/Native require a [multithreaded version](https://kotlinlang.org/docs/mobile/concurrency-and-coroutines.html#multithreaded-coroutines) of `kotlinx.coroutines`.

### iOS {id="ios"}
The `iOS` engine targets iOS and uses [NSURLSession](https://developer.apple.com/documentation/foundation/nsurlsession) internally. To use it, follow the steps below:

1. Add the `ktor-client-ios` dependency:
   <var name="artifact_name" value="ktor-client-ios"/>
   <include src="lib.md" include-id="add_ktor_artifact"/>
1. Pass the `Ios` class as an argument to the `HttpClient` constructor:
   ```kotlin
   ```
   {src="snippets/_misc_client/IosCreate.kt"}
1. To configure an engine, pass settings exposed by `IosClientEngineConfig` to the `engine` method:
   ```kotlin
   ```
   {src="snippets/_misc_client/IosConfig.kt"}


### Desktop {id="desktop"}

For desktop platforms, Ktor provides the `Curl` engine. This engine is supported for the following platforms: `linuxX64`, `macosX64`, `mingwX64`. To use the `Curl` engine, follow the steps below:

1. Install the [curl library](https://curl.se/download.html).
1. Add the `ktor-client-curl` dependency:
   <var name="artifact_name" value="ktor-client-curl"/>
   <include src="lib.md" include-id="add_ktor_artifact"/>
1. Pass the `Curl` class as an argument to the `HttpClient` constructor:
   ```kotlin
   ```
   {src="snippets/_misc_client/CurlCreate.kt"}
1. To configure an engine, pass settings exposed by `CurlClientEngineConfig` to the `engine` method:
   ```kotlin
   ```
   {src="snippets/_misc_client/CurlConfig.kt"}


## Testing {id="test"}
Ktor provides the `MockEngine` for testing the HttpClient. To learn how to use it, see [MockEngine for testing](http-client_testing.md).
