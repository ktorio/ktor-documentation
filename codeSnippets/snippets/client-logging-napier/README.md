# Client Logging - Napier

A sample Ktor project showing how to [log](https://ktor.io/docs/client-logging.html) HTTP calls using Napier.

## Running

To run this sample, execute the following command in a repository's root directory:

```bash
./gradlew :client-logging-napier:run
```

In the output, you'll see the request and response data:
```
FINEST: [VERBOSE] REQUEST: https://ktor.io/
METHOD: HttpMethod(value=GET)
COMMON HEADERS
-> Accept: */*
-> Accept-Charset: UTF-8
...
```