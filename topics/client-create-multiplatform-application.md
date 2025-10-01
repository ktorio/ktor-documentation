[//]: # (title: Creating a cross-platform mobile application)

<show-structure for="chapter" depth="2"/>

<tldr>
<var name="example_name" value="tutorial-client-kmm"/>
<include from="lib.topic" element-id="download_example"/>
</tldr>

<link-summary>
Learn how to use Ktor client in a Kotlin Multiplatform Mobile application.
</link-summary>

The Ktor HTTP client can be used in multiplatform projects. In this tutorial, we'll create a simple Kotlin Multiplatform
Mobile application, which sends a request and receives a response body as plain HTML text.

## Prerequisites {id="prerequisites"}

First, you need to set up an environment for cross-platform mobile development by installing the necessary tools on a
suitable operating system. Learn how to do this from
the [Set up an environment](https://kotlinlang.org/docs/multiplatform-mobile-setup.html) section.

> You will need a Mac with macOS to complete certain steps in this tutorial, which include writing iOS-specific code and
running an iOS application.
>
{style="note"}

## Create a new project {id="new-project"}

To create a new project, you can use the Kotlin Multiplatform project wizard in IntelliJ IDEA. It will create a basic
multiplatform project which you can expand with clients and services.

<procedure>

1. Launch IntelliJ IDEA.
2. In IntelliJ IDEA, select **File | New | Project**.
3. In the panel on the left, select **Kotlin Multiplatform**.
4. Specify the following fields in the **New Project** window:
    * **Name**: KmmKtor
    * **Group**: com.example.ktor
      ![Kotlin Multiplatform wizard settings](kmm_tutorial_create_project.png){ width="450" width="706" border-effect="rounded" style="block" }
5. Select **Android** and **iOS** targets.
6. For iOS, select the **Do not share UI** option to keep the UI native.
7. Click the **Create** button and wait for the IDE to generate and import the project.

</procedure>

## Configure build scripts {id="build-script"}

### Add Ktor dependencies {id="ktor-dependencies"}

To use the Ktor HTTP client in your project, you need to add at least two dependencies: a client dependency and an
engine dependency.

In the
<path>gradle/libs.versions.toml</path>
file add the Ktor version:

```kotlin
```

{src="snippets/tutorial-client-kmm/gradle/libs.versions.toml" include-lines="1,15"}

Then, define the Ktor client and engine libraries:

```kotlin
```

{src="snippets/tutorial-client-kmm/gradle/libs.versions.toml" include-lines="18,29-31"}

To add the dependencies, open the `shared/build.gradle.kts` file and follow the steps below:

1. To use the Ktor client in common code, add the dependency `ktor-client-core` to the `commonMain` source set:
   ```kotlin
   ```
   {src="snippets/tutorial-client-kmm/shared/build.gradle.kts" include-lines="25-27,29,39"}

2. Add an [engine dependency](client-engines.md) for each required platform to the corresponding source set:
    - For Android, add the `ktor-client-okhttp` dependency to the `androidMain` source set:
      ```kotlin
      ```
      {src="snippets/tutorial-client-kmm/shared/build.gradle.kts" include-lines="30-32"}

      For Android, you can also use [other engine types](client-engines.md#jvm-android).
    - For iOS, add the `ktor-client-darwin` dependency to `iosMain`:
      ```kotlin
      ```
      {src="snippets/tutorial-client-kmm/shared/build.gradle.kts" include-lines="33-35"}

### Add coroutines {id="coroutines"}

To use coroutines in [Android code](#android-activity), you need to add `kotlinx.coroutines` to your project:

1. Open the
   <path>gradle/libs.versions.toml</path>
   file and specify the coroutines version and libraries:

    ```kotlin
    ```
   {src="snippets/tutorial-client-kmm/gradle/libs.versions.toml" include-lines="1,16-18,32-33"}

2. Open the
   <path>build.gradle.kts</path>
   file and add the `kotlinx-coroutines-core` dependency to the `commonMain` source set:

    ```kotlin
    ```
   {src="snippets/tutorial-client-kmm/shared/build.gradle.kts" include-lines="25-29,39"}

3. Then, open the `composeApp/build.gradle.kts` and add the `kotlinx-coroutines-android` dependency to the `androidMain`
source set:

   ```kotlin
   ```
   {src="snippets/tutorial-client-kmm/composeApp/build.gradle.kts" include-lines="18-19,22-24,39"}

Select **Build | Sync Project with Gradle Files** to install the added dependencies.

## Update your application {id="code"}

### Shared code {id="shared-code"}

To update the code shared between Android and iOS, open the
<path>shared/src/commonMain/kotlin/com/example/ktor/kmmktor/Greeting.kt</path>
file and add the following code to the `Greeting` class:

```kotlin
```

{src="snippets/tutorial-client-kmm/shared/src/commonMain/kotlin/com/example/ktor/kmmktor/Greeting.kt"}

- To create the HTTP client, the `HttpClient` constructor is called.
- The suspending `greet()` function is used to make a [request](client-requests.md) and receive the body of
  a [response](client-responses.md) as a string value.

### Android code {id="android-activity"}

To call the suspending `greet()` function from the Android code, you can use [`rememberCoroutineScope`](https://developer.android.com/reference/kotlin/androidx/compose/runtime/package-summary#rememberCoroutineScope(kotlin.Function0)).

Open the `composeApp/src/androidMain/kotlin/com/example/ktor/kmmktor/MainActivity.kt` file and update `MainActivity` code as
follows:

```kotlin
```

{src="snippets/tutorial-client-kmm/composeApp/src/androidMain/kotlin/com/example/ktor/kmmktor/MainActivity.kt"}

Inside the created scope, you call the shared `greet()` function and handle possible exceptions.

### iOS code {id="ios-view"}

1. Open the `iosApp/iosApp/iOSApp.swift` file and update the entry point for the application:
   ```Swift
   ```
   {src="snippets/tutorial-client-kmm/iosApp/iosApp/iOSApp.swift"}

2. Open the `iosApp/iosApp/ContentView.swift` file and update `ContentView` code in the following way:
   ```Swift
   ```
   {src="snippets/tutorial-client-kmm/iosApp/iosApp/ContentView.swift"}

   On iOS, the `greet()` suspending function is available as a function with a callback.

## Enable internet access on Android {id="android-internet"}

The final thing you need to do is to enable internet access for the Android application.
Open the `composeApp/src/androidMain/AndroidManifest.xml` file and enable the required permission using the
`uses-permission` element:

```xml
<manifest>
    <uses-permission android:name="android.permission.INTERNET" />
    <application>
        ...
    </application>
</manifest> 
```

## Run your application on Android {id="run-android"}

1. In IntelliJ IDEA, select **composeApp** in the list of run configurations.
2. Choose an Android virtual device next to the list of configurations and click **Run**.
   ![composeApp selected with Pixel 8 API device](kmm_tutorial_run_android.png){width="381" style="block"}
   If you don't have a device in the list, create a [new Android virtual device](https://developer.android.com/studio/run/managing-avds#createavd).
3. Once loaded, the simulator should display the received HTML document as plain text.
   ![Android simulator](tutorial_client_kmm_android.png){width="381" style="block"}

## Run your application on iOS {id="run-ios"}

1. Ensure that Xcode is running.
2. In IntelliJ IDEA, select **iosApp** in the list of run configurations.
3. Choose an iOS simulated device next to the list of configurations and click **Run**.
   ![iOsApp selected with iPhone 16 device](kmm_tutorial_run_ios.png){width="381" style="block"}
   If you don't have an available iOS configuration in the list, add a [new run configuration](https://www.jetbrains.com/help/kotlin-multiplatform-dev/multiplatform-create-first-app.html#run-on-a-new-ios-simulated-device).
4. Once loaded, the simulator should display the received HTML document as plain text.
   ![iOS simulator](tutorial_client_kmm_ios.png){width="381" style="block"}




