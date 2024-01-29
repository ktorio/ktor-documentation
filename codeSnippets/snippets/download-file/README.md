# Download a File

A sample Ktor project showing how to send a file in a [response](https://ktor.io/docs/responses.html) using
the `respondFile` function. The `Content-Disposition` header is added to a response to make a file downloadable.
> This sample is a part of the [codeSnippets](../../README.md) Gradle project.

## Running

To download a file, follow the steps below:

1. To start the application, execute the following command in the repository's root directory:
   ```bash
   ./gradlew :download-file:run
   ```
2. Navigate to [http://localhost:8080/download](http://localhost:8080/download) page to download a PNG file.
3. Navigate to  [http://localhost:8080/downloadFromPath](http://localhost:8080/downloadFromPath) page to download a TXT
   file.
