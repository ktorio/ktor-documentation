[//]: # (title: Creating fat JARs using the Ktor Gradle plugin)

<microformat>
<var name="example_name" value="deployment-ktor-plugin"/>
<include src="lib.xml" include-id="download_example"/>
</microformat>

<excerpt>Learn how to create and run an executable fat JAR using the Ktor Gradle plugin.</excerpt>

The [Ktor Gradle plugin](https://github.com/ktorio/ktor-build-plugins) allows you to create and run an executable JAR that includes all code dependencies (fat JAR).

## Configure the Ktor plugin {id="configure-plugin"}
To build a fat JAR, you need to configure the Ktor plugin first:
1. Open the `build.gradle.kts` file and add the plugin to the `plugins` block:
   ```kotlin
   ```
   {src="snippets/deployment-ktor-plugin/build.gradle.kts" lines="5,8-9"}

2. Make sure the [main application class](server-dependencies.xml#create-entry-point) is configured:
   ```kotlin
   ```
   {src="snippets/deployment-ktor-plugin/build.gradle.kts" lines="11-13"}

3. Optionally, you can  configure the name of the fat JAR to be generated using the `ktor.fatJar` extension:
   ```kotlin
   ```
   {src="snippets/deployment-ktor-plugin/build.gradle.kts" lines="29-32,47"}


## Build and run a fat JAR {id="build"}

The Ktor plugin provides the following tasks for creating and running fat JARs:
- `buildFatJar`: builds a combined JAR of a project and runtime dependencies. You should see the `***-all.jar` file in the `build/libs` directory when this build completes.
- `runFatJar`: builds a fat JAR of a project and runs it.
