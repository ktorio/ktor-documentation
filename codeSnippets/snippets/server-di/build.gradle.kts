val kotlin_version: String by project
val logback_version: String by project

plugins {
    application
    kotlin("jvm")
}

application {
    mainClass.set("io.ktor.server.netty.EngineMain")
}

repositories {
    mavenCentral()
    maven { url = uri("https://maven.pkg.jetbrains.space/public/p/ktor/eap") }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

dependencies {
    implementation(ktorLibs.server.core)
    implementation(ktorLibs.server.di)
    implementation(ktorLibs.server.netty)
    implementation(ktorLibs.server.config.yaml)
    implementation(ktorLibs.server.contentNegotiation)
    implementation(ktorLibs.serialization.kotlinx.json)
    implementation("ch.qos.logback:logback-classic:${logback_version}")
    testImplementation(ktorLibs.server.testHost)
    testImplementation(kotlin("test"))
}
