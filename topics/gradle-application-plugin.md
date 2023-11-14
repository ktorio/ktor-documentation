[//]: # (title: Creating the application distribution)

<tldr>
<var name="example_name" value="deployment-ktor-plugin"/>
<include from="lib.topic" element-id="download_example"/>
</tldr>

The [Ktor Gradle plugin](https://github.com/ktorio/ktor-build-plugins) automatically applies the 
Gradle [Application plugin](https://docs.gradle.org/current/userguide/application_plugin.html), 
which provides the ability to package applications, including code dependencies and generated start scripts. 
In this topic, we'll show you how to package and run a Ktor application.


## Configure the Ktor plugin {id="configure-plugin"}
To create the application distribution, you need to apply the Ktor plugin first:
1. Open the `build.gradle.kts` file and add the plugin to the `plugins` block:
   ```kotlin
   ```
   {src="snippets/deployment-ktor-plugin/build.gradle.kts" include-lines="4,7-8"}

2. Make sure the [main application class](server-dependencies.topic#create-entry-point) is configured:
   ```kotlin
   ```
   {src="snippets/deployment-ktor-plugin/build.gradle.kts" include-lines="10-12"}


## Package the application {id="package"}
The Application plugin provides various ways for packaging the application, for example, the `installDist` task installs the application with all runtime dependencies and start scripts. To create full distribution archives, you can use the `distZip` and `distTar` tasks.

In this topic, we'll use `installDist`:
1. Open the terminal.
2. Run the `installDist` task in one of the following ways depending on your operating system:
   
   <tabs group="os">
   <tab title="Linux/macOS" group-key="unix">
   <code-block>./gradlew installDist</code-block>
   </tab>
   <tab title="Windows" group-key="windows">
   <code-block>gradlew.bat installDist</code-block>
   </tab>
   </tabs>

   The Application plugin will create an image of the application in the `build/install/<project_name>` folder. 


## Run the application {id="run"}
To run the [packaged application](#package):
1. Go to the `build/install/<project_name>/bin` folder in the terminal.
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