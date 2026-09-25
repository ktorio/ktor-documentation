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
    implementation(ktorLibs.server.jte)
    implementation(libs.jte.kotlin)
    implementation(libs.logback.classic)
    testImplementation(ktorLibs.server.testHost)
    testImplementation(libs.kotlin.test)
}

kotlin {
    // The minimal JDK version required for jte 3.0+
    jvmToolchain(17)
}
