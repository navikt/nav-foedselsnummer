import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.3.50" // apply false
    `maven-publish`
    publishing
}

repositories {
    mavenCentral()
}

subprojects {
    apply(plugin = "java")
    apply(plugin = "kotlin")
    apply(plugin = "maven-publish")

    group = "nav-foedselsnummer"
    version = "1.0-SNAPSHOT.4"
    repositories {
        mavenCentral()
    }
    dependencies {
        implementation(kotlin("stdlib-jdk8"))
        testImplementation("junit:junit:4.12")
        testImplementation("org.assertj:assertj-core:3.13.2")
    }
    tasks.withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "1.8"
    }

    publishing {
        repositories {
            maven {
                name = "Github"
                url = uri("https://maven.pkg.github.com/navikt/nav-foedselsnummer")
                credentials {
                    username = project.findProperty("gpr.user") as String? ?: System.getenv("GPR_USER")
                    password = project.findProperty("gpr.key") as String? ?: System.getenv("GPR_API_KEY")
                }
            }
        }
        publications {
            register("jar", org.gradle.api.publish.maven.MavenPublication::class) {
                from(components["java"])
                pom {
                    url.set("https://github.com/navikt/nav-foedselsnummer.git")
                }
            }
        }
    }
}

project(":core") {
    dependencies {
        compile("com.fasterxml.jackson.core:jackson-annotations:2.10.0")
    }
}

project(":testutils") {
    dependencies {
        implementation(project(":core"))
    }
}