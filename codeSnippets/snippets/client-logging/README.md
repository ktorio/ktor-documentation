# Client Logging

A sample Ktor project showing how to log HTTP calls using the [Logging](https://ktor.io/docs/client-logging.html) plugin.
> This sample is a part of the [codeSnippets](../../README.md) Gradle project.

## Running

To run this sample, execute the following command in a repository's root directory:

```bash
./gradlew :client-logging:run
```

In the output, you'll see the request and response data:
```
REQUEST: https://ktor.io/
METHOD: HttpMethod(value=GET)
COMMON HEADERS
-> Accept: */*
-> Accept-Charset: UTF-8
...
```