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
        sourceCompatibility = JavaVersion.VERSION_21.toString()
        targetCompatibility = JavaVersion.VERSION_21.toString()
    }

    jar {
        exclude("net/optifine/**")
        duplicatesStrategy = DuplicatesStrategy.INCLUDE
    }
}