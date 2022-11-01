# EngineMain - Custom environment

A sample Ktor project showing how to specify an environment using a [custom property](https://ktor.io/docs/configurations.html#custom-property).
> This sample is a part of the [codeSnippets](../../README.md) Gradle project.

## Running

Before running this sample, set the `KTOR_ENV` environment variable value:
```shell
# macOS/Linux
export KTOR_ENV=dev
   
# Windows
setx KTOR_ENV dev
```

To run a sample, execute the following command in a repository's root directory:
```bash
./gradlew :engine-main-custom-environment:run
```
