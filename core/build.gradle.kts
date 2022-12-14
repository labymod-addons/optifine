version = "0.1.0"

plugins {
    id("java-library")
    id("com.github.johnrengelman.shadow") version ("7.1.2")
}

repositories {
    mavenLocal()
}

dependencies {
    labyProcessor()
    labyApi("core")
    api(project(":api"))
}

tasks.compileJava {
    sourceCompatibility = JavaVersion.VERSION_1_8.toString()
    targetCompatibility = JavaVersion.VERSION_1_8.toString()
}

val shade = configurations.create("shade")
configurations.getByName("api").extendsFrom(shade)


dependencies {
    // FIXME
    // In theory, LabyMod should prove a remapping service
    shade(rootProject.files("libs/ForgeAutoRenamingTool-0.1.24-all.jar"))
}

tasks.jar {
    exclude("net/optifine/**")
    from(shade.map { if (it.isDirectory) it else zipTree(it) })
    duplicatesStrategy = org.gradle.api.file.DuplicatesStrategy.INCLUDE
}