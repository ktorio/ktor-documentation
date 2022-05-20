# Caching headers
A sample project demonstrating how to configure [caching headers](https://ktor.io/docs/caching.html) in Ktor.
> This sample is a part of the [codeSnippets](../../README.md) Gradle project.

## Running
To run this sample, execute the following command in a repository's root directory:
```bash
./gradlew :caching-headers:run
```

Then, you can open a browser's network tools for [http://localhost:8080/html-dsl](http://localhost:8080/html-dsl) and reload this page to make sure the `styles.css` file is served from memory cache.