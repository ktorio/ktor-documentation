# opentelemetry

A standalone sample project demonstrating basic server routes instrumented with OpenTelemetry and a Jaeger backend
via Docker. The contents of this project are referenced in the [server-opentelemetry.md](server-opentelemetry.md)
and [client-opentelemetry.md](client-opentelemetry.md) topics.

## Prerequisites

You need to have [Docker](https://docs.docker.com/desktop/) installed to run Jaeger and view traces.

To verify Docker, use the following command:

```shell
docker --version
```

## Run the server with Jaeger (Docker)

1. To see traces in Jaeger, use the helper Gradle task:

  ```shell
  ./gradlew runWithDocker
  ```
  This will:
  - Start the Jaeger all-in-one container via docker-compose.
  - Start the Ktor server configured to export spans to OTLP gRPC at `http://localhost:4317`.

2. Access Jaeger UI at http://localhost:16686.
3. Select the service `opentelemetry-ktor-sample-server` in the dropdown to explore traces.

### Stop Docker services

To stop and remove containers, use the `composeDown` command:

```shell
./gradlew composeDown
```

## OpenTelemetry configuration

- The project uses OpenTelemetry autoconfiguration with a service name set in code.
- Metrics export is disabled to avoid warnings with the Jaeger all-in-one image.
- When running via Gradle JavaExec tasks, these environment variables are set automatically (see root `build.gradle.kts`):
  - `OTEL_METRICS_EXPORTER=none`
  - `OTEL_EXPORTER_OTLP_ENDPOINT=http://localhost:4317/`

