val kotlin_version: String by project
val logback_version: String by project

plugins {
    application
    kotlin("jvm")
}

application {
    mainClass.set("io.ktor.server.jetty.jakarta.EngineMain")
}

repositories {
    mavenCentral()
    maven { url = uri("https://redirector.kotlinlang.org/maven/ktor-eap") }
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlin_version")
    implementation(ktorLibs.server.core)
    implementation(ktorLibs.server.jetty)
    implementation(ktorLibs.server.htmlBuilder)
    implementation("ch.qos.logback:logback-classic:$logback_version")
}
