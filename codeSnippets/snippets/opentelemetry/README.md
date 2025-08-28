# opentelemetry

A standalone sample project demonstrating basic server routes instrumented with [OpenTelemetry](https://opentelemetry.io/) and a [Grafana](https://grafana.com/) backend
via Docker. The contents of this project are referenced in the [server-opentelemetry.md](server-opentelemetry.md)
and [client-opentelemetry.md](client-opentelemetry.md) topics.

## Prerequisites

You need to have [Docker](https://docs.docker.com/desktop/) installed to run Jaeger and view traces.

To verify Docker, use the following command:

```shell
docker --version
```

## Run the server with Grafana (Docker)

1. To see traces in Grafana, use the helper Gradle task:

  ```shell
  ./gradlew runWithDocker
  ```
  This will:
  - Start the Grafana all-in-one container via docker-compose.
  - Start the Ktor server configured to export spans to OTLP gRPC at `http://localhost:4317`.

2. Access Grafana UI at http://localhost:3000.
3. Select the service `opentelemetry-ktor-sample-server` in the dropdown to explore traces.

### Stop Docker services

To stop and remove containers, use the `composeDown` command:

```shell
./gradlew composeDown
```

## OpenTelemetry configuration

- The project uses OpenTelemetry autoconfiguration with a service name set in code.
- When running via Gradle JavaExec tasks, these environment variables are set automatically (see root `build.gradle.kts`):
  - `OTEL_METRICS_EXPORTER=otlp`
  - `OTEL_EXPORTER_OTLP_ENDPOINT=http://localhost:4317/`

