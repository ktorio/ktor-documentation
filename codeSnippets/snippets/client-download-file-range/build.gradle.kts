val kotlin_version: String by project
val junit_version: String by project

plugins {
    application
    kotlin("jvm")
}

application {
    mainClass.set("com.example.DownloaderKt")
}

repositories {
    mavenCentral()
    maven { url = uri("https://redirector.kotlinlang.org/maven/ktor-eap") }
}

dependencies {
    implementation(ktorLibs.client.core)
    implementation(ktorLibs.client.apache)
    testImplementation(ktorLibs.client.mock)
    testImplementation("junit:junit:$junit_version")
}
