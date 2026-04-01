package com.nmd.eventCalendar.compose.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nmd.eventCalendar.compose.model.MonthHeaderLayout
import com.nmd.eventCalendar.compose.ui.config.CalendarStyle
import com.nmd.eventCalendar.compose.ui.config.defaultCalendarStyle
import com.nmd.eventcalendar.compose.R
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

private val nowMonth: YearMonth = YearMonth.now()

@Composable
fun MonthHeader(
    currentMonth: YearMonth,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    calendarStyle: CalendarStyle,
    layout: MonthHeaderLayout = MonthHeaderLayout.TopBar
) {
    val title = remember(currentMonth) {
        val showYear = currentMonth.year != nowMonth.year
        val monthName = currentMonth.month.getDisplayName(TextStyle.FULL, Locale.getDefault())
        if (showYear) "$monthName ${currentMonth.year}" else monthName
    }

    when (layout) {
        MonthHeaderLayout.TopBar -> {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                MonthNavButton(
                    isPrevious = true,
                    onClick = onPreviousMonth,
                    calendarStyle = calendarStyle
                )
                MonthTitle(
                    title = title,
                    calendarStyle = calendarStyle,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
                MonthNavButton(
                    isPrevious = false,
                    onClick = onNextMonth,
                    calendarStyle = calendarStyle
                )
            }
        }

        MonthHeaderLayout.SideBar -> {
            Column(
                modifier = Modifier.fillMaxHeight(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                MonthNavButton(
                    isPrevious = true,
                    onClick = onPreviousMonth,
                    calendarStyle = calendarStyle
                )
                MonthTitle(
                    title = title,
                    calendarStyle = calendarStyle,
                    modifier = Modifier
                        .wrapContentWidth(unbounded = true)
                        .graphicsLayer(rotationZ = -90f)
                        .padding(vertical = 8.dp),
                    textAlign = TextAlign.Center
                )
                MonthNavButton(
                    isPrevious = false,
                    onClick = onNextMonth,
                    calendarStyle = calendarStyle
                )
            }
        }
    }
}

@Composable
private fun MonthNavButton(
    isPrevious: Boolean,
    onClick: () -> Unit,
    calendarStyle: CalendarStyle
) {
    IconButton(onClick = onClick) {
        Icon(
            painter = painterResource(if (isPrevious) R.drawable.chevron_left else R.drawable.chevron_right),
            contentDescription = if (isPrevious) "Previous month" else "Next month",
            tint = calendarStyle.monthNavigationIconColor
        )
    }
}

@Composable
private fun MonthTitle(
    title: String,
    calendarStyle: CalendarStyle,
    modifier: Modifier = Modifier,
    textAlign: TextAlign? = null
) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        color = calendarStyle.monthNameTextColor,
        textAlign = textAlign,
        maxLines = 1,
        softWrap = false,
        overflow = TextOverflow.Clip,
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun MonthHeaderPreview() {
    MonthHeader(
        currentMonth = YearMonth.now(),
        onPreviousMonth = {},
        onNextMonth = {},
        calendarStyle = defaultCalendarStyle()
    )
}

@Preview(name = "MonthHeader - SideBar", showBackground = true, widthDp = 96, heightDp = 430)
@Composable
fun MonthHeaderSideBarPreview() {
    MonthHeader(
        currentMonth = YearMonth.now(),
        onPreviousMonth = {},
        onNextMonth = {},
        calendarStyle = defaultCalendarStyle(),
        layout = MonthHeaderLayout.SideBar
    )
}