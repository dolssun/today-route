buildscript {
    repositories {
        google()
    }
    dependencies {
        classpath(Dep.AndroidX.Navigation.NAVIGATION_SAFE_ARGS_PLUGIN)
        classpath(Dep.AndroidX.Hilt.HILT_PLUGIN)
        classpath("com.google.android.libraries.mapsplatform.secrets-gradle-plugin:secrets-gradle-plugin:2.0.1")
    }
}

// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "7.1.1" apply false
    id("com.android.library") version "7.1.1" apply false
    id("org.jetbrains.kotlin.android") version "1.6.21" apply false
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}