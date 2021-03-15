[//]: # (title: Engines)

The [Ktor HTTP client](client.md) can be used on [different platforms](https://kotlinlang.org/docs/multiplatform.html) including JVM, [Android](https://kotlinlang.org/docs/android-overview.html), [JavaScript](https://kotlinlang.org/docs/js-overview.html), and [Native](https://kotlinlang.org/docs/native-overview.html) (iOS and desktop). A specific platform may require a specific engine that processes network requests. For example, you can `Apache`, `Jetty`, or `CIO` for JVM, `OkHttp` for Android, and so on. Different engines may have specific features and provide different configuration options.

## Add an Engine Dependency {id="dependencies"}

Each engine requires a separate dependency. For each of the supported platform, you can see the available engines and required dependencies:
* [JVM and Android](#jvm-android)
* [iOS](#ios)
* [JavaScript](#js)
* [Desktop](#desktop)
* [Testing](#test)

## Create a Client with a Specified Engine {id="create"}
To create the HTTP client with a specific engine, pass an engine class as an argument to the [HttpClient](https://api.ktor.io/%ktor_version%/io.ktor.client/-http-client.html) constructor. For example, you can create a client with the `Apache` engine as follows:
```kotlin
val client = HttpClient(Apache)
```

### Default engine {id="default"}
If you call the `HttpClient` constructor without an argument, the client will choose an engine automatically depending on the artifacts [added in a build script](#dependencies).
```kotlin
val client = HttpClient()
```
This can be useful for [multiplatform prjects](http-client_multiplatform.md). For example, for a project targeting both [Android and iOS](https://kotlinlang.org/docs/mobile/create-first-app.html), you can add the [Android](#android) dependency to the `androidMain` source set and the [Ios](#ios) dependency to the `iosMain` source set. The required dependency will be selected at compile time.


## Configure an Engine {id="configure"}
You can configure an engine using the `engine` method in a block. All engines share several common properties exposed by [HttpClientEngineConfig](https://api.ktor.io/%ktor_version%/io.ktor.client.engine/-http-client-engine-config/index.html), for example:

```kotlin
HttpClient() {
    engine {
       // this: [[[HttpClientEngineConfig|https://api.ktor.io/%ktor_version%/io.ktor.client.engine/-http-client-engine-config/index.html]]]
       threadsCount = 4
       pipelining = true
    }
}
```

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
   val client = HttpClient(Apache)
   ```
1. To configure an engine, pass settings exposed by [ApacheEngineConfig](https://api.ktor.io/%ktor_version%/io.ktor.client.engine.apache/-apache-engine-config/index.html) to the `engine` method block:
   ```kotlin
   ```
   {src="snippets/_misc_client/ApacheConfig.kt"}


### Java (JVM) {id="java"}
https://openjdk.java.net/groups/net/httpclient/intro.html
Java 11 introduces an HTTP Client based on reactive streams. Ktor client
now supports this when targeting the JVM.
1. Add dependency `ktor-client-java`:
   <var name="artifact_name" value="ktor-client-java"/>
   <include src="lib.md" include-id="add_ktor_artifact"/>
1. Create a client [Java](https://api.ktor.io/%ktor_version%/io.ktor.client.engine.java/-java/index.html):
   ```kotlin
   val client = HttpClient(Java)
   ```
1. Configure a client [JavaHttpConfig](https://api.ktor.io/%ktor_version%/io.ktor.client.engine.java/-java-http-config/index.html):
   ```kotlin
   ```
   {src="snippets/_misc_client/JavaConfig.kt"}

### Jetty (JVM) {id="jetty"}
Jetty provides an additional sslContextFactory for configuring. It only supports HTTP/2 for now.
1. Add dependency `ktor-client-jetty`:
   <var name="artifact_name" value="ktor-client-jetty"/>
   <include src="lib.md" include-id="add_ktor_artifact"/>
1. Create a client [Jetty](https://api.ktor.io/%ktor_version%/io.ktor.client.engine.jetty/-jetty/index.html):
   ```kotlin
   val client = HttpClient(Jetty)
   ```
1. Configure a client [JettyEngineConfig](https://api.ktor.io/%ktor_version%/io.ktor.client.engine.jetty/-jetty-engine-config/index.html):
   ```kotlin
   ```
   {src="snippets/_misc_client/JettyConfig.kt"}

### CIO (JVM and Android) {id="cio"}
CIO (Coroutine-based I/O) is a Ktor implementation with no additional dependencies and is fully asynchronous. It only supports HTTP/1.x for now.
CIO provides maxConnectionsCount and a endpointConfig for configuring.
1. Add dependency `ktor-client-cio`:
   <var name="artifact_name" value="ktor-client-cio"/>
   <include src="lib.md" include-id="add_ktor_artifact"/>
1. Create a client [CIO](https://api.ktor.io/%ktor_version%/io.ktor.client.engine.cio/-c-i-o/index.html):
   ```kotlin
   val client = HttpClient(CIO)
   ```
1. Configure a client [CIOEngineConfig](https://api.ktor.io/%ktor_version%/io.ktor.client.engine.cio/-c-i-o-engine-config/index.html):
   ```kotlin
   ```
   {src="snippets/_misc_client/CioConfig.kt"}

### Android (Android) {id="android"}
The Android engine doesn't have additional dependencies and uses a ThreadPool with a normal HttpURLConnection, to perform the requests. And can be configured like this:
1. Add dependency `ktor-client-android`:
   <var name="artifact_name" value="ktor-client-android"/>
   <include src="lib.md" include-id="add_ktor_artifact"/>
1. Create a client [Android](https://api.ktor.io/%ktor_version%/io.ktor.client.engine.android/-android/index.html):
   ```kotlin
   val client = HttpClient(Android)
   ```
1. Configure a client [AndroidEngineConfig](https://api.ktor.io/%ktor_version%/io.ktor.client.engine.android/-android-engine-config/index.html):
   ```kotlin
   ```
   {src="snippets/_misc_client/AndroidConfig.kt"}

### OkHttp (Android) {id="okhttp"}
There is an engine based on OkHttp:
1. Add dependency `ktor-client-okhttp`:
   <var name="artifact_name" value="ktor-client-okhttp"/>
   <include src="lib.md" include-id="add_ktor_artifact"/>
1. Create a client [OkHttp](https://api.ktor.io/%ktor_version%/io.ktor.client.engine.okhttp/-ok-http/index.html):
   ```kotlin
   val client = HttpClient(OkHttp)
   ```
1. Configure a client [OkHttpConfig](https://api.ktor.io/%ktor_version%/io.ktor.client.engine.okhttp/-ok-http-config/index.html):
   ```kotlin
   ```
   {src="snippets/_misc_client/OkHttpConfig.kt"}

## iOS {id="ios"}
[Native](https://kotlinlang.org/docs/native-overview.html)

The iOS engine uses the asynchronous NSURLSession internally. And have no additional configuration.
1. Add dependency `ktor-client-ios`:
   <var name="artifact_name" value="ktor-client-ios"/>
   <include src="lib.md" include-id="add_ktor_artifact"/>
1. Create a client `Ios`:
   ```kotlin
   val client = HttpClient(Ios)
   ```
1. Configure a client:
   ```kotlin
   ```
   {src="snippets/_misc_client/IosConfig.kt"}


## JavaScript {id="js"}

[Browser, nodejs](https://kotlinlang.org/docs/js-overview.html).
The `Js` engine, uses the [fetch](https://developer.mozilla.org/en-US/docs/Web/API/Fetch_API) API internally(and `node-fetch` for nodejs runtime).

1. Add dependency `ktor-client-js`:
   <var name="artifact_name" value="ktor-client-js"/>
   <include src="lib.md" include-id="add_ktor_artifact"/>
1. Create a client `js`:
   ```kotlin
   val client = HttpClient(js)
   ```
   You can also call the `JsClient()` function to get the `Js` engine singleton.
   Js engine has no custom configuration.






## Desktop {id="desktop"}

[Native](https://kotlinlang.org/docs/native-overview.html)

There is an engine based on Curl. Supported platforms: `linuxX64`, `macosX64`, `mingwX64`. Please note that to use the engine you must have the installed curl library at least version 7.63.

1. Add dependency `ktor-client-curl`:
   <var name="artifact_name" value="ktor-client-curl"/>
   <include src="lib.md" include-id="add_ktor_artifact"/>
1. Create a client `Curl`:
   ```kotlin
   val client = HttpClient(Curl)
   ```



## Testing {id="test"}
The `MockEngine` is the common engine for testing. See also [MockEngine for testing](http-client_testing.md).

