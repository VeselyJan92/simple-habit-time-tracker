import org.jetbrains.kotlin.konan.properties.Properties

plugins {
    id("com.android.application")
    id("com.github.triplet.play") version "3.12.1"
    id("kotlin-android")
    id("dagger.hilt.android.plugin")

    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
    id("com.google.devtools.ksp")

    id("org.jetbrains.kotlin.plugin.compose")
    id("org.jetbrains.kotlin.plugin.parcelize")
    id("org.jetbrains.kotlin.plugin.serialization")
}

repositories {
    maven { setUrl("https://jitpack.io") }
}

android {
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
        // Enable core library desugaring
        isCoreLibraryDesugaringEnabled = true
    }

    signingConfigs {
        create("release") {
            val properties = Properties().apply {
                load(File("../imfibit-tracker-keystore/signing.properties").reader())
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

    compileSdk = 36

    buildFeatures {
        compose = true
        buildConfig = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    ksp {
        arg("room.generateKotlin", "true")
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    defaultConfig {
        applicationId = "com.imfibit.activitytracker"
        minSdk = 26
        targetSdk = 36
        versionCode = 54
        versionName = "1.4.3"
        testInstrumentationRunner  ="com.imfibit.activitytracker.HiltRunner"
    }

    buildTypes {
        getByName("release") {

            isMinifyEnabled = true

            manifestPlaceholders["crashlyticsCollectionEnabled"] = true

            signingConfig = signingConfigs.getByName("release")
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
        }

        getByName("debug") {
            manifestPlaceholders["crashlyticsCollectionEnabled"] = false
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")

            applicationIdSuffix = ".debug"
            versionNameSuffix = " - DEBUG"


            isDebuggable = true
        }

        create("stage") {
            initWith(getByName("release"))
            applicationIdSuffix = ".stage"
            versionNameSuffix = " - Stage"
        }

    }

    namespace = "com.imfibit.activitytracker"

}

play {
    serviceAccountCredentials.set(File("../../imfibit-tracker-keystore/publish-key.json"))
    track.set("production")
}

dependencies {
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.1.5")

    implementation("org.jetbrains.kotlin:kotlin-stdlib")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:1.9.0") //For navigation


    implementation(platform("com.google.firebase:firebase-bom:34.0.0"))
    implementation("com.google.firebase:firebase-crashlytics")


    val COMPOSE_VERSION = "1.8.3"
    implementation("androidx.compose.animation:animation:$COMPOSE_VERSION")
    implementation("androidx.compose.foundation:foundation:$COMPOSE_VERSION")
    implementation("androidx.activity:activity-compose:1.10.1")
    implementation("androidx.compose.material3:material3-android:1.4.0-alpha18")
    implementation("androidx.compose.material:material-icons-extended:1.7.8")
    implementation("sh.calvin.reorderable:reorderable:2.5.1")


    implementation("androidx.core:core-ktx:1.16.0")
    implementation("androidx.glance:glance-appwidget:1.1.1")
    implementation("androidx.datastore:datastore-preferences:1.2.0-alpha02")

    implementation("androidx.navigation:navigation-compose:2.9.2")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.9.2")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.9.2")


    implementation("androidx.paging:paging-runtime-ktx:3.3.6")
    implementation("androidx.paging:paging-compose:3.3.6")

    val ROOM_VERSION = "2.7.2"
    ksp("androidx.room:room-compiler:$ROOM_VERSION")
    implementation("androidx.room:room-runtime:$ROOM_VERSION")
    implementation("androidx.room:room-ktx:$ROOM_VERSION")


    implementation("androidx.hilt:hilt-common:1.3.0-alpha02")
    implementation("com.google.dagger:hilt-android:2.57")
    ksp("com.google.dagger:hilt-android-compiler:2.57")
    ksp("androidx.hilt:hilt-compiler:1.3.0-alpha02")
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")

    debugImplementation("androidx.compose.ui:ui-tooling:$COMPOSE_VERSION")
    implementation("androidx.compose.ui:ui-tooling-preview:$COMPOSE_VERSION")


    androidTestImplementation("com.google.dagger:hilt-android-testing:2.57")

    // Needed for createAndroidComposeRule, but not createComposeRule:
    debugImplementation("androidx.compose.ui:ui-test-manifest:$COMPOSE_VERSION")

    // Test rules and transitive dependencies:
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:$COMPOSE_VERSION")
    testImplementation("androidx.test:core:1.6.1")
    androidTestImplementation("androidx.test:runner:1.6.2")
    androidTestImplementation("androidx.test:rules:1.6.1")

}
