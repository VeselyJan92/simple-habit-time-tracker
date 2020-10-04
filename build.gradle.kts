// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    repositories {
        jcenter()
        mavenCentral()
        google()

        maven {
            url = uri("https://dl.bintray.com/kotlin/kotlin-dev")
        }
    }
    dependencies {
        classpath ("com.android.tools.build:gradle:4.2.0-alpha13")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.4.10")
        classpath ("com.google.dagger:hilt-android-gradle-plugin:2.28-alpha")
        //classpath ("androidx.navigation:navigation-safe-args-gradle-plugin:2.3.0")


        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle files
    }
}

allprojects {
    repositories {
        maven {
            url = uri("https://dl.bintray.com/kotlin/kotlin-dev")
        }
        jcenter()
        google()
        mavenCentral()
    }


}


/*
task clean(type: Delete) {
    delete rootProject.buildDir
}*/
