# XML components (`:xml`)

The custom Views and how they render.

## `EventCalendarView`

Full month calendar with infinite scrolling.

- **Structure:** `FrameLayout` hosting a `RecyclerView` driven by `InfiniteAdapter`, which binds
  `EcvEventCalendarViewBinding` per month.
- **Key methods:** `setMonthAndYear`, `scrollTo`, `scrollToCurrentMonth`, `updateRecyclerView`.
- **Range:** navigation is bounded by `sMonth/sYear` … `eMonth/eYear`.
- **Events:** assigning the `events` property triggers `updateRecyclerView()`.
- **Init:** attributes are read with
  `context.withStyledAttributes(attrs, R.styleable.EventCalendarView) { ... }`.

## `EventCalendarSingleWeekView`

Lightweight view showing only the current week.

- **Key methods:** `renderWeekView`, `styleTextViews`, `updateWeekNumberView`.
- **Days:** computed via `getDaysForCurrentWeek(weekStartDay)`.
- **Rendering:** unlike the month view there is no adapter — it owns a list of
  `EcvTextviewCircleBinding` day cells and updates them directly in `styleTextViews()`.

## `VerticalMaterialTextView`

Custom `TextView` that draws rotated text; used for week numbers / side labels.

## Adding a public method

1. Add it to the component class and document it — this is library API consumed by `:app`.
2. Implement using `java.util.Calendar` or the `Utils` helpers (legacy View logic is allowed here).
3. Re-sync the UI: call `updateLayout()` or `updateRecyclerView()`.
4. If it adds configurable state, extend `InstanceState.StateModel` too, or it is lost on rotation.
