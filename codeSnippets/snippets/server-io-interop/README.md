# Server I/O Interoperability

A sample Ktor server project showing how to bridge `ByteWriteChannel` with kotlinx-io's `RawSink` and Java's `OutputStream` using the `.asSink()`, `RawSink.asByteWriteChannel()`, and `OutputStream.asByteWriteChannel()` adapters across three routes.

This sample demonstrates server-side channel-level writes via `respondBytesWriter` and direct `respondBytes`, which complements the existing server-side channel-level read pattern in [`post-raw-data`](../post-raw-data).

> This sample is a part of the [codeSnippets](../../README.md) Gradle project.

## Running

To run this sample, execute the following command in a repository's root directory:

```bash
./gradlew :server-io-interop:run
```

Then exercise the routes:

```bash
# Convert ByteWriteChannel → RawSink: writes "Hello from kotlinx-io Sink!"
curl http://localhost:8080/sink

# Convert RawSink → ByteWriteChannel: writes a single byte (42) followed by "Hello via RawSink"
curl http://localhost:8080/raw-sink

# Convert OutputStream → ByteWriteChannel: writes "Hello from OutputStream-backed channel!"
curl http://localhost:8080/output-stream
```
