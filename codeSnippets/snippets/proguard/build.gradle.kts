import proguard.gradle.*

val ktor_version: String by project
val kotlin_version: String by project
val logback_version: String by project

buildscript {
    repositories {
        maven("https://plugins.gradle.org/m2")
    }
    dependencies {
        classpath("com.guardsquare:proguard-gradle:7.2.2")
    }
}

plugins {
    application
    kotlin("jvm")
    id("io.ktor.plugin") version "2.2.0"
}

application {
    mainClass.set("io.ktor.server.netty.EngineMain")
}

repositories {
    mavenLocal()
    maven { url = uri("https://maven.pkg.jetbrains.space/public/p/ktor/eap") }
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlin_version")
    implementation("io.ktor:ktor-server-core:$ktor_version")
    implementation("io.ktor:ktor-server-netty:$ktor_version")
    implementation("ch.qos.logback:logback-classic:$logback_version")
}

ktor {
    fatJar {
        archiveFileName.set("my-application.jar")
    }
}

task(name = "minimizedJar", type = ProGuardTask::class) {
    dependsOn("buildFatJar")
    injars("build/libs/my-application.jar")
    outjars("build/libs/my-application.min.jar")
    val javaHome = System.getProperty("java.home")
    if (System.getProperty("java.version").startsWith("1.")) {
        // Before Java 9, runtime classes are packaged in a single JAR file.
        libraryjars("$javaHome/lib/rt.jar")
    } else {
        // Starting from Java 9, runtime classes are packaged in modular JMOD files.
        libraryjars(
            mapOf("jarfilter" to "!**.jar", "filter" to "!module-info.class"),
            "$javaHome/jmods/java.base.jmod"
        )
    }
    printmapping("build/libs/my-application.map")
    ignorewarnings()
    dontobfuscate()
    dontoptimize()
    dontwarn()
    configuration("proguard.pro")
}
