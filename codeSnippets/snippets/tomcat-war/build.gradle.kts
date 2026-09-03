val kotlin_version: String by project
val slf4j_version: String by project

plugins {
    application
    kotlin("jvm")
    id("org.gretty") version "5.0.1"
    id("war")
}

gretty {
    servletContainer = "tomcat10"
    contextPath = "/"
}

repositories {
    mavenCentral()
    maven { url = uri("https://redirector.kotlinlang.org/maven/ktor-eap") }
}

dependencies {
    implementation(ktorLibs.server.servlet)
    implementation("org.slf4j:slf4j-jdk14:$slf4j_version")
    testImplementation(ktorLibs.server.testHost)
    testImplementation("org.jetbrains.kotlin:kotlin-test")
}

afterEvaluate {
    tasks.getByName("run") {
        dependsOn("appRun")
    }
}
