[//]: # (title: Multiplatform)

<include src="lib.md" include-id="outdated_warning"/>

The HTTP Client supports several platforms, using the experimental [multiplatform support](https://kotlinlang.org/docs/multiplatform.html)
that was introduced in [Kotlin 1.2](https://blog.jetbrains.com/kotlin/2017/11/kotlin-1-2-released/).

Right now, the supported platforms are JVM, Android, iOS, Js and native.

## Common

For [multiplatform projects](https://kotlinlang.org/docs/multiplatform.html) that for example
share code between multiple platforms, we can create a common module.
That common module can only access APIs that are available on all the targets.
Ktor HTTP Client exposes a common module that can be used for such projects:

```kotlin
dependencies {
    implementation("io.ktor:ktor-client-core:$ktor_version")
    // ...
}
```

## JVM

To use Ktor on JVM, you have to include [one of the supported JVM Engines](http-client_engines.md#jvm-android) to your  `build.gradle`(`build.gradle.kts`).

## Android

To use Android engine you have to add the dependency in your `build.gradle`(`build.gradle.kts`):

```kotlin
dependencies {
    implementation("io.ktor:ktor-client-android:$ktor_version")
    // ...
}
```

You can then use Android Studio, or Gradle to build your project.

## iOS

In the case of iOS, you have to use [Kotlin/Native](https://github.com/JetBrains/kotlin-native), and analogously
to android, you have to put this artifact as part of the `dependencies` block.

```kotlin
dependencies {
    implementation("io.ktor:ktor-client-ios:$ktor_version")
    // ...
}
```

In the case of iOS, we usually create a `.framework`, and the application project is a regular XCode project written either in Swift or Objective-C that includes that framework. So you first have to build the framework using the Gradle tasks exposed by Kotlin/Native, and then open the XCode project.

## Javascript

In the case of a browser or node-js applications, you have to use [Kotlin/Js](https://kotlinlang.org/docs/tutorials/javascript/kotlin-to-javascript/kotlin-to-javascript.html).

```kotlin
dependencies {
    implementation("io.ktor:ktor-client-js:$ktor_version")
    // ...
}
```

## Posix compatible desktops: MacOS, Linux, Windows

As an alternative to JVM compatible engines, you also could use ktor client with [Kotlin/Native](https://github.com/JetBrains/kotlin-native)using `curl` backend.

```kotlin
dependencies {
    implementation("io.ktor:ktor-client-curl:$ktor_version")
    // ...
}
```

## Samples

There is a full sample using the common client in the ktor-samples repository [mpp/client-mpp](https://github.com/ktorio/ktor-samples/tree/master/client-mpp).

You can use this project as a reference. Please check Kotlin documentation to learn about running different targets: [JVM](https://kotlinlang.org/docs/jvm-get-started.html), [Native](https://kotlinlang.org/docs/native-get-started.html), [JS](https://kotlinlang.org/docs/js-get-started.html)
