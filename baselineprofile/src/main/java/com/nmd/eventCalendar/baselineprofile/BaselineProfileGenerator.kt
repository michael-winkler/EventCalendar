package com.nmd.eventCalendar.baselineprofile

import androidx.benchmark.macro.junit4.BaselineProfileRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Generates the Baseline Profile of the :compose library from the sample app.
 *
 * Run `./gradlew :compose:generateBaselineProfile` with a device or emulator (API 33+) connected.
 */
@RunWith(AndroidJUnit4::class)
class BaselineProfileGenerator {

    @get:Rule
    val rule = BaselineProfileRule()

    @Test
    fun generate() = rule.collect(packageName = TargetPackage, includeInStartupProfile = true) {
        openComposeCalendar()
        swipeMonths()
        useTimeGrids()
    }
}
