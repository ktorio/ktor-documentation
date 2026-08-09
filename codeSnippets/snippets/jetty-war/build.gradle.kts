val kotlin_version: String by project
val logback_version: String by project

plugins {
    application
    kotlin("jvm")
    id("org.gretty") version "5.0.1"
    id("war")
}

gretty {
    servletContainer = "jetty12"
    contextPath = "/"
}

repositories {
    mavenCentral()
    maven { url = uri("https://redirector.kotlinlang.org/maven/ktor-eap") }
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlin_version")
    implementation(ktorLibs.server.core)
    implementation(ktorLibs.server.servlet)
    implementation("ch.qos.logback:logback-classic:$logback_version")
    testImplementation(ktorLibs.server.testHost)
    testImplementation("org.jetbrains.kotlin:kotlin-test")
}

afterEvaluate {
    tasks.getByName("run") {
        dependsOn("appRun")
    }
}
