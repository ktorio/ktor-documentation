plugins {
    application
    alias(libs.plugins.kotlin.jvm)
}

application {
    mainClass.set("com.example.ApplicationKt")
}

repositories {
    mavenCentral()
    maven { url = uri("https://redirector.kotlinlang.org/maven/ktor-eap") }
}

dependencies {
    implementation(libs.kotlin.stdlib.jdk8)
    implementation(ktorLibs.server.core)
    implementation(ktorLibs.server.netty)
    implementation(ktorLibs.network.tls.certificates)
    implementation(libs.logback.classic)
    testImplementation(libs.junit)
    testImplementation(project(":e2e"))
    testImplementation(ktorLibs.client.core)
    testImplementation(ktorLibs.client.cio)
}
