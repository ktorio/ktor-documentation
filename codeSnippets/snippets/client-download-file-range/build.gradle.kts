val ktor_version: String by project
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
    implementation("io.ktor:ktor-client-core:$ktor_version")
    implementation("io.ktor:ktor-client-apache:$ktor_version")
    testImplementation("io.ktor:ktor-client-mock:$ktor_version")
    testImplementation("junit:junit:$junit_version")
}
