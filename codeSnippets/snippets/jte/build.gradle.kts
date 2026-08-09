val kotlin_version: String by project
val logback_version: String by project
val jte_version: String by project

plugins {
    application
    kotlin("jvm")
}

application {
    mainClass.set("io.ktor.server.netty.EngineMain")
}

repositories {
    mavenCentral()
    maven { url = uri("https://redirector.kotlinlang.org/maven/ktor-eap") }
}

dependencies {
    implementation(ktorLibs.server.core)
    implementation(ktorLibs.server.netty)
    implementation(ktorLibs.server.jte)
    implementation("gg.jte:jte-kotlin:$jte_version")
    implementation("ch.qos.logback:logback-classic:$logback_version")
    testImplementation(ktorLibs.server.testHost)
    testImplementation("org.jetbrains.kotlin:kotlin-test")
}

kotlin {
    // The minimal JDK version required for jte 3.0+
    jvmToolchain(17)
}
