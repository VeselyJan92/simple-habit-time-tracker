import org.jetbrains.kotlin.konan.properties.Properties


val COMPOSE_VERSION = "1.2.0-beta02"


plugins {
    id("com.android.application")
    id("com.github.triplet.play") version "3.6.0"
    id("kotlin-android")
    id("kotlin-kapt")
    id("dagger.hilt.android.plugin")

    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")

}

repositories {
    maven { setUrl("https://jitpack.io") }
}

android {
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

    compileSdk = 31
    buildToolsVersion = "30.0.3"

    buildFeatures {
        compose = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    composeOptions {
        kotlinCompilerExtensionVersion = COMPOSE_VERSION
    }


    kotlinOptions {
        jvmTarget = "1.8"
        freeCompilerArgs = listOf(
            "-Xopt-in=kotlin.Experimental",
            "-Xopt-in=kotlin.RequiresOptIn"
        )
    }

    defaultConfig {
        applicationId = "com.imfibit.activitytracker"
        minSdk = 26
        targetSdk = 31
        versionCode = 12
        versionName = "1.2.1"
        testInstrumentationRunner  ="com.imfibit.activitytracker.HiltRunner"
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false

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

    }
    namespace = "com.imfibit.activitytracker"

}

play {
    serviceAccountCredentials.set(File("../../imfibit-tracker-keystore/publish-key.json"))
    track.set("production")
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))

    implementation("com.google.android.material:material:1.6.0")

    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.6.21")
    implementation("com.thedeanda:lorem:2.1")

    implementation(platform("com.google.firebase:firebase-bom:25.11.0"))
    implementation("com.google.firebase:firebase-crashlytics-ktx")



    implementation("androidx.compose.compiler:compiler:$COMPOSE_VERSION")
    implementation("androidx.compose.animation:animation:$COMPOSE_VERSION")
    implementation("androidx.compose.ui:ui:$COMPOSE_VERSION")
    implementation("androidx.compose.material:material:$COMPOSE_VERSION")
    implementation("androidx.compose.runtime:runtime:$COMPOSE_VERSION")
    implementation("androidx.compose.runtime:runtime-livedata:$COMPOSE_VERSION")
    implementation("androidx.compose.foundation:foundation:$COMPOSE_VERSION")
    implementation("androidx.compose.material:material-icons-extended:$COMPOSE_VERSION")


    implementation("androidx.glance:glance-appwidget:1.0.0-alpha03")


    implementation("androidx.core:core-ktx:1.7.0")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.3")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")

    androidTestImplementation("com.google.dagger:hilt-android-testing:2.38.1")
    kaptAndroidTest("com.google.dagger:hilt-android-compiler:2.38.1")
    androidTestAnnotationProcessor("com.google.dagger:hilt-android-compiler:2.38.1")




    debugImplementation("androidx.compose.ui:ui-tooling:$COMPOSE_VERSION")

    implementation("androidx.navigation:navigation-compose:2.5.0-rc01")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.4.1")


    val ROOM_VERSION = "2.4.1"

    kapt("androidx.room:room-compiler:$ROOM_VERSION")
    implementation("androidx.room:room-runtime:$ROOM_VERSION")
    implementation("androidx.room:room-ktx:$ROOM_VERSION")

    implementation("androidx.datastore:datastore-preferences:1.0.0")

    implementation("com.google.accompanist:accompanist-pager:0.22.0-rc")
    implementation("com.google.accompanist:accompanist-pager-indicators:0.22.0-rc")
    implementation("com.google.accompanist:accompanist-systemuicontroller:0.22.0-rc")


    implementation("org.burnoutcrew.composereorderable:reorderable:0.6.2")

    implementation("io.github.bytebeats:compose-charts:0.1.0")

    implementation( "com.github.alorma:compose-settings:0.3.0")
    implementation( "com.github.madrapps:plot:0.1.1")


    val paging_version = "3.1.0"

    implementation("androidx.paging:paging-runtime:$paging_version")
    implementation("androidx.paging:paging-compose:1.0.0-alpha14")

    implementation("com.google.dagger:hilt-android:2.38.1")
    kapt("com.google.dagger:hilt-android-compiler:2.38.1")
    implementation("androidx.hilt:hilt-navigation-compose:1.0.0")
    kapt("androidx.hilt:hilt-compiler:1.0.0")
}
