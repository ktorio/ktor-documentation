# Client Timeout

A sample Ktor project showing how to use the [HttpTimeout](https://ktor.io/docs/timeout.html) plugin.

## Running

Before running this sample, start a server from the [simulate-slow-server](../simulate-slow-server) example:
```bash
./gradlew :simulate-slow-server:run
```
In this example, a server adds a two-second delay to each response.

Then, send a client request by executing the following command in a repository's root directory:
```bash
./gradlew :client-timeout:run
```

Since `requestTimeoutMillis` property is set to 3000 in the `timeout` block, `HttpRequestTimeoutException` shouldn't be thrown. This exception will be raised if you set `requestTimeoutMillis` to 2000 or a smaller value.