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
            url = java.net.URI("https://jitpack.io")

//            name = "TarsosDSP repository"
//            url = java.net.URI("https://mvn.0110.be/releases")
        }
    }
}

rootProject.name = "MyoScopeAlert"
include(":app")
