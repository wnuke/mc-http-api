pluginManagement {
    repositories {
        maven(url="https://maven.fabricmc.net/") {
            name = "Fabric"
        }
        maven(url = "https://jitpack.io") {
            name = "Jitpack"
        }
        mavenLocal()
        mavenCentral()
        jcenter()
        gradlePluginPortal()
    }
}