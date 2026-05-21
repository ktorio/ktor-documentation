# Ktor WebSockets task application

A task application built with Ktor by following the steps explained in
the [Create a WebSocket application](https://ktor.io/docs/server-create-websocket-application.html) tutorial.

## Run

To run the application, run the following command from the project's root directory:

```bash
./gradlew run
```

Then, you can navigate to the following URLs:

- [http://0.0.0.0:8080/static/index.html](http://0.0.0.0:8080/static/index.html) to view a list of tasks.
- [http://0.0.0.0:8080/static/wsClient.html](http://0.0.0.0:8080/static/wsClient.html) to create a new task.