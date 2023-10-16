# Data Conversion

An example that demonstrates how to use the [data-conversion](https://ktor.io/docs/data-conversion.html) plugin.
> This sample is a part of the [codeSnippets](../../README.md) Gradle project.

## Run

To run the service, execute the following command in a repository's root directory:

```bash
./gradlew :data-conversion:run
```

## Test

Open [get.http](get.http) and run a `GET` request by clicking on the `play` button in the intelliJ IDEA editor.

The first `GET` request calls the `/article` endpoint with parameter `article=a`. The expected response is:

```
The received article is A
```

The second `GET` request calls the `/date` endpoint with parameter `date=20170501`. The expected response is:

```
The date is 2017-05-01
```