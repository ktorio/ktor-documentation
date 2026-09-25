plugins {
    application
    alias(libs.plugins.kotlin.jvm)
}

application {
    mainClass.set("io.ktor.server.jetty.jakarta.EngineMain")
}

repositories {
    mavenCentral()
    maven { url = uri("https://redirector.kotlinlang.org/maven/ktor-eap") }
}

dependencies {
    implementation(libs.kotlin.stdlib.jdk8)
    implementation(ktorLibs.server.core)
    implementation(ktorLibs.server.jetty)
    implementation(ktorLibs.server.htmlBuilder)
    implementation(libs.logback.classic)
}
