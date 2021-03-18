[//]: # (title: Multiplatform)

<microformat>
<p>
Code example: <a href="https://github.com/ktorio/ktor-samples/tree/master/client-mpp">client-mpp</a>
</p>
</microformat>

The [Ktor HTTP client](client.md) can be used in [multiplatform projects](https://kotlinlang.org/docs/multiplatform.html) and supports the following platforms:
* JVM
* [Android](https://kotlinlang.org/docs/android-overview.html)
* [JavaScript](https://kotlinlang.org/docs/js-overview.html)
* [Native](https://kotlinlang.org/docs/native-overview.html) (`iOS` and desktop, including `linuxX64`, `macosX64`, `mingwX64`)

## Add Dependencies {id="add-dependencies"}
To use the Ktor HTTP client in your project, you need to add at least two dependencies: a client dependency and an [engine](http-client_engines.md) dependency. For a multiplatform project, you need to add these dependencies as follows:
1. To use the Ktor client in common code, add the dependency to `ktor-client-core` to the `commonMain` source set in the `build.gradle` or `build.gradle.kts` file:
   <var name="platform_name" value="common"/>
   <var name="artifact_name" value="ktor-client-core"/>
   <include src="lib.md" include-id="add_ktor_artifact_multiplatform"/>
1. Add an [engine dependency](http-client_engines.md#dependencies) for the required platform to the corresponding source set. For Android, you can add the [Android](http-client_engines.md#android) engine dependency to the `androidMain` source set:
   <var name="platform_name" value="android"/>
   <var name="artifact_name" value="ktor-client-android"/>
   <include src="lib.md" include-id="add_ktor_artifact_multiplatform"/>
   
   For iOS, you need to add the [iOS](http-client_engines.md#ios) engine dependency to `iosMain`:
   <var name="platform_name" value="ios"/>
   <var name="artifact_name" value="ktor-client-ios"/>
   <include src="lib.md" include-id="add_ktor_artifact_multiplatform"/>
   
   To learn which engines are supported for each platform, see [](http-client_engines.md#dependencies).


## Create the Client {id="create-client"}
To create the client in a multiplatform project, call the [HttpClient](https://api.ktor.io/%ktor_version%/io.ktor.client/-http-client/index.html) constructor in a project's [common code](https://kotlinlang.org/docs/mpp-discover-project.html#source-sets):

```kotlin
```
{src="snippets/_misc_client/DefaultEngineCreate.kt"}

In this code snippet, the `HttpClient` constructor doesn't accept an engine as a parameter: the client will choose an engine for the required platform depending on the artifacts [added in a build script](#add-dependencies). 

If you need to adjust an engine configuration for specific platforms, pass a corresponding engine class as an argument to the `HttpClient` constructor and configure an engine using the `engine` method, for example:
```kotlin
```
{src="snippets/_misc_client/AndroidConfig.kt" interpolate-variables="true" disable-links="false"}

You can learn how to configure all engine types from [](http-client_engines.md).


## Use the Client {id="use-client"}
After you've [added](#add-dependencies) all the required dependencies and [created](#create-client) the client, you can use it to make requests and receive responses. Learn more from [](client.md#make-request). 


## Code Example {id="code-example"}

The [mpp/client-mpp](https://github.com/ktorio/ktor-samples/tree/master/client-mpp) project shows how to use a Ktor client in a multiplatform application. This application works on the following platforms: `Android`, `iOS`, `JavaScript`, and `macosX64`.
