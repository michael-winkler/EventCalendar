# AGENTS.md

Android **EventCalendar** library: two independent, standalone UI libraries plus a demo app.

> **This file is loaded into context on every session** (Codex reads it root→cwd; Claude Code reads
> it via `CLAUDE.md`). Keep it short. Module rules live in each module's own `AGENTS.md`, and deep
> feature details live in `docs/` — **read those on demand, don't inline them here.**

## Module map

| Module | What it is | Depends on |
| --- | --- | --- |
| `:compose` | Standalone Jetpack Compose calendar library | nothing in this repo |
| `:xml` | Standalone XML/View calendar library | nothing in this repo |
| `:app` | Demo app exercising both libraries | `:compose`, `:xml` |

**`:compose` and `:xml` must never depend on each other.** Keep resources inside the owning module.

## Non-negotiables

- **Min SDK 23.** No API that needs Java 8 desugaring. **No `java.time`** in library code.
- **Dates:** `kotlinx-datetime` for all models and Compose logic. (`:xml` additionally uses
  `java.util.Calendar` for legacy View logic — see `xml/AGENTS.md`.)
- **No `Parcelable`** in models; use `kotlinx-serialization` (custom serializer for `Color`).
- **`internal` by default.** Only documented entry points are public. `@Preview` functions are
  `internal`.
- **Localization via string resources** (day/month names), never hardcoded English.
- **Surgical edits.** Match surrounding style; don't reformat or refactor code you weren't asked to
  touch.

## Commands

```bash
./gradlew :compose:compileDebugKotlin   # compile the Compose library
./gradlew :compose:testDebugUnitTest    # unit tests (pure logic)
./gradlew :xml:compileDebugKotlin       # compile the XML library
./gradlew :app:compileDebugKotlin       # compile the demo app
```

Run at minimum the compile task for every module you touched, plus `:compose:testDebugUnitTest` when
changing Compose logic.

## Where to read next

Load **only** what your task needs.

**Working in a module** → that module's `AGENTS.md` (auto-loaded for files in it):
`compose/AGENTS.md` · `xml/AGENTS.md` · `app/AGENTS.md`

**Working on a specific feature** → read the matching file:

| Task touches | Read |
| --- | --- |
| Multi-day events, `endDate`, auto-merge | `docs/compose/multi-day-events.md` |
| Providing/consuming events, `CalendarEventsStore` | `docs/compose/events-store.md` |
| Month grid, lanes, day cells, bar layout | `docs/compose/month-view-rendering.md` |
| `EventCalendarWeekTime`, time grid | `docs/compose/week-time-view.md` |
| Colors, sizes, `CalendarStyle`, `CalendarOptions` | `docs/compose/theming.md` |
| `EventCalendarView`, `EventCalendarSingleWeekView` | `docs/xml/components.md` |
| `ecv_*` attributes, drawables, expressive mode | `docs/xml/styling.md` |
| Cutting a release / changelog | skill `changelog` (Claude), else `.claude/skills/changelog/SKILL.md` |

## Keeping these docs healthy

When you change behavior, update the **one** file that owns that topic — don't copy the same rule
into several files. If this root file grows past ~100 lines, move detail into `docs/` and leave a
pointer.
