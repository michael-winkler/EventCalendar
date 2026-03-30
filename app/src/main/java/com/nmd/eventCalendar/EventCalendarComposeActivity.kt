@file:OptIn(ExperimentalMaterial3Api::class)

package com.nmd.eventCalendar

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nmd.eventCalendar.compose.EventCalendarCompose
import com.nmd.eventCalendar.compose.ui.defaultCalendarOptions
import com.nmd.eventCalendar.compose.ui.defaultCalendarStyle
import com.nmd.eventCalendarSample.R

class EventCalendarComposeActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            Screen {
                onBackPressedDispatcher.onBackPressed()
            }
        }
    }
}

@Composable
fun Screen(callback: () -> Unit) {
    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
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
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = if (isSystemInDarkTheme()) Color.White else Color.Black
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = if (isSystemInDarkTheme()) Color(0xFF1B1B1F) else Color.White
                )
            )
        }
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
                modifier = Modifier
                    .background(
                        if (isSystemInDarkTheme()) Color(0xFF1B1B1F) else Color(0xFFFFFFFF)
                    )
                    .padding(bottom = 16.dp),
                calendarOptions = defaultCalendarOptions().copy(
                    calendarWeekVisible = true
                )
            )
            Spacer(
                modifier = Modifier.windowInsetsBottomHeight(WindowInsets.navigationBars)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EventCalendarComposePreview() {
    Screen(callback = {})
}