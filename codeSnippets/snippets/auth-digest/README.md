# Digest authentication
A sample project demonstrating how to use [digest HTTP authentication](https://ktor.io/docs/digest.html) in Ktor.
> This sample is a part of the [codeSnippets](../../README.md) Gradle project.

## Running
To run this sample, execute the following command in a repository's root directory:
```bash
./gradlew :auth-digest:run
```

Then, open [http://localhost:8080/](http://localhost:8080/) in a web browser and enter one of the credentials specified in [userTable](src/main/kotlin/com/example/Application.kt).