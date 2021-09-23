plugins {
    id("java-library")
}

repositories {
    mavenLocal()
}

dependencies {
    laby.addonProcessor()
    laby.api()
}