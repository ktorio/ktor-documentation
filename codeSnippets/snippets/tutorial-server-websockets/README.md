# Ktor WebSockets Task Application

A Task Application built with Ktor by following the steps explained in
the [Create a WebSocket application](https://ktor.io/docs/server-create-websocket-application.html) tutorial.
> This sample is a part of the [codeSnippets](../../README.md) Gradle project.

## Run

To run the application, execute the following command in the repository's root directory:

```bash
./gradlew :tutorial-server-websockets:run
```

Then, you can navigate to the following URLs:

- [http://0.0.0.0:8080/static/index.html](http://0.0.0.0:8080/static/index.html) to view a list of tasks.
- [http://0.0.0.0:8080/static/wsClient.html](http://0.0.0.0:8080/static/wsClient.html) to create a new task.