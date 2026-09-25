plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(ktorLibs.plugins.ktor)
}

application {
    mainClass.set("com.example.ApplicationKt")
}

dependencies {
    implementation(project(":core"))
    implementation(ktorLibs.server.cio)
    implementation(ktorLibs.server.config.yaml)
    implementation(ktorLibs.server.websockets)
    implementation(libs.opentelemetry.ktor)
    implementation(libs.logback.classic)
    testImplementation(ktorLibs.server.testHost)
    testImplementation(libs.kotlin.test.junit)
}
