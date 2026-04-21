package com.nmd.eventCalendar.compose.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nmd.eventCalendar.compose.model.MonthHeaderLayout
import com.nmd.eventCalendar.compose.model.YearMonth
import com.nmd.eventCalendar.compose.ui.config.CalendarStyle
import com.nmd.eventCalendar.compose.ui.config.defaultCalendarStyle
import com.nmd.eventcalendar.compose.R
import kotlinx.datetime.number
import java.time.format.TextStyle
import java.util.Locale

private val nowMonth: YearMonth = YearMonth.now()

/**
 * Displays the current month label and navigation buttons to move to the previous/next month.
 *
 * The component supports two layouts:
 * - [MonthHeaderLayout.TopBar]: a horizontal header typically used in portrait layouts
 * - [MonthHeaderLayout.SideBar]: a vertical header with rotated text typically used in landscape layouts
 *
 * The title shows the year only when it differs from the device's current year.
 *
 * @param currentMonth The month currently displayed.
 * @param onPreviousMonth Callback invoked when the user taps the "previous month" button.
 * @param onNextMonth Callback invoked when the user taps the "next month" button.
 * @param calendarStyle Style configuration for colors and typography.
 * @param layout Header layout variant.
 * @param showNavigation Whether to show the previous/next navigation buttons.
 */
@Composable
internal fun MonthHeader(
    currentMonth: YearMonth,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    calendarStyle: CalendarStyle,
    layout: MonthHeaderLayout = MonthHeaderLayout.TopBar,
    showNavigation: Boolean = true
) {
    val title = remember(currentMonth) {
        val showYear = currentMonth.year != nowMonth.year
        val monthName = java.time.Month.of(currentMonth.month.number)
            .getDisplayName(TextStyle.FULL, Locale.getDefault())
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
                if (showNavigation) {
                    MonthNavButton(
                        isPrevious = true,
                        onClick = onPreviousMonth,
                        calendarStyle = calendarStyle
                    )
                } else {
                    Spacer(modifier = Modifier.size(48.dp))
                }

                MonthTitle(
                    title = title,
                    calendarStyle = calendarStyle,
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                if (showNavigation) {
                    MonthNavButton(
                        isPrevious = false,
                        onClick = onNextMonth,
                        calendarStyle = calendarStyle
                    )
                } else {
                    Spacer(modifier = Modifier.size(48.dp))
                }
            }
        }

        MonthHeaderLayout.SideBar -> {
            Column(
                modifier = Modifier.fillMaxHeight(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                if (showNavigation) {
                    MonthNavButton(
                        isPrevious = true,
                        onClick = onPreviousMonth,
                        calendarStyle = calendarStyle
                    )
                } else {
                    Spacer(modifier = Modifier.size(48.dp))
                }

                MonthTitle(
                    title = title,
                    calendarStyle = calendarStyle,
                    modifier = Modifier
                        .wrapContentWidth(unbounded = true)
                        .graphicsLayer(rotationZ = -90f)
                        .padding(vertical = 8.dp),
                    textAlign = TextAlign.Center
                )

                if (showNavigation) {
                    MonthNavButton(
                        isPrevious = false,
                        onClick = onNextMonth,
                        calendarStyle = calendarStyle
                    )
                } else {
                    Spacer(modifier = Modifier.size(48.dp))
                }
            }
        }
    }
}

/**
 * A navigation icon button used by [MonthHeader] to move between months.
 *
 * @param isPrevious If true, shows the "previous" icon and accessibility label; otherwise "next".
 * @param onClick Callback invoked when the button is tapped.
 * @param calendarStyle Style configuration for icon tint.
 */
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

/**
 * Renders the month title text used by [MonthHeader].
 *
 * @param title The formatted title to display.
 * @param calendarStyle Style configuration for typography and text color.
 * @param modifier Modifier applied to the text.
 * @param textAlign Optional text alignment (useful for the rotated sidebar title).
 */
@Composable
private fun MonthTitle(
    title: String,
    calendarStyle: CalendarStyle,
    modifier: Modifier = Modifier,
    textAlign: TextAlign? = null
) {
    Text(
        text = title,
        fontSize = calendarStyle.monthNameTextUnit,
        fontWeight = FontWeight.Bold,
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
internal fun MonthHeaderPreview() {
    MonthHeader(
        currentMonth = YearMonth.now(),
        onPreviousMonth = {},
        onNextMonth = {},
        calendarStyle = defaultCalendarStyle()
    )
}

@Preview(name = "MonthHeader - SideBar", showBackground = true, widthDp = 96, heightDp = 430)
@Composable
internal fun MonthHeaderSideBarPreview() {
    MonthHeader(
        currentMonth = YearMonth.now(),
        onPreviousMonth = {},
        onNextMonth = {},
        calendarStyle = defaultCalendarStyle(),
        layout = MonthHeaderLayout.SideBar
    )
}