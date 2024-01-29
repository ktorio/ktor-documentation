# Data Conversion

An example that demonstrates how to use the [data-conversion](https://ktor.io/docs/data-conversion.html) plugin.
> This sample is a part of the [codeSnippets](../../README.md) Gradle project.

## Run

To run the service, execute the following command in a repository's root directory:

```bash
./gradlew :data-conversion:run
```

## Test

1. Open [get.http](get.http).
2. Run the `GET` request by clicking on the `play` button from the intelliJ IDEA editor.

The `GET` request calls the `/date` endpoint with parameter `date=20170501`. The expected response is:

```
The date is 2017-05-01
```