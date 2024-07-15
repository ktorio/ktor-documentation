# WebSockets chat server

A sample Ktor project showing how to create a [WebSocket chat server](https://ktor.io/docs/creating-web-socket-chat.html) using Ktor.
> This sample is a part of the [codeSnippets](../../README.md) Gradle project.

## Running

To run a sample, execute the following command in a repository's root directory:
```bash
./gradlew :tutorial-websockets-server:run
```
#### Testing 

1. Unit Testing:
    - Use `testApplication` from `io.ktor.server.testing`
    - Simulate WebSocket connections with `createClient { install(WebSockets) }`
    - Verify message broadcasting across multiple clients

```bash
./gradlew :tutorial-websockets-server:test
```

2. Manual Testing:
    - Run the server and connect multiple WebSocket clients to `ws://localhost:8080/ws`
    - Send messages from different clients to observe broadcasting