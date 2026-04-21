---
name: xml-components-skill
description: Technical instructions for the custom View components in the :xml module, including EventCalendarView and EventCalendarSingleWeekView.
metadata:
  author: Michael Winkler
  version: "1.0"
---

# XML Components Skill (EventCalendar)

This skill focuses on the logic and lifecycle of the custom UI components within the `:xml` module.

## Key Components

### 1. EventCalendarView

- **Purpose**: Full monthly calendar with infinite scrolling.
- **Key Methods**: `setMonthAndYear`, `scrollTo`, `updateRecyclerView`.
- **Initialization**: Reads attributes from `R.styleable.EventCalendarView`.

### 2. EventCalendarSingleWeekView

- **Purpose**: Lightweight view showing only the current week.
- **Key Methods**: `renderWeekView`, `styleTextViews`, `updateWeekNumberView`.
- **Logic**: Calculates current week days using `getDaysForCurrentWeek(weekStartDay)`.

### 3. VerticalMaterialTextView

- **Purpose**: Custom TextView for vertical text display (used for week numbers or labels).

## Component Patterns

### Attribute Reading

Always use `context.withStyledAttributes(attrs, R.styleable.ComponentName) { ... }` in the `init`
block.

### Layout Rendering

- **EventCalendarView**: Relies on `InfiniteAdapter` to bind `EcvEventCalendarViewBinding` for each
  month.
- **EventCalendarSingleWeekView**: Manages its own internal list of `EcvTextviewCircleBinding` (
  days) and manually updates them in `styleTextViews()`.

### Event List Management

Both views maintain an `eventArrayList`. When set, they trigger a re-render of the relevant
sub-views or adapters.

## Workflow: Adding a Component Method

1. **Define**: Add the method to the component class.
2. **Logic**: Implement the logic using `java.util.Calendar` or `Utils` helpers.
3. **UI Sync**: Call `updateLayout()` or `updateRecyclerView()` to reflect changes.
4. **Public API**: Ensure the method is documented and accessible to the `:app` module.
