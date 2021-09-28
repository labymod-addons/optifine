plugins {
	id("net.labymod.gradle.legacyminecraft")
	id("net.labymod.gradle.mixin")
}

dependencies {
    laby.addonProcessor()
    laby.minecraft()
    api(project(":core"))
}

legacyMinecraft {
	version("1.8.9")
	mappingFile(File(rootProject.projectDir, "mappings/1.8.9.srg"))
}

mixin {
	version("1.8.9")
	addReferenceMap(sourceSets.findByName("main"), "labymod.refmap.json")
}

tasks.compileJava {
    sourceCompatibility = JavaVersion.VERSION_1_8.toString()
    targetCompatibility = JavaVersion.VERSION_1_8.toString()
}