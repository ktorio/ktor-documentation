[//]: # (title: Engines)

<show-structure for="chapter" depth="2"/>

<link-summary>
Learn about engines that process network requests.
</link-summary>

The [Ktor HTTP client](create-client.md) can be used on different platforms, including JVM, [Android](https://kotlinlang.org/docs/android-overview.html), [JavaScript](https://kotlinlang.org/docs/js-overview.html), and [Native](https://kotlinlang.org/docs/native-overview.html). A specific platform may require a specific engine that processes network requests. 
For example, you can use `Apache` or `Jetty`for JVM applications, `OkHttp` or `Android` for Android, `Curl` for desktop applications targeting Kotlin/Native, and so on. Different engines may have specific features and provide different configuration options.


## Requirements and limitations {id="requirements"}

### Supported platforms {id="platforms"}

The table below lists platforms supported by each engine:

| Engine  | Platforms                                               |
|---------|---------------------------------------------------------|
| Apache  | [JVM](#jvm)                                             |
| Java    | [JVM](#jvm)                                             |
| Jetty   | [JVM](#jvm)                                             |
| Android | [JVM](#jvm), [Android](#jvm-android)                    |
| OkHttp  | [JVM](#jvm), [Android](#jvm-android)                    |
| Darwin  | [Native](#native)                                       |
| Curl    | [Native](#native)                                       |
| CIO     | [JVM](#jvm), [Android](#jvm-android), [Native](#native) |
| Js      | [JavaScript](#js)                                       |

### Supported Android/Java versions {id="minimal-version"}

Client engines targeting JVM or both JVM and Android support the following Android/Java versions: 

| Engine  | Android version   | Java version |
|---------|-------------------|--------------|
| Apache  |                   | 8+           |
| Java    |                   | 11+          |
| Jetty   |                   | 8+           |
| CIO     | 7.0+ <sup>*</sup> | 8+           |
| Android | 1.x+              | 8+           |
| OkHttp  | 5.0+              | 8+           |

_* To use the CIO engine on older Android versions, you need to enable [Java 8 API desugaring](https://developer.android.com/studio/write/java8-support)._


### Limitations {id="limitations"}

The table below shows whether a specific engine supports HTTP/2 and [WebSockets](websocket_client.md):

| Engine  | HTTP/2 | WebSockets |
|---------|--------|------------|
| Apache  | ✖️      | ✖️          |
| Java    | ✅      | ✖️          |
| Jetty   | ✅      | ✖️          |
| CIO     | ✖️      | ✅          |
| Android | ✖️      | ✖️          |
| OkHttp  | ✅      | ✅          |
| Js      | ✅      | ✅          |
| Darwin  | ✅      | ✅          |
| Curl    | ✅      | ✖️          |

You also need to consider the following limitations that affect general client configuration and using of specific plugins:
- If an engine supports HTTP/2, you can enable it by customizing the engine configuration (see an example for the [](#java) engine).
- To configure [SSL](client-ssl.md) in the Ktor client, you need to customize the configuration of a selected engine.
- Some engines don't support [proxy](proxy.md#supported_engines).
- The [Logging](client_logging.md) plugin provides different logger types for different platforms.
- The [HttpTimeout](timeout.md#limitations) plugin has some limitations for specific engines.
- The [XML serializer](serialization-client.md#register_xml) is supported on JVM only.


## Add an engine dependency {id="dependencies"}

Apart from the [ktor-client-core](client-dependencies.md) artifact, the Ktor client requires adding a specific dependency for each engine. For each of the supported platforms, you can see the available engines and required dependencies in a corresponding section:
* [JVM](#jvm)
* [JVM and Android](#jvm-android)
* [JavaScript](#js)
* [Native](#native)

> For different engines, Ktor provides platform-specific artifacts with suffixes such as `-jvm` or `-js`, for example, `ktor-client-cio-jvm`. Gradle resolves artifacts appropriate for a given platform while Maven doesn't support this capability. This means that for Maven you need to add a platform-specific suffix manually.
>
{type="note"}

## Create a client with a specified engine {id="create"}
To create the HTTP client with a specific engine, pass an engine class as an argument to the [HttpClient](https://api.ktor.io/ktor-client/ktor-client-core/io.ktor.client/-http-client/index.html) constructor. For example, you can create a client with the `CIO` engine as follows:
```kotlin
```
{src="snippets/_misc_client/CioCreate.kt"}

### Default engine {id="default"}
If you call the `HttpClient` constructor without an argument, the client will choose an engine automatically depending on the artifacts [added in a build script](#dependencies).
```kotlin
```
{src="snippets/_misc_client/DefaultEngineCreate.kt"}

This can be useful for multiplatform projects. For example, for a project targeting both [Android and iOS](getting_started_ktor_client_multiplatform_mobile.md), you can add the [Android](#jvm-android) dependency to the `androidMain` source set and the [Darwin](#darwin) dependency to the `iosMain` source set. The necessary dependency will be selected at compile time.


## Configure an engine {id="configure"}
You can configure an engine using the `engine` method. All engines share several common properties exposed by [HttpClientEngineConfig](https://api.ktor.io/ktor-client/ktor-client-core/io.ktor.client.engine/-http-client-engine-config/index.html), for example:

```kotlin
```
{src="snippets/_misc_client/BasicEngineConfigExample.kt" interpolate-variables="true" disable-links="false"}

To learn how to configure a specific engine, see a corresponding section below.


## JVM {id="jvm"}
In this section, we'll take a look on engines available for JVM.

### Apache {id="apache"}
The `Apache` engine supports HTTP/1.1 and provides multiple configuration options. To use it, follow the steps below:
1. Add the `ktor-client-apache` dependency:

   <var name="artifact_name" value="ktor-client-apache"/>
   <include from="lib.topic" element-id="add_ktor_artifact"/>
2. Pass the [Apache](https://api.ktor.io/ktor-client/ktor-client-apache/io.ktor.client.engine.apache/-apache/index.html) class as an argument to the `HttpClient` constructor:

   ```kotlin
   ```
   {src="snippets/_misc_client/ApacheCreate.kt"}
3. To configure an engine, pass settings exposed by [ApacheEngineConfig](https://api.ktor.io/ktor-client/ktor-client-apache/io.ktor.client.engine.apache/-apache-engine-config/index.html) to the `engine` method:

   ```kotlin
   ```
   {src="snippets/_misc_client/ApacheConfig.kt" interpolate-variables="true" disable-links="false"}


### Java {id="java"}
The `Java` engine uses the [Java HTTP Client](https://openjdk.java.net/groups/net/httpclient/intro.html) introduced in Java 11. To use it, follow the steps below:
1. Add the `ktor-client-java` dependency:

   <var name="artifact_name" value="ktor-client-java"/>
   <include from="lib.topic" element-id="add_ktor_artifact"/>
2. Pass the [Java](https://api.ktor.io/ktor-client/ktor-client-java/io.ktor.client.engine.java/-java/index.html) class as an argument to the `HttpClient` constructor:
   ```kotlin
   ```
   {src="snippets/_misc_client/JavaCreate.kt"}
3. To configure an engine, pass settings exposed by [JavaHttpConfig](https://api.ktor.io/ktor-client/ktor-client-java/io.ktor.client.engine.java/-java-http-config/index.html) to the `engine` method:
   ```kotlin
   ```
   {src="snippets/_misc_client/JavaConfig.kt" interpolate-variables="true" disable-links="false"}

### Jetty {id="jetty"}
The `Jetty` engine supports only HTTP/2 and can be configured in the following way:
1. Add the `ktor-client-jetty` dependency:

   <var name="artifact_name" value="ktor-client-jetty"/>
   <include from="lib.topic" element-id="add_ktor_artifact"/>
2. Pass the [Jetty](https://api.ktor.io/ktor-client/ktor-client-jetty/io.ktor.client.engine.jetty/-jetty/index.html) class as an argument to the `HttpClient` constructor:
   ```kotlin
   ```
   {src="snippets/_misc_client/JettyCreate.kt"}
3. To configure an engine, pass settings exposed by [JettyEngineConfig](https://api.ktor.io/ktor-client/ktor-client-jetty/io.ktor.client.engine.jetty/-jetty-engine-config/index.html) to the `engine` method:
   ```kotlin
   ```
   {src="snippets/_misc_client/JettyConfig.kt" interpolate-variables="true" disable-links="false"}



## JVM and Android {id="jvm-android"}

In this section, we'll take a look on engines available for JVM/Android and their configurations.

### Android {id="android"}
The `Android` engine targets Android and can be configured in the following way:
1. Add the `ktor-client-android` dependency:

   <var name="artifact_name" value="ktor-client-android"/>
   <include from="lib.topic" element-id="add_ktor_artifact"/>
2. Pass the [Android](https://api.ktor.io/ktor-client/ktor-client-android/io.ktor.client.engine.android/-android/index.html) class as an argument to the `HttpClient` constructor:
   ```kotlin
   ```
   {src="snippets/_misc_client/AndroidCreate.kt"}
3. To configure an engine, pass settings exposed by [AndroidEngineConfig](https://api.ktor.io/ktor-client/ktor-client-android/io.ktor.client.engine.android/-android-engine-config/index.html) to the `engine` method:
   ```kotlin
   ```
   {src="snippets/_misc_client/AndroidConfig.kt" interpolate-variables="true" disable-links="false"}

### OkHttp {id="okhttp"}
The `OkHttp` engine is based on OkHttp can be configured in the following way:
1. Add the `ktor-client-okhttp` dependency:

   <var name="artifact_name" value="ktor-client-okhttp"/>
   <include from="lib.topic" element-id="add_ktor_artifact"/>
2. Pass the [OkHttp](https://api.ktor.io/ktor-client/ktor-client-okhttp/io.ktor.client.engine.okhttp/-ok-http/index.html) class as an argument to the `HttpClient` constructor:
   ```kotlin
   ```
   {src="snippets/_misc_client/OkHttpCreate.kt"}
3. To configure an engine, pass settings exposed by [OkHttpConfig](https://api.ktor.io/ktor-client/ktor-client-okhttp/io.ktor.client.engine.okhttp/-ok-http-config/index.html) to the `engine` method:
   ```kotlin
   ```
   {src="snippets/_misc_client/OkHttpConfig.kt" interpolate-variables="true" disable-links="false"}


## Native {id="native"}
In this section, we'll have a look on how to configure engines targeted for [Kotlin/Native](https://kotlinlang.org/docs/native-overview.html).

> To use the Ktor client on Kotlin/Native, enable the New Kotlin/Native memory model as described here: [Enable the new MM](https://github.com/JetBrains/kotlin/blob/master/kotlin-native/NEW_MM.md#enable-the-new-mm).

### Darwin {id="darwin"}
The `Darwin` engine targets [Darwin-based](https://en.wikipedia.org/wiki/Darwin_(operating_system)) operating systems (such as macOS, iOS, tvOS, and so on) and uses [NSURLSession](https://developer.apple.com/documentation/foundation/nsurlsession) under the hood. To use the `Darwin` engine, follow the steps below:

1. Add the `ktor-client-darwin` dependency:

   <var name="artifact_name" value="ktor-client-darwin"/>
   <var name="target" value="-macosx64"/>
   <include from="lib.topic" element-id="add_ktor_artifact_mpp"/>
2. Pass the `darwin` class as an argument to the `HttpClient` constructor:
   ```kotlin
   import io.ktor.client.*
   import io.ktor.client.engine.darwin.*
   
   val client = HttpClient(Darwin)
   ```
3. To configure an engine, pass settings exposed by [DarwinClientEngineConfig](https://api.ktor.io/ktor-client/ktor-client-darwin/io.ktor.client.engine.darwin/-darwin-client-engine-config/index.html) to the `engine` function.
   For instance, you can use the `configureRequest` function to access `NSMutableURLRequest` or `configureSession` to customize a session configuration. The code snippet below shows how to use `configureRequest`:
   ```kotlin
   ```
   {src="snippets/client-engine-darwin/src/nativeMain/kotlin/Main.kt" include-lines="8-14"}

   You can find the full example here: [client-engine-darwin](https://github.com/ktorio/ktor-documentation/tree/%ktor_version%/codeSnippets/snippets/client-engine-darwin).

### Curl {id="curl"}

For desktop platforms, Ktor also provides the `Curl` engine. This engine is supported for the following platforms: `linuxX64`, `macosX64`, `macosArm64`, `mingwX64`. To use the `Curl` engine, follow the steps below:

1. Install the [libcurl library](https://curl.se/libcurl/).
   > On Windows, you may want to consider the [MinGW/MSYS2](FAQ.topic#native-curl) `curl` binary. 
2. Add the `ktor-client-curl` dependency:

   <var name="artifact_name" value="ktor-client-curl"/>
   <var name="target" value="-macosx64"/>
   <include from="lib.topic" element-id="add_ktor_artifact_mpp"/>
3. Pass the `Curl` class as an argument to the `HttpClient` constructor:
   ```kotlin
   import io.ktor.client.*
   import io.ktor.client.engine.curl.*
   
   val client = HttpClient(Curl)
   ```

4. To configure an engine, pass settings exposed by `CurlClientEngineConfig` to the `engine` method.
   The code snippet below shows how to disable SSL verification for testing purposes:
   ```kotlin
   ```
   {src="snippets/client-engine-curl/src/nativeMain/kotlin/Main.kt" include-lines="8-12"}

   You can find the full example here: [client-engine-curl](https://github.com/ktorio/ktor-documentation/tree/%ktor_version%/codeSnippets/snippets/client-engine-curl).


## JVM, Android, and Native {id="jvm-android-native"}

### CIO {id="cio"}
CIO is a fully asynchronous coroutine-based engine that can be used on JVM, Android, and Native platforms. It supports only HTTP/1.x for now. To use it, follow the steps below:
1. Add the `ktor-client-cio` dependency:

   <var name="artifact_name" value="ktor-client-cio"/>
   <include from="lib.topic" element-id="add_ktor_artifact"/>
2. Pass the [CIO](https://api.ktor.io/ktor-client/ktor-client-cio/io.ktor.client.engine.cio/-c-i-o/index.html) class as an argument to the `HttpClient` constructor:
   ```kotlin
   ```
   {src="snippets/_misc_client/CioCreate.kt"}

3. To configure an engine, pass settings exposed by [CIOEngineConfig](https://api.ktor.io/ktor-client/ktor-client-cio/io.ktor.client.engine.cio/-c-i-o-engine-config/index.html) to the `engine` method:
   ```kotlin
   ```
   {src="snippets/_misc_client/CioConfig.kt" interpolate-variables="true" disable-links="false"}


## JavaScript {id="js"}

The `Js` engine can be used for [JavaScript projects](https://kotlinlang.org/docs/js-overview.html). This engine uses the [fetch API](https://developer.mozilla.org/en-US/docs/Web/API/Fetch_API) for browser applications and `node-fetch` for Node.js. To use it, follow the steps below:

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

   You can also call the `JsClient` function to get the `Js` engine singleton:
   ```kotlin
   import io.ktor.client.engine.js.*

   val client = JsClient()
   ```

You can find the full example here: [client-engine-js](https://github.com/ktorio/ktor-documentation/tree/%ktor_version%/codeSnippets/snippets/client-engine-js).

## Example: How to configure an engine in a multiplatform mobile project {id="mpp-config"}

To configure engine-specific options in a multiplatform mobile project, you can use [expect/actual declarations](https://kotlinlang.org/docs/multiplatform-mobile-connect-to-platform-specific-apis.html).
Let's demonstrate how to achieve this using a project created in the [](getting_started_ktor_client_multiplatform_mobile.md) tutorial:

1. Open the `shared/src/commonMain/kotlin/com/example/kmmktor/Platform.kt` file and add a top-level `httpClient` function, which accepts a client configuration and returns `HttpClient`:
   ```kotlin
   expect fun httpClient(config: HttpClientConfig<*>.() -> Unit = {}): HttpClient
   ```
   
2. Open `shared/src/androidMain/kotlin/com/example/kmmktor/Platform.kt` and add an actual declaration of the `httpClient` function for the Android module:
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
   This example shows how to configure the [OkHttp](#okhttp) engine but you can also use other engines [supported for Android](#jvm-android).

3. Open `shared/src/iosMain/kotlin/com/example/kmmktor/Platform.kt` and add an actual declaration of the `httpClient` function for the iOS module:
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

4. Finally, open `shared/src/commonMain/kotlin/com/example/kmmktor/Greeting.kt` and replace the `HttpClient()` constructor with the `httpClient` function call:
   ```kotlin
   private val client = httpClient()
   ```

