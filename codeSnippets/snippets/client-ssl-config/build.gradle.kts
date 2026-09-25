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
    implementation(ktorLibs.client.core)
    implementation(ktorLibs.client.apache5)
    implementation(ktorLibs.client.java)
    implementation(ktorLibs.client.jetty)
    implementation(libs.jetty.alpn.java.client)
    implementation(ktorLibs.client.cio)
    implementation(ktorLibs.client.android)
    implementation(ktorLibs.client.okhttp)
}
