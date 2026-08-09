val logback_version: String by project
val junit_version: String by project
val slf4j_version: String by project

plugins {
    application
    kotlin("jvm")
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
    implementation(ktorLibs.client.apache5)
    implementation(ktorLibs.client.java)
    implementation(ktorLibs.client.jetty)
    implementation("org.eclipse.jetty:jetty-alpn-java-client:11.0.20")
    implementation(ktorLibs.client.cio)
    implementation(ktorLibs.client.android)
    implementation(ktorLibs.client.okhttp)
}
