buildscript {
    repositories {
        maven("https://dist.labymod.net/api/v1/maven/release/") {
            name = "LabyMod Distributor"
        }

        maven("https://repo.spongepowered.org/repository/maven-public") {
            name = "SpongePowered Repository"
        }
        mavenLocal()
    }

    dependencies {
        classpath("net.labymod.gradle", "addon", "0.3.0-pre5")
    }
}

plugins {
    id("java-library")
    id("org.cadixdev.licenser") version ("0.6.1")
}

plugins.apply("net.labymod.gradle")
plugins.apply("net.labymod.gradle.addon")

group = "org.example"
version = "1.0.0"

java.toolchain.languageVersion.set(JavaLanguageVersion.of(17))

labyMod {
    defaultPackageName = "net.labymod.addons.damageindicator" //change this to your main package name (used by all modules)
    addonInfo {
        namespace = "optifine"
        displayName = "OptiFine"
        author = "sp614x"
        version = System.getenv().getOrDefault("VERSION", "0.0.1")
    }

    minecraft {
        registerVersions("1.8.9", "1.17.1", "1.18.2", "1.19.2", "1.19.3") { version, provider ->
            configureRun(provider, version)
        }

        subprojects.forEach {
            if (it.name != "game-runner") {
                filter(it.name)
            }
        }
    }

    addonDev {
        localRelease()
        //snapshotRelease()
    }
}

subprojects {
    plugins.apply("java-library")
    plugins.apply("net.labymod.gradle")
    plugins.apply("net.labymod.gradle.addon")
    plugins.apply("org.cadixdev.licenser")

    repositories {
        maven("https://libraries.minecraft.net/")
        maven("https://repo.spongepowered.org/repository/maven-public/")
        mavenLocal()
    }

    license {
        header(rootProject.file("gradle/LICENSE-HEADER.txt"))
        newLine.set(true)
    }
}

fun configureRun(provider: net.labymod.gradle.core.minecraft.provider.VersionProvider, gameVersion: String) {
    provider.runConfiguration {
        mainClass = "net.minecraft.launchwrapper.Launch"
        jvmArgs("-Dnet.labymod.running-version=${gameVersion}")
        jvmArgs("-Dmixin.debug=true")
        jvmArgs("-Dnet.labymod.debugging.all=true")

        if (org.gradle.internal.os.OperatingSystem.current() == org.gradle.internal.os.OperatingSystem.MAC_OS) {
            jvmArgs("-XstartOnFirstThread")
        }

        args("--tweakClass", "net.labymod.core.loader.vanilla.launchwrapper.LabyModLaunchWrapperTweaker")
        args("--labymod-dev-environment", "true")
        args("--addon-dev-environment", "true")
    }

    provider.javaVersion = when (gameVersion) {
        "1.8.9", "1.12.2", "1.16.5" -> {
            JavaVersion.VERSION_1_8
        }

        "1.17.1" -> {
            JavaVersion.VERSION_16
        }

        else -> {
            JavaVersion.VERSION_17
        }
    }

    provider.mixin {
        val mixinMinVersion = when (gameVersion) {
            "1.8.9", "1.12.2", "1.16.5" -> {
                "0.6.6"
            }

            else -> {
                "0.8.2"
            }
        }

        minVersion = mixinMinVersion
    }
}
