plugins {
    id("java-library")
}

repositories {
    mavenLocal()
}

dependencies {
    //laby.addonProcessor()
    //laby.core()

    annotationProcessor(files("../libs/addon-annotation-processor-0.1.0-local.jar"))
    annotationProcessor(files("../libs/models-0.1.0-local.jar"))
    annotationProcessor("com.google.code.gson:gson:2.8.6")
    api(files("../libs/core-4.0.0-local.jar"))
    api("commons-io:commons-io:2.5")
    api("net.minecraft:launchwrapper:3.0.5")
    api(project(":api"))
}

addon {
    internalRelease()
}

tasks.compileJava {
    sourceCompatibility = JavaVersion.VERSION_1_8.toString()
    targetCompatibility = JavaVersion.VERSION_1_8.toString()
}

tasks.jar {
    archiveBaseName.set("optifine")
}