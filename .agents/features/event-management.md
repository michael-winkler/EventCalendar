# Event Management

## Data Models
- **`Event`**: Plain data class (KMP-ready).
- **`EventTimeRange`**: Start/end time within a **single** day (never crosses midnight).
- **Multi-Day**: Optional inclusive `endDate`. Helpers: `lastDate`, `isMultiDay`, `spanDays`, `occursOn(date)`. An `endDate` before `date` coerces to single-day.
- **Visibility**: `internal` where possible.

## Multi-Day Events (month view)
Two ways to model a span — both render as **one continuous bar**:
- **Explicit**: one `Event` with `date` + `endDate`.
- **Auto-merge**: several separate same-type single-day events on consecutive days.

Rules for auto-merge (`mergeAdjacentEvents(events)`, public; gated by `CalendarOptions.mergeAdjacentEvents`, default `true`):
- All-day only (`timeRange == null`) — keeps daily recurring appointments separate.
- Exact same type (name + colors) and strictly consecutive, gap-free days.
- **Render-only**: the store keeps the original events, so `onDaySelected` reports the real per-day events.

## Logic Layer
- **ViewModels**: Process events. Use `viewModelScope`.
- **Store**: `CalendarEventsStore` exposes `eventsFlow: StateFlow<List<Event>>` (flat, stably ordered). Resolve a day with `event.occursOn(date)` — cost scales with the number of events, not with span length.
- **Deprecated**: `eventsByDateFlow` is a `@Deprecated` alias (grouped by **start** date only). Don't use in new code.
- **Update Flow**: Unidirectional updates. Setter triggers re-render.
