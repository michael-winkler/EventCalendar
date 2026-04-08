plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.parcelize)
    id("maven-publish")
}

android {
    namespace = "com.nmd.eventcalendar.compose"
    compileSdk = 36

    defaultConfig {
        minSdk = 26
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

    publishing {
        singleVariant("release")
    }
}

dependencies {
    api(libs.androidx.compose.ui)
    api(libs.androidx.compose.material3)
    api(libs.androidx.compose.ui.tooling.preview)
    api(libs.androidx.lifecycle.viewmodel.ktx)
    api(libs.androidx.compose.foundation)
    api(libs.androidx.lifecycle.viewmodel.compose)

    debugImplementation(libs.androidx.compose.ui.tooling)
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