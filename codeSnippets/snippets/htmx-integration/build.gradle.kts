plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(ktorLibs.plugins.ktor)
}

group = "com.example"
version = "0.0.1"

application {
    mainClass = "io.ktor.server.netty.EngineMain"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(ktorLibs.server.core)
    implementation(ktorLibs.server.netty)
    implementation(libs.logback.classic)
    implementation(ktorLibs.server.core)
    implementation(ktorLibs.server.htmx)
    implementation(ktorLibs.htmx)
    implementation(ktorLibs.htmx.html)
    implementation(ktorLibs.server.htmlBuilder)
    implementation(ktorLibs.server.config.yaml)
    testImplementation(ktorLibs.server.testHost)
    testImplementation(libs.kotlin.test.junit)
}
