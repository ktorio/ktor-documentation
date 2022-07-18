[//]: # (title: Creating a cross-platform mobile application)

<tldr>
<var name="example_name" value="tutorial-client-kmm"/>
<include src="lib.xml" include-id="download_example"/>
<p>
<b>Video</b>: <a href="https://youtu.be/_Q62iJoNOfg">Ktor for Networking in Kotlin Multiplatform Mobile projects</a> 
</p>
</tldr>

<link-summary>
Learn how to create a Kotlin Multiplatform Mobile application.
</link-summary>

The Ktor HTTP client can be used in multiplatform projects. In this tutorial, we'll create a simple Kotlin Multiplatform Mobile application, which sends a request and receives a response body as plain HTML text.

> To learn how to create your first Kotlin Multiplatform Mobile application, see [Create your first cross-platform mobile app](https://kotlinlang.org/docs/multiplatform-mobile-create-first-app.html).


## Prerequisites {id="prerequisites"}

First, you need to set up an environment for cross-platform mobile development by installing the necessary tools on a suitable operating system. Learn how to do this from the [Set up an environment](https://kotlinlang.org/docs/multiplatform-mobile-setup.html) section.

> You will need a Mac with macOS to complete certain steps in this tutorial, which include writing iOS-specific code and running an iOS application.
>
{type="note"}

## Create a new project {id="new-project"}

1. In Android Studio, select **File | New | New Project**.
2. Select **Kotlin Multiplatform App** in the list of project templates, and click **Next**.
3. Specify a name for your application, and click **Next**. In our tutorial, the application name is `KmmKtor`.
4. On the next page, leave the default settings and click **Finish** to create a new project.
   Now, wait while your project is set up. It may take some time to download and set up the required components when you do this for the first time.
   > To view the complete structure of the generated multiplatform project, switch from **Android** to **Project** in a [Project view](https://developer.android.com/studio/projects#ProjectView).

## Configure build scripts {id="build-script"}

### Add Ktor dependencies {id="ktor-dependencies"}

To use the Ktor HTTP client in your project, you need to add at least two dependencies: a client dependency and an engine dependency. Open the `shared/build.gradle.kts` file and follow the steps below:

1. Specify Ktor version inside `sourceSets`:
   ```kotlin
   ```
   {src="snippets/tutorial-client-kmm/shared/build.gradle.kts" lines="25-26,64"}

   <include src="getting_started_ktor_client.xml" include-id="eap-note"/>

3. To use the Ktor client in common code, add the dependency to `ktor-client-core` to the `commonMain` source set:
   ```kotlin
   ```
   {src="snippets/tutorial-client-kmm/shared/build.gradle.kts" lines="27-31"}

4. Add an [engine dependency](http-client_engines.md) for the required platform to the corresponding source set:
   - For Android, add the `ktor-client-okhttp` dependency to the `androidMain` source set:
     ```kotlin
     ```
     {src="snippets/tutorial-client-kmm/shared/build.gradle.kts" lines="37-41"}
   
     For Android, you can also use [other engine types](http-client_engines.md#jvm-android).
   - For iOS, add the `ktor-client-darwin` dependency to `iosMain`:
     ```kotlin
     ```
     {src="snippets/tutorial-client-kmm/shared/build.gradle.kts" lines="46-54"}


### Add coroutines {id="coroutines"}

In this tutorial, we'll use coroutines in [Android code](#android-activity).
Open the `androidApp/build.gradle.kts` and add the following dependencies:

```kotlin
```
{src="snippets/tutorial-client-kmm/androidApp/build.gradle.kts" lines="22,27-29"}

### Enable the New Kotlin/Native memory model {id="new_memory_model"}
To use Ktor client on Kotlin/Native targets (such as iOS), we need to enable the New Kotlin/Native memory model.
Open the `gradle.properties` file and add the following line:

```Gradle
```
{src="snippets/tutorial-client-kmm/gradle.properties" lines="14"}

## Update your application {id="code"}

### Shared code {id="shared-code"}

To update code shared between Android and iOS, open the `shared/src/commonMain/kotlin/com/example/kmmktor/Greeting.kt` file and add the following code to the `Greeting` class:

```kotlin
```
{src="snippets/tutorial-client-kmm/shared/src/commonMain/kotlin/com/example/kmmktor/Greeting.kt" lines="7,12-17"}

- To create the HTTP client, the `HttpClient` constructor is called.
- The suspending `getHtml` function is used to make a [request](request.md) and receive the body of a [response](response.md) as a string value.

### Android code {id="android-activity"}

To call the suspending `getHtml` function from the Android code, we need to create a coroutine scope.
We'll be using [MainScope](https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines/-main-scope.html), which can be used as the main coroutine scope for UI components.

Open the `androidApp/src/main/java/com/example/kmmktor/android/MainActivity.kt` file and update `MainActivity` code as follows:

```kotlin
```
{src="snippets/tutorial-client-kmm/androidApp/src/main/java/com/example/kmmktor/android/MainActivity.kt" lines="10-29"}

Inside the created `MainScope`, the [runCatching](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/run-catching.html) function is used to call the shared `getHtml` function and handle possible exceptions.


### iOS code {id="ios-view"}

Open the `iosApp/iosApp/ContentView.swift` file and update `ContentView` code in the following way:

```Swift
```
{src="snippets/tutorial-client-kmm/iosApp/iosApp/ContentView.swift" lines="4-24"}

On iOS, the `getHtml` suspending function is available as a function with a callback.

## Enable internet access on Android {id="android-internet"}

The final thing we need to do is to enable internet access for the Android application.
Open the `androidApp/src/main/AndroidManifest.xml` file and enable the required permission using the `uses-permission` element:

```xml
<manifest>
    <uses-permission android:name="android.permission.INTERNET" />
    <application>
        ...
    </application>
</manifest> 
```

## Run your application {id="run"}

To run the created multiplatform application on the Android or iOS simulator, select **androidApp** or **iosApp** and click **Run**.
The simulator should display the received HTML document as plain text.

<tabs>
<tab title="Android">

![Android simulator](tutorial_client_kmm_android.png){width="381"}

</tab>
<tab title="iOS">

![iOS simulator](tutorial_client_kmm_ios.png){width="351"}

</tab>
</tabs>



