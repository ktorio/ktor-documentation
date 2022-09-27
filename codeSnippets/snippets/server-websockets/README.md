# Server WebSockets

A sample Ktor project showing how to use [WebSockets](https://ktor.io/docs/websocket.html) in a Ktor server.
This project contains two WebSocket endpoints: `echo` and `chat`.
> This sample is a part of the [codeSnippets](../../README.md) Gradle project.

## Running

To run a sample, execute the following command in a repository's root directory:
```bash
./gradlew :server-websockets:run
```

Then, you can test an echo server using [Postman](https://learning.postman.com/docs/sending-requests/supported-api-frameworks/websocket/) or a [Ktor client](../client-websockets/README.md).
