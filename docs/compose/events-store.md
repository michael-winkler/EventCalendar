# Events store (`:compose`)

How events get into the calendar.

## Contract

```kotlin
interface CalendarEventsStore {
    val eventsFlow: StateFlow<List<Event>>   // stable order: start date, start time, name
    fun setEvents(events: List<Event>)
}
```

A **flat list**, not a per-day map. The UI resolves "what is on this day" on demand with
`event.occursOn(date)`.

**Why a list:** the store used to expose `Map<LocalDate, List<Event>>`, built by expanding every
multi-day event across each covered day. That made work and memory scale with an event's *span* — a
far-future `endDate` materialized thousands of entries. With a flat list the cost scales with the
*number of events*, independent of how long they run. Don't reintroduce eager per-day expansion.

## Implementations

- `rememberCalendarEventsStore(initialEvents)` — ViewModel-backed, survives configuration changes;
  seeds `initialEvents` only while the store is empty.
- `PreviewCalendarEventsStore` — `internal`, for previews/tests.

Both order events through the shared `List<Event>.sortedForDisplay()` helper.

## Deprecated compatibility alias

`eventsByDateFlow` still exists as a `@Deprecated` interface default with
`ReplaceWith("eventsFlow")`, so upgrading breaks nothing and Android Studio offers a one-click
replace. It groups by **start date only** (it does not expand spans) and derives lazily from
`eventsFlow`, so it costs nothing unless someone reads it. Backed by a small read-only `StateFlow`
adapter that needs `@OptIn(ExperimentalForInheritanceCoroutinesApi::class)`.

Remove it in a future major release; the migration is documented in `compose/README.md`.

## Consuming it

```kotlin
val events by store.eventsFlow.collectAsStateWithLifecycle()
val onDay = events.filter { it.occursOn(date) }          // overlap-aware
val merged = mergeAdjacentEvents(events)                 // if you want auto-merged spans too
```

`CalendarScreen` builds exactly this `eventsForDate` lambda and passes it down; `EventCalendarWeekTime`
instead builds a small map for **only its visible days**, keyed by start day, because the time grid
positions events within a single day.
