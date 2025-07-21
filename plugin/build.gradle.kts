plugins {
    `java-gradle-plugin`
    `maven-publish`
    id("ca.cutterslade.analyze") version "2.0.0"
    id("com.asarkar.gradle.build-time-tracker") version "5.0.1"
    id("com.diffplug.spotless") version "7.2.0"
    id("com.github.ben-manes.versions") version "0.52.0"
    id("io.github.ngyewch.git-describe") version "0.2.0"
    id("com.gradle.plugin-publish") version "1.3.1"
    id("se.ascp.gradle.gradle-versions-filter") version "0.1.16"
}

group = "io.github.ngyewch.gradle"
version = "0.1.0"

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

dependencies {
    implementation(gradleApi())
}

repositories {
    mavenCentral()
}

gradlePlugin {
    website.set("https://github.com/ngyewch/gradle-nfpm-plugin")
    vcsUrl.set("https://github.com/ngyewch/gradle-nfpm-plugin.git")
    plugins {
        create("gradle-nfpm-plugin") {
            id = "io.github.ngyewch.nfpm"
            displayName = "Gradle nfpm Plugin"
            description = "Gradle plugin for nfpm."
            implementationClass = "io.github.ngyewch.gradle.nfpm.NfpmPlugin"
            tags.set(listOf("nfpm"))
        }
    }
}

versionsFilter {
    gradleReleaseChannel.set("current")
    checkConstraints.set(true)
    outPutFormatter.set("json")
}

spotless {
    java {
        googleJavaFormat("1.28.0").reflowLongStrings().skipJavadocFormatting()
        formatAnnotations()
        targetExclude("build/**")
    }
}
