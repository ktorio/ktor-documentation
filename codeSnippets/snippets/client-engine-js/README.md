# Client - JavaScript engine

A sample Ktor project showing how to use the [JavaScript](https://ktor.io/docs/http-client-engines.html#js) client engine.
> This sample is a part of the [codeSnippets](../../README.md) Gradle project.

## Running

Before running this sample, update the [cors](../cors) server example to allow cross-origin requests from any host.
In the `Application.kt` file, add the `anyHost()` function call inside the `install(CORS)` block.
Then, run [cors](../cors):

```bash
./gradlew :cors:run
```

Open another terminal tab and run this sample by executing the following command:

```bash
./gradlew :client-engine-js:browserProductionRun --continuous
```

On the opened page, click the button to make a request.
