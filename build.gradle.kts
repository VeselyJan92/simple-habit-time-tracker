buildscript {
    repositories {
        google()
        mavenCentral()
    }


    dependencies {
        classpath("com.android.tools.build:gradle:8.13.1")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:2.2.21")
        classpath("com.google.gms:google-services:4.4.4")
        classpath("com.google.firebase:firebase-crashlytics-gradle:3.0.6")
        classpath("com.google.dagger:hilt-android-gradle-plugin:2.57.2")
    }
}

plugins {
    id("org.jetbrains.kotlin.plugin.compose") version "2.2.21" apply false
    id("com.google.devtools.ksp") version "2.2.21-2.0.4" apply false
    id("org.jetbrains.kotlin.plugin.parcelize") version "2.2.21" apply false
    id("org.jetbrains.kotlin.plugin.serialization") version "2.2.21" apply false
}

allprojects {
    repositories {
        google()
        mavenCentral()
        maven { setUrl("https://jitpack.io") }
    }
}