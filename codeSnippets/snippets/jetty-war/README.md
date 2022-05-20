# Jetty WAR

A sample Ktor project running as a [WAR](https://ktor.io/docs/war.html) application under the [Jetty](https://www.eclipse.org/jetty/) application server.
> This sample is a part of the [codeSnippets](../../README.md) Gradle project.

## Running

To run this sample, execute the following command in a repository's root directory:

```bash
./gradlew :jetty-war:run
```
 
Then, navigate to [http://localhost:8080/](http://localhost:8080/) to see the sample home page.


## Deploying and running

To build and run this application with Docker, execute the following commands:

```bash
./gradlew :jetty-war:war
docker build -t my-application snippets/jetty-war
docker run -p 8080:8080 my-application
```

Then, navigate to [http://localhost:8080/jetty-war](http://localhost:8080/jetty-war) to see the sample home page.