plugins {
    application
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.gretty)
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
    implementation(libs.kotlin.stdlib.jdk8)
    implementation(ktorLibs.server.core)
    implementation(ktorLibs.server.servlet)
    implementation(libs.logback.classic)
    testImplementation(ktorLibs.server.testHost)
    testImplementation(libs.kotlin.test)
}

afterEvaluate {
    tasks.getByName("run") {
        dependsOn("appRun")
    }
}
