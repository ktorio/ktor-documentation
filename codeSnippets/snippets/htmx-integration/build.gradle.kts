
val kotlin_version: String by project
val logback_version: String by project

plugins {
    kotlin("jvm")
    id("io.ktor.plugin") version "3.3.2"
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
    implementation("io.ktor:ktor-server-core-jvm")
    implementation("io.ktor:ktor-server-netty")
    implementation("ch.qos.logback:logback-classic:$logback_version")
    implementation("io.ktor:ktor-server-core")
    implementation("io.ktor:ktor-server-htmx")
    implementation("io.ktor:ktor-htmx")
    implementation("io.ktor:ktor-htmx-html")
    implementation("io.ktor:ktor-server-html-builder")
    implementation("io.ktor:ktor-server-config-yaml")
    testImplementation("io.ktor:ktor-server-test-host")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlin_version")
}
