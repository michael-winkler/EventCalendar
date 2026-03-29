package com.nmd.eventCalendar

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.nmd.eventCalendar.compose.EventCalendarCompose
import com.nmd.eventCalendarSample.R

class EventCalendarComposeActivity : ComponentActivity() {

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                topBar = {
                    TopAppBar(
                        title = { Text(text = stringResource(R.string.app_name_compose)) }
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
                    EventCalendarCompose()
                    Spacer(
                        modifier = Modifier.windowInsetsBottomHeight(WindowInsets.navigationBars)
                    )
                }

            }

        }
    }
}

@Preview(showBackground = true)
@Composable
fun EventCalendarComposePreview() {
    EventCalendarCompose()
}