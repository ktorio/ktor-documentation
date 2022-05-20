# Client basic authentication

A sample project demonstrating how to authenticate a client using basic [authentication](https://ktor.io/docs/auth.html).
> This sample is a part of the [codeSnippets](../../README.md) Gradle project.

## Running

Before running this sample, start the [auth-basic](../auth-basic) server sample with a resource protected using basic authentication:

```bash
./gradlew :auth-basic:run
```

Then, run this client sample:

```bash
./gradlew :client-auth-basic:run
```

A server should respond with the 'Hello, jetbrains!' message.