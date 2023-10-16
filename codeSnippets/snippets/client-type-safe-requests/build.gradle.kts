val ktorVersion: String by project
val logbackVersion: String by project
val junitVersion: String by project
val hamcrestVersion: String by project

plugins {
    application
    kotlin("jvm")
    kotlin("plugin.serialization").version("1.9.10")
}

application {
    mainClass.set("com.example.ApplicationKt")
}

repositories {
    mavenCentral()
    maven { url = uri("https://maven.pkg.jetbrains.space/public/p/ktor/eap") }
}

dependencies {
    implementation("io.ktor:ktor-client-core:$ktorVersion")
    implementation("io.ktor:ktor-client-cio:$ktorVersion")
    implementation("io.ktor:ktor-client-resources:$ktorVersion")
    implementation("ch.qos.logback:logback-classic:$logbackVersion")
    implementation(project(":resource-routing"))
    implementation(project(":e2e"))
    testImplementation("junit:junit:$junitVersion")
    testImplementation("org.hamcrest:hamcrest:$hamcrestVersion")
}

