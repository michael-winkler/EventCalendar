@file:OptIn(ExperimentalMaterial3Api::class)

package com.nmd.eventCalendar

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nmd.eventCalendar.compose.EventCalendarCompose
import com.nmd.eventCalendar.compose.model.CalendarDay
import com.nmd.eventCalendar.compose.model.Event
import com.nmd.eventCalendar.compose.ui.config.CalendarOptions
import com.nmd.eventCalendar.compose.ui.config.defaultCalendarStyle
import com.nmd.eventCalendar.compose.ui.controller.rememberCalendarController
import com.nmd.eventCalendar.compose.ui.events.rememberCalendarEventsStore
import com.nmd.eventCalendar.theme.AppTheme
import com.nmd.eventCalendarSample.R
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import kotlin.random.Random

@RequiresApi(Build.VERSION_CODES.O)
class EventCalendarComposeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                Screen { onBackPressedDispatcher.onBackPressed() }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Screen(
    callback: () -> Unit
) {
    var weekStartValue by rememberSaveable { mutableIntStateOf(DayOfWeek.MONDAY.value) }
    var headerVisible by rememberSaveable { mutableStateOf(true) }
    var showCalendarWeek by rememberSaveable { mutableStateOf(true) }

    val calendarOptions = remember(weekStartValue, headerVisible, showCalendarWeek) {
        CalendarOptions(
            weekStart = DayOfWeek.of(weekStartValue),
            headerVisible = headerVisible,
            calendarWeekVisible = showCalendarWeek,
            minDate = null,
            maxDate = null,
            openEndedWindowMonths = 5 * 12
        )
    }

    val calendarController = rememberCalendarController(calendarOptions)

    val baseStyle = defaultCalendarStyle()
    val calendarStyle = remember(baseStyle) { baseStyle.copy(textUnit = 12.sp) }

    val initialSeed = rememberSaveable { System.currentTimeMillis() }
    val eventsStore = rememberCalendarEventsStore(
        initialEvents = shuffleEventsForCurrentYear(
            templates = eventTemplates,
            eventCount = 250,
            seed = initialSeed
        )
    )

    val isDark = isSystemInDarkTheme()
    val topBarColor = remember(isDark) { if (isDark) Color(0xFF1B1B1F) else Color.White }
    val iconTint = remember(isDark) { if (isDark) Color.White else Color.Black }
    val containerColor = remember(isDark) { if (isDark) Color(0xFF1B1B1F) else Color.White }

    val onDaySelected: (CalendarDay) -> Unit = remember {
        {
            Log.i("EventCalendarCompose", "Selected day: ${it.date}")
            Log.i("EventCalendarCompose", "Selected events: ${it.events}")
        }
    }

    val onMonthChange: (YearMonth) -> Unit = remember {
        { Log.i("EventCalendarCompose", "Selected month: $it") }
    }

    val onWeekStartClick: () -> Unit = remember {
        { weekStartValue = (weekStartValue % 7) + 1 }
    }

    val onJumpToCurrentMonth: () -> Unit = remember(calendarController) {
        { calendarController.jumpToCurrentMonth() }
    }

    val onToggleCalendarWeek: () -> Unit = remember {
        { showCalendarWeek = !showCalendarWeek }
    }

    val onShuffleEvents: () -> Unit = remember(eventsStore) {
        {
            eventsStore.setEvents(
                shuffleEventsForCurrentYear(
                    templates = eventTemplates,
                    eventCount = 250
                )
            )
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.app_name_compose),
                        color = iconTint
                    )
                },
                navigationIcon = {
                    IconButton(onClick = callback) {
                        Icon(
                            painter = painterResource(R.drawable.icon_arrow_left),
                            contentDescription = "Back",
                            tint = iconTint
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onWeekStartClick) {
                        Icon(
                            painter = painterResource(R.drawable.icon_calendar_start),
                            contentDescription = "Change week start",
                            tint = iconTint
                        )
                    }
                    IconButton(onClick = onJumpToCurrentMonth) {
                        Icon(
                            painter = painterResource(R.drawable.icon_calendar_today),
                            contentDescription = "Jump to current month",
                            tint = iconTint
                        )
                    }
                    IconButton(onClick = onShuffleEvents) {
                        Icon(
                            painter = painterResource(R.drawable.icon_shuffle),
                            contentDescription = "Shuffle events",
                            tint = iconTint
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = topBarColor)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onToggleCalendarWeek,
                containerColor = Color(0xFFF0E3A8),
                contentColor = Color(0xFF895900)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.icon_calendar_week_begin_outline),
                    contentDescription = "Toggle calendar week"
                )
            }
        },
        containerColor = containerColor
    ) { paddingValues ->
        val layoutDirection = LocalLayoutDirection.current
        Column(
            modifier = Modifier.padding(
                start = paddingValues.calculateStartPadding(layoutDirection),
                end = paddingValues.calculateEndPadding(layoutDirection),
                top = paddingValues.calculateTopPadding()
            )
        ) {
            EventCalendarCompose(
                modifier = Modifier.padding(bottom = 16.dp),
                calendarController = calendarController,
                eventsStore = eventsStore,
                onDaySelected = onDaySelected,
                onMonthChange = onMonthChange,
                calendarOptions = calendarOptions,
                calendarStyle = calendarStyle
            )

            Spacer(modifier = Modifier.windowInsetsBottomHeight(WindowInsets.navigationBars))
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
private fun shuffleEventsForCurrentYear(
    templates: List<Pair<String, Color>>,
    eventCount: Int = 250,
    seed: Long = System.currentTimeMillis()
): List<Event> {
    if (templates.isEmpty() || eventCount <= 0) return emptyList()

    val year = LocalDate.now().year
    val start = LocalDate.of(year, 1, 1)
    val daysInYear = if (start.isLeapYear) 366 else 365
    val rnd = Random(seed)

    return List(eventCount) {
        val (name, shape) = templates[rnd.nextInt(templates.size)]
        val date = start.plusDays(rnd.nextInt(daysInYear).toLong())

        Event(
            date = date,
            name = name,
            shapeColor = shape,
            textColor = Color.White
        )
    }
}

private val eventTemplates: List<Pair<String, Color>> = listOf(
    "Meeting" to Color(0xFFE07912),
    "Vacation" to Color(0xFF4BADEB),
    "Birthday Party" to Color(0xFFFF6F00),
    "Concert" to Color(0xFFD500F9),
    "Job Interview" to Color(0xFF7CB342),
    "Doctor's Appointment" to Color(0xFF29B6F6),
    "Gym Session" to Color(0xFFEF5350),
    "Networking Event" to Color(0xFFAB47BC),
    "Movie Night" to Color(0xFFFFEE58),
    "Dinner Date" to Color(0xFF26A69A),
    "Business Trip" to Color(0xFF8D6E63),
    "Charity Event" to Color(0xFFFF9800),
    "Book Club Meeting" to Color(0xFFB71C1C),
    "Coffee with Friends" to Color(0xFF9CCC65),
    "Music Festival" to Color(0xFF7E57C2),
    "Volunteering" to Color(0xFF78909C),
    "Sports Game" to Color(0xFFF44336),
    "Art Exhibition" to Color(0xFF9C27B0),
    "Language Exchange" to Color(0xFF4CAF50),
    "Hiking Trip" to Color(0xFFCDDC39),
    "Yoga Class" to Color(0xFF26C6DA),
    "Baking Workshop" to Color(0xFFFFAB00),
    "Science Fair" to Color(0xFF6A1B9A),
    "Board Game Night" to Color(0xFF607D8B),
    "Fashion Show" to Color(0xFFF57C00),
    "Political Rally" to Color(0xFF009688),
    "Writing Workshop" to Color(0xFFFF4081),
    "Tech Conference" to Color(0xFF1565C0),
    "Wine Tasting" to Color(0xFF8BC34A),
    "Cooking Class" to Color(0xFFF4511E),
    "Open Mic Night" to Color(0xFF673AB7),
    "Karaoke Night" to Color(0xFFFF5252),
    "Outdoor Concert" to Color(0xFF64DD17),
    "Flea Market" to Color(0xFF9E9E9E),
    "Art Museum Tour" to Color(0xFFFF1744),
    "Escape Room" to Color(0xFF00BCD4),
    "Photography Workshop" to Color(0xFFFFD600),
    "Ballet Performance" to Color(0xFF9FA8DA),
    "Fashion Design Course" to Color(0xFF4CAF4F),
    "Community Service" to Color(0xFFC2185B),
    "Trivia Night" to Color(0xFF2196F3),
    "Chess Tournament" to Color(0xFFAFB42B),
    "Stand-up Comedy Show" to Color(0xFF795548),
    "Book Signing" to Color(0xFFE91E63),
    "Potluck Party" to Color(0xFF689F38),
    "Art Auction" to Color(0xFFBA68C8),
    "Game Night" to Color(0xFF00897B),
    "Beer Tasting" to Color(0xFFFFD54F),
    "Stand-up Paddleboarding" to Color(0xFF0277BD),
    "Charity Run" to Color(0xFFF57F17),
    "Poetry Slam" to Color(0xFFF44336),
    "Board Game Cafe" to Color(0xFF8BC34A),
    "Movie Marathon" to Color(0xFFB71C1C),
    "Bike Tour" to Color(0xFF4DB6AC),
    "Wine and Paint Night" to Color(0xFF9C27B0),
    "Plant Swap" to Color(0xFF388E3C),
    "Beach Clean-up" to Color(0xFF009688),
    "Indoor Skydiving" to Color(0xFFFF6F00),
    "Ice Skating" to Color(0xFF0277BD),
    "Farmers Market" to Color(0xFFCDDC39),
    "Game of Thrones Marathon" to Color(0xFF6D4C41),
    "Soap Making Workshop" to Color(0xFF26A69A),
    "Beer and Cheese Pairing" to Color(0xFFFFAB00),
    "Group Painting Session" to Color(0xFFFFA000),
    "Food Truck Festival" to Color(0xFFF06292),
    "Ghost Tour" to Color(0xFF7E57C2),
    "Sushi Making Class" to Color(0xFF0091EA),
    "Aquarium Visit" to Color(0xFFB2FF59),
    "Murder Mystery Dinner" to Color(0xFFD500F9),
    "Vintage Clothing Market" to Color(0xFFF57C00),
    "Rock Climbing" to Color(0xFF6A1B9A),
    "DIY Woodworking Class" to Color(0xFF795548),
    "Meditation Retreat" to Color(0xFF0097A7),
    "Group Bike Ride" to Color(0xFFD32F2F),
    "Cooking Competition" to Color(0xFFFF5252),
    "Haunted House Visit" to Color(0xFFBA68C8),
    "Beach Volleyball" to Color(0xFFFFA000),
    "Gardening Workshop" to Color(0xFF4DB6AC),
    "Laser Tag" to Color(0xFF673AB7),
    "Bird Watching Tour" to Color(0xFFFF4081),
    "Movie in the Park" to Color(0xFF43A047),
    "Cider Tasting" to Color(0xFFEF5350),
    "Escape Game" to Color(0xFF29B6F6),
    "Cheese Making Class" to Color(0xFFFFC107),
)

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun EventCalendarComposePreview() {
    AppTheme {
        Screen(callback = {})
    }
}