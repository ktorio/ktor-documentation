[//]: # (title: Creating a cross-platform mobile application)


In this tutorial, we'll create a simple cross-platform application for sending a request and receiving a response.


## Prerequisites {id="prerequisites"}

[Set up an environment](https://kotlinlang.org/docs/multiplatform-mobile-setup.html)

## Create a new project {id="new-project"}

## Configure the build script {id="build-script"}

Open the `shared/build.gradle.kts` file.

### Add Ktor dependency {id="ktor-dependency"}

```kotlin
val commonMain by getting {
    dependencies {
        implementation("io.ktor:ktor-client-core:%ktor_version%")
    }
}
```
{interpolate-variables="true"}

### Add engine dependencies {id="engine-dependency"}

[Engine](http-client_engines.md) - Android:

```kotlin
val androidMain by getting {
    dependencies {
        implementation("io.ktor:ktor-client-okhttp:%ktor_version%")
    }
}
```

iOS:

```kotlin
val iosMain by creating {
    dependsOn(commonMain)
    // ...
    dependencies {
        implementation("io.ktor:ktor-client-darwin:%ktor_version%")
    }
}
```

## Code {id="code"}

File `shared/src/commonMain/kotlin/com/example/kmmapplication/Greeting.kt`, the `Greeting` class:

```kotlin
class Greeting {
    fun greeting(): String {
        val client = HttpClient()
        var responseBody: String = ""
        runBlocking {
            responseBody = client.get("https://ktor.io").bodyAsText()
        }
        client.close()
        return responseBody
    }
}
```

## Internet access {id="access"}

Open `androidApp/src/main/AndroidManifest.xml`:

```xml
<manifest>
    <uses-permission android:name="android.permission.INTERNET" />
    <application>
        ...
    </application>
</manifest> 
```

## Run you application {id="run"}

[](https://kotlinlang.org/docs/multiplatform-mobile-create-first-app.html#run-your-application)


