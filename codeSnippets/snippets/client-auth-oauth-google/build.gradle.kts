val logback_version: String by project
val junit_version: String by project
val hamcrest_version: String by project

plugins {
    application
    kotlin("jvm")
    kotlin("plugin.serialization").version("2.2.20")
}

application {
    mainClass.set("com.example.ApplicationKt")
}

repositories {
    mavenCentral()
    maven { url = uri("https://redirector.kotlinlang.org/maven/ktor-eap") }
}

tasks.named<JavaExec>("run") {
    standardInput = System.`in`
}

dependencies {
    implementation(ktorLibs.client.core)
    implementation(ktorLibs.client.cio)
    implementation(ktorLibs.client.auth)
    implementation(ktorLibs.client.logging)
    implementation(ktorLibs.client.contentNegotiation)
    implementation(ktorLibs.serialization.kotlinx.json)
    implementation(ktorLibs.server.core)
    implementation(ktorLibs.server.netty)
    implementation("ch.qos.logback:logback-classic:$logback_version")
    testImplementation("junit:junit:$junit_version")
    testImplementation(ktorLibs.client.mock)
}
