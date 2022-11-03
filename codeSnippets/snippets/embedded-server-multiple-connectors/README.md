# embeddedServer - Multiple connectors

A sample Ktor server created using [embeddedServer](https://ktor.io/docs/create-server.html#embedded-server) with multiple connector endpoints.
> This sample is a part of the [codeSnippets](../../README.md) Gradle project.

## Running

To run a sample, execute the following command in a repository's root directory:
```bash
./gradlew :embedded-server-multiple-connectors:run
```

Navigate to [http://localhost:8080](http://localhost:8080) from a localhost or from another computer in the same network to see a public page.
Then, navigate to [http://127.0.0.1:9090](http://127.0.0.1:9090) to see a private page just accessible from localhost.
