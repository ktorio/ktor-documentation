# Shutdown URL

A sample Ktor project showing how to use the [Shutdown URL](https://ktor.io/docs/shutdown-url.html) plugin.

## Running

To run a sample, execute the following command in a repository's root directory:
```bash
./gradlew :shutdown-url:run
```

Then, open [http://0.0.0.0:8080/shutdown](http://0.0.0.0:8080/shutdown). Ktor should display a message in a console...

```text
WARN  Application - Shutdown URL was called: server is going down
```

... and stop a server.
