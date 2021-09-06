[//]: # (title: Gradle Shadow plugin)

<microformat>
<p>
<control>Initial project</control>: <a href="https://github.com/ktorio/ktor-gradle-sample/tree/main">ktor-gradle-sample</a>
</p>
</microformat>

The Gradle [Shadow](https://plugins.gradle.org/plugin/com.github.johnrengelman.shadow) plugin allows you to create an executable JAR that includes all code dependencies (fat JAR). In this topic, we'll show you how to generate and run a fat JAR for a Ktor application created in the [](Gradle.xml) topic.

## Prerequisites {id="prerequisites"}
Before starting this tutorial, clone the [ktor-gradle-sample](https://github.com/ktorio/ktor-gradle-sample) repository.

## Configure the Shadow plugin {id="configure-plugin"}
To build a Fat JAR, you need to configure the Shadow plugin first:
1. Open the `build.gradle(.kts)` file and add the plugin to the `plugins` block:
   
   <tabs group="languages">
   <tab title="Gradle (Groovy)" group-key="groovy">
   
   ```groovy
   plugins {
       id 'com.github.johnrengelman.shadow' version '%shadow_version%'
   }
   ```
   {interpolate-variables="true"}
   
   </tab>
   <tab title="Gradle (Kotlin)" group-key="kotlin">

   ```kotlin
   plugins {
       id("com.github.johnrengelman.shadow") version "%shadow_version%"
   }
   ```
   {interpolate-variables="true"}
   
   </tab>
   </tabs>

2. Add the `shadowJar` task:

   <tabs group="languages">
   <tab title="Gradle (Groovy)" group-key="groovy">

   ```groovy
   shadowJar {
       manifest {
           attributes 'Main-Class': 'com.example.ApplicationKt'
       }
   }
   ```

   </tab>
   <tab title="Gradle (Kotlin)" group-key="kotlin">

   ```kotlin
   tasks{
       shadowJar {
           manifest {
               attributes(Pair("Main-Class", "com.example.ApplicationKt"))
           }
       }
   }
   ```

   </tab>
   </tabs>

   If you use [EngineMain](create_server.xml#engine-main) without the explicit `main` function, your `Main-Class` depends on the used engine and might look as follows: `io.ktor.server.netty.EngineMain`.


## Build a Fat JAR {id="build"}
To build a Fat JAR, open the terminal and execute the `shadowJar` task created in the [previous step](#configure-plugin):

<tabs group="os">
<tab title="Linux/MacOS" group-key="unix">
<code style="block" lang="Bash">./gradlew shadowJar</code>
</tab>
<tab title="Windows" group-key="windows">
<code style="block" lang="CMD">gradlew.bat shadowJar</code>
</tab>
</tabs>

When this build completes, you should see the `ktor-gradle-sample-1.0-SNAPSHOT-all.jar` file in the `build/libs` directory.


## Run the application {id="run"}
To run the [built application](#build):
1. Go to the `build/libs` folder in a terminal.
1. Execute the following command to run the application:
   ```Bash
   java -jar ktor-gradle-sample-1.0-SNAPSHOT-all.jar
   ```
1. Wait until the following message is shown:
   ```
   [main] INFO  Application - Responding at http://0.0.0.0:8080
   ```
   You can click the link to open the application in a default browser:
   <img src="ktor_idea_new_project_browser.png" alt="Ktor app in a browser" width="430"/>
