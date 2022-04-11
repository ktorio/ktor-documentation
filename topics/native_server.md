[//]: # (title: Native server)

<microformat>
<var name="example_name" value="embedded-server-native"/>
<include src="lib.xml" include-id="download_example"/>
</microformat>

Ktor supports [Kotlin/Native](https://kotlinlang.org/docs/native-overview.html) and allows you to run a server without an additional runtime or virtual machine.

## Limitations {id="limitations"}

Currently, running a Ktor server under Kotlin/Native has the following limitations:
* only the [CIO engine](Engines.md) is supported
* [HTTPS](ssl.md) without a reverse proxy is not supported
* Windows target is not supported


## Add dependencies {id="add-dependencies"}

To use Ktor server in a Kotlin/Native project, you need to add at least two dependencies: a `ktor-server-core` dependency and an engine dependency (CIO). The code snippet below shows how to add dependencies to the `nativeMain` source set in the `build.gradle.kts` file:

```kotlin
```
{src="snippets/embedded-server-native/build.gradle.kts" lines="32-38,44"}

## Declare native binaries {id="binaries"}

To [declare a native binary](https://kotlinlang.org/docs/mpp-build-native-binaries.html), use the `binaries` property of a native target:

```kotlin
```
{src="snippets/embedded-server-native/build.gradle.kts" lines="16-31"}

You can find the full example here: [embedded-server-native](https://github.com/ktorio/ktor-documentation/tree/%current-branch%/codeSnippets/snippets/embedded-server-native).

## Create a server {id="create-server"}

After configuring your Gradle build script, you can create a Ktor server as described here: [](create_server.xml).


