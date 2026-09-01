# AGENTS.md — `:xml`

Standalone XML / View-System calendar library. Root rules in `/AGENTS.md` still apply.

## Entry points

- `EventCalendarView` — full month calendar, `RecyclerView` + `InfiniteAdapter`, infinite scroll.
- `EventCalendarSingleWeekView` — lightweight current-week view.
- `VerticalMaterialTextView` — vertical text (week numbers / labels).

## Conventions that bite if ignored

- **`ecv_` prefix on everything** in `res/` — attributes, layouts, drawables, ids. This module ships
  as a library, so unprefixed resources would collide with the consuming app.
- **Hybrid date handling:** `java.util.Calendar` for legacy View logic is expected here; models in
  `model/` stay plain `kotlinx-datetime` data classes (no `Parcelable`) so they mirror `:compose`.
- **`InstanceState` must be updated** whenever you add a configurable property, or it is lost on
  configuration change.
- **ViewBinding only** — no `findViewById`.
- **Night mode:** every color needs an entry in both `values/colors.xml` and
  `values-night/colors.xml`.
- **`internal` by default** for adapters, view holders and helpers.

## Typical flows

- **Event list changes:** setting the `events` property triggers `updateRecyclerView()`.
- **Programmatic navigation:** `scrollTo()` / `scrollToCurrentMonth()`; the navigable range is
  `sMonth/sYear` … `eMonth/eYear`.
- **Reading attributes:** in `init`, via
  `context.withStyledAttributes(attrs, R.styleable.<Component>) { ... }`.

## Before you finish

```bash
./gradlew :xml:compileDebugKotlin
```

## Feature details (read only what you touch)

- `docs/xml/components.md` — the view classes, their methods and render paths
- `docs/xml/styling.md` — `attrs.xml`, drawables, tinting, expressive UI mode
