[//]: # (title: Native server)

<tldr>
<var name="example_name" value="embedded-server-native"/>
<include from="lib.topic" element-id="download_example"/>
</tldr>

<link-summary>
Ktor supports Kotlin/Native and allows you to run a server without an additional runtime or virtual machine.
</link-summary>

Ktor supports [Kotlin/Native](https://kotlinlang.org/docs/native-overview.html) and allows you to run a server without an additional runtime or virtual machine. Currently, running a Ktor server under Kotlin/Native has the following limitations:
* a [server should be created](create_server.topic) using `embeddedServer`
* only the [CIO engine](Engines.md) is supported
* [HTTPS](ssl.md) without a reverse proxy is not supported
* Windows [target](#targets) is not supported

> Using the Ktor server in a Kotlin/Native project requires a [new memory manager](https://kotlinlang.org/docs/native-memory-manager.html).
> Starting with Kotlin 1.7.20, the new Kotlin/Native memory manager is enabled by default.



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



## Add dependencies {id="add-dependencies"}

Ktor server in a Kotlin/Native project requires at least two dependencies: a `ktor-server-core` dependency and an engine dependency (CIO). The code snippet below shows how to add dependencies to the `nativeMain` source set in the `build.gradle.kts` file:

```kotlin
```
{src="snippets/embedded-server-native/build.gradle.kts" include-lines="31-37,43"}

## Configure native targets {id="native-target"}

Specify the required native targets and [declare a native binary](https://kotlinlang.org/docs/mpp-build-native-binaries.html) using the `binaries` property:

```kotlin
```
{src="snippets/embedded-server-native/build.gradle.kts" include-lines="16-30"}

You can find the full example here: [embedded-server-native](https://github.com/ktorio/ktor-documentation/tree/%ktor_version%/codeSnippets/snippets/embedded-server-native).

## Create a server {id="create-server"}

After configuring your Gradle build script, you can create a Ktor server as described here: [](create_server.topic).


