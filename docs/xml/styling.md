# XML styling (`:xml`)

Attribute-driven styling for the View-based module.

## Attributes

- Declared in `res/values/attr.xml`, exposed through `<declare-styleable name="EventCalendarView">`.
- **Every attribute is prefixed `ecv_`** — this ships as a library, so unprefixed names would collide
  with the consuming app.
- Mostly `color`, `boolean` and `enum`. Hardcoded defaults must match `res/values/colors.xml`.

## Expressive UI mode

`ecv_expressive_ui` switches a distinct look:

- dividers hidden,
- "expressive" drawables with custom tinting,
- `ecv_expressive_circle` instead of `ecv_circle`.

When adding a styled element, decide and implement **both** variants — a half-supported expressive
mode is how visual inconsistencies creep in.

## Runtime tinting

Drawables are tinted programmatically via `ColorStateList` / the `setItemTint` helper (e.g.
`RippleDrawable.setItemTint(color)`) rather than duplicating drawables per color.

## Resource layout

| Path | Contents |
| --- | --- |
| `res/values/attr.xml` | source of truth for configurable properties |
| `res/drawable/` | ripples, backgrounds, shapes, icons (`ecv_*`) |
| `res/layout/` | layouts, all `ecv_*` |
| `res/values-night/colors.xml` | dark-mode counterpart — required for every color |

## Adding a style property

1. `<attr name="ecv_my_property" format="..." />` in `attr.xml` + add to the styleable.
2. Add an `internal` property on the View; read it in `init` via `withStyledAttributes`.
3. Apply it where the view is styled (e.g. `styleTextViews()`).
4. Add it to `InstanceState.StateModel` so it survives configuration changes.
5. Add light **and** night colors.

Use the `Utils` helpers for dimension conversion (`getDimensInt`) and tinting rather than ad-hoc math.
