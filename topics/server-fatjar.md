[//]: # (title: Creating fat JARs using the Ktor Gradle plugin)

<tldr>
<var name="example_name" value="deployment-ktor-plugin"/>
<include from="lib.topic" element-id="download_example"/>
</tldr>

<link-summary>Learn how to create and run an executable fat JAR using the Ktor Gradle plugin.</link-summary>

The [Ktor Gradle plugin](https://github.com/ktorio/ktor-build-plugins) allows you to create and run an executable JAR that includes all code dependencies (fat JAR).

## Configure the Ktor plugin {id="configure-plugin"}

To build a fat JAR, you need to configure the Ktor plugin first:

1. Open the `build.gradle.kts` file and add the plugin to the `plugins` block:
   ```kotlin
   ```
   {src="snippets/deployment-ktor-plugin/build.gradle.kts" include-lines="4,7-8"}

2. Make sure the [main application class](server-dependencies.topic#create-entry-point) is configured:
   ```kotlin
   ```
   {src="snippets/deployment-ktor-plugin/build.gradle.kts" include-lines="10-12"}

3. Optionally, you can configure the name of the fat JAR to be generated using the `ktor.fatJar` extension:
   ```kotlin
   ```
   {src="snippets/deployment-ktor-plugin/build.gradle.kts" include-lines="28-31,53"}

> The fat JAR creation feature is automatically disabled if you apply the Ktor Gradle plugin along with the Kotlin Multiplatform Gradle plugin.
> To use this feature with KMP, you'll need to move the Ktor plugin to a separate project:
> 1. Create a JVM-only project with the Ktor Gradle plugin applied as shown above.
> 2. Add the Kotlin Multiplatform project as a dependency to that JVM-only project.
>
> If in your case it is necessary to apply both Gradle plugins in the same project,
> let us know by leaving a comment in [KTOR-8464](https://youtrack.jetbrains.com/issue/KTOR-8464).
>
{style="warning"}

## Build and run a fat JAR {id="build"}

The Ktor plugin provides the following tasks for creating and running fat JARs:
- `buildFatJar`: builds a combined JAR of a project and runtime dependencies. You should see the `***-all.jar` file in the `build/libs` directory when this build completes.
- `runFatJar`: builds a fat JAR of a project and runs it.

> To learn how to minimize the generated JAR using ProGuard, refer to the [proguard](https://github.com/ktorio/ktor-documentation/tree/%ktor_version%/codeSnippets/snippets/proguard) sample.
