# Theming & options (`:compose`)

## `CalendarStyle` — looks

Built by `defaultCalendarStyle()`; every field is overridable via `.copy(...)`. Colors are
**theme-aware** (`isSystemInDarkTheme()`), so add both light and dark values when introducing one.

Groups: month header, weekday header, day cell (`dayItemBackgroundColor`, `dayItemTextColor`),
today badge (`currentDay*`), week-number column (`weekItem*`), plus `textUnit` /
`monthNameTextUnit` for sizing.

## `CalendarOptions` — behavior

| Option | Effect |
| --- | --- |
| `weekStart` | First column of the grid; everything downstream derives from the generated day order |
| `headerVisible` | Show the month header |
| `calendarWeekVisible` | Show the ISO week-number column |
| `minDate` / `maxDate` | Bound the pager range |
| `openEndedWindowMonths` | Total months generated when the range is open-ended |
| `isCurrentWeekOnly` | Render only the current week (ignores min/max and the window) |
| `noOfVisibleDays` | Days in `EventCalendarWeekTime` (1 / 3 / 7) |
| `mergeAdjacentEvents` | Auto-merge consecutive same-type all-day events (default `true`) |

## Conventions

- **Don't hardcode colors or sizes in composables** — read them from `CalendarStyle` so consumers can
  restyle. Layout constants that are structural rather than cosmetic (lane height, cell padding) live
  as private values next to the component that owns them.
- Shapes are prebuilt in `ui/shapes/` and remembered, not allocated per recomposition.
- Layout mode switches (portrait / phone-landscape / single-week) live in `ui/config/CalendarLayout.kt`
  as `Modifier` extensions — extend those rather than branching inside components.
