pluginManagement {
    repositories {
        maven("https://maven.fabricmc.net/") { name = "Fabric" }
        maven("https://repo.papermc.io/repository/maven-public/") { name = "PaperMC" }
        mavenCentral()
        gradlePluginPortal()
    }

    includeBuild("build-logic")
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

rootProject.name = "HeroBot"

// Fabric mod not ported to 26.3 yet; only the Paper plugin is built.
include(":herobot-common", ":herobot-plugin")
