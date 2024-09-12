import com.google.gson.GsonBuilder
import com.google.gson.annotations.SerializedName
import net.labymod.labygradle.common.extension.model.GameVersion
import net.labymod.labygradle.common.extension.model.labymod.ReleaseChannels
import java.nio.file.Files

plugins {
    id("net.labymod.labygradle")
    id("net.labymod.labygradle.addon")
    id("org.cadixdev.licenser") version ("0.6.1")
}

val versions = providers.gradleProperty("net.labymod.minecraft-versions").get().split(";")

group = "org.example"
version = providers.environmentVariable("VERSION").getOrElse("1.0.0")

labyMod {
    defaultPackageName = "net.labymod.addons.optifine" //change this to your main package name (used by all modules)

    minecraft {
        registerVersion(versions.toTypedArray()) {
            useOptiFine(true)

            mixin {
                val versionMappings = file("./game-runner/mappings/").resolve("$versionId.tsrg")
                if (versionMappings.exists()) {
                    extraMappings.add(versionMappings)
                }
                extraMappings.add(file("./game-runner/mappings/shared.tsrg"))
            }

            val file = file("./game-runner/src/${this.sourceSetName}/resources/optifine-${versionId}.accesswidener");
            accessWidener.set(file)
        }
    }

    addonInfo {
        namespace = "optifine"
        displayName = "OptiFine"
        author = "sp614x"
        version = rootProject.version.toString()
        releaseChannel = ReleaseChannels.PRODUCTION
    }
}

fun GameVersion.useOptiFine(enabled: Boolean) {
    if (!enabled) {
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
    versionManifest.findVersion(versionId)?.apply {
        optiFineVersion.set(this.ofVersion.trim())
    }
}

subprojects {
    plugins.apply("net.labymod.labygradle")
    plugins.apply("net.labymod.labygradle.addon")
    plugins.apply("org.cadixdev.licenser")

    group = rootProject.group
    version = rootProject.version

    license {
        header(rootProject.file("gradle/LICENSE-HEADER.txt"))
        newLine.set(true)
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