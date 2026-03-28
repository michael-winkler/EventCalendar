package com.nmd.eventCalendar

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.nmd.eventCalendar.compose.EventCalendarCompose

class EventCalendarComposeActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            EventCalendarCompose()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EventCalendarComposePreview() {
    EventCalendarCompose()
}