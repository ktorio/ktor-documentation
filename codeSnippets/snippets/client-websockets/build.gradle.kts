plugins {
    application
    alias(libs.plugins.kotlin.jvm)
}

application {
    mainClass.set("com.example.ApplicationKt")
}

repositories {
    mavenCentral()
    maven { url = uri("https://redirector.kotlinlang.org/maven/ktor-eap") }
}

tasks.named<JavaExec>("run") {
    standardInput = System.`in`
}

dependencies {
    implementation(ktorLibs.client.core)
    implementation(ktorLibs.client.cio)
    implementation(ktorLibs.client.websockets)
    implementation(ktorLibs.client.logging)
    implementation(libs.logback.classic)
    testImplementation(libs.junit)
    testImplementation(libs.hamcrest)
    testImplementation(project(":e2e"))
}
