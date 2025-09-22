plugins {
    id("com.avast.gradle.docker-compose") version "0.17.1"
}

subprojects {
    group = "com.example"
    version = "0.0.1"

    repositories {
        mavenCentral()
    }
}

dockerCompose {
    useComposeFiles.add("docker/docker-compose.yml")
}

tasks.register("runWithDocker") {
    dependsOn("composeUp", ":server:run")
}

project(":server").setEnvironmentVariablesForOpenTelemetry()
project(":client").setEnvironmentVariablesForOpenTelemetry()

fun Project.setEnvironmentVariablesForOpenTelemetry() {
    tasks.withType<JavaExec> {
        environment("OTEL_METRICS_EXPORTER", "otlp")
        environment("OTEL_EXPORTER_OTLP_PROTOCOL", "grpc")
        environment("OTEL_EXPORTER_OTLP_ENDPOINT", "http://localhost:4317/")
    }
}
