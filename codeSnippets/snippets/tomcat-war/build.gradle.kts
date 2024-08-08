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
}

repositories {
    mavenCentral()
    maven { url = uri("https://maven.pkg.jetbrains.space/public/p/ktor/eap") }
}

dependencies {
    implementation("io.ktor:ktor-server-servlet-jakarta:$ktor_version")
    implementation("org.slf4j:slf4j-jdk14:$slf4j_version")
    testImplementation("io.ktor:ktor-server-test-host-jvm:$ktor_version")
    testImplementation("org.jetbrains.kotlin:kotlin-test")
}

afterEvaluate {
    tasks.getByName("run") {
        dependsOn("appRun")
    }
}
