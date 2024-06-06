import java.io.FileNotFoundException

val ktor_version: String by project
val kotlin_version: String by project
val logback_version: String by project

buildscript {
    repositories {
        maven("https://plugins.gradle.org/m2")
    }
    dependencies {
        classpath("com.guardsquare:proguard-gradle:7.5.0")
    }
}

plugins {
    application
    kotlin("jvm")
    id("io.ktor.plugin") version "2.3.11"
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

val buildMinimizedJar = tasks.register<proguard.gradle.ProGuardTask>("buildMinimizedJar") {
    group = "ktor"

    dependsOn(tasks.buildFatJar)

    val fatJarFile = tasks.shadowJar.flatMap { it.archiveFile }
    val fatJarFileNameWithoutExtension = fatJarFile.get().asFile.nameWithoutExtension
    val fatJarDestinationDirectory = tasks.shadowJar.get().destinationDirectory
    injars(fatJarFile)
    outjars(
        fatJarDestinationDirectory.file("${fatJarFileNameWithoutExtension}.min.jar")
    )

    // Automatically handle the Java version of this build.
    val javaHome = System.getProperty("java.home")
    if (System.getProperty("java.version").startsWith("1.")) {
        // Before Java 9, runtime classes are packaged in a single JAR file.
        libraryjars("$javaHome/lib/rt.jar")
    } else {
        // Starting from Java 9, runtime classes are packaged in modular JMOD files.
        fun includeJavaModuleFromJDK(jModFileName: String) {
            val jModFilePath = "$javaHome/jmods/$jModFileName"
            val jModFile = File(jModFilePath)
            if (!jModFile.exists()) {
                throw FileNotFoundException("The '$jModFile' at '$jModFilePath' doesn't exist.")
            }
            libraryjars(
                mapOf("jarfilter" to "!**.jar", "filter" to "!module-info.class"),
                jModFilePath,
            )
        }
        val javaModules = listOf(
            "java.base.jmod",
            // All imports from "org.mozilla.javascript.tools.debugger" use Java Swing/Desktop imports
            "java.desktop.jmod",
            // Some libraries depends on Java logging
            "java.logging.jmod",
            // Some libraries depends on Java xml
            "java.xml.jmod",
            // Needed by some Ktor modules such as "ktor-server-auth-jvm"
            "java.naming.jmod",
            // Needed by some Ktor packages such as "io.ktor.util.debug"
            "java.management.jmod",
            // Needed by some libraries such as "com.github.fge.jsonschema"
            "java.scripting.jmod",
            // All classes in "com.fasterxml.jackson.databind" need this
            "java.sql.jmod",
            // Might be needed when using Java engine in Ktor Client
            "java.net.http.jmod"
        )
        javaModules.forEach { includeJavaModuleFromJDK(it) }
    }

    // This will include the Kotlin library jars, it will be needed even though Shadow JAR already includes it
    // to solve all warnings that are related to Kotlin without '-dontwarn kotlin.**'
    // this will also solve warnings that are coming from some libraries
    injars(sourceSets.main.get().compileClasspath)

    printmapping(fatJarDestinationDirectory.file("$fatJarFileNameWithoutExtension.map"))
    dontobfuscate()
    dontoptimize()

    configuration(file("proguard.pro"))
}

tasks.register<JavaExec>("runMinimizedJar") {
    group = "ktor"

    dependsOn(buildMinimizedJar)
    classpath = files(tasks.shadowJar.get().archiveFile)
}
