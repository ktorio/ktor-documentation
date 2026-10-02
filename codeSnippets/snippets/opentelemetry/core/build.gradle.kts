plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(ktorLibs.plugins.ktor)
}

dependencies {
    implementation(libs.opentelemetry.sdk.extension.autoconfigure)
    implementation(libs.opentelemetry.semconv)
    implementation(libs.opentelemetry.exporter.otlp)
    implementation(ktorLibs.server.core)
}
