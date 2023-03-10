plugins {
    kotlin("multiplatform") version "1.7.21"
    `maven-publish`
    signing
    id("org.jetbrains.dokka") version "1.7.20"
    id("org.jetbrains.kotlinx.kover") version "0.6.1"
}

group = "io.github.seisuke"
version = "0.2.0"

val javadocJar= task<Jar>("dokkaJar") {
    group = JavaBasePlugin.DOCUMENTATION_GROUP
    archiveClassifier.set("javadoc")
}

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
            credentials {
                val nexusUsername: String? by project
                val nexusPassword: String? by project
                username = nexusUsername
                password = nexusPassword
            }

            val releasesRepoUrl = uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
            val snapshotsRepoUrl = uri("https://s01.oss.sonatype.org/content/repositories/snapshots/")
            url = if (version.toString().endsWith("SNAPSHOT")) snapshotsRepoUrl else releasesRepoUrl
        }
    }
    publications.all {
        if (this !is MavenPublication) {
            return@all
        }
        artifact(javadocJar)
        pom {
            name.set("kemoji")
            description.set("Kotlin Multiplatform Framework Emoji Support Library")
            url.set("https://github.com/seisuke/kemoji")

            organization {
                name.set("io.github.seisuke")
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

kover {
    filters {
        classes {
            excludes += listOf(
                "**emojiListGenerator**",
                "**EmojiList"
            )
        }
    }
}

signing {
    sign(publishing.publications)
}
