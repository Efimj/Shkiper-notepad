buildscript {
    dependencies {
        classpath(libs.hilt.gradle)
        classpath(libs.realm.gradle.plugin)
        classpath(libs.detekt.gradle)
        classpath(libs.agp.gradle)
        classpath(libs.kotlin.gradle)
    }
}

// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.7.0-alpha07" apply false
    id("com.android.library") version "8.7.0-alpha07" apply false
    id("org.jetbrains.kotlin.android") version "1.9.24" apply false
    id("io.realm.kotlin") version "1.16.0" apply false
    id("com.google.devtools.ksp") version "1.9.24-1.0.20" apply false
    id("com.google.dagger.hilt.android") version "2.51.1" apply false
    id("com.android.test") version "8.7.0-alpha07" apply false
    alias(libs.plugins.baselineprofile) apply false
}

tasks.register("clean", Delete::class) {
    delete(rootProject.layout.buildDirectory)
}