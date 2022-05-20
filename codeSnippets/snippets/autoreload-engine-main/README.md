# Auto-reload - EngineMain

A sample Ktor project showing [Auto-reload](https://ktor.io/docs/auto-reload.html) functionality. Auto-reload works only in a development mode, which is enabled in [application.conf](src/main/resources/application.conf).
> This sample is a part of the [codeSnippets](../../README.md) Gradle project.

## Running

Since Auto-reload detects changes in output files, we need to enable automatic project rebuilding. To do this, execute the following command in a repository's root directory:
```bash
./gradlew -t :autoreload-engine-main:build
```

Follow the steps below to see Auto-reload in action:
1. Open another terminal tab and run a sample:
   ```bash
   ./gradlew :autoreload-engine-main:run
   ```
1. Open [http://localhost:8080/](http://localhost:8080/) to see a sample text in a browser.
1. Change a text passed to the  `respondText` method in [Application.kt](src/main/kotlin/com/example/Application.kt) and save a file.
1. Refresh the [http://localhost:8080/](http://localhost:8080/) page to see the updated text.
