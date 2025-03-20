plugins {
    kotlin("jvm") version "2.1.20"
    kotlin("plugin.serialization") version "2.1.20"
    id("io.ktor.plugin") version "3.0.3"
    id("org.jetbrains.kotlinx.rpc.plugin") version "0.5.1"
}

group = "com.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.ktor:ktor-client-cio-jvm")
    implementation("org.jetbrains.kotlinx:kotlinx-rpc-krpc-client:0.5.1")
    implementation("org.jetbrains.kotlinx:kotlinx-rpc-krpc-ktor-client:0.5.1")
    implementation("io.ktor:ktor-server-netty-jvm")
    implementation("org.jetbrains.kotlinx:kotlinx-rpc-krpc-server:0.5.1")
    implementation("org.jetbrains.kotlinx:kotlinx-rpc-krpc-ktor-server:0.5.1")
    implementation("org.jetbrains.kotlinx:kotlinx-rpc-krpc-serialization-json:0.5.1")
    implementation("ch.qos.logback:logback-classic:1.5.6")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(17)
}
