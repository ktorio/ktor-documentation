# Client I/O Interoperability

A sample Ktor client project showing how to bridge `ByteReadChannel` with kotlinx-io's `RawSource` using the `.asSource()` adapter, demonstrated on a streaming HTTP response.
> This sample is a part of the [codeSnippets](../../README.md) Gradle project.

## Running

To run this sample, execute the following command in a repository's root directory:

```bash
./gradlew :client-io-interop:run
```

The client downloads 1024 random bytes from `https://httpbin.org/bytes/1024`, wraps the response channel as a kotlinx-io `RawSource`, and prints the first byte read through the `RawSource` API.
