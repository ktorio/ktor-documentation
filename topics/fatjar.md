[//]: # (title: Gradle Shadow plugin)

<tldr>
<var name="example_name" value="fatjar"/>
<include src="lib.xml" include-id="download_example"/>
</tldr>

<link-summary>Learn how to create an executable Fat JAR using the Gradle Shadow plugin.</link-summary>

The Gradle [Shadow](https://plugins.gradle.org/plugin/com.github.johnrengelman.shadow) plugin allows you to create an executable JAR that includes all code dependencies (fat JAR).

## Configure the Shadow plugin {id="configure-plugin"}
To build a Fat JAR, you need to configure the Shadow plugin first:
1. Open the `build.gradle.kts` file and add the plugin to the `plugins` block:
   ```kotlin
   ```
   {src="snippets/fatjar/build.gradle.kts" lines="5,8-9"}

2. Make sure that the [main application class](server-dependencies.xml#create-entry-point) is configured:
   ```kotlin
   ```
   {src="snippets/fatjar/build.gradle.kts" lines="11-13"}


## Build a Fat JAR {id="build"}
To build a Fat JAR, open the terminal and execute the `shadowJar` task provided by the [Shadow plugin](#configure-plugin).

<tabs group="os">
<tab title="Linux/MacOS" group-key="unix">
<code style="block" lang="Bash">./gradlew shadowJar</code>
</tab>
<tab title="Windows" group-key="windows">
<code style="block" lang="CMD">gradlew.bat shadowJar</code>
</tab>
</tabs>

When this build completes, you should see the `***-all.jar` file in the `build/libs` directory.
For example, for the `fatjar` project a file name is `fatjar-all.jar`.

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
