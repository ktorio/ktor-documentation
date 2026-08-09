val kotlin_version: String by project
val logback_version: String by project
val postgres_version: String by project
val exposed_version: String by project
val h2_version: String by project

plugins {
    application
    kotlin("jvm")
    alias(ktorLibs.plugins.ktor)
    id("org.jetbrains.kotlin.plugin.serialization") version "2.2.20"
}

group = "com.example"
version = "0.0.1"

application {
    mainClass.set("io.ktor.server.netty.EngineMain")
}

repositories {
    mavenCentral()
    maven { url = uri("https://redirector.kotlinlang.org/maven/ktor-eap") }
}

dependencies {
    implementation(ktorLibs.server.core)
    implementation("io.ktor:ktor-server-host-common:${ktorLibs.versions.ktor.get()}")
    implementation(ktorLibs.server.statusPages)
    implementation(ktorLibs.serialization.kotlinx.json)
    implementation(ktorLibs.server.contentNegotiation)
    implementation("org.postgresql:postgresql:$postgres_version")
    implementation("com.h2database:h2:$h2_version")
    implementation("org.jetbrains.exposed:exposed-core:$exposed_version")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposed_version")
    implementation("org.jetbrains.exposed:exposed-dao:$exposed_version")
    implementation(ktorLibs.server.netty)
    implementation("ch.qos.logback:logback-classic:$logback_version")
    implementation(ktorLibs.server.config.yaml)
    testImplementation(ktorLibs.server.testHost)
    testImplementation(ktorLibs.client.contentNegotiation)
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlin_version")
}
