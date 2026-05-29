import net.labymod.labygradle.common.extension.LabyModAnnotationProcessorExtension.ReferenceType

dependencies {
    labyProcessor()
    labyApi("core")
    labyApi("loader-vanilla-launchwrapper")
    api(project(":api"))

    addonMavenDependency("com.fasterxml.jackson.core:jackson-core:2.18.0")
}

labyModAnnotationProcessor {
    referenceType = ReferenceType.DEFAULT
}

tasks {
    processResources {
        from(rootProject.file("game-runner/mappings/optifine")) {
            into("assets/optifine/mappings")
            include("*.srg")
        }
    }

    jar {
        exclude("net/optifine/**")
        duplicatesStrategy = DuplicatesStrategy.INCLUDE
    }
}