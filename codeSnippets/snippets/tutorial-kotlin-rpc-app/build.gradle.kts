plugins {
    kotlin("jvm") version "1.9.25"
    kotlin("plugin.serialization") version "1.9.25"
    id("io.ktor.plugin") version "2.3.12"
    id("com.google.devtools.ksp") version "1.9.24-1.0.20"
    id("org.jetbrains.kotlinx.rpc.plugin") version "0.2.1"
}

group = "com.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.ktor:ktor-client-cio-jvm")
    implementation("org.jetbrains.kotlinx:kotlinx-rpc-krpc-client")
    implementation("org.jetbrains.kotlinx:kotlinx-rpc-krpc-ktor-client")
    implementation("io.ktor:ktor-server-netty-jvm:2.3.12")
    implementation("org.jetbrains.kotlinx:kotlinx-rpc-krpc-server")
    implementation("org.jetbrains.kotlinx:kotlinx-rpc-krpc-ktor-server")
    implementation("org.jetbrains.kotlinx:kotlinx-rpc-krpc-serialization-json")
    implementation("ch.qos.logback:logback-classic:1.5.6")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(17)
}