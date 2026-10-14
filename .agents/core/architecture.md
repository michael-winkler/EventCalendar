# Architecture & Boundaries

## Module Structure
- **`:app`**: Coordinator. No business logic.
- **`:compose`**: Independent UI library (Compose). KMP-ready.
- **`:xml`**: Independent UI library (Views). Legacy/Classic.
- **Constraint**: `:compose` and `:xml` **must not** depend on each other.

## Layering (Both Modules)
1. **UI Layer**: visual components (Stateless favor).
2. **Logic Layer**: ViewModels & Controllers.
3. **Domain Layer**: Models (plain data classes).

## Compatibility & KMP
- **Min SDK**: Android API 23.
- **Date/Time**: Use `kotlinx-datetime`. **Avoid** `java.time` in UI layer to support API 23.
- **Localization**: Use resource-based localization for day/month names.
- **Serialization**: Use `kotlinx-serialization`. No `Parcelable` in common models.
- **Platform boundary (`:compose`)**: Only two files may use Android/JVM-only APIs:
  `util/AndroidResources.kt` (Android `R` resources, exposed to the UI as composable accessors such
  as `dayOfWeekShortName()`) and `util/ProcessUniqueId.kt` (`java.util.concurrent` counter). They
  become the `actual`s when the module moves to Kotlin Multiplatform. Everywhere else: no `R`,
  `java.*`, `android.*`, `String.format` or `::class.java`; icons are `ImageVector`s in code
  (`ui/icons/CalendarIcons.kt`), number formatting goes through `twoDigits()`.

## Performance (`:compose`)
- **Baseline Profile**: `compose/src/main/generated/baselineProfiles/baseline-prof.txt` is shipped
  with the library. It is generated from the sample app by `:baselineprofile`; regenerate it after
  changing hot paths (month paging, time grid) with `./gradlew :compose:generateBaselineProfile`
  (device/emulator with API 33+ connected). Never edit it by hand.
- **Benchmark**: `./gradlew :baselineprofile:connectedBenchmarkReleaseAndroidTest` compares frame
  times without/with the profile (on an emulator add
  `-Pandroid.testInstrumentationRunnerArguments.androidx.benchmark.suppressErrors=EMULATOR`).
- **Per-page work must not scale with days x events**: distribute events in one pass
  (`eventsInRange`), never filter the full list once per day.

## Visibility
- **Default**: `internal` for all implementation details.
- **Public**: Only main entry points (e.g., `EventCalendarView`, `EventCalendarCompose`).
