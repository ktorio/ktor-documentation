# Client - Handle non-2xx exceptions

A sample Ktor project showing how to customize default validation and handle exceptions for non-2xx responses in a specific way using [HttpCallValidator](https://ktor.io/docs/response-validation.html).
> This sample is a part of the [codeSnippets](../../README.md) Gradle project.

## Running

To run this sample, execute the following command in a repository's root directory:

```bash
./gradlew :client-validate-non-2xx-response:run
```

A client should raise a `MissingPageException` for a requested page.

