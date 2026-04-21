---
name: compose-ui-skill
description: AI-optimized instructions for developing Jetpack Compose UI in the :compose module. Focuses on KMP-ready state management and component patterns.
metadata:
  author: Michael Winkler
  version: "1.3"
---

# Jetpack Compose UI Skill (EventCalendar)

This skill provides deep technical guidance for the `:compose` module, emphasizing KMP readiness and
reactive patterns.

## Module Architecture & Entry Point

- **Main Entry**: `EventCalendarCompose` (in `com.nmd.eventCalendar.compose`).
- **Date/Time**: Uses `kotlinx-datetime` and the internal `YearMonth` model for all date operations.
- **Internal Visibility**: Use `internal` for all sub-components and controllers.

## Core Development Principles

1. **Unidirectional Data Flow (UDF)**: UI consumes state from `CalendarEventsStore` and sends events
   back via callbacks.
2. **KMP Readiness**: Avoid `java.time` and `android.os.Parcelable`. Use `kotlinx-datetime` and
   plain data classes.
3. **Internal by Default**: All implementation details must be `internal`. Only the main entry
   points should be public.
4. **Preview Hygiene**: All `@Preview` composables must be `internal`.

## Component Development

- **Packages**:
    - `ui.components`: Small elements (`DayItem`, `EventChip`, `MonthHeader`).
    - `ui.controller`: Logic for calendar navigation (`CalendarController`).
    - `ui.events`: Store abstractions (`CalendarEventsStore`).
    - `viewmodel`: Standard Android ViewModels for state processing.
- **Patterns**:
    - **Statelessness**: Favor stateless composables with lambda callbacks.
    - **Previews**: Every UI component should have an `internal` `@Preview` function.

## Step-by-Step Workflows

### Creating a New UI Component

1. **Locate**: Place in `ui.components` or a relevant sub-package.
2. **Stateless**: Define parameters for data and lambda callbacks for events.
3. **Preview**: Add an `internal` `@Preview` function.

### Modifying Event Logic

1. **ViewModel**: Update `CalendarEventsViewModel` (in `viewmodel/`). Ensure it uses
   `kotlinx-datetime` for grouping.
2. **Store**: Update `CalendarEventsStore` if the data contract needs to change.
3. **Observation**: Ensure the UI uses `.collectAsStateWithLifecycle()` for reactive updates.

## Best Practices

- Use `kotlinx.datetime.Clock.System.todayIn(TimeZone.currentSystemDefault())` for current dates.
- Use the internal `YearMonth` class for month-level operations.
- Avoid `String.format` in common code; use string templates or KMP-friendly formatting.
