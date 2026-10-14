package com.nmd.eventCalendar.baselineprofile

import androidx.benchmark.macro.MacrobenchmarkScope
import androidx.test.uiautomator.By
import androidx.test.uiautomator.Direction
import androidx.test.uiautomator.Until
import java.util.regex.Pattern

internal const val TargetPackage = "com.nmd.eventCalendar"

// Content descriptions of the sample's toolbar actions (English and German device locales).
private val SwitchView = Pattern.compile("Switch view|Ansicht wechseln")

/** Opens the sample's Compose screen from the launcher activity. */
internal fun MacrobenchmarkScope.openComposeCalendar() {
    pressHome()
    startActivityAndWait()
    device.findObject(By.res(packageName, "activityMainCompose")).click()
    check(device.wait(Until.hasObject(By.desc(SwitchView)), 5_000)) { "Compose calendar did not open" }
    device.waitForIdle()
}

/** Swipes through several months forward and back. */
internal fun MacrobenchmarkScope.swipeMonths(count: Int = 4) {
    val y = device.displayHeight / 2
    val left = device.displayWidth / 8
    val right = device.displayWidth - left
    repeat(count) { device.swipe(right, y, left, y, 10); device.waitForIdle() }
    repeat(count) { device.swipe(left, y, right, y, 10); device.waitForIdle() }
}

/** Cycles through the week, 3-day and day time grids, scrolling each, and back to the month. */
internal fun MacrobenchmarkScope.useTimeGrids() {
    repeat(3) {
        device.findObject(By.desc(SwitchView)).click()
        device.waitForIdle()
        val x = device.displayWidth / 2
        device.swipe(x, device.displayHeight * 3 / 4, x, device.displayHeight / 3, 10)
        device.waitForIdle()
        device.swipe(x, device.displayHeight / 3, x, device.displayHeight * 3 / 4, 10)
        device.waitForIdle()
    }
    device.findObject(By.desc(SwitchView)).click()
    device.waitForIdle()
}
