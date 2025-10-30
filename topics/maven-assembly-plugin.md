[//]: # (title: Creating fat JARs using the Maven Assembly plugin)

<tldr>
<p>
<control>Sample project</control>: <a href="https://github.com/ktorio/ktor-documentation/tree/%ktor_version%/codeSnippets/snippets/tutorial-server-get-started-maven">tutorial-server-get-started-maven</a>
</p>
</tldr>

The [Maven Assembly plugin](http://maven.apache.org/plugins/maven-assembly-plugin/) provides the ability to combine
project output into a single distributable archive that contains dependencies, modules, site documentation, and other
files.

## Configure the Assembly plugin {id="configure-plugin"}

To build an assembly, you need to configure the Assembly plugin first:

1. Navigate to the **pom.xml** file and ensure
   the [main application class](server-dependencies.topic#create-entry-point) is
   specified:
   ```xml
   ```
   {src="snippets/tutorial-server-get-started-maven/pom.xml" include-lines="10,18-19"}

    In this example `EngineMain` is used to create a server, so the application's main class depends on
    the used engine. If you use [embeddedServer](server-create-and-configure.topic#embedded-server), the application's
    main class is: `com.example.ApplicationKt`.

2. Add the `maven-assembly-plugin` to the `plugins` block:
   ```xml
   ```
   {src="snippets/tutorial-server-get-started-maven/pom.xml" include-lines="116-140"}

## Build an assembly {id="build"}

To build an assembly for the application, open the terminal and execute the following command:

```Bash
mvn package
```

This will create a new **target** directory for the assembly, including the **.jar** files.

> To learn how to use the resulting package to deploy your application using Docker, see the [](docker.md) help topic.

## Run the application {id="run"}

To run the built application, follow the steps below:

1. In a new terminal window, use the `java -jar` command to run the application. For the sample project it looks
   like the following:
   ```Bash
   java -jar target/tutorial-server-get-started-maven-0.0.1-jar-with-dependencies.jar
   ```
2. You will see a confirmation message once your app is running:
   ```Bash
   [main] INFO  Application - Responding at http://0.0.0.0:8080
   ```
3. Click on the URL link to open the application in your default browser:

   <img src="server_get_started_ktor_sample_app_output.png" alt="Output of generated ktor project"
                     border-effect="rounded" width="706"/>


