plugins {
    id("org.spongepowered.gradle.vanilla")
    id("net.labymod.gradle.mixin")
}

version = "1.0.0"

minecraft {
    version("1.17.1")
    runs {
        client() {
            mainClass("net.minecraft.launchwrapper.Launch")
            args("--tweakClass", "net.labymod.core.loader.launch.LabyModTweaker")
            args("--labymod-dev-environment", "true")
            args("--addon-dev-environment", "true")
        }
    }
}

dependencies {
    laby.addonProcessor()
    laby.minecraft()
    api(project(":core"))
}

mixin {
    version("1.17.1")
    addReferenceMap(sourceSets.findByName("main"), "labymod.refmap.json")
}