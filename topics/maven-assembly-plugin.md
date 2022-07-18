[//]: # (title: Creating fat JARs using the Maven Assembly plugin)

<tldr>
<p>
<control>Sample project</control>: <a href="https://github.com/ktorio/ktor-maven-sample/">ktor-maven-sample</a>
</p>
</tldr>

The Maven [Assembly plugin](http://maven.apache.org/plugins/maven-assembly-plugin/) provides the ability to combine project output into a single distributable archive that contains dependencies, modules, site documentation, and other files.


## Configure the Assembly plugin {id="configure-plugin"}
To build an assembly, you need to configure the Assembly plugin first:
1. Go to the `pom.xml` file and make sure the [main application class](server-dependencies.topic#create-entry-point) is specified:
   ```xml
   ```
   {src="https://raw.githubusercontent.com/ktorio/ktor-maven-sample/main/pom.xml" lines="10,17-18"}
   
   If you use [EngineMain](create_server.topic#engine-main) without the explicit `main` function, the application's main class depends on the used engine and might look as follows: `io.ktor.server.netty.EngineMain`.
2. Add `maven-assembly-plugin` to the `plugins` block as follows:
   ```xml
   ```
   {src="https://raw.githubusercontent.com/ktorio/ktor-maven-sample/main/pom.xml" lines="109-133"}

## Build an assembly {id="build"}
To build an assembly for the application, open the terminal and execute the following command:
```Bash
mvn package
```
When this build completes, you should see the `ktor-maven-sample-0.0.1-jar-with-dependencies.jar` file in the `target` directory.

> To learn how to use the resulting package to deploy your application using Docker, see the [](docker.md) help topic.


## Run the application {id="run"}
To run the [built application](#build):
1. Open the terminal and execute the following command to run the application:
   ```Bash
   java -jar target/ktor-maven-sample-0.0.1-jar-with-dependencies.jar
   ```
1. Wait until the following message is shown:
   ```Bash
   [main] INFO  Application - Responding at http://0.0.0.0:8080
   ```
   You can click the link to open the application in a default browser:
   <img src="ktor_idea_new_project_browser.png" alt="Ktor app in a browser" width="430"/>


