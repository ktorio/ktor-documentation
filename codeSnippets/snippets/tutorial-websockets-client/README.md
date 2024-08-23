# WebSockets chat client

A sample Ktor project showing how to create a [WebSocket chat client](https://ktor.io/docs/getting-started-ktor-client-chat.html) using Ktor.
> This sample is a part of the [codeSnippets](../../README.md) Gradle project.

## Running

Before running this sample, start a [Ktor server](../tutorial-websockets-server) first:

```bash
./gradlew :tutorial-server-websockets:run
```

Then, execute the following command to run a client sample:

```bash
./gradlew :tutorial-websockets-client:run -q --console=plain
```
