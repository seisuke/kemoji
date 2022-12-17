plugins {
    kotlin("jvm") version "1.7.21"
    id("org.jetbrains.kotlin.plugin.serialization") version "1.7.21"
}

group = "com.github.seisuke"
version = "0.1"

dependencies {
    implementation("com.squareup:kotlinpoet:1.12.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.4.1")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}
