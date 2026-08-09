val kotlinx_html_version: String by project

plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization").version("2.2.20")
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
                implementation("org.jetbrains.kotlinx:kotlinx-html:$kotlinx_html_version")
                implementation(ktorLibs.client.core)
                implementation(ktorLibs.client.js)
                implementation(ktorLibs.client.contentNegotiation)
                implementation(ktorLibs.serialization.kotlinx.json)
            }
        }
    }
}
