import org.gradle.internal.impldep.org.junit.experimental.categories.Categories.CategoryFilter.include

plugins {
    kotlin("jvm") version "2.0.0"
    application
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "cn.cutemic.jujiubot.warden"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.mongodb:mongodb-driver-kotlin-coroutine:5.1.2")
    implementation("org.mongodb:bson-kotlinx:5.1.2")
    implementation("it.unimi.dsi:fastutil:8.2.1")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(17)
}

application {
    mainClass = "cn.cutemic.jujiubot.warden.MainKt"
}

tasks.jar{
    manifest{
        attributes("Main-Class" to "cn.cutemic.jujiubot.warden.MainKt")
    }
}
