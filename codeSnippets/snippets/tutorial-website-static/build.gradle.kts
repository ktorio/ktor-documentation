plugins {
    application
    alias(libs.plugins.kotlin.jvm)
    alias(ktorLibs.plugins.ktor)
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
    implementation(ktorLibs.server.freemarker)
    implementation(ktorLibs.server.netty)
    implementation(libs.logback.classic)
    testImplementation(ktorLibs.server.testHost)
    testImplementation(libs.kotlin.test.junit)
}
