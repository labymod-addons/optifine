version = "0.1.0"

plugins {
    id("net.labymod.gradle.vanilla")
    id("net.labymod.gradle.volt")
}

val minecraftGameVersion: String = "1.17.1"
val minecraftVersionTag: String = "1.17"

version = "1.0.0"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

minecraft {
    version(minecraftGameVersion)
    platform(org.spongepowered.gradle.vanilla.repository.MinecraftPlatform.CLIENT)
    runs {
        client {

            val versionRepo = project.gradle.gradleUserHomeDir.toPath()
                    .resolve("caches")
                    .resolve("labymod-gradle")
                    .resolve("repository")
                    .resolve("net")
                    .resolve("minecraft")
                    .resolve("client")
                    .resolve(minecraftGameVersion)

            mainClass("net.minecraft.launchwrapper.Launch")
            args("--tweakClass", "net.labymod.core.loader.vanilla.launchwrapper.LabyModLaunchWrapperTweaker")
            args("--labymod-dev-environment", "true")
            args("--addon-dev-environment", "true")
            jvmArgs("-Dmixin.debug=true")
            jvmArgs("-Doptifine.dev.obf-mc-jar=${versionRepo.resolve("client-$minecraftGameVersion-obfuscated.jar")}")
            jvmArgs("-Doptifine.dev.obf-mappings=${versionRepo.resolve("client-$minecraftGameVersion.proguard")}")
        }
    }
}

dependencies {
    annotationProcessor("org.spongepowered:mixin:0.8.5-SNAPSHOT")

    labyProcessor()
    labyApi("v1_17")
    api(project(":core"))
}

volt {
    mixin {
        compatibilityLevel = "JAVA_16"
        minVersion = "0.8.2"
    }

    packageName("org.example.addon.v1_17.mixins")

    version = minecraftGameVersion
}

intellij {
    minorMinecraftVersion(minecraftVersionTag)
    val javaVersion = project.findProperty("net.labymod.runconfig-v1_17-java-version")

    if (javaVersion != null) {
        run {
            javaVersion(javaVersion as String)
        }
    }
}
