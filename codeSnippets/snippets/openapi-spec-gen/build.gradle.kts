plugins {
    application
    alias(libs.plugins.kotlin.jvm)
    alias(ktorLibs.plugins.ktor)
}

application {
    mainClass = "io.ktor.server.netty.EngineMain"
}

repositories {
    mavenCentral()
    maven("https://redirector.kotlinlang.org/maven/ktor-eap")
}

ktor {
    openApi {
        enabled = true
        codeInferenceEnabled = true
        onlyCommented = false
    }
}


dependencies {
    implementation(ktorLibs.server.core)
    implementation(ktorLibs.server.routingOpenapi)
    implementation(ktorLibs.server.openapi)
    implementation(ktorLibs.server.contentNegotiation)
    implementation(ktorLibs.serialization.kotlinx.json)
    implementation(ktorLibs.server.swagger)
    implementation(ktorLibs.server.netty)
    implementation(libs.logback.classic)
    testImplementation(ktorLibs.server.testHost)
    testImplementation(libs.kotlin.test)
}
