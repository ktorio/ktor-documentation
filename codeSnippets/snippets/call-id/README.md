# CallId

A sample Ktor project showing how to use the [CallId](https://ktor.io/docs/call-id.html) plugin to trace requests.
> This sample is a part of the [codeSnippets](../../README.md) Gradle project.

## Running

To run a sample, execute the following command in a repository's root directory:
```bash
./gradlew :call-id:run
```
Then, open the [request.http](request.http) file and make the `GET` request with a randomly generated `X-Request-ID` header value. The application output will show the request ID in the log:

```
2021-10-26 18:22:55.164 [eventLoopGroupProxy-4-1] 13a3feb9-d83b-40ad-839a-d7d5cb147f54 INFO  Application - 200 OK: GET - /
```
