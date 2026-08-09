val kotlin_version: String by project
val logback_version: String by project
val junit_version: String by project

plugins {
    application
    kotlin("jvm")
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
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlin_version")
    implementation(ktorLibs.server.core)
    implementation(ktorLibs.server.netty)
    implementation("ch.qos.logback:logback-classic:$logback_version")

    testImplementation("junit:junit:$junit_version")
    testImplementation(project(":e2e"))

    testImplementation(ktorLibs.client.core)
    testImplementation(ktorLibs.client.cio)
}
