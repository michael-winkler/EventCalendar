package com.nmd.eventCalendar.compose.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
internal fun EventChip(
    text: String,
    shapeColor: Color,
    textColor: Color,
    modifier: Modifier = Modifier,
) {
    val shape = remember { RoundedCornerShape(6.dp) }
    Text(
        text = text,
        color = textColor,
        fontSize = 10.sp,
        maxLines = 1,
        softWrap = false,
        lineHeight = 10.sp,
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(shapeColor)
            .padding(2.dp)
    )
}

@Preview(showBackground = true)
@Composable
fun EventChipPreview() {
    EventChip(
        text = "Cooking",
        shapeColor = Color(0xFF673AB7),
        textColor = Color.White
    )
}