# XForwardedHeader

A sample Ktor project showing how to use the [XForwardedHeader](https://ktor.io/docs/forward-headers.html) plugin to get information about the original request.
> This sample is a part of the [codeSnippets](../../README.md) Gradle project.

## Running

To run this application under Docker Compose, follow the steps below:
1. Create a fat JAR containing a Ktor application:
   ```Bash
   ./gradlew :forwarded-header:buildFatJar
   ```
2. Run the `docker compose up` command:
   ```Bash
   docker compose --project-directory snippets/forwarded-header up
   ```
   Wait until Docker Compose pulls/builds the images and starts containers.
   Then, open [http://localhost/](http://localhost/) to see information about the proxy and original requests.