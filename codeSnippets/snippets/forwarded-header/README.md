# XForwardedHeaderSupport

A sample Ktor project showing how to use the [XForwardedHeaderSupport](https://ktor.io/docs/eap/forward-headers.html) plugin to get information about the original request.

## Running

To run this application under Docker Compose, follow the steps below:
1. Create a fat JAR containing a Ktor application:
   ```Bash
    ./gradlew :forwarded-header:shadowJar
   ```
2. Run the `docker-compose up` command:
   ```Bash
   docker compose --project-directory snippets/forwarded-header up
   ```
   Wait until Docker Compose pulls/builds the images and starts containers.
   Then, open [http://0.0.0.0/](http://0.0.0.0/) to see information about the proxy and original requests.