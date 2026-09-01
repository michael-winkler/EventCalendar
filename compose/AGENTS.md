# AGENTS.md — `:compose`

Standalone Jetpack Compose calendar library. Root rules in `/AGENTS.md` still apply.

## Entry points (the only public surface that matters)

- `EventCalendarCompose` — month grid with horizontal month paging.
- `EventCalendarWeekTime` — time grid for 1 / 3 / 7 days.
- `rememberCalendarController(...)`, `rememberCalendarEventsStore(...)`.

Everything else is `internal`. Adding a new public symbol is an API decision — say so explicitly.

## Package layout

| Package | Holds |
| --- | --- |
| `model/` | `Event`, `EventTimeRange`, `CalendarDay`, `YearMonth` — plain, serializable, no Android types |
| `ui/components/` | Composables: `MonthView`, `WeekRow`, `DayNumberCell`, `WeekHeader`, `TimeGridView`, … |
| `ui/config/` | `CalendarOptions`, `CalendarStyle`, layout modifiers |
| `ui/controller/` | `CalendarController` — paging / navigation |
| `ui/events/` | `CalendarEventsStore` and implementations |
| `ui/shapes/` | Prebuilt corner shapes (allocated once, not per recomposition) |
| `util/` | Pure logic: `generateMonthDays`, `EventSpanUtil`, `isoWeekNumber`, `toStringRes` |
| `viewmodel/` | `CalendarEventsViewModel` |

## Conventions that bite if ignored

- **Dates:** `kotlinx-datetime` only. Current day:
  `Clock.System.todayIn(TimeZone.currentSystemDefault())`. Month math goes through `YearMonth`.
- **Unidirectional data flow:** composables read state and report back through lambdas; they never
  mutate the store directly.
- **Collect with lifecycle:** `collectAsStateWithLifecycle()`, not `collectAsState()`.
- **Keep pure logic in `util/`,** not inside composables — that's what the unit tests cover
  (`compose/src/test/.../EventSpanUtilTest.kt`).
- **Cross-module smart casts don't work.** A public nullable property of `Event` (e.g. `timeRange`)
  cannot be smart-cast in `:app`; bind it to a local `val` first.
- **`@Preview` functions are `internal`** so they stay out of the public API.

## Before you finish

```bash
./gradlew :compose:compileDebugKotlin :compose:testDebugUnitTest
```

## Feature details (read only what you touch)

- `docs/compose/multi-day-events.md` — `endDate`, auto-merge, span semantics
- `docs/compose/events-store.md` — `CalendarEventsStore`, `eventsFlow`, deprecated alias
- `docs/compose/month-view-rendering.md` — lanes, column grid, "+N" overflow, click layer
- `docs/compose/week-time-view.md` — `EventCalendarWeekTime` / `TimeGridView`
- `docs/compose/theming.md` — `CalendarStyle`, `CalendarOptions`
