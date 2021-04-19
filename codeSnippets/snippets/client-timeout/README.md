# Client Timeout

A sample Ktor project showing how to use the [HttpTimeout](https://ktor.io/docs/timeout.html) feature.

## Running

Before running this sample, start a server from the [simulate-slow-server](https://github.com/ktorio/ktor-documentation/tree/main/codeSnippets/snippets/simulate-slow-server) example:
```bash
./gradlew :simulate-slow-server:run
```
In this example, a server adds a two second delay to each response.

Then, run this sample by executing the following command in a repository's root directory:
```bash
./gradlew :client-timeout:run
```
