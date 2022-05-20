# Micrometer metrics

A sample Ktor project showing how to enable and configure [Micrometer metrics](https://ktor.io/docs/micrometer-metrics.html) in your Ktor server application.
> This sample is a part of the [codeSnippets](../../README.md) Gradle project.

## Running a sample

To run a sample, execute the following command in a repository's root directory:

```bash
./gradlew :micrometer-metrics:run
```
Then, open the [http://0.0.0.0:8080/](http://0.0.0.0:8080/) page. You can also open [http://0.0.0.0:8080/metrics](http://0.0.0.0:8080/metrics) to check that a scrape endpoint is available.

## Running the Prometheus server
To monitor the metrics of a running sample, you need to install Prometheus either locally or using Docker. Let's see how to install and run Prometheus using Docker:
1. Open the `micrometer-metrics` directory in a terminal.
2. Build an image with the following command:
   ```Bash
   docker build -t my-prometheus .
   ```
3. Then, run a server:
   ```Bash
   docker run -p 9090:9090 my-prometheus
   ```
4. Open the [http://0.0.0.0:9090/graph](http://0.0.0.0:9090/graph) page to access the Prometheus expression browser.
5. Enter the desired Ktor metric, for example, `ktor_http_server_requests_active`or `ktor_http_server_requests_seconds_count` to see monitoring results.