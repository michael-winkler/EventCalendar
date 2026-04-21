---
name: xml-ui-skill
description: AI-optimized instructions for developing View-based XML UI in the :xml module. Focuses on the overall architecture, month-scrolling, and adapter patterns.
metadata:
  author: Michael Winkler
  version: "1.3"
---

# XML / View-Based UI Skill (EventCalendar)

This skill provides specialized instructions for the `:xml` module, focusing on the
`EventCalendarView` implementation and its surrounding ecosystem.

## Main Components & Architecture

- **Primary View**: `EventCalendarView` (Custom FrameLayout). Manages a `RecyclerView` with
  `InfiniteAdapter`.
- **Month Scrolling**: Uses `PagerSnapHelper` and `SnapOnScrollListener` to provide a "
  ViewPager-like" experience for months.
- **Data Model**: `com.nmd.eventCalendar.model.Event`. Uses `Parcelable` for state saving.
- **Binding**: Uses ViewBinding with the `Ecv` prefix (e.g., `EcvEventCalendarBinding`).

## Core Development Principles

1. **Hybrid Date API**: Be aware that the module uses both `java.util.Calendar` and `java.time`. Use
   `Calendar` for legacy range calculations and `java.time` for modern formatting/comparisons where
   appropriate.
2. **State Preservation**: The `InstanceState` class is crucial. Always update it when adding new
   configurable properties to `EventCalendarView`.
3. **Internal Visibility**: Most implementation details (Adapters, ViewHolders, Internal Methods)
   must be `internal`.
4. **Namespacing**: All resources (IDs, layouts, drawables) must use the `ecv_` prefix.

## Step-by-Step Workflows

### Modifying Month Logic

1. **Range**: Navigation range is defined by `sMonth/sYear` to `eMonth/eYear`.
2. **Adapter**: Update `InfiniteAdapter` to change how month pages are created.
3. **Scroll**: Use `scrollTo()` or `scrollToCurrentMonth()` in `EventCalendarView` for programmatic
   navigation.

### Handling Event Updates

1. **Setter**: The `events` property in `EventCalendarView` triggers `updateRecyclerView()`.
2. **DiffUtil**: (Optional) For high-performance updates, implement `DiffUtil` in `EventsAdapter`.

## Best Practices

- **ViewBinding**: Always use `binding` for type-safe access.
- **Namespacing**: Strictly follow `ecv_` for all resources.
- **Performance**: Ensure `setItemViewCacheSize` is set appropriately on the RecyclerViews.
