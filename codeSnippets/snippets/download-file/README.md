# Download a File

A sample Ktor project showing how to send a file in a [response](https://ktor.io/docs/responses.html) using the `respondFile` function. The `Content-Disposition` header is added to a response to make a file downloadable.
> This sample is a part of the [codeSnippets](../../README.md) Gradle project.

## Running
To download a file, follow the steps below:
1. Open a terminal and run a sample:
   ```bash
   ./gradlew :download-file:run
   ```
1. Open the [http://localhost:8080/download](http://localhost:8080/download) page to download a sample file from a server.

