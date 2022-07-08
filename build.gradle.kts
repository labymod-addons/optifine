buildscript {
    repositories {
        var bearerToken = System.getenv("LABYMOD_BEARER_TOKEN")

        if (bearerToken == null && project.hasProperty("net.labymod.distributor.bearer-token")) {
            bearerToken = project.property("net.labymod.distributor.bearer-token").toString()
        }

        maven("https://dist.labymod.net/api/v1/maven/release/") {
            name = "LabyMod Distributor"

            authentication {
                create<HttpHeaderAuthentication>("header")
            }

            credentials(HttpHeaderCredentials::class) {
                name = "Authorization"
                value = "Bearer $bearerToken"
            }
        }


        maven("https://repo.spongepowered.org/repository/maven-public") {
            name = "SpongePowered Repository"
        }
        mavenLocal()
    }

    dependencies {
        classpath("net.labymod.gradle", "addon", "0.2.28")
    }
}

plugins {
    id("java-library")
}

group = "org.example"
version = "1.0.0"

plugins.apply("net.labymod.gradle.addon")

java.toolchain.languageVersion.set(JavaLanguageVersion.of(17))

subprojects {
    plugins.apply("java-library")
    plugins.apply("net.labymod.gradle.addon")

    repositories {
        maven("https://libraries.minecraft.net/")
        maven("https://repo.spongepowered.org/repository/maven-public/")
        mavenLocal()
    }
}

createReleaseJar {
    // Exclude a project from the release jar generation process
    exclude(project(":versions"))

    // You can also exclude version implementation if your addon
    // does not require version implementation
    //
    // exclude(project(":v1_8"))
    // exclude(project(":v1_17"))
    // exclude(project(":v1_18"))
}

addon {
    addonInfo {
        namespace("optifine")
        displayName("OptiFine")
        author("sp614x")
        version(System.getenv().getOrDefault("VERSION", "0.0.0"))
    }


    dev {
        releaseChannel = "develop"
        commitReference = "unknown"
    }

    internalRelease()
}
