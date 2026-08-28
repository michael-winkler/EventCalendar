---
name: changelog
description: >-
  Write a release changelog / release notes for the EventCalendar library in this repo's house
  style, and bump the version. Use this whenever the user asks to "make a release", "cut a release",
  "prepare vX.Y.Z", "bump the version", "write the changelog", "release notes", or anything about
  publishing a new version — even if they don't name a version. Produces GitHub-release-ready
  Markdown with the project's emoji sections and the "Full Changelog" compare link, and updates
  VERSION_NAME in gradle.properties.
---

# Release changelog (EventCalendar)

This repo ships releases via JitPack: a GitHub release/tag drives the published version, and
`VERSION_NAME` in `gradle.properties` sets the artifact version. The changelog itself lives in the
**GitHub release body** (there is no `CHANGELOG.md` in the repo), so the deliverable is Markdown the
maintainer pastes into the release — plus the version bump.

## Steps

1. **Find the previous version.** Read `VERSION_NAME` in `gradle.properties`. That is the version
   being superseded (the "from" side of the compare link).
2. **Determine the new version** (semver): the user usually gives it. If not, infer from the changes —
   new public API or features → minor bump; only fixes/deps → patch; a breaking change with no
   compatibility shim → major.
3. **Gather what changed** since the last release. Prefer the real history over memory:
   ```bash
   git fetch origin main
   git log --oneline origin/main   # read commit subjects since the previous release commit/tag
   ```
   Group commits into the sections below. Collapse many small commits on one feature into a single,
   user-facing bullet — the reader cares about behavior, not the commit-by-commit path.
4. **Bump the version** in `gradle.properties` (`VERSION_NAME=<new>`). This is the only file to edit
   for the version; README badges are dynamic (JitPack). Commit it (e.g.
   `chore(release): released version <new>`).
5. **Write the changelog Markdown** in the exact structure below and hand it to the user so they can
   create the GitHub release. Do not create the tag/release yourself unless asked.

## Structure (use this exact skeleton)

Keep the emoji headings and order. Omit a section if it has nothing real to say — never pad. Lead
each area with the highest-impact, most user-visible change.

```markdown
# Changelog - Version X.Y.Z

## 🚀 New Features
*   **Area / entry point**: One-line summary of the capability.
    *   **Sub-point**: Notable detail worth calling out.

## 🔧 API Changes
*   **Symbol**: What changed and, if it affects callers, how to adapt (one line; link the README
    migration section if there is one). Call out clearly whether it is breaking or non-breaking.

## 🛠 Bug Fixes & Improvements
*   **Short label**: What was wrong and what now happens instead.

## 📝 Documentation
*   **README / docs**: What was added or corrected.

## ℹ️ Note
One or two sentences framing the release — the theme and who benefits.

**Full Changelog**: https://github.com/michael-winkler/EventCalendar/compare/<previous>...<new>
```

## House-style guidance

- **Audience: library users**, not contributors. Describe observable behavior and public API, not
  internal refactors. ("Multi-day events render as one continuous bar" — not "rewrote WeekRow to a
  shared column grid".) Fold the internal work into the outcome it produced.
- **Bold lead-in per bullet** (`**Label**: ...`) as in prior releases; nested `*` sub-bullets for
  details of the same feature.
- **Name the public symbols** users touch — composables, options, `Event` fields, helper functions —
  so the notes double as an upgrade reference.
- **API changes get their own section** when present. If a change is a breaking-but-shimmed one
  (e.g. a `@Deprecated` alias with `ReplaceWith`), say it's non-breaking and point to the migration.
- **Include the compare link** with the previous and new version filled in — it's the standard footer.
- **`ℹ️ Note`** is a short framing paragraph, not a list.

## Example (abridged, from a real release)

```markdown
# Changelog - Version 2.2.1

## 🚀 New Features
*   **EventCalendarWeekTime (Compose)**: Introduced a new entry point for time-based calendar views.
    *   **Time-Grid Layout**: Events are positioned vertically according to their start and end times.
    *   **Flexible Views**: Supports 1-, 3-, and 7-day layouts via the `noOfVisibleDays` option.

## 🛠 Bug Fixes & Improvements
*   **Leap Year Logic**: Fixed the `daysInYear` calculation in `shuffleEventsForCurrentYear`.

## ℹ️ Note
This update focuses on bringing detailed scheduling capabilities to the Compose library.

**Full Changelog**: https://github.com/michael-winkler/EventCalendar/compare/2.2.0...2.2.1
```
