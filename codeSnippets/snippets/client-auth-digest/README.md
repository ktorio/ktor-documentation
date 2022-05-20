# Client digest authentication

A sample project demonstrating how to authenticate a client using digest [authentication](https://ktor.io/docs/auth.html).
> This sample is a part of the [codeSnippets](../../README.md) Gradle project.

## Running

Before running this sample, start the [auth-digest](../auth-digest) server sample with a resource protected using digest authentication:

```bash
./gradlew :auth-digest:run
```

Then, run this client sample:

```bash
./gradlew :client-auth-digest:run
```

A server should respond with the 'Hello, jetbrains!' message.