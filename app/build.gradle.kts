import org.jetbrains.kotlin.konan.properties.Properties

plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-kapt")
    id("kotlin-android-extensions")

   // id("dagger.hilt.android.plugin")
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
        kotlinCompilerVersion = "1.4.10"
        kotlinCompilerExtensionVersion ="1.0.0-alpha05"

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
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.4.10")
    implementation("androidx.core:core-ktx:1.3.1")
    implementation("com.thedeanda:lorem:2.1")

    implementation(platform("com.google.firebase:firebase-bom:25.11.0"))

    implementation("com.google.firebase:firebase-crashlytics-ktx")
    implementation("com.google.firebase:firebase-analytics-ktx")



   // implementation( "com.android.support:support-compat:28.0.0")


   // implementation("com.google.dagger:hilt-android:2.28-alpha")
   // kapt("com.google.dagger:hilt-android-compiler:2.28-alpha")

    implementation("androidx.appcompat:appcompat:1.2.0")

    implementation("com.google.android.material:material:1.2.1")

    implementation("androidx.constraintlayout:constraintlayout:2.0.1")


    implementation("androidx.lifecycle:lifecycle-extensions:2.2.0")

    implementation("androidx.navigation:navigation-fragment-ktx:2.3.0")
    implementation("androidx.navigation:navigation-ui-ktx:2.3.0")



    implementation("androidx.compose.ui:ui:1.0.0-alpha06")
    implementation("androidx.compose.material:material:1.0.0-alpha06")
    implementation("androidx.ui:ui-tooling:1.0.0-alpha06")
    implementation("androidx.compose.runtime:runtime-livedata:1.0.0-alpha06")
    implementation("androidx.compose.foundation:foundation:1.0.0-alpha06")
    implementation("androidx.compose.material:material-icons-extended:1.0.0-alpha06")



    implementation ("androidx.room:room-runtime:2.3.0-alpha02")
    kapt("androidx.room:room-compiler:2.3.0-alpha02")
    implementation ("androidx.room:room-ktx:2.3.0-alpha02")
    testImplementation ("androidx.room:room-testing:2.3.0-alpha02")

    testImplementation("junit:junit:4.13")
    androidTestImplementation("androidx.test.ext:junit:1.1.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.2.0")

}


tasks.withType(org.jetbrains.kotlin.gradle.tasks.KotlinCompile::class.java).configureEach {
    kotlinOptions {
        jvmTarget = "1.8"
        freeCompilerArgs += "-Xallow-jvm-ir-dependencies"
        freeCompilerArgs += "-Xskip-prerelease-check"
        freeCompilerArgs += "-Xopt-in=androidx.compose.ui.node.ExperimentalLayoutNodeApi"


    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().all {
    kotlinOptions.freeCompilerArgs +=  "-Xopt-in=kotlin.RequiresOptIn"


}
