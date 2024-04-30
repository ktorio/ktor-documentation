val ktor_version: String by project
val kotlin_version: String by project
val slf4j_version: String by project

plugins {
    application
    kotlin("jvm")
    id("org.gretty") version "4.0.3"
    id("war")
}

gretty {
    servletContainer = "tomcat10"
    contextPath = "/"
    logbackConfigFile = "src/main/resources/logback.xml"
    httpsEnabled = true
}

repositories {
    mavenCentral()
    maven { url = uri("https://maven.pkg.jetbrains.space/public/p/ktor/eap") }
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlin_version")
    implementation("io.ktor:ktor-server-servlet:$ktor_version")
    implementation("org.slf4j:slf4j-jdk14:$slf4j_version")
}

afterEvaluate {
    tasks.getByName("run") {
        dependsOn("appRun")
    }
}
