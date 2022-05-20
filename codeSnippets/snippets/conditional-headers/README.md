# Caching headers
A sample project demonstrating how to configure [conditional headers](https://ktor.io/docs/conditional-headers.html) in Ktor.
> This sample is a part of the [codeSnippets](../../README.md) Gradle project.

## Running
To run this sample, execute the following command in a repository's root directory:
```bash
./gradlew :conditional-headers:run
```

Then, open a browser's network tools for [http://localhost:8080/html-dsl](http://localhost:8080/html-dsl), choose `styles.css` and make sure that the `ETag` and `Last-Modified` headers are passed in a response.
