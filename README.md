# EventCalendar

[![](https://jitpack.io/v/michael-winkler/EventCalendar.svg)](https://jitpack.io/#michael-winkler/EventCalendar)
[![Last commit](https://img.shields.io/github/last-commit/michael-winkler/EventCalendar?style=flat)](https://github.com/michael-winkler/EventCalendar/commits)
![GitHub all releases](https://img.shields.io/github/downloads/michael-winkler/EventCalendar/total)
[![API](https://img.shields.io/badge/API-21%2B-orange.svg?style=flat)](https://android-arsenal.com/api?level=21)
[![License Apache 2.0](https://img.shields.io/badge/License-Apache%202.0-blue.svg?style=true)](http://www.apache.org/licenses/LICENSE-2.0)

This library uses Android X depencies and is written in Kotlin.


## Usage
Add a dependency to your build.gradle file:
```kotlin
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}

dependencies {
    implementation 'com.github.michael-winkler:EventCalendar:1.3.7'
}
```

```kotlin
import com.nmd.eventCalendar.EventCalendarView
```

## Exampe code
You can create a new `EventCalendarView` inside your XML-Layout like this:
```kotlin
<com.nmd.eventCalendar.EventCalendarView
    android:id="@+id/eventCalendarView"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    app:ecv_count_background_text_color="@android:color/white"
    app:ecv_count_background_tint_color="@android:color/holo_blue_light"
    app:ecv_count_visible="true"
    app:ecv_current_day_background_tint_color="@android:color/holo_red_dark"
    app:ecv_current_day_text_color="@android:color/white"
    app:ecv_disallow_intercept="false"
    app:ecv_edge_to_edge_enabled="true"
    app:ecv_event_item_automatic_text_color="true"
    app:ecv_event_item_text_color="@android:color/black"
    app:ecv_header_visible="true"
    app:ecv_calendar_week_visible="true"/>
```

Or if you just want the current calendar week you can use this one:
```kotlin
<com.nmd.eventCalendar.EventCalendarSingleWeekView
    android:id="@+id/eventCalendarView"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    app:ecv_count_background_text_color="@android:color/white"
    app:ecv_count_background_tint_color="@android:color/holo_blue_light"
    app:ecv_count_visible="true"
    app:ecv_current_day_background_tint_color="@android:color/holo_red_dark"
    app:ecv_current_day_text_color="@android:color/white"
    app:ecv_event_item_automatic_text_color="true"
    app:ecv_event_item_text_color="@android:color/black"
    app:ecv_header_visible="true"
    app:ecv_calendar_week_visible="true"/>
```

Here you can see all custom `app` parameters which you can use:    
https://github.com/michael-winkler/EventCalendar/blob/main/library/src/main/res/values/attr.xml

The `EventCalendarSingleWeekView` can use all `app:evc` too except `ecv_disallow_intercept` and `ecv_edge_to_edge_enabled`.
Also the `EventCalendarSingleWeekView` displays only `Events` where the date is in range of the current calendar week.
You don't need anything to do by yourself. The library will filter automatically the events for the
week view by itself.

Now in your class you can get a reference to it like this:
```kotlin
binding.eventCalendarView.addOnDayClickListener(object :
    EventCalendarDayClickListener {
    override fun onClick(day: Day) {
        // val eventList = binding.eventCalendarView.events.filter { it.date == day.date }
        // You can use this to get the events for the selected day
        Log.i("ECV", "TEST 1: " + day.date)
    }
})
```
The `EventCalendarDayClickListener` listener works also for the `EventCalendarSingleWeekView`.

The `Day` object structure is following:
```kotlin
data class Day(
    /**
     * eg: 31
     */
    val value: String,
    /**
     * eg: true | false
     */
    val isCurrentMonth: Boolean,
    /**
     * eg: true | false
     */
    var isCurrentDay: Boolean,
    /**
     * eg: 31.12.2024
     */
    var date: String,
)
```

<hr>

You can also watch to calendar scroll changes.

```kotlin
binding.eventCalendarView.addOnCalendarScrollListener(object : EventCalendarScrollListener {
    override fun onScrolled(month: Int, year: Int) {
        Log.i("ECV", "Scrolled to: $month $year")
    }
})
```
If you scroll for example to january 2023, the `month` value will be `1` and the year `2023`.

<hr>

It is really to easy to add events to the calendar.
Here is an example code:
```kotlin
binding.eventCalendarView.events = arrayListOf(
    Event(date = "15.04.2023", name = "Vacation", backgroundHexColor = "#4badeb"),
    Event(date = "16.04.2023", name = "Home office", backgroundHexColor = "#e012ad"),
    Event(date = "17.04.2023", name = "Meeting", backgroundHexColor = "#e07912"),
    Event(date = "18.04.2023", name = "Vacation", backgroundHexColor = "#4badeb", data = "Let's go!")
)
```
The date format have to be in format "dd.MM.yyyy".

The `Event` object structure is following:
```kotlin
@Parcelize
data class Event(
    val date: String,
    val name: String,
    val backgroundHexColor: String,
    val data: @RawValue Any? = null,
) : Parcelable
```
The event text color is automatically determined. If the background color is dark, the text color is white. Otherwise, the text color is gray.
You can change this behaviour with `app:ecv_event_item_automatic_text_color="false"`. And then with `app:ecv_event_item_text_color="@android:color/black"` you can set the event item text color.

## Javadoc
Each function has also a javadoc documentation.

## Edge to edge support
The `EventCalendarView` does also support edge to edge.
```kotlin
app:ecv_edge_to_edge_enabled="true"
```
You can enable or disable the edge to edge handling inside your xml configuration.
The default value is `false`.

## Screenshots
<img src="https://github.com/michael-winkler/EventCalendar/blob/main/images/Screenshot.png" height=400px> <img src="https://github.com/michael-winkler/EventCalendar/blob/main/images/Screenshot2.png" height=400px>


## Sample app
https://github.com/michael-winkler/EventCalendar/releases/download/1.3.7/app-release.apk


## License
```
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
## Last words
If you like this library feel free to "star" it:<br>
![star](https://github.com/michael-winkler/Screenshot/blob/master/Images/star.png)

```
This library has been successfully tested with:
Android Studio Koala Feature Drop | 2024.1.2 Patch 1
```