
plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-kapt")
    id("kotlin-android-extensions")

   // id("androidx.navigation.safeargs.kotlin")


    id("dagger.hilt.android.plugin")
}

android {
    compileSdkVersion(30)
    buildToolsVersion = "30.0.2"

    buildFeatures {
        // Enables Jetpack Compose for this module
        compose =  true
    }

    lintOptions {
        isAbortOnError = false
    }

    composeOptions {
        kotlinCompilerVersion = "1.4.0"
        kotlinCompilerExtensionVersion ="1.0.0-alpha02"

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
        applicationId ="com.janvesely.activitytracker"
        minSdkVersion(26)
        targetSdkVersion(30)
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
        }
    }

}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.4.0")
    implementation("androidx.core:core-ktx:1.3.1")

    implementation("com.google.dagger:hilt-android:2.28-alpha")
    kapt("com.google.dagger:hilt-android-compiler:2.28-alpha")

    implementation("androidx.appcompat:appcompat:1.2.0")

    implementation("com.google.android.material:material:1.2.1")

    implementation("androidx.constraintlayout:constraintlayout:2.0.1")


    implementation("androidx.lifecycle:lifecycle-extensions:2.2.0")

    implementation("androidx.navigation:navigation-fragment-ktx:2.3.0")
    implementation("androidx.navigation:navigation-ui-ktx:2.3.0")



    implementation("androidx.compose.ui:ui:1.0.0-alpha03")
    implementation("androidx.compose.material:material:1.0.0-alpha03")
    implementation("androidx.ui:ui-tooling:1.0.0-alpha03")
    implementation("androidx.compose.runtime:runtime-livedata:1.0.0-alpha03")
    implementation("androidx.compose.foundation:foundation:1.0.0-alpha03")
    implementation("androidx.compose.material:material-icons-extended:1.0.0-alpha03")



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
