# OpenAPI documentation

A sample Ktor project showing how to build OpenAPI documentation using routing annotations and the compiler 
extension of the Ktor Gradle plugin.

> This sample is a part of the [`codeSnippets`](../../README.md) Gradle project.

## Run the application

To run the application, execute the following command in the repository's root directory:

```bash
./gradlew :openapi-spec-gen:run
```

To view the OpenAPI documentation, navigate to the following URLs:

- [http://0.0.0.0:8080/docs.json](http://0.0.0.0:8080/docs.json) to view a JSON document of the API spec.
- [http://0.0.0.0:8080/openApi](http://0.0.0.0:8080/openApi) to view the OpenAPI UI for the API spec.
- [http://0.0.0.0:8080/swaggerUI](http://0.0.0.0:8080/swaggerUI) to view the Swagger UI for the API spec.
