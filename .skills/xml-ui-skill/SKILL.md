---
name: xml-ui-skill
description: AI-optimized instructions for developing View-based XML UI in the :xml module. Updated for KMP-compatible models.
metadata:
  author: Michael Winkler
  version: "1.5"
---

# XML / View-Based UI Skill (EventCalendar)

This skill provides specialized instructions for the `:xml` module, focusing on the
`EventCalendarView` implementation with KMP-compatible data models.

## Main Components & Architecture

- **Primary View**: `EventCalendarView` (Custom FrameLayout). Manages a `RecyclerView` with
  `InfiniteAdapter`.
- **Date Handling**: Uses a mix of `java.util.Calendar` for legacy Android View logic and
  `kotlinx-datetime` for modern data models.
- **Model Compatibility**: Models in `model/` must be plain data classes without `Parcelable` to
  maintain consistency with the `:compose` module.
- **State Management**: `InstanceState` handles saving and restoring view state in-memory.

## Core Development Principles

1. **Hybrid Date API & API 23**:
    - **Min SDK 23**: Native support for API 23+.
    - **Logic**: Uses a mix of `java.util.Calendar` for UI logic and `kotlinx-datetime` for data
      models.
2. **State Preservation**: The `InstanceState` class is crucial. Always update it when adding new
   configurable properties.
3. **Internal Visibility**: Most implementation details (Adapters, ViewHolders, Internal Methods)
   must be `internal`.
4. **Namespacing & Localization**:
    - Strictly use the `ecv_` prefix for all resources.
    - Ensure all UI strings are localized via `strings.xml`.

## Step-by-Step Workflows

### Modifying Month Logic

1. **Range**: Navigation range is defined by `sMonth/sYear` to `eMonth/eYear`.
2. **Scroll**: Use `scrollTo()` or `scrollToCurrentMonth()` in `EventCalendarView` for programmatic
   navigation.

### Handling Event Updates

1. **Setter**: The `events` property in `EventCalendarView` triggers `updateRecyclerView()`.
2. **Model**: Ensure `model/Event.kt` remains a plain data class without Android-specific
   serialization.

## Best Practices

- **ViewBinding**: Always use `binding` for type-safe access.
- **Namespacing**: Strictly follow `ecv_` for all resources.
- **Date Mapping**: Use `Utils` helpers for any conversion between `Calendar` and
  `kotlinx-datetime`.
