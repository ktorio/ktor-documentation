[//]: # (title: GraalVM)

[//]: # (title: GraalVM)

<tldr>
<p>
<control>Sample project</control>: <a href="https://github.com/ktorio/ktor-samples/tree/main/graalvm">graalvm</a>
</p>
</tldr>

<link-summary>
Ktor Server applications can make use of GraalVM in order to have native images for different platforms.
</link-summary>

Ktor Server applications can make use of [GraalVM](https://graalvm.org) in order to have native images for different platforms, and of course, take advantage of the faster start-up times and other benefits that GraalVM provides.

Currently, Ktor Server applications that want to leverage GraalVM have to use CIO as the [application engine](Engines.md).

## Prepare for GraalVM

In addition to installing GraalVM and having the installation directory in the system path, you need to prepare your application
so that all dependencies are bundled, i.e. you need to create a fat jar.

### Reflection configuration

GraalVM has [some requirements](https://www.graalvm.org/22.1/reference-manual/native-image/Reflection/) when it comes to applications that use reflection,
which is the case of Ktor. It requires that you provide it a [JSON file](https://github.com/ktorio/ktor-samples/blob/main/graalvm/src/main/resources/META-INF/native-image/reflect-config.json) with
certain type information. This configuration file is then passed as an argument to the `native-image` tool.

## Execute the `native-image` tool

Once the fat jar is ready, the only step required is to create the native image using the `native-image` CLI tool. 
This could be also done by [Gradle plugin](https://graalvm.github.io/native-build-tools/0.9.8/gradle-plugin.html). 
You can see the example of the `build.gradle.kts` file [here](https://github.com/ktorio/ktor-samples/blob/main/graalvm/build.gradle.kts).
However, please note that some options may vary depending on the dependencies being used, the package name of your project, etc.

## Run the resulting binary

If the shell script executes without any errors, you should obtain a native application, which in the case of the sample is
called `graal-server`. Executing it will launch the Ktor Server, responding on `https://0.0.0.0:8080`.


[//]: # (<tldr>)

[//]: # (<var name="example_name" value="deployment-ktor-plugin"/>)

[//]: # (<include from="lib.topic" element-id="download_example"/>)

[//]: # (</tldr>)

[//]: # ()
[//]: # (<link-summary>)

[//]: # (Ktor server applications can make use of GraalVM in order to have native images for different platforms.)

[//]: # (</link-summary>)

[//]: # ()
[//]: # (Ktor server applications can make use of [GraalVM]&#40;https://graalvm.org&#41; in order to have native images for different platforms and, of course, take advantage of the faster start-up times and other benefits that GraalVM provides. The [Ktor Gradle plugin]&#40;https://github.com/ktorio/ktor-build-plugins&#41; allows you to build a project's GraalVM native image.)

[//]: # ()
[//]: # (> Currently, Ktor server applications that want to leverage GraalVM have to use CIO as the [application engine]&#40;Engines.md&#41;.)

[//]: # ()
[//]: # (## Prepare for GraalVM)

[//]: # ()
[//]: # (Before building a project's GraalVM native image, make sure the following prerequisites are met:)

[//]: # (- [GraalVM]&#40;https://www.graalvm.org/docs/getting-started/&#41; and [Native Image]&#40;https://www.graalvm.org/reference-manual/native-image/&#41; are installed.)

[//]: # (- The `GRAALVM_HOME` and `JAVA_HOME` environment variables are set.)

[//]: # ()
[//]: # (## Configure the Ktor plugin {id="configure-plugin"})

[//]: # (To build a native executable, you need to configure the Ktor plugin first:)

[//]: # (1. Open the `build.gradle.kts` file and add the plugin to the `plugins` block:)

[//]: # (   ```kotlin)

[//]: # (   ```)

[//]: # (   {src="snippets/deployment-ktor-plugin/build.gradle.kts" include-lines="5,8-9"})

[//]: # ()
[//]: # (2. Make sure the [main application class]&#40;server-dependencies.xml#create-entry-point&#41; is configured:)

[//]: # (   ```kotlin)

[//]: # (   ```)

[//]: # (   {src="snippets/deployment-ktor-plugin/build.gradle.kts" include-lines="11-13"})

[//]: # ()
[//]: # (3. Optionally, you can  configure the name of the native executable to be generated using the `ktor.nativeImage` extension:)

[//]: # (   ```kotlin)

[//]: # (   ```)

[//]: # (   {src="snippets/deployment-ktor-plugin/build.gradle.kts" include-lines="29,48-51"})

[//]: # ()
[//]: # ()
[//]: # (## Build and run a native executable {id="build"})

[//]: # ()
[//]: # (The `buildNativeImage` task provided by the Ktor plugin generates a native executable with your application in the `build/native/nativeCompile` directory.)

[//]: # (Executing it will launch the Ktor server, responding on `https://0.0.0.0:8080` by default.)
