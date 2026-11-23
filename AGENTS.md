# EventCalendar - Agent Index

The one instruction file for every agent (Claude, Copilot, Cursor, Gemini, ...). Tool-specific files
only point here. **Read only what your task needs.**

## Project
- Android calendar library with two UI modules: `:compose` (Jetpack Compose, Material 3) and `:xml`
  (classic Views). `:app` is the sample app and only coordinates.
- Kotlin, KMP-ready: `kotlinx-datetime`, `kotlinx-serialization`.
- **Min SDK**: 24 for `:compose` (Material3 1.5 needs it), 23 for `:xml`.

## Commands
- Build: `./gradlew assembleDebug`
- Test: `./gradlew test`
- Lint: `./gradlew lint`

## Core Rules
1. **Module separation**: `:compose` and `:xml` never depend on each other; no mixed UI.
2. **`:xml` is deprecated**: no new features or API there. New work goes into `:compose`; touch
   `:xml` only for upkeep that keeps it compiling.
3. **KMP compatibility**: `kotlinx-datetime`, no `java.time` in UI code (it needs API 26).
4. **Internal by default**: only the main entry points (`EventCalendarCompose`,
   `EventCalendarView`) are public.
5. **Surgical edits**: change only what the task needs, no unrelated refactors, keep public APIs
   stable, follow the existing style.
6. **Text files use CRLF** (`.gitattributes`).

## Instructions
- [Architecture & boundaries](.agents/core/architecture.md): modules, layering, platform boundary,
  baseline profile, visibility.
- [Working approach](.agents/core/working-approach.md): analyse, edit, verify.
- [`:compose` guidelines](.agents/modules/compose.md)
- [`:xml` guidelines](.agents/modules/xml.md) - deprecated module.
- [Calendar & date logic](.agents/features/calendar-logic.md)
- [Event management](.agents/features/event-management.md): models, multi-day events, stores.
- [Styling](.agents/features/styling.md)

## Skills (`.agents/skills/`)
- [commit](.agents/skills/commit/SKILL.md) - "commit", "commit everything", "commit as me".
- [changelog](.agents/skills/changelog/SKILL.md) - "bump the version", "write the changelog".
- [release](.agents/skills/release/SKILL.md) - "make a release", "release X.Y.Z", "ein Release
  machen"; calls the changelog skill.
- [compose-ui-skill](.agents/skills/compose-ui-skill/SKILL.md) - deep guidance for `:compose`.
- [xml-ui-skill](.agents/skills/xml-ui-skill/SKILL.md),
  [xml-components-skill](.agents/skills/xml-components-skill/SKILL.md),
  [xml-styling-skill](.agents/skills/xml-styling-skill/SKILL.md) - `:xml` only, deprecated.