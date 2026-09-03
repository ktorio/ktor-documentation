# HTTP/3 Netty

A sample project demonstrating how to enable [HTTP/3](https://ktor.io/docs/server-http3.html) with the Netty server engine.
> This sample is a part of the [codeSnippets](../../README.md) Gradle project.

## Run

From the project root, run the following command:

```bash
./gradlew :http3-netty:run
```

The server starts on [https://localhost:8443](https://localhost:8443).

Because the sample uses a self-signed certificate, your client may require you to trust or bypass the certificate
before connecting.