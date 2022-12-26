# Client caching

A sample Ktor project showing how to use the [HttpCache](https://ktor.io/docs/client-caching.html) plugin.
> This sample is a part of the [codeSnippets](../../README.md) Gradle project.

## Running

This sample uses the server from the [caching-headers](../caching-headers) example,
which has the `/index` route responding with a plain text with the configured `Cache-Control` header.

To run this sample, execute the following command:

```bash
./gradlew :client-caching:run
```

The client will cache the result of the first `GET` request and won't send a second request.

> This example uses the [Logging](https://ktor.io/docs/client-logging.html) plugin to show a request in a console.
