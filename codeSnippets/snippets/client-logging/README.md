# Client Logging

A sample Ktor project showing how to log HTTP calls using the [Logging](https://ktor.io/docs/client-logging.html) plugin.
> This sample is a part of the [codeSnippets](../../README.md) Gradle project.

## Running

From the documentation repository's `codeSnippets` directory, enable development logging with:

```bash
./gradlew :client-logging:run --args="--development"
```

In the output, you'll see the request and response data:
```
REQUEST: https://ktor.io/
METHOD: GET
COMMON HEADERS
-> Accept: */*
-> Accept-Charset: UTF-8
...
```

The sample passes an `isDevelopment` setting to `createClient`. With `--development`, it selects
`LogLevel.HEADERS`; without the argument, it selects `LogLevel.NONE`:

```bash
./gradlew :client-logging:run
```

This is application-controlled configuration, not automatic environment detection. The sample sanitizes
`Authorization` headers. Sanitization does not remove sensitive values from URLs or bodies.
On JVM, the backend must allow INFO messages to display the default logger's output.
