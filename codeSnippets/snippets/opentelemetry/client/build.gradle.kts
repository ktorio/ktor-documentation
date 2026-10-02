plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(ktorLibs.plugins.ktor)
}

application {
    mainClass.set("com.example.ClientKt")
}

dependencies {
    implementation(project(":core"))
    implementation(ktorLibs.client.core)
    implementation(ktorLibs.client.cio)
    implementation(ktorLibs.client.websockets)
    implementation(libs.opentelemetry.ktor)
}