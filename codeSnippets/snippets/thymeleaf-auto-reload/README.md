# Thymeleaf - Auto reload

A sample Ktor project showing how to enable auto-reload for [Thymeleaf](https://ktor.io/docs/thymeleaf.html) templates in [development mode](https://ktor.io/docs/development-mode.html).
> This sample is a part of the [codeSnippets](../../README.md) Gradle project.

## Running

To run a sample, execute the following command in a repository's root directory:
```bash
./gradlew :thymeleaf-auto-reload:run
```
Open the [http://localhost:8080/index](http://localhost:8080/index) page.
Then, you can update the [index.html](src/main/resources/templates/index.html) content and reload the [http://localhost:8080/index](http://localhost:8080/index) page to see auto-reload in action.