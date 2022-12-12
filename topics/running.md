[//]: # (title: Running)

<show-structure for="chapter" depth="2"/>

<link-summary>
Learn how to run a server Ktor application.
</link-summary>

When running a Ktor server application, take into account the following specifics:
* The way used to [create a server](create_server.topic) affects whether you can override server parameters by passing command-line arguments when running a [packaged Ktor application](#package).
* Gradle/Maven build scripts should specify the main class name when starting a server using [EngineMain](create_server.topic#engine-main).
* Running your application inside a [servlet container](war.md) requires a specific servlet configuration.

In this topic, we'll take a look at these configuration specifics and show you how to run a Ktor application in IntelliJ IDEA and as a packaged application.


## Configuration specifics {id="specifics"}

### Configuration: code vs configuration file {id="code-vs-config"}

Running a Ktor application depends on the way you used to [create a server](create_server.topic) - `embeddedServer` or `EngineMain`:
* For `embeddedServer`, server parameters (such as a host address and port) are configured in code, so you cannot change these parameters when running an application.
* For `EngineMain`, Ktor loads its configuration from an external file that uses the `HOCON` or `YAML` format. Using this approach, you can run a [packaged application](#package) from a command line and override the required server parameters by passing corresponding [command-line arguments](Configurations.topic#command-line).


### Starting EngineMain: Gradle and Maven specifics {id="gradle-maven"}

If you use `EngineMain` to create a server, you need to specify the `main` function for starting a server with the desired [engine](Engines.md).
The [example](https://github.com/ktorio/ktor-documentation/tree/%ktor_version%/codeSnippets/snippets/engine-main) below demonstrates the `main` function used to run a server with the Netty engine:

```kotlin
```
{src="snippets/engine-main/src/main/kotlin/com/example/Application.kt" include-lines="7"}

To run a Ktor server using Gradle/Maven without configuring the engine inside the `main` function, you need to specify the main class name in the build script as follows:

<include from="Engines.md" element-id="main-class-set-engine-main"/>


### WAR specifics

Ktor allows you to [create and start a server](create_server.topic) with the desired engine (such as Netty, Jetty, or Tomcat) right in the application. In this case, your application has control over engine settings, connection, and SSL options.

In contrast to this approach, a servlet container should control the application lifecycle and connection settings. Ktor provides a special `ServletApplicationEngine` engine that delegates control over your application to a servlet container. You can learn how to configure your application from [](war.md#configure-war).


## Run an application {id="run"}
> Restarting a server during development might take some time. Ktor allows you to overcome this limitation by using [Auto-reload](Auto_reload.topic), which reloads application classes on code changes and provides a fast feedback loop.

### Run an application in IDEA {id="idea"}

To learn how to run a Ktor application in IntelliJ IDEA, see the [Run a Ktor application](https://www.jetbrains.com/help/idea/ktor.html#run_ktor_app) section in the IntelliJ IDEA documentation.


### Run an application using Gradle/Maven {id="gradle-maven-run"}

To run a Ktor application using Gradle or Maven, use corresponding plugins:
* [Application](https://docs.gradle.org/current/userguide/application_plugin.html) plugin for Gradle.
* [Exec](https://www.mojohaus.org/exec-maven-plugin/) plugin for Maven.



### Run a packaged application {id="package"}

Before deploying your application, you need to package it in one of the ways described in the [](deploy.md#packaging) section. 
Running a Ktor application from the resulting package depends on the package type and might look as follows:
* To run a Ktor server packaged in a fat JAR with and override the configured port, execute the following command:
   ```Bash
   java -jar sample-app.jar -port=8080
   ```
* To run an application packaged using the Gradle [Application](gradle-application-plugin.md) plugin, run a corresponding executable:

   <include from="gradle-application-plugin.md" element-id="run_executable"/>
  
* To run a servlet Ktor application, use the `run` task of the [Gretty](war.md#run) plugin.
