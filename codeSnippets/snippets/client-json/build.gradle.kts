val logback_version: String by project

plugins {
    application
    kotlin("jvm")
}

application {
    mainClass.set("com.example.JsonClientKt")
}

repositories {
    mavenCentral()
    maven { url = uri("https://redirector.kotlinlang.org/maven/ktor-eap") }
}

dependencies {
    implementation(ktorLibs.client.core)
    implementation(ktorLibs.client.apache5)
    implementation(ktorLibs.client.contentNegotiation)
    implementation(ktorLibs.serialization.gson)
    implementation("ch.qos.logback:logback-classic:$logback_version")
}
