val kotlin_version = "2.2.20"
val logback_version: String by project
val swagger_codegen_version: String by project

plugins {
    application
    kotlin("jvm")
    kotlin("plugin.serialization").version("2.2.20")
}

application {
    mainClass.set("io.ktor.server.netty.EngineMain")
}

repositories {
    mavenCentral()
    maven { url = uri("https://redirector.kotlinlang.org/maven/ktor-eap") }
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlin_version")
    implementation(ktorLibs.server.core)
    implementation(ktorLibs.server.netty)
    implementation(ktorLibs.server.swagger)
    implementation(ktorLibs.server.openapi)
    implementation(ktorLibs.server.cors)
    implementation(ktorLibs.server.contentNegotiation)
    implementation(ktorLibs.serialization.kotlinx.json)
    implementation(ktorLibs.client.contentNegotiation)
    implementation(ktorLibs.server.routingOpenapi)
    implementation("io.swagger.codegen.v3:swagger-codegen-generators:$swagger_codegen_version")
    implementation("ch.qos.logback:logback-classic:$logback_version")
    testImplementation(ktorLibs.server.testHost)
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlin_version")
}
