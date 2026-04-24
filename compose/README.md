# EventCalendarCompose

A simple, highly customizable **month, week, and day calendar** for **Jetpack Compose** with
per-day events, time-based layouts, optional ISO week numbers, and horizontal paging.

---

## 📸 Screenshot

<p align="center">
  <img src="../images/Screenshot_compose.png" height="500px">
</p>

---

## Installation (JitPack)

### 1) Add JitPack repository

In your **root** `settings.gradle.kts`:

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

Replace `LATEST_VERSION`
with [![](https://jitpack.io/v/michael-winkler/EventCalendar.svg)](https://jitpack.io/#michael-winkler/EventCalendar)

```kotlin
dependencies {
    implementation("com.github.michael-winkler.EventCalendar:compose:LATEST_VERSION")
}
```

---

## Requirements

- **Min SDK:** 23
- **Kotlin:** 2.0+
- **kotlinx-datetime:** 0.6.0+
- **Jetpack Compose:** 1.7.0+
- **Material3:** 1.2.0+
- **Localization:** Supports English, German, Czech, and Polish.

---

## Minimal Setup

```kotlin
@Composable
fun MyCalendarScreen() {
    val options = defaultCalendarOptions().copy(
        calendarWeekVisible = true
    )
    val controller = rememberCalendarController(options)

    // Using kotlinx-datetime to get current date
    val today = kotlinx.datetime.Clock.System.todayIn(TimeZone.currentSystemDefault())

    val eventsStore = rememberCalendarEventsStore(
        initialEvents = listOf(
            Event(
                date = today,
                name = "Project Kickoff",
                shapeColor = Color.Blue,
                textColor = Color.White
            )
        )
    )

    EventCalendarCompose(
        calendarStyle = defaultCalendarStyle(),
        calendarOptions = options,
        calendarController = controller,
        calendarEventsStore = eventsStore,
        onDaySelected = { day ->
            println("Selected: ${day.date}")
        },
        onMonthChange = { month ->
            println("Changed to: $month")
        }
    )
}
```

## Time-Based Week/Day View

For a more detailed view with time-based event positioning, use `EventCalendarWeekTime`. It
supports 1, 3, or 7-day layouts.

```kotlin
@Composable
fun MyWeekCalendarScreen() {
    val options = defaultCalendarOptions().copy(
        noOfVisibleDays = 7 // Use 1 for Day, 3 for 3-Day, 7 for Week view
    )
    val eventsStore = rememberCalendarEventsStore(
        initialEvents = listOf(
            Event(
                date = today,
                name = "Team Meeting",
                shapeColor = Color.Red,
                textColor = Color.White,
                timeRange = EventTimeRange(startHour = 9, startMinute = 0, endHour = 10, endMinute = 30)
            )
        )
    )

    EventCalendarWeekTime(
        calendarStyle = defaultCalendarStyle(),
        calendarOptions = options,
        calendarEventsStore = eventsStore,
        onDaySelected = { date ->
            println("Background clicked: $date")
        },
        onEventSelected = { event ->
            println("Event clicked: ${event.name}")
        }
    )
}
```

---

## Key Components

### 📅 Event Model

The `Event` class represents a calendar entry.

- **`date`**: `LocalDate` (from `kotlinx.datetime`) of the event.
- **`name`**: Title shown in the calendar.
- **`shapeColor`**: Background color of the event chip.
- **`textColor`**: Text color of the event chip.
- **`timeRange`**: Optional `EventTimeRange(startHour, startMinute, endHour, endMinute)`.

### 🎮 Entry Points

- **`EventCalendarCompose`**: Standard month-grid view.
- **`EventCalendarWeekTime`**: Time-grid view for 1, 3, or 7 days.

### 🎮 CalendarController

Use the controller to navigate programmatically:

- `controller.goToNextMonth()`
- `controller.goToPreviousMonth()`
- `controller.jumpToCurrentMonth()`
- `controller.goToMonth(YearMonth(2025, Month.DECEMBER))`

### ⚙️ CalendarOptions

Configure the behavior of the calendar:

- `weekStart`: Set starting day (e.g., `DayOfWeek.MONDAY`).
- `calendarWeekVisible`: Show/hide ISO week numbers.
- `minDate` / `maxDate`: Restrict navigation range.
- `isCurrentWeekOnly`: If true, only the current calendar week is displayed. `minDate`, `maxDate`
  and `openEndedWindowMonths` will be ignored. The calendar will automatically filter and show only
  events that fall within the current week.
- `noOfVisibleDays`: Number of days to show in `EventCalendarWeekTime` (1, 3, or 7).

### 🎨 CalendarStyle

Customize colors, text sizes, and shapes:

- `monthNameTextColor`
- `dayItemBackgroundColor`
- `currentDayTextColor`
- and more...

---

## Features

- **Paging:** Smooth horizontal paging between months.
- **Time-Based Grid:** Detailed weekly and daily views with precise event positioning and overlap
  handling.
- **Dynamic Events:** Cells automatically handle multiple events; if more than 3 exist, the cell
  becomes scrollable.
- **Theming:** Full support for Material 3 and Dark Mode.
- **Lightweight:** Minimal dependencies, focused on performance.
- **KMP Ready:** Uses `kotlinx-datetime` and resource-based localization for future multiplatform
  support.
- **Backward Compatible:** Supports Android API level 23 and above without requiring Java 8+ API
  desugaring.
