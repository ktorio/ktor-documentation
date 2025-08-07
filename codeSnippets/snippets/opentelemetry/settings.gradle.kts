rootProject.name = "opentelemetry"

dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }
}

include(":core")
include(":client")
include(":server")
