plugins {
    id("com.android.library")
    id("kotlin-android")
    id("kotlin-parcelize")
    id("maven-publish")
}

android {
    namespace = "com.nmd.eventCalendar"
    compileSdk = 34

    defaultConfig {
        minSdk = 21
    }

    buildTypes {
        debug {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    buildFeatures {
        viewBinding = true
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlin {
        jvmToolchain(17)
    }
    lint {
        abortOnError = false
    }
    publishing {
        singleVariant("release") {
            withSourcesJar()
            withJavadocJar()
        }
    }
}

dependencies {
    implementation("androidx.appcompat:appcompat:1.7.0-alpha03")
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.viewpager2:viewpager2:1.1.0-beta02")
    implementation("androidx.recyclerview:recyclerview:1.3.1")
    implementation("com.google.android.material:material:1.10.0")
}

afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("release") {
                from(components.findByName("java"))

                groupId = "com.nmd.eventCalendar"
                artifactId = "Eventcalendar"
                version = "1.0.10"
            }
        }
    }
}