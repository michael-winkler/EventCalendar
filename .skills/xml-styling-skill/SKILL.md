---
name: xml-styling-skill
description: Guidelines for XML styling, custom attributes (attrs.xml), drawables, and programmatic UI updates in the :xml module.
metadata:
  author: Michael Winkler
  version: "1.0"
---

# XML Styling Skill (EventCalendar)

This skill covers the extensive styling system used in the legacy XML module, including attributes,
theme colors, and custom drawables.

## Styling Principles

### 1. Attribute-Driven Design

The module is highly customizable via `attrs.xml`.

- **Naming**: All attributes start with `ecv_`.
- **Types**: Primarily `color`, `boolean`, and `enum`.
- **Defaulting**: Hardcoded defaults should match the values in `res/values/colors.xml`.

### 2. Expressive UI Mode

A specialized "expressive" mode (`ecv_expressive_ui`) changes:

- **Dividers**: Hidden in expressive mode.
- **Backgrounds**: Uses specific "expressive" drawables with custom tinting.
- **Shapes**: Often uses `ecv_expressive_circle` instead of standard `ecv_circle`.

### 3. Programmatic Tinting

Many drawables are tinted at runtime using `ColorStateList` or `setItemTint` helper.

- **Example**: `RippleDrawable.setItemTint(color)` for expressive ripples.

## Resource Directory Structure

- `res/values/attr.xml`: Source of truth for all configurable properties.
- `res/drawable/`: Contains complex XML drawables for ripples, backgrounds, and icons.
- `res/layout/`: Prefixed with `ecv_` (e.g., `ecv_event_calendar.xml`).

## Workflow: Adding a New Style Property

1. **Define**: Add `<attr name="ecv_my_new_property" format="..." />` to `attr.xml`.
2. **Declare**: Include it in `<declare-styleable name="EventCalendarView">`.
3. **Implement**:
    - Add internal property to the View class.
    - Read in `init` block using `withStyledAttributes`.
    - Apply property (e.g., `textView.setTextColor(myNewProperty)`).
4. **State**: Update `InstanceState.StateModel` if the property needs to survive configuration
   changes.

## Best Practices

- **Night Mode**: Ensure colors are defined in both `values/colors.xml` and
  `values-night/colors.xml`.
- **Consistency**: Use the `Utils` helpers for dimension conversion (`getDimensInt`) and tinting.
