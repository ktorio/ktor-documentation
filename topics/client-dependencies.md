[//]: # (title: Adding client dependencies)

<link-summary>Learn how to add client dependencies to the existing project.</link-summary>

To use the Ktor HTTP client in your project, you need to [configure repositories](#repositories) and add the following dependencies:
- **[ktor-client-core](#client-dependency)**

   `ktor-client-core` contains core Ktor client functionality. 
- **[Engine dependency](#engine-dependency)** 
   
   Engines are used to process network requests. Note that a [specific platform](#platforms) may require a specific engine that processes network requests.
- (Optional) **[Plugin dependency](#plugin-dependency)**
   
   Plugins are used to extend the client with a specific functionality.

<include src="server-dependencies.xml" include-id="repositories"/>


## Add dependencies {id="add-ktor-dependencies"}

> For [different platforms](#platforms), Ktor provides platform-specific artifacts with suffixes such as `-jvm` or `-js`, for example, `ktor-client-core-jvm`. Note that Gradle resolves artifacts appropriate for a given platform automatically while Maven doesn't support this capability. This means that for Maven you need to add a platform-specific suffix manually.
>
{type="tip"}

### Client dependency {id="client-dependency"}
The main client functionality is available in the `ktor-client-core` artifact. Depending on your build system, you can add it in the following way:
<var name="artifact_name" value="ktor-client-core"/>
<include src="lib.xml" include-id="add_ktor_artifact"/>

For a multiplatform project, you need to add the `ktor-client-core` artifact to the `commonMain` source set:

```kotlin
```
{src="snippets/tutorial-client-kmm/shared/build.gradle.kts" lines="27-31"}




### Engine dependency {id="engine-dependency"}
An [engine](http-client_engines.md) is responsible for processing network requests. There are different client engines available for various platforms, such as Apache, CIO, Android, iOS, and so on. For example, you can add a `CIO` engine dependency as follows:
<var name="artifact_name" value="ktor-client-cio"/>
<include src="lib.xml" include-id="add_ktor_artifact"/>

For a multiplatform project, you need to add a dependency for the required engine to a corresponding source set.
For example, the code snippet below shows how to add the `ktor-client-okhttp` dependency to the `androidMain` source set:

```kotlin
```
{src="snippets/tutorial-client-kmm/shared/build.gradle.kts" lines="37-41"}

For a full list of dependencies required for a specific engine, see [](http-client_engines.md#dependencies).


### Plugin dependency {id="plugin-dependency"}
Ktor lets you use additional client functionality ([plugins](http-client_plugins.md)) that is not available by default, for example, logging, authorization, or serialization. Some of them are provided in separate artifacts. You can learn which dependencies you need from a topic for a required plugin.

> For a multiplatform project, a plugin dependency should be added to the `commonMain` source set. Note that some plugins might have  [limitations](http-client_engines.md#limitations) for specific platforms.


## Supported platforms {id="platforms"}

The Ktor HTTP client can be used on different platforms supported by Kotlin:
- JVM
- [Android](https://kotlinlang.org/docs/android-overview.html)
- [Native](https://kotlinlang.org/docs/native-overview.html)
   > Using the Ktor client on Kotlin/Native requires enabling the [New memory model](https://github.com/JetBrains/kotlin/blob/master/kotlin-native/NEW_MM.md#enable-the-new-mm).
- [JavaScript](https://kotlinlang.org/docs/js-overview.html)

You can use it in [multiplatform projects](https://kotlinlang.org/docs/multiplatform.html), be it a multiplatform mobile or a full-stack web application. The following [targets](https://kotlinlang.org/docs/multiplatform-dsl-reference.html#targets) are supported for multiplatform projects:

<table>
<tr>
    <td>
        Target platform
    </td>
    <td>
        Target preset
    </td>
</tr>
<tr>
    <td>
        Kotlin/JVM
    </td>
    <td>
        <list>
            <li>
                <code>jvm</code>
            </li>
        </list>
    </td>
</tr>

<tr>
    <td>
        Kotlin/JS
    </td>
    <td>
        <list>
            <li>
                <code>js</code>
            </li>
        </list>
    </td>
</tr>

<tr>
    <td>
        Android
    </td>
    <td>
        <list>
            <li>
                <code>android</code>
            </li>
        </list>
    </td>
</tr>

<tr>
    <td>
        iOS
    </td>
    <td>
        <list>
            <li>
                <code>iosArm32</code>
            </li>
            <li>
                <code>iosArm64</code>
            </li>
            <li>
                <code>iosX64</code>
            </li>
            <li>
                <code>iosSimulatorArm64</code>
            </li>
        </list>
    </td>
</tr>

<tr>
    <td>
        watchOS
    </td>
    <td>
        <list>
            <li>
                <code>watchosArm32</code>
            </li>
            <li>
                <code>watchosArm64</code>
            </li>
            <li>
                <code>watchosX86</code>
            </li>
            <li>
                <code>watchosX64</code>
            </li>
            <li>
                <code>watchosSimulatorArm64</code>
            </li>
        </list>
    </td>
</tr>

<tr>
    <td>
        tvOS
    </td>
    <td>
        <list>
            <li>
                <code>tvosArm64</code>
            </li>
            <li>
                <code>tvosX64</code>
            </li>
            <li>
                <code>tvosSimulatorArm64</code>
            </li>
        </list>
    </td>
</tr>

<tr>
    <td>
        macOS
    </td>
    <td>
        <list>
            <li>
                <code>macosX64</code>
            </li>
            <li>
                <code>macosArm64</code>
            </li>
        </list>
    </td>
</tr>

<tr>
    <td>
        Linux
    </td>
    <td>
        <list>
            <li>
                <code>linuxX64</code>
            </li>
        </list>
    </td>
</tr>

<tr>
    <td>
        Windows
    </td>
    <td>
        <list>
            <li>
                <code>mingwX64</code>
            </li>
        </list>
    </td>
</tr>
</table>



