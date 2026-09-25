import com.google.cloud.tools.gradle.appengine.appyaml.AppEngineAppYamlExtension


plugins {
    application
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.appengine)
    alias(libs.plugins.shadow)
}

application {
    mainClass.set("io.ktor.server.netty.EngineMain")
}

repositories {
    mavenCentral()
    maven { url = uri("https://redirector.kotlinlang.org/maven/ktor-eap") }
}

configure<AppEngineAppYamlExtension> {
    stage {
        setArtifact("build/libs/${project.name}-all.jar")
    }
    deploy {
        version = "GCLOUD_CONFIG"
        projectId = "GCLOUD_CONFIG"
    }
}

dependencies {
    implementation(libs.kotlin.stdlib)
    implementation(ktorLibs.server.core)
    implementation(ktorLibs.server.netty)
    implementation(libs.google.cloud.logging.logback)
    testImplementation(ktorLibs.server.testHost)
    testImplementation(libs.kotlin.test)
}
