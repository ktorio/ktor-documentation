# Basic authentication with UserHashedTableAuth

A sample project demonstrating how to use [basic HTTP authentication](https://ktor.io/docs/basic.html) to authenticate users whose credentials are stored in an in-memory table.
> This sample is a part of the [codeSnippets](../../README.md) Gradle project.

## Running
To run this sample, execute the following command in a repository's root directory:
```bash
./gradlew :auth-basic-hash-table:run
```

Then, open [http://localhost:8080/](http://localhost:8080/) in a web browser and enter one of the credentials specified in [hashedUserTable](src/main/kotlin/com/example/Application.kt).
