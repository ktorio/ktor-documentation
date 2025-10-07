plugins {
    kotlin("jvm") version "2.2.20"
    kotlin("plugin.serialization") version "2.2.20"
    id("io.ktor.plugin") version "3.3.0"
    id("org.jetbrains.kotlinx.rpc.plugin") version "0.10.0"
}

group = "com.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.ktor:ktor-client-cio-jvm")
    implementation("org.jetbrains.kotlinx:kotlinx-rpc-krpc-client:0.10.0")
    implementation("org.jetbrains.kotlinx:kotlinx-rpc-krpc-ktor-client:0.10.0")
    implementation("io.ktor:ktor-server-netty-jvm")
    implementation("org.jetbrains.kotlinx:kotlinx-rpc-krpc-server:0.10.0")
    implementation("org.jetbrains.kotlinx:kotlinx-rpc-krpc-ktor-server:0.10.0")
    implementation("org.jetbrains.kotlinx:kotlinx-rpc-krpc-serialization-json:0.10.0")
    implementation("ch.qos.logback:logback-classic:1.5.18")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(17)
}
