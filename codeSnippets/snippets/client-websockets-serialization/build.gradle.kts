val ktor_version: String by project
val logback_version: String by project
val junit_version: String by project
val hamcrest_version: String by project

plugins {
    application
    kotlin("jvm")
    kotlin("plugin.serialization").version("1.9.23")
}

application {
    mainClass.set("com.example.ApplicationKt")
}

repositories {
    mavenCentral()
    maven { url = uri("https://maven.pkg.jetbrains.space/public/p/ktor/eap") }
}

tasks.named<JavaExec>("run") {
    standardInput = System.`in`
}

dependencies {
    implementation("io.ktor:ktor-client-core:$ktor_version")
    implementation("io.ktor:ktor-client-cio:$ktor_version")
    implementation("io.ktor:ktor-client-websockets:$ktor_version")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktor_version")
    implementation("io.ktor:ktor-serialization-kotlinx-xml:$ktor_version")
    implementation("io.ktor:ktor-serialization-kotlinx-cbor:$ktor_version")
    implementation("io.ktor:ktor-serialization-kotlinx-protobuf:$ktor_version")
    implementation("io.ktor:ktor-client-logging:$ktor_version")
    implementation("ch.qos.logback:logback-classic:$logback_version")
    testImplementation("junit:junit:$junit_version")
    testImplementation("org.hamcrest:hamcrest:$hamcrest_version")
    testImplementation(project(":e2e"))
}
