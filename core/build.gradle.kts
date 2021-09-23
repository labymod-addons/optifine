plugins {
    id("java-library")
}

repositories {
    mavenLocal()
}

dependencies {
    laby.addonProcessor()
    laby.core()
    api(project(":api"))
}

addon {
    internalRelease()
}