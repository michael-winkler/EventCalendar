# Multi-day events (`:compose`)

How an event that covers more than one day becomes **one continuous bar** in the month grid.

## Two ways to model a span

1. **Explicit** — one `Event` with an inclusive `endDate`. Unambiguous; use this when the caller
   knows the range.
2. **Auto-merge** — several *separate* single-day events of the same type on consecutive days get
   combined for rendering. Controlled by `CalendarOptions.mergeAdjacentEvents` (default `true`).

## `Event` span API

| Member | Meaning |
| --- | --- |
| `date` | Start day (inclusive) |
| `endDate` | End day (inclusive), `null` = single-day |
| `lastDate` | Effective end; coerces an `endDate` **before** `date` back to `date` |
| `isMultiDay` | `lastDate > date` |
| `spanDays` | Inclusive day count, always ≥ 1 |
| `occursOn(day)` | Overlap test — use this for "what's on this day", never `date == day` |

## Auto-merge rules (`util/EventSpanUtil.kt` → `mergeAdjacentEvents`, public)

Merging is deliberately conservative, because *a daily recurring appointment and a real multi-day
event look identical in the data*. The safeguards:

- **All-day only** (`timeRange == null`). A repeating time-of-day signals a recurring appointment,
  so timed events are never merged. This is the single most important guard.
- **Pure single-day only** (`endDate == null`); explicit spans pass through untouched.
- **Exact same type**: name + `shapeColor` + `textColor` + `autoAdjustTextColorForBackground`.
- **Strictly consecutive, gap-free** days; a gap splits into two spans.
- **A day may hold several same-type events**: one representative per day forms the span, extras
  survive as their own single-day chips. (Regression: a duplicate on one day used to cancel the
  *entire* type's merge.)

The synthesized span is `representative.copy(endDate = runEnd)` so ids/keys stay stable.

## Invariants worth protecting

- **Merging is a rendering transform, not a data change.** The store keeps the original events, so
  `onDaySelected` reports the real per-day events. If a consumer wants the merged span in their own
  UI (e.g. a details sheet showing "Mon–Wed"), they call `mergeAdjacentEvents(...)` themselves —
  that is exactly why it is public.
- **Timed cross-midnight spans are not supported** in the time grid; `EventTimeRange` lives inside
  one day. Multi-day spanning is a month-view feature.

## Tests

`compose/src/test/java/.../util/EventSpanUtilTest.kt` pins the merge behavior: consecutive merge,
gaps, timed events, differing colors, explicit `endDate`, duplicate-in-chain, multiple runs. Add a
case there before changing merge semantics.
