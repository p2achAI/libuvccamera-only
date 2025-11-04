pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        maven("https://raw.githubusercontent.com/saki4510t/libcommon/master/repository/")
        maven("https://jitpack.io")
    }

    plugins {

        id("com.android.library") version "8.5.2"
        id("org.jetbrains.kotlin.android") version "1.9.24"

    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.PREFER_SETTINGS)
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        maven("https://raw.githubusercontent.com/saki4510t/libcommon/master/repository/")
        maven("https://jitpack.io")
    }
}

rootProject.name = "libuvccamera"