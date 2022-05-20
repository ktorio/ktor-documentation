# DoubleReceive

A sample Ktor project showing how to use [DoubleReceive](https://ktor.io/docs/double-receive.html) to receive a request body already received by the [CallLogging](https://ktor.io/docs/call-logging.html) plugin.
> This sample is a part of the [codeSnippets](../../README.md) Gradle project.

## Running

To run a sample, execute the following command in a repository's root directory:

```bash
./gradlew :double-receive:run
```

Then, open [post.http](post.http) and make a `POST` request. A request body will be received and displayed twice:
- In the application output.
- In the HTTP Client. 

