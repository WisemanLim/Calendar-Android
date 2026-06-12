// WiseCalA — app module (빌드 플레이버 = 환경 매핑)
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
}

android {
    namespace = "com.wisecal.android"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.wisecal.android"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "0.1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    flavorDimensions += "env"
    productFlavors {
        create("dev") {
            dimension = "env"
            applicationIdSuffix = ".dev"
            buildConfigField("String", "API_BASE_URL", "\"http://10.0.2.2:8000\"")
        }
        create("staging") {
            dimension = "env"
            applicationIdSuffix = ".staging"
            buildConfigField("String", "API_BASE_URL", "\"https://stg.api.example\"")
        }
        create("prod") {
            dimension = "env"
            buildConfigField("String", "API_BASE_URL", "\"https://api.example\"")
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
    }
}

dependencies {
    val composeBom = platform(libs.compose.bom)
    implementation(composeBom)
    androidTestImplementation(composeBom)

    implementation(libs.compose.material3)
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.activity.compose)
    implementation(libs.lifecycle.viewmodel.compose)
    implementation(libs.lifecycle.runtime.ktx)
    implementation(libs.navigation.compose)
    implementation(libs.appcompat)
    implementation(libs.datastore.preferences)

    // 날씨 API (Open-Meteo)
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.gson)
    implementation(libs.okhttp.logging)

    // 위치 (날씨용)
    implementation(libs.play.services.location)

    // 이미지 (복용 사진 첨부)
    implementation(libs.coil.compose)

    // 테스트
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.ext)
    androidTestImplementation(libs.compose.ui.test.junit4)
    debugImplementation(libs.compose.ui.tooling)
}
