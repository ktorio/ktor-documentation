[//]: # (title: Gradle Application plugin)

<microformat>
<p>
<control>Sample project</control>: <a href="https://github.com/ktorio/ktor-samples/tree/main/graalvm">graalvm</a>
</p>
</microformat>

Ktor Server applications can make use of [GraalVM](https://graalvm.org) in order to have native images for different platforms, and of course,
take advantage of the faster start-up times and other benefits that GraalVM provides. 

Currently, Ktor Server applications that want to leverage GraalVM has to use CIO as the [application engine](Engines.md). 

## Preparing for GraalVM

In addition to installing GraalVM and having the installation directory in the system path, you need to prepare your application 
so that all dependencies are bundled, i.e. you need to create a [fatjar](fatjar.md).

### Reflection Configuration 

GraalVM has [some requirements](https://www.graalvm.org/reference-manual/native-image/Reflection/) when it comes to applications that use reflection, 
which is the case of Ktor. It requires that you provide it a [JSON file](https://github.com/ktorio/ktor-samples/blob/main/graalvm/reflection.json) with 
certain type information. This configuration file is then passed as an argument to the `native-image` tool.

## Executing the `native-image` tool

Once the fat jar is ready, the only step required is to create the native image using the `native-image` tool. As this usually requires a 
number of parameters, for convenience, the sample application provides a shell script ([build.sh](https://github.com/ktorio/ktor-samples/blob/main/graalvm/build.sh) for macOS/Linux 
and [build.cmd](https://github.com/ktorio/ktor-samples/blob/main/graalvm/build.cmd) for Windows) that can be executed to obtain the final
binary. You can use this script as a starting point for your own applications. However, please note that some options may vary
depending on the dependencies being used, the package name of your project, etc.

## Running the resulting binary

If the shell script executes without any errors, you should obtain a native application, which in the case of the sample is 
called `graal-server`. Executing it will launch the Ktor Server, responding on `https://0.0.0.0:8080`.
