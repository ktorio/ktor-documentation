# Client File Uploading

A sample Ktor project showing how to upload a file sent as a part of a multipart [request](https://ktor.io/docs/request.html).

## Running

First, you need to run a [server sample](../upload-file). Execute the following command in a repository's root folder:

```bash
./gradlew :upload-file:run
```

Then, execute the following command to upload a file to a server:

```bash
./gradlew :client-upload-file:run
```