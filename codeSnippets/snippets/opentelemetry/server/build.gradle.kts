plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.ktor)
}

application {
    mainClass.set("com.example.ApplicationKt")
}

dependencies {
    implementation(project(":core"))
    implementation(libs.ktor.server.cio)
    implementation(libs.ktor.server.config.yaml)
    implementation(libs.ktor.server.websockets)
    implementation(libs.opentelemetry.ktor)
    implementation(libs.logback.classic)
    testImplementation(libs.ktor.server.test.host)
    testImplementation(libs.kotlin.test.junit)
}
