plugins {
    id("io.papermc.paperweight.userdev")
    id("io.freefair.lombok") version "8.12.1"
    id("com.diffplug.spotless") version "6.18.0"
}

version = "0.0.1"

dependencies {
    paperweight {
        paperDevBundle(libs.versions.paper)
    }

    compileOnly(project(":plugins:civmodcore-paper"))
    compileOnly(project(":plugins:itemexchange-paper"))
//    implementation("org.codejargon.feather:feather:1.0")
}

spotless {
    java {
        palantirJavaFormat()
        removeUnusedImports()
        trimTrailingWhitespace()
        endWithNewline()
    }
}
