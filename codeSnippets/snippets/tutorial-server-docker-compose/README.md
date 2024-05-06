# Docker Compose

A sample Ktor project showing how to run a Ktor application consisting of a web frontend and a database backend
under [Docker Compose](https://ktor.io/docs/docker-compose.html).
> This sample is a part of the [codeSnippets](../../README.md) Gradle project.

## Running

To run this application under Docker Compose, follow the steps below:

1. Create a fat JAR containing a Ktor application:
   ```Bash
    ./gradlew :tutorial-server-docker-compose:buildFatJar
   ```
2. Run the `docker-compose up` command:
   ```Bash
   docker compose --project-directory snippets/tutorial-server-docker-compose up
   ```
   Wait until Docker Compose pulls/builds the images and starts containers.