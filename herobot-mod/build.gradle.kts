plugins {
    alias(libs.plugins.fabric.loom)
    id("herobot.java")
    id("herobot.shade")
    `maven-publish`
}

description = "HeroBot for Fabric."

version = extra["artifactVersion"] as String

base {
    archivesName = "herobot-fabric"
}

loom {
    splitEnvironmentSourceSets()

    mods {
        register("herobot") {
            sourceSet(sourceSets.main.get())
            sourceSet(sourceSets["client"])
        }
    }
}

repositories {
    maven("https://api.modrinth.com/maven") { name = "Modrinth" }
    maven("https://maven.fabricmc.net/") { name = "Fabric" }
}

// What gets bundled into the mod jar: `common` plus everything it exposes as `api`
// (pathetic). Transitive, unlike the rest of the runtime classpath, which is Minecraft
// and the mod dependencies and must stay out of the jar.
val shade = configurations.dependencyScope("shade")
val shadeClasspath = configurations.resolvable("shadeClasspath") {
    extendsFrom(shade.get())
}

dependencies {
    minecraft(libs.minecraft)
    implementation(libs.fabric.loader)

    implementation(libs.fabric.api)

    // Carpet for 26.3: the CI looks up the current version on Modrinth and passes -PcarpetVersion.
    compileOnly("maven.modrinth:carpet:${findProperty("carpetVersion") ?: libs.versions.carpet.get()}")

    compileOnly(libs.voicechat.api)
    "clientCompileOnly"(libs.voicechat.api)

    implementation(projects.herobotCommon)
    add(shade.name, projects.herobotCommon)
}

tasks {
    processResources {
        val props = mapOf(
            "version" to project.version,
            "minecraft_version" to libs.versions.minecraft.get(),
            "loader_version" to libs.versions.fabric.loader.get(),
        )

        inputs.properties(props)
        filteringCharset = "UTF-8"

        filesMatching("fabric.mod.json") {
            expand(props)
        }
    }

    // 26.x ships unobfuscated, so there is no remapJar any more: the shadowJar is the final mod jar.
    shadowJar {
        archiveClassifier = ""

        configurations = listOf(shadeClasspath.get())

        // Loom adds the client source set to `jar`, not to shadowJar.
        from(sourceSets["client"].output)

        from(rootProject.file("LICENSE.txt")) {
            rename { "${it}_${base.archivesName.get()}" }
        }
    }

    jar {
        archiveClassifier = "thin"
    }
}

java {
    // Loom attaches sourcesJar to a RemapSourcesJar task and to `build` when present.
    withSourcesJar()
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            artifactId = base.archivesName.get()
            from(components["java"])
        }
    }
}
