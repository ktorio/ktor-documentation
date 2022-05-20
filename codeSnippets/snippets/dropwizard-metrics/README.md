# Dropwizard Metrics

A sample Ktor project demonstrating the [Dropwizard Metrics](https://ktor.io/docs/dropwizard-metrics.html) plugin.
> This sample is a part of the [codeSnippets](../../README.md) Gradle project.

## Running

To run this sample, execute the following command in a repository's root directory:

```bash
./gradlew :dropwizard-metrics:run
```
 
Then, navigate to [http://localhost:8080/](http://localhost:8080/) to see the sample home page. Metrics will be logged every 10 seconds and displayed in a server's output. You can also explore the metrics exposed by `JmxReporter` using the [JConsole](https://docs.oracle.com/en/java/javase/17/management/using-jconsole.html).