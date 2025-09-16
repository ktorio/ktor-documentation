[//]: # (title: Multiplatform)

<tldr>
<p>
Code example: <a href="https://github.com/ktorio/ktor-samples/tree/main/client-mpp">client-mpp</a>
</p>
</tldr>

<link-summary>
The Ktor client can be used in multiplatform projects and supports Android, JavaScript, and Native platforms.
</link-summary>

The [Ktor HTTP client](client-create-and-configure.md) can be used in [multiplatform projects](https://kotlinlang.org/docs/multiplatform.html) and supports the following platforms:
* JVM
* [Android](https://kotlinlang.org/docs/android-overview.html)
* [JavaScript](https://kotlinlang.org/docs/js-overview.html)
* [Native](https://kotlinlang.org/docs/native-overview.html)

## Add dependencies {id="add-dependencies"}
To use the Ktor HTTP client in your project, you need to add at least two dependencies: a client dependency and an [engine](client-engines.md) dependency. For a multiplatform project, you need to add these dependencies as follows:
1. To use the Ktor client in common code, add the dependency to `ktor-client-core` to the `commonMain` source set in the `build.gradle` or `build.gradle.kts` file:
   <var name="platform_name" value="common"/>
   <var name="artifact_name" value="ktor-client-core"/>
   <include from="lib.topic" element-id="add_ktor_artifact_multiplatform"/>
2. Add an [engine dependency](client-engines.md#dependencies) for the required platform to the corresponding source set. For Android, you can add the [Android](client-engines.md#android) engine dependency to the `androidMain` source set:
   <var name="platform_name" value="android"/>
   <var name="artifact_name" value="ktor-client-android"/>
   <include from="lib.topic" element-id="add_ktor_artifact_multiplatform"/>
   
   For iOS, you need to add the [Darwin](client-engines.md#darwin) engine dependency to `iosMain`:
   <var name="platform_name" value="ios"/>
   <var name="artifact_name" value="ktor-client-darwin"/>
   <include from="lib.topic" element-id="add_ktor_artifact_multiplatform"/>
   
   To learn which engines are supported for each platform, see [](client-engines.md#dependencies).


## Create the client {id="create-client"}
To create the client in a multiplatform project, call the [HttpClient](https://api.ktor.io/ktor-client/ktor-client-core/io.ktor.client/-http-client/index.html) constructor in a project's [common code](https://kotlinlang.org/docs/mpp-discover-project.html#source-sets):

```kotlin
```
{src="snippets/_misc_client/DefaultEngineCreate.kt"}

In this code snippet, the `HttpClient` constructor doesn't accept an engine as a parameter: the client will choose an engine for the required platform depending on the artifacts [added in a build script](#add-dependencies). 

If you need to adjust an engine configuration for specific platforms, pass a corresponding engine class as an argument to the `HttpClient` constructor and configure an engine using the `engine` method, for example:
```kotlin
```
{src="snippets/_misc_client/AndroidConfig.kt" interpolate-variables="true" disable-links="false"}

You can learn how to configure all engine types from [](client-engines.md).



## Code example {id="code-example"}

The [mpp/client-mpp](https://github.com/ktorio/ktor-samples/tree/main/client-mpp) project shows how to use a Ktor client in a multiplatform application. This application works on the following platforms: `Android`, `iOS`, `JavaScript`, and `macosX64`.
