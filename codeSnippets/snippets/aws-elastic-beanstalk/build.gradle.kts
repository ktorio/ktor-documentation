val logback_version: String by project

plugins {
    application
    kotlin("jvm")
    alias(ktorLibs.plugins.ktor)
}

application {
    mainClass.set("io.ktor.server.netty.EngineMain")
}

version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib")
    implementation(ktorLibs.server.core)
    implementation(ktorLibs.server.netty)
    implementation("ch.qos.logback:logback-classic:$logback_version")
    testImplementation(ktorLibs.server.testHost)
    testImplementation("org.jetbrains.kotlin:kotlin-test")
}
