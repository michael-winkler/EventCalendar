# AGENTS.md — `:app`

Demo app. Its job is to **exercise and showcase** both libraries — it is not where features belong.

Root rules in `/AGENTS.md` still apply.

## Layout

- `MainActivity` — chooser between the two implementations.
- `EventCalendarComposeActivity` — the Compose demo (options toolbar, day sheet, event generator).
- `DayEventsSheet.kt` — bottom sheet listing a tapped day's events.

## Rules of thumb

- **Put library behavior in the library.** If the demo needs a helper the library should expose,
  expose it there (public API decision) rather than duplicating logic in `:app`.
- **The generator is documentation.** `shuffleEventsForCurrentYear` deliberately produces every
  shape a consumer might hit — single-day, timed, explicit `endDate` spans, and *separate
  consecutive same-type events* that exercise auto-merge. Keep that coverage when editing it.
- **Deterministic showcase events** anchored around "today" (e.g. a week-crossing "Road Trip") must
  stay, so the demo always shows a multi-day bar on launch.
- **Cross-module smart casts fail here:** `Event.timeRange` and friends are public properties from
  `:compose`, so bind them to a local `val` before a null check.

## Before you finish

```bash
./gradlew :app:compileDebugKotlin
```
