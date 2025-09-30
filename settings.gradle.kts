rootProject.name = "ktor-krpc-sample"

dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }
}

include(":server")
include(":core")
include(":client")
