# WebSockets chat client

A sample Ktor project showing how to create a [WebSocket chat client](https://ktor.io/docs/getting-started-ktor-client-chat.html) using Ktor.

## Running

Before running this sample, start a [Ktor server](../tutorial-websockets-server) first:

```bash
./gradlew :tutorial-websockets-server:run
```

Then, execute the following command to run a client sample:

```bash
./gradlew :tutorial-websockets-client:run -q --console=plain
```
