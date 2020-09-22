plugins {
    id("java")
    id("fabric-loom") version "0.4-SNAPSHOT"
    kotlin("jvm") version "1.4.0"
    id("com.github.johnrengelman.shadow") version "6.0.0"
}

val modid = "mchttpapi"
val ktorVersion = "1.4.0"
val kotlinVersion = "1.4.0+build.1"
val fabricApiVersion = "0.21.0+build.407-1.16"
val fabricLoaderVersion = "0.9.3+build.207"
version = "2.0.0"
group = "dev.wnuke"

java {
    sourceCompatibility = JavaVersion.VERSION_14
    targetCompatibility = JavaVersion.VERSION_14
}

sourceSets.main {
    java.srcDirs("src/main/java", "src/main/kotlin")
}

base {
    archivesBaseName = "mchttpapi"
}

dependencies {
    minecraft("com.mojang:minecraft:1.16.3")
    mappings("net.fabricmc:yarn:1.16.3+build.11:v2")

    modImplementation("net.fabricmc:fabric-loader:$fabricLoaderVersion")
    modImplementation("net.fabricmc.fabric-api:fabric-api:$fabricApiVersion")

    implementation("org.jetbrains.kotlin:kotlin-stdlib:$kotlinVersion")
    implementation("net.fabricmc:fabric-language-kotlin:$kotlinVersion")

    implementation("io.ktor:ktor-jackson:$ktorVersion")
    implementation("io.ktor:ktor-server-core:$ktorVersion")
    implementation("io.ktor:ktor-server-netty:$ktorVersion")
}

var shadowOut = file("build/libs/$modid-$version-shadow.jar")

val sourcesJar = tasks.create<Jar>("sourcesJar") {
    archiveClassifier.set("sources")
    from(sourceSets["main"].allSource)
}

tasks {
    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions.jvmTarget = JavaVersion.VERSION_14.toString()
    }
    named<net.fabricmc.loom.task.RemapJarTask>("remapJar") {
        dependsOn(shadowJar)
        mustRunAfter(shadowJar)
        input.set(shadowOut)
        archiveClassifier.set("")
    }
    named<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar>("shadowJar") {
        minimize()
        archiveClassifier.set("shadow")
    }
    named<ProcessResources>("processResources") {
        include("fabric.mod.json")
        include("mixins.json")
    }
}