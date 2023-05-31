plugins {
    id("java-library")
}

dependencies {
    labyApi("core")
    labyApi("loader-vanilla-launchwrapper")
    api(project(":api"))
}

labyModProcessor {
    referenceType = net.labymod.gradle.core.processor.ReferenceType.DEFAULT
}

tasks {
    compileJava {
        sourceCompatibility = JavaVersion.VERSION_17.toString()
        targetCompatibility = JavaVersion.VERSION_17.toString()
    }

    jar {
        exclude("net/optifine/**")
        duplicatesStrategy = DuplicatesStrategy.INCLUDE
    }
}