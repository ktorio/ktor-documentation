plugins {
    application
    alias(libs.plugins.kotlin.jvm)
}

application {
    mainClass.set("com.example.DownloaderKt")
}

repositories {
    mavenCentral()
    maven { url = uri("https://redirector.kotlinlang.org/maven/ktor-eap") }
}

dependencies {
    implementation(ktorLibs.client.core)
    implementation(ktorLibs.client.apache)
    testImplementation(ktorLibs.client.mock)
    testImplementation(libs.junit)
}
