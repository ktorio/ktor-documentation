[//]: # (title: Gradle Shadow plugin)

<microformat>
<p>
<control>Initial project</control>: <a href="https://github.com/ktorio/ktor-documentation/tree/%current-branch%/codeSnippets/snippets/engine-main">engine-main</a>
</p>
<p>
<control>Final project</control>: <a href="https://github.com/ktorio/ktor-documentation/tree/%current-branch%/codeSnippets/snippets/fatjar">fatjar</a>
</p>
</microformat>

<excerpt>Learn how to create an executable Fat JAR using the Gradle Shadow plugin.</excerpt>

The Gradle [Shadow](https://plugins.gradle.org/plugin/com.github.johnrengelman.shadow) plugin allows you to create an executable JAR that includes all code dependencies (fat JAR). In this topic, we'll show you how to generate and run a fat JAR for the [engine-main](https://github.com/ktorio/ktor-documentation/tree/%current-branch%/codeSnippets/snippets/engine-main) sample project.

## Configure the Shadow plugin {id="configure-plugin"}
To build a Fat JAR, you need to configure the Shadow plugin first:
1. Open the `build.gradle(.kts)` file and add the plugin to the `plugins` block:
   
   <tabs group="languages">
   <tab title="Gradle (Kotlin)" group-key="kotlin">

   ```kotlin
   ```
   {src="snippets/fatjar/build.gradle.kts" lines="5,8-9"}

   </tab>
   <tab title="Gradle (Groovy)" group-key="groovy">
   
   ```groovy
   plugins {
       id 'com.github.johnrengelman.shadow' version '%shadow_version%'
   }
   ```
   {interpolate-variables="true"}
   
   </tab>
   </tabs>

2. Add the `shadowJar` task. Note that the application main class specified for this task depends on the way used to [create a server](create_server.xml). 
   In the example below, `Main-Class` depends on the used engine and looks as follows: `io.ktor.server.netty.EngineMain`.

   <tabs group="languages">
   <tab title="Gradle (Kotlin)" group-key="kotlin">

   ```kotlin
   ```
   {src="snippets/fatjar/build.gradle.kts" lines="29-35"}

   </tab>
   <tab title="Gradle (Groovy)" group-key="groovy">

   ```groovy
   shadowJar {
       manifest {
           attributes 'Main-Class': 'io.ktor.server.netty.EngineMain'
       }
   }
   ```

   </tab>
   </tabs>


## Build a Fat JAR {id="build"}
To build a Fat JAR, open the terminal and execute the `shadowJar` task created in the [previous step](#configure-plugin):

<tabs group="os">
<tab title="Linux/MacOS" group-key="unix">
<code style="block" lang="Bash">./gradlew :fatjar:shadowJar</code>
</tab>
<tab title="Windows" group-key="windows">
<code style="block" lang="CMD">gradlew.bat :fatjar:shadowJar</code>
</tab>
</tabs>

When this build completes, you should see the `fatjar-all.jar` file in the `build/libs` directory.

> To learn how to use the resulting package to deploy your application using Docker, see the [](docker.md) help topic.


## Run the application {id="run"}
To run the [built application](#build):
1. Go to the `build/libs` folder in a terminal.
1. Execute the following command to run the application:
   ```Bash
   java -jar fatjar-all.jar
   ```
1. Wait until the following message is shown:
   ```Bash
   [main] INFO  Application - Responding at http://0.0.0.0:8080
   ```
   Open the link in a browser to see a running application:
   <img src="ktor_idea_new_project_browser.png" alt="Ktor app in a browser" width="430"/>
