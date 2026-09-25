plugins {
    application
    alias(libs.plugins.kotlin.jvm)
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
    implementation(ktorLibs.server.auth)
    implementation(libs.logback.classic)
    testImplementation(libs.kotlin.test)
    testImplementation(ktorLibs.server.testHost)
}
