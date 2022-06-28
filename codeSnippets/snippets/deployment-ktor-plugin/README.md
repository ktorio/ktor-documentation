# Deployment - Ktor plugin

A sample Ktor project demonstrating how to configure the [Ktor Gradle plugin](https://github.com/ktorio/ktor-build-plugins) that simplifies the [deployment](https://ktor.io/docs/deploy.html) process of server Ktor applications.

To see the plugin's configuration, open the [build.gradle.kts](build.gradle.kts) and scroll down to the `ktor` extension.

> This sample is a part of the [codeSnippets](../../README.md) Gradle project.


## Build a fat JAR

To build the project's fat JAR, run the `buildFatJar` task:

```bash
./gradlew :deployment-ktor-plugin:buildFatJar
```

## Build and run a Docker image

To build and run the project's Docker image, execute the `runDocker` task:

```bash
./gradlew :deployment-ktor-plugin:runDocker
```

## Build a GraalVM native image

To build the project's GraalVM native image, execute the `buildNativeImage` task:

```bash
./gradlew :deployment-ktor-plugin:buildNativeImage
```

Then, run the generated executable file:

```bash
./snippets/deployment-ktor-plugin/build/native/nativeCompile/native-image-sample
```
