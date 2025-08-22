plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.ktor)
}

dependencies {
    implementation(libs.opentelemetry.sdk.extension.autoconfigure)
    implementation(libs.opentelemetry.semconv)
    implementation(libs.opentelemetry.exporter.otlp)
    implementation(libs.ktor.server.core)
}
