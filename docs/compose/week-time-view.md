# Time grid view (`:compose`)

`EventCalendarWeekTime` → `TimeGridView`: a day/3-day/week view where events are positioned
vertically by their time.

## Shape

- `CalendarOptions.noOfVisibleDays` = `1`, `3` or `7`; the visible days start at the current week's
  `weekStart`.
- `EventCalendarWeekTime` builds a per-day map for **only the visible days**, keyed by the event's
  **start day** — this view is single-day-positioned, so it does not expand spans.
- `TimeGridView` renders a 24-hour background grid plus one `DayTimeColumn` per day.

## Positioning

- Only events with a **valid** `timeRange` render: `resolveOverlaps` drops anything where
  `timeRange == null` or `isValid()` is false (end not after start). All-day events therefore never
  appear here — that is intended.
- Vertical offset and height come from the time range against `hourHeight`.
- Overlapping events are clustered and split into columns so they share the width.

## Boundaries worth knowing

- **`EventTimeRange` never crosses midnight.** A 22:00→02:00 range has a negative duration, fails
  `isValid()`, and is skipped. Cross-midnight support would be a new feature, not a bug fix.
- **Multi-day spanning is a month-view feature.** A multi-day event is not drawn as one spanning
  block here; see `docs/compose/multi-day-events.md`.
- The `timeRange!!` assertions inside `TimeGridView` are safe only because `resolveOverlaps` filtered
  first — keep that ordering if you refactor.
