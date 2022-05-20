# HTTP/2 Jetty

A sample project demonstrating how to enable [HTTP/2](https://ktor.io/docs/advanced-http2.html) for the Jetty engine.

## Running

Execute this command in the repository's root directory to run this sample:

```bash
# macOS/Linux
./gradlew :http2-jetty:run

# Windows
.\gradlew.bat :http2-jetty:run
```

Then, open [https://0.0.0.0:8443](https://0.0.0.0:8443) to make sure that HTTP/2 is used.
