# Compression

A sample Ktor project showing how to compress outgoing content by using the [Compression](https://ktor.io/docs/compression.html) plugin.
> This sample is a part of the [codeSnippets](../../README.md) Gradle project.

## Running

To run a sample, execute the following command in a repository's root directory:
```bash
./gradlew :compression:run
```

Then, open a browser's network tools for [http://localhost:8080/](http://localhost:8080/) and make sure that the `deflate` encoding is applied to a response.