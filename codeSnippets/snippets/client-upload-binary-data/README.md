# Client: Uploading binary data

A sample Ktor project showing how to send a file as a binary data with a [request](https://ktor.io/docs/request.html).
> This sample is a part of the [codeSnippets](../../README.md) Gradle project.

## Running

First, you need to run a [server sample](../post-raw-data). Execute the following command in a repository's root folder:

```bash
./gradlew :post-raw-data:run
```

Then, execute the following command to upload a file to a server:

```bash
./gradlew :client-upload-binary-data:run
```