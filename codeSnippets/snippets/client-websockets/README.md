# Client WebSockets

A sample Ktor project showing how to use [WebSockets](https://ktor.io/docs/websocket-client.html) in a Ktor client.
> This sample is a part of the [codeSnippets](../../README.md) Gradle project.

## Running

Before running this sample, start a [Ktor server](../server-websockets) first:

```bash
./gradlew :server-websockets:run
```

Then, execute the following command to run a client sample:

```bash
./gradlew :client-websockets:run -q --console=plain
```
