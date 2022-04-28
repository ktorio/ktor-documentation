[//]: # (title: Native server)

<microformat>
<var name="example_name" value="embedded-server-native"/>
<include src="lib.xml" include-id="download_example"/>
</microformat>

Ktor supports [Kotlin/Native](https://kotlinlang.org/docs/native-overview.html) and allows you to run a server without an additional runtime or virtual machine. Currently, running a Ktor server under Kotlin/Native has the following limitations:
* only the [CIO engine](Engines.md) is supported
* [HTTPS](ssl.md) without a reverse proxy is not supported
* Windows [target](#targets) is not supported

## Supported targets {id="targets"}

The following [targets](https://kotlinlang.org/docs/multiplatform-dsl-reference.html#targets) are supported for multiplatform projects:

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
</table>



## Enable the New Kotlin/Native memory model {id="new_memory_model"}
To use Ktor server in a Kotlin/Native project, enable the New Kotlin/Native memory model as described here: [Enable the new MM](https://github.com/JetBrains/kotlin/blob/master/kotlin-native/NEW_MM.md#enable-the-new-mm).
For example, if you use Gradle, you can enable it by using the `kotlin.native.binary.memoryModel` flag in `gradle.properties`:

```Gradle
kotlin.native.binary.memoryModel=experimental
```


## Add dependencies {id="add-dependencies"}

Ktor server in a Kotlin/Native project requires at least two dependencies: a `ktor-server-core` dependency and an engine dependency (CIO). The code snippet below shows how to add dependencies to the `nativeMain` source set in the `build.gradle.kts` file:

```kotlin
```
{src="snippets/embedded-server-native/build.gradle.kts" lines="31-37,43"}

## Configure native targets {id="native-target"}

Specify the required native targets and [declare a native binary](https://kotlinlang.org/docs/mpp-build-native-binaries.html) using the `binaries` property:

```kotlin
```
{src="snippets/embedded-server-native/build.gradle.kts" lines="16-30"}

You can find the full example here: [embedded-server-native](https://github.com/ktorio/ktor-documentation/tree/%current-branch%/codeSnippets/snippets/embedded-server-native).

## Create a server {id="create-server"}

After configuring your Gradle build script, you can create a Ktor server as described here: [](create_server.xml).


