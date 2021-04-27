package e2e

import java.io.InputStream

fun runGradleAppWaiting(): Process = runGradleWaiting("run")
fun runGradleApp(): Process = runGradle("run")

fun runGradleWaiting(vararg args: String): Process {
    val process = runGradle(*args)
    process.waitFor()
    return process
}

fun runGradle(vararg args: String): Process {
    val gradlewPath = System.getProperty("gradlew") ?: error("System property 'gradlew' should point to Gradle Wrapper file")
    val processArgs = listOf(gradlewPath, "-Dorg.gradle.logging.level=quiet", "--quiet") + args

    return ProcessBuilder(processArgs).start()
}

fun InputStream.readString(): String = readAllBytes().toString(Charsets.UTF_8)