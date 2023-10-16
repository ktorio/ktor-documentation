import proguard.gradle.*

val ktorVersion: String by project
val kotlinVersion: String by project
val logbackVersion: String by project

buildscript {
    repositories {
        maven("https://plugins.gradle.org/m2")
    }
    dependencies {
        classpath("com.guardsquare:proguard-gradle:7.3.2")
    }
}

plugins {
    application
    kotlin("jvm")
    id("io.ktor.plugin") version "2.3.5"
}

application {
    mainClass.set("io.ktor.server.netty.EngineMain")
}

repositories {
    mavenLocal()
    maven { url = uri("https://maven.pkg.jetbrains.space/public/p/ktor/eap") }
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlinVersion")
    implementation("io.ktor:ktor-server-core:$ktorVersion")
    implementation("io.ktor:ktor-server-netty:$ktorVersion")
    implementation("ch.qos.logback:logback-classic:$logbackVersion")
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
