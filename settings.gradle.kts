pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}

plugins {
    // Lets Gradle auto-download a matching JDK when kotlin { jvmToolchain(21) }
    // doesn't match any locally installed JDK, instead of failing outright.
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }
}

rootProject.name = "mbt-chasm-kmp"

include(":shared")
