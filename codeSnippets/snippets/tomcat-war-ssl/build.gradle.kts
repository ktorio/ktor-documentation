val ktorVersion: String by project
val kotlinVersion: String by project
val slf4jVersion: String by project

plugins {
    application
    kotlin("jvm")
    id("org.gretty") version "3.0.6"
    id("war")
}

gretty {
    servletContainer = "tomcat9"
    contextPath = "/"
    logbackConfigFile = "src/main/resources/logback.xml"
    httpsEnabled = true
}

repositories {
    mavenCentral()
    maven { url = uri("https://maven.pkg.jetbrains.space/public/p/ktor/eap") }
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlinVersion")
    implementation("io.ktor:ktor-server-servlet:$ktorVersion")
    implementation("org.slf4j:slf4j-jdk14:$slf4jVersion")
}

afterEvaluate {
    tasks.getByName("run") {
        dependsOn("appRun")
    }
}
