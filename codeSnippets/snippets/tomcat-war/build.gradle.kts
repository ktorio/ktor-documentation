plugins {
    application
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.gretty)
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
    implementation(libs.slf4j.jdk14)
    testImplementation(ktorLibs.server.testHost)
    testImplementation(libs.kotlin.test)
}

afterEvaluate {
    tasks.getByName("run") {
        dependsOn("appRun")
    }
}
