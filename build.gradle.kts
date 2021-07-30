
buildscript {
    repositories {
        google()
        jcenter()
        mavenCentral()

    }
    dependencies {
        classpath("com.android.tools.build:gradle:7.1.0-alpha02")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.4.21")
        classpath("com.google.gms:google-services:4.3.4")
        classpath("com.google.firebase:firebase-crashlytics-gradle:2.5.2")
    }
}

allprojects {
    repositories {
        google()
        jcenter()
        mavenCentral()
    }
}