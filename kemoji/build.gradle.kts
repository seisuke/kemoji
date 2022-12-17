plugins {
    kotlin("multiplatform") version "1.7.21"
}

group = "com.github.seisuke"
version = "0.1"

kotlin {
    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "11"
        }
        withJava()
        testRuns["test"].executionTask.configure {
            useJUnitPlatform()
        }
    }
    js(IR) {
        browser {}
        binaries.executable()
    }
    sourceSets {
        val commonMain by getting
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        val jvmMain by getting
        val jvmTest by getting {
            dependencies {
                implementation("org.junit.jupiter:junit-jupiter:5.8.2")
                implementation("org.junit.jupiter:junit-jupiter-params:5.8.2")
            }
        }
        val jsMain by getting
        val jsTest by getting
    }
}
