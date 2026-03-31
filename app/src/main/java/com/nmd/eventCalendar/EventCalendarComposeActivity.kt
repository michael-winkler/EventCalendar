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
import com.nmd.eventCalendar.compose.ui.CalendarOptions
import com.nmd.eventCalendar.compose.ui.defaultCalendarStyle
import com.nmd.eventCalendar.compose.ui.rememberCalendarController
import com.nmd.eventCalendar.theme.AppTheme
import com.nmd.eventCalendarSample.R
import java.time.DayOfWeek

@RequiresApi(Build.VERSION_CODES.O)
class EventCalendarComposeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                Screen {
                    onBackPressedDispatcher.onBackPressed()
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Screen(callback: () -> Unit) {
    val calendarController = rememberCalendarController()

    // Saveable primitives
    var weekStartValue by rememberSaveable { mutableIntStateOf(DayOfWeek.MONDAY.value) }
    var headerVisible by rememberSaveable { mutableStateOf(true) }
    var showCalendarWeek by rememberSaveable { mutableStateOf(true) }

    val calendarOptions = CalendarOptions(
        weekStart = DayOfWeek.of(weekStartValue),
        headerVisible = headerVisible,
        calendarWeekVisible = showCalendarWeek
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.app_name_compose),
                        color = if (isSystemInDarkTheme()) Color.White else Color.Black
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { callback() }) {
                        Icon(
                            painter = painterResource(R.drawable.icon_arrow_left),
                            contentDescription = "Back",
                            tint = if (isSystemInDarkTheme()) Color.White else Color.Black
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {
                        weekStartValue = (weekStartValue % 7) + 1
                    }) {
                        Icon(
                            painter = painterResource(R.drawable.icon_calendar_start),
                            contentDescription = "Change week start",
                            tint = if (isSystemInDarkTheme()) Color.White else Color.Black
                        )
                    }

                    IconButton(onClick = { calendarController.jumpToCurrentMonth() }) {
                        Icon(
                            painter = painterResource(R.drawable.icon_calendar_today),
                            contentDescription = "Jump to current month",
                            tint = if (isSystemInDarkTheme()) Color.White else Color.Black
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = if (isSystemInDarkTheme()) Color(0xFF1B1B1F) else Color.White
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showCalendarWeek = !showCalendarWeek },
                containerColor = Color(0xFFF0E3A8),
                contentColor = Color(0xFF895900)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.icon_calendar_week_begin_outline),
                    contentDescription = "Toggle calendar week"
                )
            }
        },
        containerColor = if (isSystemInDarkTheme()) Color(0xFF1B1B1F) else Color.White
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
                onDaySelected = {
                    Log.i("EventCalendarCompose", "Selected day: ${it.date}")
                },
                calendarOptions = calendarOptions,
                calendarStyle = defaultCalendarStyle().copy(
                    fontsize = 12.sp
                )
            )

            Spacer(
                modifier = Modifier.windowInsetsBottomHeight(WindowInsets.navigationBars)
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun EventCalendarComposePreview() {
    AppTheme {
        Screen(callback = {})
    }
}