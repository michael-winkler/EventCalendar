plugins {
    id("com.android.application")
    id("kotlin-android")
}

android {
    compileSdk = 34
    namespace = "com.nmd.eventCalendarSample"

    defaultConfig {
        applicationId = "com.nmd.eventCalendar"
        minSdk = 23
        targetSdk = 34
        versionCode = 1
        versionName = "1.0.8"
    }
    buildFeatures {
        viewBinding = true
    }
    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }

        debug {
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
    kotlin {
        jvmToolchain(17)
    }
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.aar", "*.jar"))))

    implementation("androidx.appcompat:appcompat:1.7.0-alpha03")
    implementation("androidx.constraintlayout:constraintlayout:2.2.0-alpha13")
    implementation("com.google.android.material:material:1.10.0")

    implementation(project(":library"))
}