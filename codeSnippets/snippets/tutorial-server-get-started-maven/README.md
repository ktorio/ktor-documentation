# Getting started with a Ktor Server (Maven)

A sample project using the Maven build system, generated with the [Ktor Project Generator](https://start.ktor.io/).

For more information, see the topic
on [creating fat JARs using the Maven Assembly plugin](https://ktor.io/docs/maven-assembly-plugin.html).

# Build

To build an assembly for the application, execute the following command from the project's root:

```bash
mvn package
```

# Run

To run the application, execute the following command:

```Bash
java -jar target/tutorial-server-get-started-maven-0.0.1-jar-with-dependencies.jar
```

Then, navigate to [http://0.0.0.0:8080](http://0.0.0.0:8080) to see the output.