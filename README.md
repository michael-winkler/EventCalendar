# EventCalendar

[![](https://jitpack.io/v/michael-winkler/EventCalendar.svg)](https://jitpack.io/#michael-winkler/EventCalendar)
[![Last commit](https://img.shields.io/github/last-commit/michael-winkler/EventCalendar?style=flat)](https://github.com/michael-winkler/EventCalendar/commits)
![GitHub all releases](https://img.shields.io/github/downloads/michael-winkler/EventCalendar/total)
[![API](https://img.shields.io/badge/API-23%2B-orange.svg?style=flat)](https://android-arsenal.com/api?level=23)
[![License Apache 2.0](https://img.shields.io/badge/License-Apache%202.0-blue.svg?style=true)](http://www.apache.org/licenses/LICENSE-2.0)

A powerful and highly customizable **Event Calendar Library** for Android. Whether you are using the classic **XML/View System** or the modern **Jetpack Compose**, this library provides a smooth, Material 3 inspired calendar experience.

---

## 📸 Screenshots

<p align="center">
  <img src="images/Screenshot_compose.png" height="500px"> &nbsp;&nbsp;&nbsp;&nbsp;
  <img src="images/Screenshot_xml.png" height="500px">
</p>
<p align="center">
  <i>Left: Jetpack Compose Module | Right: XML / View System Module</i>
</p>

---

## 📦 Modules

Choose the module that fits your project:

### 🚀 [EventCalendar Compose](./compose/README.md)
*For modern Jetpack Compose projects.*
- Built 100% with Compose.
- Supports horizontal paging.
- Custom `CalendarController` and `CalendarEventsStore`.
- **[Read Compose Documentation →](./compose/README.md)**

### 🏛️ [EventCalendar XML (View System)](./xml/README.md)
*For classic XML-based projects.*
- `EventCalendarView` & `EventCalendarSingleWeekView`.
- Paging via ViewPager2.
- Full XML attribute support.
- **[Read XML Documentation →](./xml/README.md)**

---

## 🛠 Installation

### 1) Add JitPack repository
Add it to your `settings.gradle.kts`:

```kotlin
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
}
```

### 2) Add the dependency
Choose your preferred module (latest version: `1.13.3`):

```kotlin
dependencies {
    // For Jetpack Compose
    implementation("com.github.michael-winkler.EventCalendar:compose:1.13.3")

    // For XML / View System
    implementation("com.github.michael-winkler.EventCalendar:xml:1.13.3")
}
```

---

## 📱 Sample App
You can download the latest sample APK here:  
**[Download Sample App (v1.13.3)](https://github.com/michael-winkler/EventCalendar/releases/download/1.13.3/app-release-unsigned.apk)**

---

## 📄 License
```text
Copyright Author @NMD [Next Mobile Development - Michael Winkler]

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```

---
If you like this library, feel free to **star** it! ⭐
