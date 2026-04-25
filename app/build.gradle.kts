plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.example.android_object_detection"
    compileSdk = 37

    defaultConfig {
        applicationId = "com.example.android_object_detection"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "com.example.android_object_detection.HiltTestRunner"
    }

    androidResources {
        noCompress += "tflite"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlin {
        jvmToolchain(17)
    }

    buildFeatures {
        compose = true
    }
}

dependencies {
    //  AndroidX Core ─
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)

    //  Compose 
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    debugImplementation(libs.androidx.compose.ui.tooling)

    //  Navigation 3 
    implementation(libs.navigation3.runtime)
    implementation(libs.navigation3.ui)
    implementation(libs.lifecycle.viewmodel.nav3)
    implementation(libs.hilt.navigation.compose)

    //  Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    //  Image Loading
    implementation(libs.coil.compose)

    //  LiteRT (TensorFlow Lite Runtime)
    implementation(libs.litert.core)

    //  Unit Tests 
    testImplementation(libs.junit)
    testImplementation(libs.test.mockk)
    testImplementation(libs.test.coroutines)
    testImplementation(libs.test.turbine)

    //  Android Instrumented Tests
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidtest.mockk)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidtest.hilt)
    kspAndroidTest(libs.androidtest.hilt.compiler)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}