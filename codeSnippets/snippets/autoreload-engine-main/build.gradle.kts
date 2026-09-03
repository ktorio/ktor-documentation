val kotlin_version: String by project
val logback_version: String by project
val junit_version: String by project

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
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlin_version")
    implementation(ktorLibs.server.core)
    implementation(ktorLibs.server.netty)
    implementation("ch.qos.logback:logback-classic:$logback_version")
    implementation(ktorLibs.server.config.yaml)
    testImplementation("junit:junit:$junit_version")
    testImplementation("com.typesafe:config:1.4.8")
    testImplementation(ktorLibs.server.testHost)
}
