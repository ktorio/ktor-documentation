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

dependencies {
    implementation(ktorLibs.client.core)
    implementation(ktorLibs.client.cio)
    implementation(ktorLibs.client.encoding)
    implementation(libs.logback.classic)
    implementation(project(":compression"))
    implementation(project(":e2e"))
    testImplementation(libs.junit)
    testImplementation(libs.hamcrest)
}
