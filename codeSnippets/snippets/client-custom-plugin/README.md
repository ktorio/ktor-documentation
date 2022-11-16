# Client - Custom plugin

A sample Ktor project showing how to create [custom plugins](https://ktor.io/docs/client-custom-plugins.html) for the client.
> This sample is a part of the [codeSnippets](../../README.md) Gradle project.

## Running

To run this sample, execute the following command in a repository's root directory:

```bash
./gradlew :client-custom-plugin:run
```

Each of the installed plugins should add some information to output, for example:
- A custom header added to each request.
- A list of request and response headers.
- The time between sending a request and receiving a response.
