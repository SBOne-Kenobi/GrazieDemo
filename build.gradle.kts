plugins {
    kotlin("jvm") version "1.9.0"
}

group = "org.sbone.research.grazie.demo"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()

    maven {
        url = uri("https://packages.jetbrains.team/maven/p/grazi/grazie-platform-public")
    }
}

dependencies {
    implementation("ai.grazie.api:api-gateway-api-jvm:0.3.1")
    implementation("ai.grazie.api:api-gateway-client-jvm:0.3.1")
    implementation("ai.grazie.client:client-ktor-jvm:0.3.1")

    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(17)
}