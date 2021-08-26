# Server WebSockets

A sample Ktor project showing how to use [WebSockets](https://ktor.io/docs/websocket.html) in a Ktor server.
This project contains two WebSocket endpoints: `echo` and `chat`.

## Running

To run a sample, execute the following command in a repository's root directory:
```bash
./gradlew :server-websockets:run
```

Then, you can test WebSocket sessions using [Postman](https://learning.postman.com/docs/sending-requests/supported-api-frameworks/websocket/) or a [Ktor client](../client-websockets/README.md).

### Echo server
To test an echo server, connect the `ws://0.0.0.0:8080/echo` endpoint and send test messages.

### Chat server
To test a chat server, connect two separate clients to the `ws://0.0.0.0:8080/chat` endpoint and send test messages.