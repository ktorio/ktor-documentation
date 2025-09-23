[//]: # (title: Native server)

<tldr>
<var name="example_name" value="embedded-server-native"/>
<include from="lib.topic" element-id="download_example"/>
</tldr>

<link-summary>
Ktor supports Kotlin/Native and allows you to run a server without an additional runtime or virtual machine.
</link-summary>

Ktor supports [Kotlin/Native](https://kotlinlang.org/docs/native-overview.html) and allows you to run a server without an additional runtime or virtual machine. Currently, running a Ktor server under Kotlin/Native has the following limitations:
* a [server should be created](server-create-and-configure.topic) using `embeddedServer`
* only the [CIO engine](server-engines.md) is supported
* [HTTPS](server-ssl.md) without a reverse proxy is not supported

<include from="client-engines.md" element-id="newmm-note"/>




## Add dependencies {id="add-dependencies"}

Ktor server in a Kotlin/Native project requires at least two dependencies: a `ktor-server-core` dependency and an engine dependency (CIO). The code snippet below shows how to add dependencies to the `nativeMain` source set in the `build.gradle.kts` file:

```kotlin
```
{src="snippets/embedded-server-native/build.gradle.kts" include-lines="33-39,46"}

To [test](server-testing.md) a Native server, add the `ktor-server-test-host` artifact to the `nativeTest` source set:

```kotlin
```
{src="snippets/embedded-server-native/build.gradle.kts" include-lines="33,40-46"}

## Configure native targets {id="native-target"}

Specify the required native targets and [declare a native binary](https://kotlinlang.org/docs/mpp-build-native-binaries.html) using the `binaries` property:

```kotlin
```
{src="snippets/embedded-server-native/build.gradle.kts" include-lines="16-32"}

You can find the full example here: [embedded-server-native](https://github.com/ktorio/ktor-documentation/tree/%ktor_version%/codeSnippets/snippets/embedded-server-native).

## Create a server {id="create-server"}

After configuring your Gradle build script, you can create a Ktor server as described here: [](server-create-and-configure.topic).


