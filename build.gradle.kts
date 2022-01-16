
buildscript {
    repositories {
        google()
        mavenCentral()


    }


    dependencies {
        classpath("com.android.tools.build:gradle:7.1.0-rc01")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.6.10")
        classpath("com.google.gms:google-services:4.3.10")
        classpath("com.google.firebase:firebase-crashlytics-gradle:2.8.1")

        classpath("com.google.dagger:hilt-android-gradle-plugin:2.38.1")
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()

        maven { setUrl("https://jitpack.io") }
    }
}