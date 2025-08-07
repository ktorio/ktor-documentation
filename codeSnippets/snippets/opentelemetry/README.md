# ktor-opentelemetry

Ktor sample demonstrating basic server routes instrumented with OpenTelemetry and a Jaeger backend via Docker.

## Prerequisites

You need to have Docker installed to run Jaeger a view traces.

To verify Docker, use the following command:

```shell
docker --version
```

## Run the server with Jaeger (Docker)

If you want to see traces in Jaeger, use the helper Gradle task. It will:
1. Start the Jaeger all-in-one container via docker-compose.
2. Start the Ktor server configured to export spans to OTLP gRPC at `http://localhost:4317`.

```shell
./gradlew runWithDocker
```

Jaeger UI will be available at: http://localhost:16686

- Select the service `opentelemetry-ktor-sample-server` in the dropdown to explore traces.

### Stopping Docker services

To stop and remove containers, use the following command:

```shell
./gradlew composeDown
```

## OpenTelemetry configuration

- The project uses OpenTelemetry autoconfiguration with a service name set in code.
- Metrics export is disabled to avoid warnings with the Jaeger all-in-one image.
- When running via Gradle JavaExec tasks, these environment variables are set automatically (see root `build.gradle.kts`):
  - `OTEL_METRICS_EXPORTER=none`
  - `OTEL_EXPORTER_OTLP_ENDPOINT=http://localhost:4317/`

