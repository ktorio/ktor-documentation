import java.nio.file.Files
import java.nio.file.NoSuchFileException
import java.nio.file.Paths

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
    id("io.ktor.plugin") version "3.2.1"
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
    implementation("io.ktor:ktor-server-core-jvm")
    implementation("io.ktor:ktor-server-netty-jvm")
    implementation("ch.qos.logback:logback-classic:$logback_version")
}

ktor {
    fatJar {
        archiveFileName.set("my-application.jar")
    }
}

fun getMinimizedJarFile(
    fatJarFileNameWithoutExtension: String,
    fatJarDestinationDirectory: DirectoryProperty
): Provider<RegularFile> {
    return fatJarDestinationDirectory.file("$fatJarFileNameWithoutExtension.min.jar")
}

val buildMinimizedJar = tasks.register<proguard.gradle.ProGuardTask>("buildMinimizedJar") {
    group = "ktor"

    dependsOn(tasks.buildFatJar)

    val fatJarFile = tasks.shadowJar.flatMap { it.archiveFile }
    val fatJarFileNameWithoutExtension = fatJarFile.get().asFile.nameWithoutExtension
    val fatJarDestinationDirectory = tasks.shadowJar.get().destinationDirectory

    val minimizedJarFile = getMinimizedJarFile(
        fatJarFileNameWithoutExtension = fatJarFileNameWithoutExtension,
        fatJarDestinationDirectory = fatJarDestinationDirectory
    )

    injars(fatJarFile)
    outjars(minimizedJarFile)

    // Automatically handle the Java version of this build.
    val javaHome = System.getProperty("java.home")
    val javaVersion = System.getProperty("java.version")
    if (javaVersion.startsWith("1.")) {
        // Before Java 9, runtime classes are packaged in a single JAR file.
        libraryjars(Paths.get(javaHome, "lib", "rt.jar"))
    } else {
        // Starting from Java 9, runtime classes are packaged in modular JMOD files.
        fun includeJavaModuleFromJdk(jModFileNameWithoutExtension: String) {
            val jModFilePath = Paths.get(javaHome, "jmods", "$jModFileNameWithoutExtension.jmod")
            if (!Files.exists(jModFilePath)) {
                throw NoSuchFileException("The Java module '$jModFileNameWithoutExtension' at '$jModFilePath' doesn't exist.")
            }
            libraryjars(
                mapOf("jarfilter" to "!**.jar", "filter" to "!module-info.class"),
                jModFilePath,
            )
        }
        val javaModules = listOf(
            "java.base",
            // All imports from "org.mozilla.javascript.tools.debugger" use Java Swing/Desktop imports
            "java.desktop",
            // Some libraries depends on Java logging
            "java.logging",
            // Some libraries depends on Java xml
            "java.xml",
            // Needed by some Ktor modules such as "ktor-server-auth-jvm"
            "java.naming",
            // Needed by some Ktor packages such as "io.ktor.util.debug"
            "java.management",
            // Needed by some libraries such as "com.github.fge.jsonschema"
            "java.scripting",
            // All classes in "com.fasterxml.jackson.databind" need this
            "java.sql",
            // Might be needed when using Java engine in Ktor Client
            "java.net.http"
        )
        javaModules.forEach { includeJavaModuleFromJdk(jModFileNameWithoutExtension = it) }
    }

    // Includes the main source set's compile classpath for Proguard.
    // Notice that Shadow JAR already includes Kotlin standard library and dependencies, yet this
    // is essential for resolving Kotlin and other library warnings without using '-dontwarn kotlin.**'
    injars(sourceSets.main.get().compileClasspath)

    printmapping(fatJarDestinationDirectory.file("${minimizedJarFile.get().asFile.nameWithoutExtension}.map"))
    // Disabling obfuscation makes the JAR file size a bit larger and the debugging process a bit less easy
    dontobfuscate()
    // Kotlinx serialization breaks when using optimizations
    dontoptimize()

    configuration(file("proguard.pro"))
}

tasks.register<JavaExec>("runMinimizedJar") {
    group = "ktor"

    dependsOn(buildMinimizedJar)

    val fatJarFileNameWithoutExtension = tasks.shadowJar.get().archiveFile.get().asFile.nameWithoutExtension
    val fatJarDestinationDirectory = tasks.shadowJar.get().destinationDirectory

    val minimizedJarFile = getMinimizedJarFile(
        fatJarFileNameWithoutExtension = fatJarFileNameWithoutExtension,
        fatJarDestinationDirectory = fatJarDestinationDirectory
    )
    classpath = files(minimizedJarFile)
}
