import com.android.build.api.dsl.ApplicationExtension
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.konan.properties.Properties

plugins {
    id("com.android.application")
    id("com.github.triplet.play") version "4.0.0"
    id("dagger.hilt.android.plugin")

    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
    id("com.google.devtools.ksp")

    id("org.jetbrains.kotlin.plugin.compose")
    id("org.jetbrains.kotlin.plugin.parcelize")
    id("org.jetbrains.kotlin.plugin.serialization") version "2.3.20"
}

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_17)
        freeCompilerArgs.add("-opt-in=kotlin.time.ExperimentalTime")
    }
}

configure<ApplicationExtension> {
    namespace = "com.imfibit.activitytracker"

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
        isCoreLibraryDesugaringEnabled = true
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    compileSdk = 36

    defaultConfig {
        applicationId = "com.imfibit.activitytracker"
        minSdk = 26
        targetSdk = 36
        versionCode = 58
        versionName = "1.5.0"
        testInstrumentationRunner = "com.imfibit.activitytracker.HiltRunner"
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

    buildTypes {
        getByName("release") {
            isMinifyEnabled = true

            manifestPlaceholders["crashlyticsCollectionEnabled"] = true

            signingConfig = signingConfigs.getByName("release")
            proguardFiles("proguard-rules.pro")
        }

        getByName("debug") {
            manifestPlaceholders["crashlyticsCollectionEnabled"] = false
            isMinifyEnabled = false
            proguardFiles("proguard-rules.pro")

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
}

play {
    serviceAccountCredentials.set(File("../../imfibit-tracker-keystore/publish-key.json"))
    track.set("production")
}

dependencies {
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.1.5")

    implementation("org.jetbrains.kotlin:kotlin-stdlib")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:1.10.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.10.0")
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.7.1")


    implementation(platform("com.google.firebase:firebase-bom:34.11.0"))
    implementation("com.google.firebase:firebase-crashlytics")


    val COMPOSE_VERSION = "1.10.6"
    implementation("androidx.compose.animation:animation:$COMPOSE_VERSION")
    implementation("androidx.compose.foundation:foundation:$COMPOSE_VERSION")
    implementation("androidx.activity:activity-compose:1.13.0")
    implementation("androidx.compose.material3:material3-android:1.4.0")
    implementation("androidx.compose.material:material-icons-extended:1.7.8")
    implementation("sh.calvin.reorderable:reorderable:3.0.0")


    implementation("androidx.core:core-ktx:1.18.0")
    implementation("androidx.glance:glance:1.2.0-rc01")
    implementation("androidx.glance:glance-appwidget:1.2.0-rc01")
    implementation("androidx.glance:glance-preview:1.2.0-rc01")
    implementation("androidx.glance:glance-appwidget-preview:1.2.0-rc01")

    implementation("androidx.datastore:datastore-preferences:1.2.1")

    // Navigation 3
    implementation("androidx.navigation3:navigation3-runtime:1.1.0-rc01")
    implementation("androidx.navigation3:navigation3-ui:1.1.0-rc01")
    implementation("androidx.lifecycle:lifecycle-viewmodel-navigation3:2.10.0")
    implementation("androidx.compose.material3.adaptive:adaptive-navigation3:1.3.0-alpha09")

    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.10.0")

    implementation("androidx.paging:paging-runtime-ktx:3.4.2")
    implementation("androidx.paging:paging-compose:3.4.2")

    val ROOM_VERSION = "2.8.4"
    ksp("androidx.room:room-compiler:$ROOM_VERSION")
    implementation("androidx.room:room-runtime:$ROOM_VERSION")
    implementation("androidx.room:room-ktx:$ROOM_VERSION")


    implementation("androidx.hilt:hilt-common:1.3.0")
    implementation("com.google.dagger:hilt-android:2.59.2")
    ksp("com.google.dagger:hilt-android-compiler:2.59.2")
    ksp("androidx.hilt:hilt-compiler:1.3.0")
    implementation("androidx.hilt:hilt-navigation-compose:1.3.0")


    debugImplementation("androidx.compose.ui:ui-tooling:$COMPOSE_VERSION")
    implementation("androidx.compose.ui:ui-tooling-preview:$COMPOSE_VERSION")


    androidTestImplementation("com.google.dagger:hilt-android-testing:2.59.2")

    // Needed for createAndroidComposeRule, but not createComposeRule:
    debugImplementation("androidx.compose.ui:ui-test-manifest:$COMPOSE_VERSION")

    // Test rules and transitive dependencies:
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:$COMPOSE_VERSION")
    testImplementation("androidx.test:core:1.7.0")
    androidTestImplementation("androidx.test:runner:1.7.0")
    androidTestImplementation("androidx.test:rules:1.7.0")
}
