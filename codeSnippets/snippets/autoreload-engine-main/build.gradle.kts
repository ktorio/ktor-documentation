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
    implementation(libs.logback.classic)
    implementation(ktorLibs.server.config.yaml)
    testImplementation(libs.junit)
    testImplementation(libs.config)
    testImplementation(ktorLibs.server.testHost)
}
