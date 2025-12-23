# OpenAPI documentation

A sample Ktor project showing how to build OpenAPI documentation using routing annotations and the compiler 
extension of the Ktor Gradle plugin.

> This sample is a part of the [`codeSnippets`](../../README.md) Gradle project.

## Generate the OpenAPI specification

```bash
./gradlew :openapi-spec-gen:buildOpenApi
```

The generated OpenAPI specification is located in `build/ktor/openapi/generated.json`.

## Run the application

To run the application, execute the following command in the repository's root directory:

```bash
./gradlew :openapi-spec-gen:run
```

Navigate to [http://0.0.0.0:8080/docs](http://0.0.0.0:8080/docs) to access the OpenAPI documentation.
