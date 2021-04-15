[//]: # (title: Maven Assembly plugin)

<microformat>
<p>
<control>Initial project</control>: <a href="https://github.com/ktorio/ktor-maven-sample/">ktor-maven-sample</a>
</p>
<p>
<control>Final project</control>: <a href="https://github.com/ktorio/ktor-maven-sample/tree/maven-assembly">ktor-maven-sample</a>, the <code>maven-assembly</code> branch
</p>
</microformat>

A Maven [Assembly plugin](http://maven.apache.org/plugins/maven-assembly-plugin/) provides the ability to combine project output into a single distributable archive that contains dependencies, modules, site documentation, and other files. In this topic, we'll show you how to build an assembly and run it for a Ktor application created in the [](Maven.xml) topic.

## Prerequisites {id="prerequisites"}
Before starting this tutorial, do the following:
* [Install IntelliJ IDEA](https://www.jetbrains.com/help/idea/installation-guide.html)
* Clone the [ktor-maven-sample](https://github.com/ktorio/ktor-maven-sample) repository.


## Configure the Assembly plugin {id="configure-plugin"}
To build an assembly, you need to configure the Assembly plugin first:
1. Open the [ktor-maven-sample](https://github.com/ktorio/ktor-maven-sample) project in IntelliJ IDEA.
1. Go to the `pom.xml` file and add `maven-assembly-plugin` to the `plugins` block as follows:
   ```xml
   ```
   {src="https://raw.githubusercontent.com/ktorio/ktor-maven-sample/maven-assembly/pom.xml" lines="55-76"}
   
   Among other settings, this configuration contains the `Main-Class` attribute of the JAR manifest for building an executable JAR.

## Build an assembly {id="build"}
To build an assembly for the application, open the terminal and execute the following command:
```Bash
mvn package
```
When this build completes, you should see the `mainModule-1.0-SNAPSHOT-jar-with-dependencies.jar` file in the `target` directory.


## Run the application {id="run"}
To run the [built application](#build):
1. Go to the `target` folder in a terminal.
1. Execute the following command to run the application:
   ```Bash
   java -jar mainModule-1.0-SNAPSHOT-jar-with-dependencies.jar
   ```
1. Wait until the following message is shown:
   ```
   [main] INFO  Application - Responding at http://0.0.0.0:8080
   ```
   You can click the link to open the application in a default browser:
   <img src="ktor_idea_new_project_browser.png" alt="Ktor app in a browser" width="430"/>




