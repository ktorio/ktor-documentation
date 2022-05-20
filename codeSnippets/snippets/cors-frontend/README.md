# CORS - frontend

A sample application used to make a `fetch` [cross-origin request](https://ktor.io/docs/cors.html) to the [cors-backend](../cors-backend) server. To make a request cross-origin, this sample uses the non-default `8081` port as opposed to the `8080` port used by the [cors-backend](../cors-backend) sample.
> This sample is a part of the [codeSnippets](../../README.md) Gradle project.

## Running

Before running this sample, you need to run [cors-backend](../cors-backend):

```bash
./gradlew :cors-backend:run
```

To run this sample, execute the following command:

```bash
./gradlew :cors-frontend:run
```

Then, open the [http://localhost:8081/](http://localhost:8081/) page and click the button to make a cross-origin request.
