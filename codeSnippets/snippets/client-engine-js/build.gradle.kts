val ktor_version: String by project
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
                implementation("io.ktor:ktor-client-core:$ktor_version")
                implementation("io.ktor:ktor-client-js:$ktor_version")
                implementation("io.ktor:ktor-client-content-negotiation:$ktor_version")
                implementation("io.ktor:ktor-serialization-kotlinx-json:$ktor_version")            }
        }
    }
}
