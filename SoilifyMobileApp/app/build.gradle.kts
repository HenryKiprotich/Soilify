plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.example.soilifymobileapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.soilifymobileapp"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    // Correctly structured buildTypes block
    buildTypes {
        // 'release' configuration
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            // Define the production URL for the "release" version
            buildConfigField("String", "BASE_URL", "\"https://soilifyapi.onrender.com/\"")
        }

        // 'debug' configuration
        getByName("debug") {
            // This is automatically created, but we configure it here
            // Define the development/testing URL for the "debug" version
            buildConfigField("String", "BASE_URL", "\"http://192.168.100.46:8000/\"")
        }

        // 'staging' configuration for live tests
        create("staging") {
            initWith(getByName("debug"))
            // Define the production URL for the "staging" version
            buildConfigField("String", "BASE_URL", "\"https://soilifyapi.onrender.com/\"")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.cardview)
    implementation(libs.philjay.mpandroidchart)
    implementation(libs.androidx.recyclerview)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    // Retrofit for networking
    implementation(libs.retrofit)
    implementation(libs.converter.gson)

    // OkHttp for logging
    implementation(libs.okhttp.logging.interceptor)
}