plugins {
    application
    id("io.github.ngyewch.nfpm")
}

project.version = "0.1.0"

repositories {
    mavenCentral()
}

application {
    mainClass = "Main"
}

nfpm {
    packageName = "test2"
    packagers = listOf("deb")
}
