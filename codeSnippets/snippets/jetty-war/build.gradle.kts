val ktor_version: String by project
val kotlin_version: String by project
val logback_version: String by project

plugins {
    application
    kotlin("jvm")
    id("org.gretty") version "3.0.6"
    id("war")
}

gretty {
    contextPath = "/"
    logbackConfigFile = "src/main/resources/logback.xml"
}

repositories {
    mavenCentral()
    maven { url = uri("https://maven.pkg.jetbrains.space/public/p/ktor/eap") }
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlin_version")
    implementation("io.ktor:ktor-server-core:$ktor_version")
    implementation("io.ktor:ktor-server-servlet:$ktor_version")
    implementation("ch.qos.logback:logback-classic:$logback_version")
}

afterEvaluate {
    tasks.getByName("run") {
        dependsOn("appRun")
    }
}
