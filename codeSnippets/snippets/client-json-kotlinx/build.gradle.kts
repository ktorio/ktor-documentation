plugins {
    application
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.serialization)
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
    implementation(ktorLibs.serialization.kotlinx.xml)
    implementation(ktorLibs.serialization.kotlinx.cbor)
    implementation(ktorLibs.serialization.kotlinx.protobuf)
    implementation(ktorLibs.client.logging)
    implementation(libs.logback.classic)
    implementation(project(":json-kotlinx"))
    implementation(project(":e2e"))
    testImplementation(libs.junit)
    testImplementation(libs.hamcrest)
}
