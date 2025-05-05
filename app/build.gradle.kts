// Updated build.gradle.kts in libs.versions.toml format
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.example.nathali"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.nathali"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.firebase.crashlytics.buildtools)


    // Additions for project functionality
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")
    implementation ("androidx.cardview:cardview:1.0.0")
    implementation(libs.androidx.material3.android)
    implementation(libs.androidx.work.runtime.ktx)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}