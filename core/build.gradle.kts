plugins {
    id("java-library")
    id("com.github.johnrengelman.shadow") version ("7.1.2")
}

repositories {
    mavenLocal()
}

val shade = configurations.create("shade")
configurations.getByName("api").extendsFrom(shade)

dependencies {
    labyProcessor("processor")
    labyApi("core")
    api(project(":api"))

    // FIXME
    // In theory, LabyMod should prove a remapping service
    shade(rootProject.files("libs/ForgeAutoRenamingTool-0.1.24-all.jar"))
}

tasks {
    compileJava {
        sourceCompatibility = JavaVersion.VERSION_1_8.toString()
        targetCompatibility = JavaVersion.VERSION_1_8.toString()
    }

    jar {
        exclude("net/optifine/**")
        from(shade.map { if (it.isDirectory) it else zipTree(it) })
        duplicatesStrategy = DuplicatesStrategy.INCLUDE
    }
}