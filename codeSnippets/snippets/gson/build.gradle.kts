plugins {
    application
    alias(libs.plugins.kotlin.jvm)
}

application {
    mainClass.set("io.ktor.server.netty.EngineMain")
}

repositories {
    mavenCentral()
    maven { url = uri("https://redirector.kotlinlang.org/maven/ktor-eap") }
}

dependencies {
    implementation(libs.kotlin.stdlib.jdk8)
    implementation(ktorLibs.server.core)
    implementation(ktorLibs.server.netty)
    implementation(ktorLibs.server.compression)
    implementation(ktorLibs.server.callLogging)
    implementation(ktorLibs.server.defaultHeaders)
    implementation(ktorLibs.server.contentNegotiation)
    implementation(ktorLibs.serialization.gson)
    implementation(libs.logback.classic)
    testImplementation(ktorLibs.client.contentNegotiation)
    testImplementation(ktorLibs.server.testHost)
    testImplementation(libs.kotlin.test)
}
