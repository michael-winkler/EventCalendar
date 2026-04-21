---
name: compose-ui-skill
description: AI-optimized instructions for developing Jetpack Compose UI in the :compose module. Focuses on the EventCalendarCompose architecture, state management with CalendarController, and component patterns.
metadata:
  author: Michael Winkler
  version: "1.0"
---

# Jetpack Compose UI Skill (EventCalendar)

This skill provides deep technical guidance for the `:compose` module. It is designed to maintain
the established patterns of the `EventCalendarCompose` library.

## Module Architecture & Entry Point

- **Main Entry**: `EventCalendarCompose` (in `com.nmd.eventCalendar.compose`).
- **Core Components**:
    - `CalendarController`: Manages pager state and navigation.
    - `CalendarEventsStore`: Reactive interface for event data.
    - `CalendarStyle` / `CalendarOptions`: Type-safe configuration.
- **Internal Visibility**: Use `internal` for all sub-components, controllers, and internal logic.

## Core Development Principles

1. **Unidirectional Data Flow (UDF)**: UI consumes state from `CalendarEventsStore` and sends events
   back via callbacks.
2. **State Separation**: Keep UI components (Composables) stateless where possible. Pass state down
   and hoist events up.
3. **Internal by Default**: All implementation details must be `internal`. Only the main entry
   points (e.g., `EventCalendarCompose`) should be public.
4. **Preview Hygiene**: All `@Preview` composables must be `internal`.

## Component Development

- **Packages**:
    - `ui.components`: Small elements (`DayItem`, `EventChip`, `MonthHeader`).
    - `ui.controller`: Logic for calendar navigation (`CalendarController`).
    - `ui.events`: Store abstractions (`CalendarEventsStore`) and implementations.
    - `viewmodel`: Standard Android ViewModels for state processing.
- **Patterns**:
    - **Statelessness**: Favor stateless composables with lambda callbacks.
    - **Previews**: Every UI component should have an `internal` `@Preview` function.
    - **Adaptive Lists**: `DayItem` switches between `Column` and `LazyColumn` based on event
      count (> 3).

## Step-by-Step Workflows

### Creating a New UI Component

1. **Locate**: Place in `ui.components` or a relevant sub-package.
2. **Stateless**: Define parameters for data and lambda callbacks for events.
3. **Preview**: Add an `internal` `@Preview` function.
4. **Integration**: Connect to the parent screen or a controller.

### Modifying Event Logic

1. **ViewModel**: Update `CalendarEventsViewModel` (in `viewmodel/`) to adjust how raw events are
   grouped/sorted.
2. **Store**: Update `CalendarEventsStore` if the data contract needs to change.
3. **Observation**: Ensure the UI uses `.collectAsStateWithLifecycle()` for reactive updates.

## Best Practices

- Use `rememberUpdatedState` for long-running lambdas.
- Leverage `LaunchedEffect` for side effects tied to the Composable lifecycle.
- Keep `build.gradle` changes minimal.
