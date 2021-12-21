# Docker Compose

A sample Ktor project showing how to run a Ktor application consisting of a web frontend and a database backend under Docker Compose.

## Running

To run this application under Docker Compose, follow the steps below:
1. Create a fat JAR containing a Ktor application:
   ```Bash
    ./gradlew :tutorial-website-interactive-docker-compose:shadowJar
   ```
2. Open `docker-compose.yml` file and click `docker-compose up` in the gutter. Then, wait until Docker Compose pulls/builds the images and starts containers.