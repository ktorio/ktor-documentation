plugins {
    application
    alias(libs.plugins.kotlin.jvm)
    alias(ktorLibs.plugins.ktor)
}

application {
    mainClass.set("com.example.ApplicationKt")
}

repositories {
    mavenCentral()
    maven { url = uri("https://redirector.kotlinlang.org/maven/ktor-eap") }
}

dependencies {
    implementation(libs.kotlin.stdlib.jdk8)
    implementation(ktorLibs.server.core)
    implementation(ktorLibs.server.cio)
    implementation(libs.logback.classic)
    testImplementation(ktorLibs.server.testHost)
    testImplementation(libs.kotlin.test)
}

ktor {
    fatJar {
        archiveFileName.set("fat.jar")
    }

    docker {
        jreVersion.set(JavaVersion.VERSION_17)
        localImageName.set("sample-docker-image")
        imageTag.set("0.0.1-preview")
        portMappings.set(listOf(
            io.ktor.plugin.features.DockerPortMapping(
                80,
                8080,
                io.ktor.plugin.features.DockerPortMappingProtocol.TCP
            )
        ))

        externalRegistry.set(
            io.ktor.plugin.features.DockerImageRegistry.dockerHub(
                appName = provider { "ktor-app" },
                username = providers.environmentVariable("DOCKER_HUB_USERNAME"),
                password = providers.environmentVariable("DOCKER_HUB_PASSWORD")
            )
        )
    }
}
