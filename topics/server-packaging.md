[//]: # (title: Creating the application distribution)

<tldr>
<var name="example_name" value="deployment-ktor-plugin"/>
<include from="lib.topic" element-id="download_example"/>
</tldr>

The [Ktor Gradle plugin](https://github.com/ktorio/ktor-build-plugins) automatically applies the 
Gradle [Application plugin](https://docs.gradle.org/current/userguide/application_plugin.html), 
which provides the ability to package applications, including code dependencies and generated start scripts.

In this topic, you will learn how to package and run a Ktor application.

## Configure the Ktor plugin {id="configure-plugin"}

To create the application distribution, you need to apply the Ktor plugin first:
1. Open the <path>build.gradle.kts</path> file and add the plugin to the `plugins {}` block:
   ```kotlin
   ```
   {src="snippets/deployment-ktor-plugin/build.gradle.kts" include-lines="1,4-5"}

   <include from="lib.topic" element-id="code_with_ktor_version_catalog"/>

2. Ensure that the [main application class](server-dependencies.topic#create-entry-point) is configured:
   ```kotlin
   ```
   {src="snippets/deployment-ktor-plugin/build.gradle.kts" include-lines="7-9"}


## Package the application {id="package"}

The Application plugin provides various ways for packaging your application. For example, the `installDist` task installs
the application with all runtime dependencies and start scripts. To create full distribution archives, you can use the
`distZip` and `distTar` tasks.

To install your application with all runtime dependencies and start scripts, open a new terminal window and run the
`installDist` task:
   
<tabs group="os">
<tab title="Linux/macOS" group-key="unix">
<code-block>./gradlew installDist</code-block>
</tab>
<tab title="Windows" group-key="windows">
<code-block>gradlew.bat installDist</code-block>
</tab>
</tabs>

The Application plugin creates an image of the application in the <path>build/install/<project_name></path> folder. 

## Run the application {id="run"}

To run the [packaged application](#package), follow the steps below:

1. In the terminal, navigate to the <path>build/install/<project_name>/bin</path> folder.
2. Depending on your operating system, run the `<project_name>` or `<project_name>.bat` executable, for example:

   <snippet id="run_executable">
   <tabs group="os">
   <tab title="Linux/macOS" group-key="unix">
   <code-block>./ktor-sample</code-block>
   </tab>
   <tab title="Windows" group-key="windows">
   <code-block>ktor-sample.bat</code-block>
   </tab>
   </tabs>
   </snippet>
   
3. Wait until the following message is shown:
   ```Bash
   [main] INFO  Application - Responding at http://0.0.0.0:8080
   ```
   Open the link in a browser to see a running application:

   <img src="ktor_idea_new_project_browser.png" alt="Ktor app in a browser" width="430"/>