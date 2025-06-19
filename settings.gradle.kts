pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven(url = "https://jitpack.io")
    }
}

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

gradle.settingsEvaluated {
    println("✅ Gradle toolchain auto-download enabled")
}

gradle.rootProject {
    extensions.extraProperties["org.gradle.jvm.toolchain.install"] = true
}

rootProject.name = "MorseXpress"
include(":app")
 