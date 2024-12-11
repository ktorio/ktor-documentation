val ktor_version: String by project
val kotlin_version: String by project
val logback_version: String by project
val junit_version: String by project

plugins {
    application
    kotlin("jvm")
}

application {
    mainClass.set("com.example.ApplicationKt")
}

tasks.register<JavaExec>("runConfiguredServer") {
    group = "application"
    description = "Run the configured server on port 8080"
    classpath = sourceSets["main"].runtimeClasspath
    mainClass.set("com.example.ApplicationKt")
    args("configured")
}

repositories {
    mavenCentral()
    maven { url = uri("https://maven.pkg.jetbrains.space/public/p/ktor/eap") }
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlin_version")
    implementation("io.ktor:ktor-server-core:$ktor_version")
    implementation("io.ktor:ktor-server-netty:$ktor_version")
    implementation("ch.qos.logback:logback-classic:$logback_version")

    testImplementation("junit:junit:$junit_version")
    testImplementation(project(":e2e"))

    testImplementation("io.ktor:ktor-client-core:$ktor_version")
    testImplementation("io.ktor:ktor-client-cio:$ktor_version")
}
