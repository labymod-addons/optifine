version = "0.1.0"

plugins {
    id("java-library")
    id("com.github.johnrengelman.shadow") version("7.1.2")
}

repositories {
    mavenLocal()
}

dependencies {
    labyProcessor()
    labyApi("core")
    api(project(":api"))
}

tasks.compileJava {
    sourceCompatibility = JavaVersion.VERSION_1_8.toString()
    targetCompatibility = JavaVersion.VERSION_1_8.toString()
}

dependencies {
    api("net.minecraftforge:ForgeAutoRenamingTool:0.1.22-local")
}

tasks.shadowJar {

    dependencies {
        exclude(fun(it: ResolvedDependency): Boolean {
            if (it.moduleGroup.startsWith("net.labymod4")) {
                if (it.moduleName == "sponge-mixin") {
                    return true;
                }

                if (it.moduleName == "fabric-loader") {
                    return true;
                }

                return false;
            }

            if(it.moduleGroup.startsWith("net.minecraftforge")) {
                return false;
            }

            return true;
        })
    }

}
