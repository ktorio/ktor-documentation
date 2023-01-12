# CORS

A sample project demonstrating how to configure [CORS](https://ktor.io/docs/cors.html) on a Ktor server. 

> This sample is a part of the [codeSnippets](../../README.md) Gradle project.

This project includes two modules:
- `backendModule`: accepts `POST` requests on the `8080` port.
- `frontendModule`: provides frontend working on the `8081` port. 
   This module is used to make a cross-origin `fetch` request to `backendModule`.

## Running
To run this sample, execute the following command in a repository's root directory:
```bash
./gradlew :cors:run
```

Then, open the [http://localhost:8081/](http://localhost:8081/) page and click the button to make a cross-origin request.
