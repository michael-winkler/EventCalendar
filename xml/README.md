# EventCalendar (XML/View System)

A highly customizable **Month Calendar View** for the Android View System (XML), featuring event support, Material 3 design, and smooth navigation.

---

## 📸 Screenshot

<p align="center">
  <img src="../images/Screenshot_xml.png" height="500px">
</p>

---

## 🚀 Installation

### 1) Add JitPack repository
In your `settings.gradle.kts` or root `build.gradle.kts`:

```kotlin
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
}
```

### 2) Add the dependency
In your app's `build.gradle.kts`:

```kotlin
dependencies {
    implementation("com.github.michael-winkler.EventCalendar:xml:1.13.3")
}
```

---

## 🛠 Features

- **Paging:** Horizontal month-to-month paging using ViewPager2.
- **Expressive UI:** Modern Material 3 look and feel.
- **Single Week View:** `EventCalendarSingleWeekView` for space-saving layouts.
- **Customizable:** Extensive XML attributes for colors, visibility, and behavior.
- **Edge-to-Edge:** Native support for edge-to-edge drawing.
- **Event Handling:** Easy-to-use API for adding and managing events.

---

## 📖 Usage

### XML Layout
Add the `EventCalendarView` to your layout:

```xml
<com.nmd.eventCalendar.EventCalendarView
    android:id="@+id/eventCalendarView"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    app:ecv_calendar_week_visible="true"
    app:ecv_expressive_ui="true"
    app:ecv_header_visible="true"
    app:ecv_week_start_day="monday" />
```

### Programmatic Setup
Initialize events and listeners in your Activity or Fragment:

```kotlin
val calendarView = binding.eventCalendarView

// Add Events
calendarView.events = arrayListOf(
    Event(date = "24.12.2025", name = "Christmas", backgroundHexColor = "#FF0000"),
    Event(date = "01.01.2026", name = "New Year", backgroundHexColor = "#00FF00")
)

// Listen for Clicks
calendarView.addOnDayClickListener(object : EventCalendarDayClickListener {
    override fun onClick(day: Day) {
        Log.d("Calendar", "Clicked on: ${day.date}")
    }
})

// Listen for Scroll/Month Changes
calendarView.addOnCalendarScrollListener(object : EventCalendarScrollListener {
    override fun onScrolled(month: Int, year: Int) {
        Log.d("Calendar", "Visible month: $month/$year")
    }
})
```

---

## ⚙️ Configuration

### Important XML Attributes
| Attribute | Description |
|-----------|-------------|
| `app:ecv_expressive_ui` | Enables modern Material 3 design |
| `app:ecv_week_start_day` | `monday` or `sunday` |
| `app:ecv_calendar_week_visible` | Show/Hide ISO week numbers |
| `app:ecv_header_visible` | Toggle visibility of the month header |
| `app:ecv_edge_to_edge_enabled` | Enable edge-to-edge support |

### Customizing Colors
You can override the default styles in your `themes.xml`:

```xml
<style name="ECV_TEXT_ICON_COLOR">
    <item name="android:textColor">#FF5733</item>
    <item name="tint">#FF5733</item>
</style>
```

---

## 📦 Data Models

### Event
```kotlin
data class Event(
    val date: String, // Format: dd.MM.yyyy
    val name: String,
    val backgroundHexColor: String,
    val data: Any? = null,
    val timeRange: EventTimeRange? = null
)
```

### Day
```kotlin
data class Day(
    val value: String,        // Day of month (e.g. "31")
    val isCurrentMonth: Boolean,
    var isCurrentDay: Boolean,
    var date: String          // Format: dd.MM.yyyy
)
```
