val ktorVersion: String by project
val kotlinVersion: String by project
val logbackVersion: String by project
val tcnativeVersion = "2.0.61.Final"

plugins {
    application
    kotlin("jvm")
}

application {
    mainClass.set("com.example.MainKt")
}

repositories {
    mavenCentral()
    maven { url = uri("https://maven.pkg.jetbrains.space/public/p/ktor/eap") }
}

val osName = System.getProperty("os.name").toLowerCase()
val tcnativeClassifier = when {
    osName.contains("win") -> "windows-x86_64"
    osName.contains("linux") -> "linux-x86_64"
    osName.contains("mac") -> "osx-x86_64"
    else -> null
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlinVersion")
    implementation("io.ktor:ktor-server-core:$ktorVersion")
    implementation("io.ktor:ktor-server-netty:$ktorVersion")
    implementation("io.ktor:ktor-server-html-builder:$ktorVersion")
    implementation("io.ktor:ktor-server-default-headers:$ktorVersion")
    implementation("io.ktor:ktor-server-call-logging:$ktorVersion")
    implementation("io.ktor:ktor-network-tls:$ktorVersion")
    implementation("io.ktor:ktor-network-tls-certificates:$ktorVersion")
    implementation("ch.qos.logback:logback-classic:$logbackVersion")
    if (tcnativeClassifier != null) {
        implementation("io.netty:netty-tcnative-boringssl-static:$tcnativeVersion:$tcnativeClassifier")
    } else {
        implementation("io.netty:netty-tcnative-boringssl-static:$tcnativeVersion")
    }
}
