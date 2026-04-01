plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.plugin.compose")
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
    api("androidx.compose.ui:ui:1.10.6")
    api("androidx.compose.material3:material3:1.4.0")
    api("androidx.compose.ui:ui-tooling-preview:1.10.6")
    api("androidx.lifecycle:lifecycle-viewmodel-ktx:2.10.0")
    api("androidx.compose.foundation:foundation:1.11.0-beta02")
    api("androidx.lifecycle:lifecycle-viewmodel-compose:2.10.0")

    debugImplementation("androidx.compose.ui:ui-tooling:1.10.6")
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
                description.set("An Android event calendar library written in Kotlin, offering customizable views and event handling, supporting calendar week display and edge-to-edge functionality.")
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