pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven {
            google()
            url = java.net.URI("https://oss.sonatype.org/content/repositories/snapshots/")
        }
    }
}

rootProject.name = "MyoScopeAlert"
include(":app")
