# Client Retry

A sample Ktor project showing how to use the [HttpRequestRetry](https://ktor.io/docs/client-retry.html) plugin.
> This sample is a part of the [codeSnippets](../../README.md) Gradle project.

## Running

This client sample uses the server from the [simulate-slow-server](../simulate-slow-server) example.
The server sample has the `/error` route that returns the `200 OK` response from the third attempt only.

To see `HttpRequestRetry` in action, run this example by executing the following command:

```bash
./gradlew :client-retry:run
```

The client will send three consequent requests automatically to get a success response from the server.

> Note that this example uses the [Logging](https://ktor.io/docs/client-logging.html) plugin to show all requests in a console.
