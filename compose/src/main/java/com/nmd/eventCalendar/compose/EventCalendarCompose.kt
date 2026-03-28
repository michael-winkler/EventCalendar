package com.nmd.eventCalendar.compose

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun EventCalendarCompose(

) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Text(text = "Hello world!")
    }
}

@Preview(showBackground = true)
@Composable
fun EventCalendarComposePreview() {
    EventCalendarCompose()
}