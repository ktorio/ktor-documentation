[//]: # (title: Gradle Application plugin)

<microformat>
<p>
<control>Sample project</control>: <a href="https://github.com/ktorio/ktor-gradle-sample/tree/main">ktor-gradle-sample</a>
</p>
</microformat>

A Gradle [Application plugin](https://docs.gradle.org/current/userguide/application_plugin.html) provides the ability to package applications, including code dependencies and generated start scripts. In this topic, we'll show you how to package and run a Ktor application created in the [](Gradle.xml) topic.

## Prerequisites {id="prerequisites"}
Before starting this tutorial, do the following:
* [Install IntelliJ IDEA](https://www.jetbrains.com/help/idea/installation-guide.html)
* Clone the [ktor-gradle-sample](https://github.com/ktorio/ktor-gradle-sample) repository.


## Apply the Application plugin and configure the main class {id="apply-plugin"}
To package an application, you need to apply the Application plugin first.
1. Open the [ktor-gradle-sample](https://github.com/ktorio/ktor-gradle-sample) project in IntelliJ IDEA.
1. Make sure that the `build.gradle` file contains the following code:
   ```groovy
   ```
   {src="https://raw.githubusercontent.com/ktorio/ktor-gradle-sample/main/build.gradle" lines="1,3-4,8,9-11"}
    * The `id 'application'` line applies the Application plugin. 
    * The `mainClass` property is used to configure the main class of the application.


## Package the application {id="package"}
The Application plugin provides various ways for packaging the application, for example:
* The `installDist` task installs the application with all runtime dependencies and start scripts into a specified directory.
* The `distZip` and `distTar` tasks create full distribution archives.

In this topic, we'll use `installDist`:
1. Open the [terminal](https://www.jetbrains.com/help/idea/terminal-emulator.html).
1. Run the `installDist` in one of the following ways depending on your operating system:
   <tabs>
   <tab title="Linux/MacOS">
   <code style="block" lang="Bash">./gradlew installDist</code>
   </tab>
   <tab title="Windows">
   <code style="block" lang="CMD">gradlew.bat installDist</code>
   </tab>
   </tabs>
   
   Gradle will create an image of the application in the `build/install/ktor-gradle-sample` folder. 

## Run the application {id="run"}
To run the [packaged application](#package):
1. Go to the `build/install/ktor-gradle-sample` folder in a terminal.
1. Depending on your operating system, run the `ktor-gradle-sample` or `ktor-gradle-sample.bat` executable:
   <tabs>
   <tab title="Linux/MacOS">
   <code style="block" lang="Bash">./ktor-gradle-sample</code>
   </tab>
   <tab title="Windows">
   <code style="block" lang="CMD">ktor-gradle-sample.bat</code>
   </tab>
   </tabs>
1. Wait until the following message is shown:
   ```
   [main] INFO  Application - Responding at http://0.0.0.0:8080
   ```
   You can click the link to open the application in a default browser:
   <img src="ktor_idea_new_project_browser.png" alt="Ktor app in a browser" width="430"/>