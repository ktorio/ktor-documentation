# opentelemetry

A standalone sample project demonstrating basic server routes instrumented with [OpenTelemetry](https://opentelemetry.io/) and a [Grafana](https://grafana.com/) backend
via Docker. The contents of this project are referenced in the [server-opentelemetry.md](server-opentelemetry.md)
and [client-opentelemetry.md](client-opentelemetry.md) topics.

## Prerequisites

You need to have [Docker](https://docs.docker.com/desktop/) installed to run Grafana and view traces.

To verify Docker installation, use the following command:

```shell
docker --version
```

To start Docker:
- **On macOS / Windows**: Make sure Docker Desktop is running.
- **On Linux**: Start the Docker service with the following command:
  ```shell
  sudo systemctl start docker
  ```

To ensure Docker is running, use the following command:

```shell
docker ps
```

## Run the server with Grafana (Docker)

1. To see traces in Grafana, use the helper Gradle task:

  ```shell
  ./gradlew runWithDocker
  ```
  This will:
  - Start the Grafana all-in-one container via docker-compose.
  - Start the Ktor server configured to export spans to OTLP gRPC at `http://localhost:4317`.

2. Run the client application to perform requests to the server:
```shell
./gradlew :client:run
```
3. Access Grafana UI at http://localhost:3000/.
4. In the left-hand navigation menu, go to Drilldown / Traces  to explore traces.

### Stop Docker services

To stop and remove containers, use the `composeDown` command:

```shell
./gradlew composeDown
```

## OpenTelemetry configuration

- The project uses OpenTelemetry autoconfiguration with a service name set in code.
- When running via Gradle JavaExec tasks, these environment variables are set automatically (see root `build.gradle.kts`):
  - `OTEL_METRICS_EXPORTER=otlp`
  - `OTEL_EXPORTER_OTLP_PROTOCOL=grpc`
  - `OTEL_EXPORTER_OTLP_ENDPOINT=http://localhost:4317/`

