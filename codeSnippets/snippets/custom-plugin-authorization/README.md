# Custom plugin - Authorization

A sample Ktor project demonstrating how to create a [custom plugin](https://ktor.io/docs/custom-plugins.html) for authorizing users by handling the [AuthenticationChecked](src/main/kotlin/com/example/plugins/AuthorizationPlugin.kt) hook.
> This sample is a part of the [codeSnippets](../../README.md) Gradle project.

## Running

To run a sample, execute the following command in a repository's root directory:

```bash
./gradlew :custom-plugin-authorization:run
```

Then, use the `admin` or `jetbrains` username with a non-empty password to log in to the following resources:
- [http://localhost:8080/admin](http://localhost:8080/admin)
- [http://localhost:8080/profile](http://localhost:8080/profile)