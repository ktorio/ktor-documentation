val logback_version: String by project

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

dependencies {
    implementation(ktorLibs.client.core)
    implementation(ktorLibs.client.cio)
    implementation(ktorLibs.client.contentNegotiation)
    implementation(ktorLibs.serialization.kotlinx.json)
    implementation("ch.qos.logback:logback-classic:$logback_version")
    testImplementation(ktorLibs.client.mock)
    testImplementation("org.jetbrains.kotlin:kotlin-test")
}
