import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

group = "org.vivecraft"
version = "2.0.0"

plugins {
    `java-library`
    `maven-publish`
    id("com.github.johnrengelman.shadow") version "7.1.0"
    id("net.minecrell.plugin-yml.bukkit") version "0.5.2"
}

configurations {
    compileClasspath.get().extendsFrom(create("shadeOnly"))
}

// See https://github.com/Minecrell/plugin-yml
bukkit {
    main = "org.vivecraft.VSE"
    apiVersion = "1.13"
    website = "https://www.vivecraft.org"
    authors = listOf("jrbudda", "jaron780")
    softDepend = listOf("Vault")
}

repositories {
    mavenCentral()

    maven {
        url = uri("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    }
}

dependencies {
    compileOnly(project(":")) // base project
    compileOnly(project(":Vivecraft_1_19_R1"))
}

// The shadowJar task builds a "fat jar" (a jar with all dependencies built in).
tasks.named<ShadowJar>("shadowJar") {
    classifier = null
    archiveFileName.set("Vivecraft_Spigot_Extensions-${project.version}.jar")
    configurations = listOf(project.configurations["shadeOnly"], project.configurations["runtimeClasspath"])

    // This automatically "shades" (adds to jar) the bstats libs into the
    // org.vivecraft.bstats package.
    dependencies {
        include(":") // base project
        include(":Vivecraft_1_19_R1", "reobf")

        relocate("org.bstats", "org.vivecraft.bstats") {
            include(dependency("org.bstats:"))
        }
    }
}

tasks.named("assemble").configure {
    dependsOn("shadowJar")
}

tasks {
    compileJava {
        options.encoding = Charsets.UTF_8.name() // We want UTF-8 for everything
        options.release.set(8)
    }
}