# Month view rendering (`:compose`)

How `MonthView` → `WeekRow` turns events into aligned bars. Most visual bugs in this repo have come
from breaking one of the invariants below.

## Pipeline

1. `MonthView` builds 42 `CalendarDay`s via `generateMonthDays(yearMonth, weekStart, …)` — already
   ordered by the configured `weekStart`, which is why nothing downstream needs week-start logic.
2. Each day gets `events = eventsForDate(date)` (overlap-aware, see `events-store.md`).
3. Distinct events → optional `mergeAdjacentEvents` → `assignEventLanes` → per-week
   `segmentsForWeek`.
4. `WeekRow` draws one week.

## Lanes

`assignEventLanes` assigns a **global** lane per event across the whole grid (greedy, longest-first
by `spanDays`), so a bar keeps the same vertical row across consecutive weeks — that is what makes a
span read as one object. Longest-first matters: it keeps long spans on low lanes so they survive a
tight row.

`segmentsForWeek` clips each event to one week and yields `EventSegment(lane, startColumn,
endColumn, continuesBefore, continuesAfter)`. Columns are **relative to the week's own dates**, so
any `weekStart` works for free.

## The shared column grid (do not reintroduce weights)

`WeekRow` computes integer pixel boundaries **once**:

```kotlin
bounds[i] = round(i * totalWidthPx / 7)   // i in 0..7
```

Day-cell backgrounds, day numbers **and** event bars are all placed against these same `bounds`.

This exists because Compose `weight()` rounds independently per element: seven `weight(1f)` cells and
one `weight(3f)` bar do **not** land on the same pixels, which made multi-day bars sit 1–2 px off
their cells. Positioning everything from one integer grid removes the drift by construction. If you
find yourself adding `weight()` or extra horizontal padding to this layer, you are re-creating that
bug.

## Bar geometry

- Both ends are inset by `CellPadding` (the same value the cell background uses), so a bar sits
  exactly inside its day cells — including a segment that continues from another week.
- **Rounded corners mark the real start/end; flat (square) corners mean "continues into the adjacent
  week."** That is the only continuation cue — bars deliberately do not overhang the grid edge.
- A multi-day bar bridges the gaps *between* the cells it spans; that continuity is the point.

## Overflow ("+N")

Lanes are global, but the visible-row budget is computed **per week from the lanes actually present
there**, compacted so gaps don't waste rows. A "+N" row is reserved only when at least one real lane
still remains visible — otherwise a single-lane row would hide everything, including a multi-day
bar. `+N` counts hidden events **per day**, so a hidden span legitimately counts on each day it
covers.

## Interaction

The day-cell backgrounds are the click layer; numbers and bars are a non-interactive overlay, so a
tap anywhere in a column (bars included) selects that day and reports that day's events.
