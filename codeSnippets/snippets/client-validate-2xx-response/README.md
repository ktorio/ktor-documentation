# Client - Validate 2xx responses

A sample Ktor project showing how to add custom validation of 2xx responses by using [HttpCallValidator](https://ktor.io/docs/response-validation.html). In this example, a client raises a `CustomResponseException` when error details come in a 2xx response in a JSON format.
> This sample is a part of the [codeSnippets](../../README.md) Gradle project.

## Running

Before running this sample, you need to run a server that sends a sample error as a JSON object.
This sample uses the modified version of the [json-kotlinx](../json-kotlinx) example with the `/error` endpoint: [Server.kt](../client-validate-2xx-response/src/main/kotlin/com/example/server/Server.kt).

To run this sample, execute the following command in a repository's root directory:

```bash
./gradlew :client-validate-2xx-response:run
```
