package com.nmd.eventCalendar.baselineprofile

import androidx.benchmark.macro.BaselineProfileMode
import androidx.benchmark.macro.CompilationMode
import androidx.benchmark.macro.FrameTimingMetric
import androidx.benchmark.macro.junit4.MacrobenchmarkRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Measures frame times while swiping through months in the Compose calendar, without and with the
 * Baseline Profile, to verify the profile's effect.
 *
 * Run `./gradlew :baselineprofile:connectedBenchmarkReleaseAndroidTest` (on an emulator add
 * `-Pandroid.testInstrumentationRunnerArguments.androidx.benchmark.suppressErrors=EMULATOR`).
 */
@RunWith(AndroidJUnit4::class)
class MonthSwipeBenchmark {

    @get:Rule
    val rule = MacrobenchmarkRule()

    @Test
    fun withoutProfile() = swipe(CompilationMode.None())

    @Test
    fun withBaselineProfile() = swipe(CompilationMode.Partial(BaselineProfileMode.Require))

    private fun swipe(compilationMode: CompilationMode) = rule.measureRepeated(
        packageName = TargetPackage,
        metrics = listOf(FrameTimingMetric()),
        compilationMode = compilationMode,
        iterations = 5,
        // Fresh process per iteration, but no startupMode: that would kill the app again between
        // setup and measurement, leaving nothing on screen to swipe.
        setupBlock = {
            killProcess()
            openComposeCalendar()
        }
    ) {
        swipeMonths()
    }
}
