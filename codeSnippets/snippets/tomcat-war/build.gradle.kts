val ktorVersion: String by project
val kotlinVersion: String by project
val slf4jVersion: String by project

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
    implementation("io.ktor:ktor-server-servlet-jakarta:$ktorVersion")
    implementation("org.slf4j:slf4j-jdk14:$slf4jVersion")
    testImplementation("io.ktor:ktor-server-test-host:$ktorVersion")
    testImplementation("org.jetbrains.kotlin:kotlin-test")
}

afterEvaluate {
    tasks.getByName("run") {
        dependsOn("appRun")
    }
}
