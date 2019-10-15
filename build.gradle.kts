import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.3.50" // apply false
}

subprojects {
    apply(plugin = "java")
    apply(plugin = "kotlin")
    group = "nav-foedselsnummer"
    version = "1.0"
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