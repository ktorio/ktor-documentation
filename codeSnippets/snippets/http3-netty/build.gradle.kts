plugins {
    application
    alias(libs.plugins.kotlin.jvm)
}

group = "com.example"
version = "0.0.1"

application {
    mainClass.set("com.example.ApplicationKt")
}

repositories {
    mavenCentral()
    maven {
        url = uri("https://redirector.kotlinlang.org/maven/ktor-eap")
    }
}

dependencies {
    implementation(ktorLibs.server.core)
    implementation(ktorLibs.server.netty)
    implementation(libs.logback.classic)
}