val ktorVersion: String by project
val kotlinVersion: String by project
val junitVersion: String by project

plugins {
    application
    kotlin("jvm")
}

application {
    mainClass.set("com.example.DownloaderKt")
}

repositories {
    mavenCentral()
    maven { url = uri("https://maven.pkg.jetbrains.space/public/p/ktor/eap") }
}

dependencies {
    implementation("io.ktor:ktor-client-core:$ktorVersion")
    implementation("io.ktor:ktor-client-apache:$ktorVersion")
    testImplementation("io.ktor:ktor-client-mock:$ktorVersion")
    testImplementation("junit:junit:$junitVersion")
}
