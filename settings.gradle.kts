// WiseCalA — android-compose (정적 템플릿 / static scaffold)
pluginManagement {
    repositories { google(); mavenCentral(); gradlePluginPortal() }
}
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.10.0"
}
dependencyResolutionManagement {
    repositories { google(); mavenCentral() }
}
rootProject.name = "WiseCalA"
include(":app")
