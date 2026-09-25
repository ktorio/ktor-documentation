plugins {
    alias(libs.plugins.kotlin.jvm)
}

repositories {
    mavenCentral()
    maven { url = uri("https://redirector.kotlinlang.org/maven/ktor-eap") }
}

dependencies {
    implementation(libs.junit)
    api(ktorLibs.server.core)
    api(ktorLibs.server.cio)
}

kotlin {
    jvmToolchain(11)
}
