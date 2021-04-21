package e2e

import java.io.InputStream

fun runGradleApp(): Process = runGradle("run")

fun runGradle(vararg args: String): Process {
    val gradlewPath = System.getProperty("gradlew") ?: error("System property 'gradlew' should point to Gradle Wrapper file")
    val processArgs = listOf(gradlewPath, "-Dorg.gradle.logging.level=quiet", "--quiet") + args

    val process = ProcessBuilder(processArgs).start()
    process.waitFor()
    return process
}

fun InputStream.readString(): String = readAllBytes().toString(Charsets.UTF_8)