import org.jetbrains.kotlin.konan.properties.Properties

plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-kapt")

    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
}

android {
    signingConfigs {
        create("release"){
            val properties = Properties().apply {
                load(File("signing.properties").reader())
            }

            storeFile = File(properties.getProperty("key_store"))
            storePassword = properties.getProperty("key_store_password")
            keyPassword = properties.getProperty("key_password")
            keyAlias = properties.getProperty("key_alias")
        }

        getByName("debug") {
            storeFile = rootProject.file("debug.keystore")
            storePassword = "android"
            keyAlias = "androiddebugkey"
            keyPassword = "android"
        }

    }

    compileSdkVersion(30)
    buildToolsVersion = "30.0.2"

    buildFeatures {
        compose =  true
    }

    lintOptions {

        isAbortOnError = false
        lintOptions {


            checkOnly("release")
        }
    }

    composeOptions {
        kotlinCompilerVersion = "1.4.20"
        kotlinCompilerExtensionVersion ="1.0.0-alpha08"

    }

    compileOptions {
        sourceCompatibility =  JavaVersion.VERSION_1_8
        targetCompatibility  = JavaVersion.VERSION_1_8
    }


    kotlinOptions {
        jvmTarget = "1.8"
        useIR = true
    }

    defaultConfig {
        applicationId ="com.imfibit.activitytracker"
        minSdkVersion(26)
        targetSdkVersion(30)
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            manifestPlaceholders["crashlyticsCollectionEnabled"] = true

            signingConfig = signingConfigs.getByName("release")
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
            debuggable(true)
            versionNameSuffix = " - PROD"
        }

        getByName("debug") {
            manifestPlaceholders["crashlyticsCollectionEnabled"] = false
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
            debuggable(true)

            applicationIdSuffix =  ".debug"
            versionNameSuffix = " - DEBUG"

        }

    }

}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.4.20")
    implementation("com.thedeanda:lorem:2.1")


    implementation(platform("com.google.firebase:firebase-bom:25.11.0"))
    implementation("com.google.firebase:firebase-crashlytics-ktx")
    implementation("com.google.firebase:firebase-analytics-ktx")


    implementation("androidx.appcompat:appcompat:1.2.0")

    implementation("androidx.lifecycle:lifecycle-extensions:2.2.0")


    implementation("androidx.navigation:navigation-compose:1.0.0-alpha03")

    implementation("androidx.compose.compiler:compiler:1.0.0-alpha08")
    implementation("androidx.compose.ui:ui:1.0.0-alpha08")
    implementation("androidx.compose.material:material:1.0.0-alpha08")
    implementation("androidx.compose.runtime:runtime-livedata:1.0.0-alpha08")
    implementation("androidx.compose.foundation:foundation:1.0.0-alpha08")
    implementation("androidx.compose.material:material-icons-extended:1.0.0-alpha08")


    implementation ("androidx.room:room-runtime:2.3.0-alpha03")
    kapt("androidx.room:room-compiler:2.3.0-alpha03")
    implementation ("androidx.room:room-ktx:2.3.0-alpha03")
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions {
        jvmTarget = "1.8"
        freeCompilerArgs +=  "-Xallow-jvm-ir-dependencies"
        freeCompilerArgs +=  "-Xskip-prerelease-check"
        freeCompilerArgs +=  "-Xopt-in=kotlin.Experimental"
        freeCompilerArgs +=  "-Xopt-in=kotlin.RequiresOptIn"
    }
}
