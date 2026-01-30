rootProject.name = "firepalace"

pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

includeBuild("build-logic")

include(
    "api",
    "common",
    "manager",
    "gui",
    "game-creative"
)
