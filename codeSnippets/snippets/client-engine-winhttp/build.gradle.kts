plugins {
    application
    alias(libs.plugins.kotlin.multiplatform)
}

repositories {
    mavenCentral()
    maven { url = uri("https://redirector.kotlinlang.org/maven/ktor-eap") }
}

kotlin {
    val hostOs = System.getProperty("os.name")
    val isMingwX64 = hostOs.startsWith("Windows")
    val nativeTarget = when {
        isMingwX64 -> mingwX64("native")
        else -> throw GradleException("Host OS is not supported in Kotlin/Native.")
    }

    nativeTarget.apply {
        binaries {
            executable {
                entryPoint = "main"
            }
        }
    }
    sourceSets {
        getByName("nativeMain") {
            dependencies {
                implementation(ktorLibs.client.core)
                implementation(ktorLibs.client.winhttp)
            }
        }
        getByName("nativeTest") {
            dependencies {
                implementation(libs.kotlin.test)
            }
        }
    }
}
