val logback_version: String by project

plugins {
    application
    kotlin("jvm")
    id("io.ktor.plugin") version "2.3.6"
}

application {
    mainClass.set("io.ktor.server.netty.EngineMain")
}

version "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib")
    implementation("io.ktor:ktor-server-core-jvm")
    implementation("io.ktor:ktor-server-netty-jvm")
    implementation("ch.qos.logback:logback-classic:$logback_version")
    testImplementation("io.ktor:ktor-server-tests-jvm")
    testImplementation("org.jetbrains.kotlin:kotlin-test")
}
