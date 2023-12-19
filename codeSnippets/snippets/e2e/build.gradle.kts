val ktor_version: String by project
val junit_version: String by project

plugins {
    kotlin("jvm")
}

repositories {
    mavenCentral()
    maven { url = uri("https://maven.pkg.jetbrains.space/public/p/ktor/eap") }
}

dependencies {
    implementation("junit:junit:$junit_version")
    api("io.ktor:ktor-server-core:$ktor_version")
    api("io.ktor:ktor-server-cio:$ktor_version")
}
