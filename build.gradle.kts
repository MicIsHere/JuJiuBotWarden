plugins {
    kotlin("jvm") version "2.0.0"
    application
}

group = "cn.cutemic.jujiubot.warden"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.mongodb:mongodb-driver-kotlin-coroutine:5.1.2")
    implementation("org.mongodb:bson-kotlinx:5.1.2")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(17)
}

application {
    mainClass = "cn.cutemic.jujiubot.warden.Main"
}