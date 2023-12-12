import com.google.gson.GsonBuilder
import com.google.gson.annotations.SerializedName
import net.labymod.gradle.core.minecraft.provider.VersionProvider
import java.nio.file.Files
import java.util.function.Supplier

plugins {
    id("java-library")
    id("net.labymod.gradle")
    id("net.labymod.gradle.addon")
    id("org.cadixdev.licenser") version ("0.6.1")
}

group = "org.example"
version = "1.0.0"

java.toolchain.languageVersion.set(JavaLanguageVersion.of(17))

labyMod {
    defaultPackageName = "net.labymod.addons.optifine" //change this to your main package name (used by all modules)

    addonInfo {
        namespace = "optifine"
        displayName = "OptiFine"
        author = "sp614x"
        version = System.getenv().getOrDefault("VERSION", "0.0.1")
    }

    minecraft {
        registerVersions(
                "1.8.9",
                "1.12.2",
                "1.16.5",
                "1.17.1",
                "1.18.2",
                "1.19.2",
                "1.19.3",
                "1.19.4",
                "1.20.1"
        ) { version, provider ->
            configureRun(provider, version)
            provider.applyOptiFine(version, true)

            provider.accessWidener = Supplier {
                val sourceSetName = version.replace(".", "_").replace("-", "_")
                file("./game-runner/src/v$sourceSetName/resources/optifine-$version.accesswidener")
            }
        }

        subprojects.forEach {
            if (it.name != "game-runner") {
                filter(it.name)
            }
        }
    }

    addonDev {
        productionRelease()
    }
}

fun VersionProvider.applyOptiFine(version: String, useOptiFine: Boolean) {
    if (!useOptiFine) {
        return
    }

    val extra = project.extra
    val versionManifest: OptiVersionManifest
    val gson = GsonBuilder().create()
    if (!extra.has("of-cache")) {

        val file = rootProject.file("./core/src/main/resources/assets/optifine/versions.json")
        if (!file.exists()) {
            println("Failed to find versions.json")
            return
        }

        versionManifest = Files.newBufferedReader(file.toPath()).use { gson.fromJson(it, OptiVersionManifest::class.java) }
        extra["of-cache"] = gson.toJson(versionManifest)
    } else {
        versionManifest = gson.fromJson(extra.get("of-cache") as String, OptiVersionManifest::class.java)
    }
    versionManifest.findVersion(version)?.apply {
        optiFineVersion = this.ofVersion.trim()
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


fun configureRun(provider: VersionProvider, gameVersion: String) {
    provider.runConfiguration {
        mainClass = "net.minecraft.launchwrapper.Launch"
        jvmArgs("-Dnet.labymod.running-version=${gameVersion}")
        jvmArgs("-Dmixin.debug=true")
        jvmArgs("-Dnet.labymod.debugging.all=true")
        jvmArgs("-Dmixin.env.disableRefMap=true")

        args("--tweakClass", "net.labymod.core.loader.vanilla.launchwrapper.LabyModLaunchWrapperTweaker")
        args("--labymod-dev-environment", "true")
        args("--addon-dev-environment", "true")
    }

    provider.javaVersion = JavaVersion.VERSION_17

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
        extraMappings.add(file("./game-runner/mappings/shared.tsrg"))
    }
}

data class OptiVersionManifest(val versions: List<OptiFineVersion>) {

    fun findVersion(gameVersion: String): OptiFineVersion? {
        return versions.find { it.gameVersion == gameVersion }
    }

}

data class OptiFineVersion(
        @SerializedName("game_version") val gameVersion: String,
        @SerializedName("of_version") val ofVersion: String,
        @SerializedName("preview") val preview: Boolean
)