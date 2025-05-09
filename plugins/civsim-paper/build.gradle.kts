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
    implementation("com.github.Metaphoriker.pathetic:pathetic-bukkit:4.0.4")

    compileOnly(project(":plugins:civmodcore-paper"))
    compileOnly(project(":plugins:itemexchange-paper"))
}

spotless {
    java {
        palantirJavaFormat()
        removeUnusedImports()
        trimTrailingWhitespace()
        endWithNewline()
    }
}
