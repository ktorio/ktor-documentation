# CORS - backend

A sample project demonstrating how to configure [CORS](https://ktor.io/docs/cors.html) on a Ktor server. You can test this sample using the [cors-frontend](../cors-frontend) application, which allows you to make a `fetch` cross-origin request.
> This sample is a part of the [codeSnippets](../../README.md) Gradle project.

## Running
To run this sample, execute the following command in a repository's root directory:
```bash
./gradlew :cors-backend:run
```

To run the [cors-frontend](../cors-frontend) sample, execute the following command:

```bash
./gradlew :cors-frontend:run
```

Then, open the [http://localhost:8081/](http://localhost:8081/) page and click the button to make a cross-origin request.
