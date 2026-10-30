plugins {
    application
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.gretty)
    id("war")
}

gretty {
    servletContainer = "tomcat10"
    contextPath = "/"
    httpsEnabled = true
}

repositories {
    mavenCentral()
    maven { url = uri("https://redirector.kotlinlang.org/maven/ktor-eap") }
}

dependencies {
    implementation(libs.kotlin.stdlib)
    implementation(ktorLibs.server.servlet)
    implementation(libs.slf4j.jdk14)
}

afterEvaluate {
    tasks.getByName("run") {
        dependsOn("appRun")
    }
}
