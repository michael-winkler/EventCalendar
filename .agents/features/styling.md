# Styling System

## XML Styling (:xml)
- **Attributes**: Defined in `attrs.xml`, prefixed with `ecv_`.
- **Defaults**: Match `res/values/colors.xml`.
- **Expressive Mode**: Handle `ecv_expressive_ui` flag for rounded shapes and hidden dividers.
- **Tinting**: Use `Utils.setItemTint` or `ColorStateList` for programmatic updates.

## Compose Styling (:compose)
- **Themes**: Use Material 3 with internal `CalendarStyle` configurations.
- **Components**: Styled via parameters, not XML attributes.
- **Dark Mode**: Support via `isSystemInDarkTheme()`.

## Shared Principles
- **Localization**: Use string resources for any text in styling (e.g., content descriptions).
- **Consistency**: Maintain visual parity between Compose and XML implementations where possible.
