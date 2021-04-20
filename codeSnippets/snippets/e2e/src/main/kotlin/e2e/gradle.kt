package e2e

import java.io.InputStream

fun runGradleApp(): Process {
    val gradlewPath = System.getProperty("gradlew") ?: error("System property 'gradlew' should point to Gradle Wrapper file")
    return ProcessBuilder(gradlewPath, "-Dorg.gradle.logging.level=quiet", "--quiet", "run").start()
}

fun InputStream.readString(): String = readAllBytes().toString(Charsets.UTF_8)