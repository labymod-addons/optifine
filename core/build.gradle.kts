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
    shade("net.minecraftforge:ForgeAutoRenamingTool:0.1.22-local")
}

tasks.jar {
    from(shade.map { if (it.isDirectory) it else zipTree(it) })
    duplicatesStrategy = org.gradle.api.file.DuplicatesStrategy.INCLUDE
}