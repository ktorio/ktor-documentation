
val kotlin_version: String by project
val logback_version: String by project

plugins {
    kotlin("jvm")
    alias(ktorLibs.plugins.ktor)
}

group = "com.example"
version = "0.0.1"

application {
    mainClass = "io.ktor.server.netty.EngineMain"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(ktorLibs.server.core)
    implementation(ktorLibs.server.netty)
    implementation("ch.qos.logback:logback-classic:$logback_version")
    implementation(ktorLibs.server.core)
    implementation(ktorLibs.server.htmx)
    implementation(ktorLibs.htmx)
    implementation(ktorLibs.htmx.html)
    implementation(ktorLibs.server.htmlBuilder)
    implementation(ktorLibs.server.config.yaml)
    testImplementation(ktorLibs.server.testHost)
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlin_version")
}
