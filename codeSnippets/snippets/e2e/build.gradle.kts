val junit_version: String by project

plugins {
    kotlin("jvm")
}

repositories {
    mavenCentral()
    maven { url = uri("https://redirector.kotlinlang.org/maven/ktor-eap") }
}

dependencies {
    implementation("junit:junit:$junit_version")
    api(ktorLibs.server.core)
    api(ktorLibs.server.cio)
}

kotlin {
    jvmToolchain(11)
}
