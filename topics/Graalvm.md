[//]: # (title: GraalVM)

<microformat>
<var name="example_name" value="deployment-ktor-plugin"/>
<include src="lib.xml" include-id="download_example"/>
</microformat>

<excerpt>
Ktor server applications can make use of GraalVM in order to have native images for different platforms.
</excerpt>

Ktor server applications can make use of [GraalVM](https://graalvm.org) in order to have native images for different platforms and, of course, take advantage of the faster start-up times and other benefits that GraalVM provides. The [Ktor Gradle plugin](https://github.com/ktorio/ktor-build-plugins) allows you to build a project's GraalVM native image.

> Currently, Ktor server applications that want to leverage GraalVM have to use CIO as the [application engine](Engines.md).

## Prepare for GraalVM

Before building a project's GraalVM native image, make sure the following prerequisites are met:
- [GraalVM](https://www.graalvm.org/docs/getting-started/) and [Native Image](https://www.graalvm.org/reference-manual/native-image/) are installed.
- The `GRAALVM_HOME` and `JAVA_HOME` environment variables are set.

## Configure the Ktor plugin {id="configure-plugin"}
To build a native executable, you need to configure the Ktor plugin first:
1. Open the `build.gradle.kts` file and add the plugin to the `plugins` block:
   ```kotlin
   ```
   {src="snippets/deployment-ktor-plugin/build.gradle.kts" lines="5,8-9"}

2. Make sure the [main application class](server-dependencies.xml#create-entry-point) is configured:
   ```kotlin
   ```
   {src="snippets/deployment-ktor-plugin/build.gradle.kts" lines="11-13"}

3. Optionally, you can  configure the name of the native executable to be generated using the `ktor.nativeImage` extension:
   ```kotlin
   ```
   {src="snippets/deployment-ktor-plugin/build.gradle.kts" lines="29,48-51"}


## Build and run a native executable {id="build"}

The `buildNativeImage` task provided by the Ktor plugin generates a native executable with your application in the `build/native/nativeCompile` directory.
Executing it will launch the Ktor server, responding on `https://0.0.0.0:8080`.
