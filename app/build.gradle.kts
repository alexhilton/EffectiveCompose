plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "net.toughcoder.effectivecompose"
    compileSdk = 35

    defaultConfig {
        applicationId = "net.toughcoder.effectivecompose"
        minSdk = 28
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    // Kotlin
    implementation(libs.kotlinx.serialization)

    implementation(libs.androidx.ktx)

    // Jetpack
    implementation(libs.lifecycle.runtime)
    implementation(libs.lifecycle.common)
    implementation(libs.lifecycle.runtimeCompose)
    implementation(libs.lifecycle.viewmodel)

    implementation(libs.androidx.activityCompose)
    implementation(libs.androidx.store)
    implementation(libs.navigation.runtime)
    implementation(libs.navigation.compose)

    // Compose
    val composeBom = platform(libs.androidx.compose.bom)
    implementation(composeBom)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.uiGraphics)
    implementation(libs.androidx.compose.uiPreview)
    implementation(libs.androidx.compose.material3)

    // Test
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test)
    androidTestImplementation(libs.androidx.testEspresso)
    androidTestImplementation(composeBom)
    androidTestImplementation(libs.androidx.compose.uiJunit)

    // Debug
    debugImplementation(libs.androidx.compose.uiTooling)
    debugImplementation(libs.androidx.compose.uiTestManifest)
}