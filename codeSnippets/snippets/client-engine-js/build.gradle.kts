plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.serialization)
}

repositories {
    mavenCentral()
    maven("https://redirector.kotlinlang.org/maven/kotlinx-html")
    maven("https://redirector.kotlinlang.org/maven/ktor-eap")
}

kotlin {
    js(IR) {
        binaries.executable()
        browser {
            commonWebpackConfig {
                cssSupport {
                    enabled.set(true)
                }
            }
        }
    }
    sourceSets {
        val jsMain by getting {
            dependencies {
                implementation(libs.kotlinx.html)
                implementation(ktorLibs.client.core)
                implementation(ktorLibs.client.js)
                implementation(ktorLibs.client.contentNegotiation)
                implementation(ktorLibs.serialization.kotlinx.json)
            }
        }
    }
}
