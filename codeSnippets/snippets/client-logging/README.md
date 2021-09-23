# Client Logging

A sample Ktor project showing how to log HTTP calls using the [Logging](https://ktor.io/docs/client-logging.html) plugin.

## Running

To run this sample, execute the following command in a repository's root directory:

```bash
./gradlew :client-logging:run
```

In the output, you'll see the request and response data:
```
INFO io.ktor.client.HttpClient - REQUEST: https://ktor.io/
INFO io.ktor.client.HttpClient - METHOD: HttpMethod(value=GET)
INFO io.ktor.client.HttpClient - COMMON HEADERS
INFO io.ktor.client.HttpClient - -> Accept: */*
INFO io.ktor.client.HttpClient - -> Accept-Charset: UTF-8
...
```