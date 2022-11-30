# Rate limiting

A sample Ktor project showing how to enable [rate limiting](https://ktor.io/docs/rate-limit.html) for incoming requests.
> This sample is a part of the [codeSnippets](../../README.md) Gradle project.

## Running

To run a sample, execute the following command in a repository's root directory:
```bash
./gradlew :rate-limit:run
```

Then, open and refresh one of the following pages to test rate limiting:
- [http://localhost:8080/](http://localhost:8080/)
- [http://localhost:8080/free-api](http://localhost:8080/free-api)
- [http://localhost:8080/protected-api?login=jetbrains](http://localhost:8080/protected-api?login=jetbrains)

For the latest endpoint, you can change the `login` query parameter to another value to see how a request weight affects rate limiting.
