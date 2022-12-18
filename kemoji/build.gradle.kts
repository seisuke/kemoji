plugins {
    kotlin("multiplatform") version "1.7.21"
    `maven-publish`
}

group = "io.github.seisuke"
version = "0.1.0"

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

publishing {
    repositories {
        maven {
            name = "kemoji"
            url = uri("https://maven.pkg.github.com/seisuke/kemoji")
            credentials {
                username = project.findProperty("gpr.user") as String? ?: System.getenv("USERNAME")
                password = project.findProperty("gpr.key") as String? ?: System.getenv("TOKEN")
            }
        }
    }
    publications {
        register<MavenPublication>("gpr") {
            //from(components["kemoji"])
            pom {
                name.set("seisuke")
                description.set("Kotlin Multiplatform Framework Emoji Support Library")
                url.set("https://github.com/seisuke/kemoji")

                organization {
                    name.set("com.github.seisuke")
                    url.set("https://github.com/seisuke")
                }
                licenses {
                    license {
                        name.set("MIT")
                        url.set("https://github.com/seiske/kemoji/blob/master/LICENSE")
                    }
                }
                issueManagement {
                    system.set("Github")
                    url.set("https://github.com/seisuke/kemoji/issues")
                }
                scm {
                    url.set("https://github.com/seisuke/kemoji")
                    connection.set("scm:git:git://github.com/seisuke/kemoji.git")
                    developerConnection.set("https://github.com/seisuke/kemoji")
                }
                developers {
                    developer {
                        name.set("seisuke")
                    }
                }
            }
        }
    }
}
