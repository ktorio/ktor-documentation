# WebSockets chat server

A sample Ktor project showing how to create
a WebSocket chat server using Ktor.

> This sample is a part of the [codeSnippets](../../README.md) Gradle project.

## Run

To run the service, execute the following command in the repository's root directory:

```bash
./gradlew :tutorial-websockets-server:run
```

The service will be running at `http://0.0.0.0:8080`. To test the functionality,
connect multiple WebSocket clients to `ws://localhost:8080/ws` and send messages to observe broadcasting.

You can use the `websocket.http` file to run the requests directly from the IDE.

## Unit Testing

Unit tests are defined in `src/test/kotlin/com/example`. To run the tests, use the following command:

```bash
./gradlew :tutorial-websockets-server:test
```