plugins {
    alias(libs.plugins.kotlinJvm)
    alias(ktorLibs.plugins.ktor)
}

group = "com.example.ktor"
version = "1.0.0"
application {
    mainClass = "com.example.ktor.ApplicationKt"
}

dependencies {
    //...
    api(projects.core)
    implementation(libs.logback)
    implementation(ktorLibs.server.core)
    implementation(ktorLibs.server.netty)
    implementation(ktorLibs.serialization.kotlinx.json)
    implementation(ktorLibs.server.contentNegotiation)
    implementation(ktorLibs.server.cors)
    testImplementation(ktorLibs.server.testHost)
    testImplementation(libs.kotlin.testJunit)
}