plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.baselineprofile)
    id("maven-publish")
}

android {
    namespace = "com.nmd.eventcalendar.compose"
    compileSdk = 37

    defaultConfig {
        minSdk = 23
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        buildConfig = true
        compose = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    // The Baseline Profile is generated from the sample app (see :baselineprofile) and shipped with the
// library, so apps using it get the calendar's hot paths precompiled at install time.
baselineProfile {
    filter { include("com.nmd.eventCalendar.compose.**") }
}

publishing {
        singleVariant("release")
    }

    sourceSets {
        // UI tests run both on the JVM (Robolectric) and on a device/emulator.
        getByName("test").kotlin.directories.add("src/sharedTest/java")
        getByName("androidTest").kotlin.directories.add("src/sharedTest/java")
    }

    testOptions {
        unitTests {
            // Compose UI tests run on the JVM via Robolectric and need the merged resources.
            isIncludeAndroidResources = true
        }
    }
}

dependencies {
    api(libs.androidx.compose.ui)
    api(libs.androidx.compose.material3)
    api(libs.androidx.compose.ui.tooling.preview)
    api(libs.androidx.lifecycle.viewmodel.ktx)
    api(libs.androidx.compose.foundation)
    api(libs.androidx.lifecycle.viewmodel.compose)
    api(libs.kotlinx.datetime)
    api(libs.kotlinx.serialization.json)

    debugImplementation(libs.androidx.compose.ui.tooling)

    testImplementation(libs.junit)
    testImplementation(libs.robolectric)
    testImplementation(libs.androidx.compose.ui.test.junit4)
    testImplementation(libs.androidx.compose.ui.test.manifest)
    testImplementation(libs.androidx.test.ext.junit)

    androidTestImplementation(libs.androidx.test.runner)
    androidTestImplementation(libs.androidx.test.ext.junit)
    // ui-test-junit4 pulls Espresso 3.5, which crashes on API 35+ (InputManager.getInstance removed).
    androidTestImplementation(libs.androidx.test.espresso.core)
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.compose.ui.test.manifest)

    baselineProfile(project(":baselineprofile"))
}

publishing {
    publications {
        create<MavenPublication>("release") {
            groupId = "com.nmd.eventcalendar"
            artifactId = "compose"
            version = project.property("VERSION_NAME") as String

            afterEvaluate {
                from(components["release"])
            }

            pom {
                name.set("eventcalendar-compose")
                description.set("A powerful and highly customizable Event Calendar Library for Android, supporting both Jetpack Compose and the classic XML/View System with Material 3 design, customizable views, event handling, calendar week display, and edge-to-edge functionality.")
                url.set("https://github.com/michael-winkler/EventCalendar")

                licenses {
                    license {
                        name.set("Apache License 2.0")
                        url.set("https://www.apache.org/licenses/LICENSE-2.0")
                    }
                }

                developers {
                    developer {
                        id.set("michael-winkler")
                        name.set("Michael Winkler")
                    }
                }

                scm {
                    connection.set("scm:git:git://github.com/michael-winkler/EventCalendar.git")
                    developerConnection.set("scm:git:ssh://github.com/michael-winkler/EventCalendar.git")
                    url.set("https://github.com/michael-winkler/EventCalendar")
                }
            }
        }
    }
}