plugins {
    application
    alias(libs.plugins.kotlin.jvm)
}

application {
    mainClass.set("com.example.ApplicationKt")
}

tasks.register<JavaExec>("runConfiguredServer") {
    group = "application"
    description = "Run the configured server on port 8080"
    classpath = sourceSets["main"].runtimeClasspath
    mainClass.set("com.example.ApplicationKt")
    args("configured")
}

repositories {
    mavenCentral()
    maven { url = uri("https://redirector.kotlinlang.org/maven/ktor-eap") }
}

dependencies {
    implementation(libs.kotlin.stdlib.jdk8)
    implementation(ktorLibs.server.core)
    implementation(ktorLibs.server.netty)
    implementation(libs.logback.classic)

    testImplementation(libs.junit)
    testImplementation(project(":e2e"))

    testImplementation(ktorLibs.client.core)
    testImplementation(ktorLibs.client.cio)
}
