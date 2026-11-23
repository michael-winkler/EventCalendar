# Calendar & Date Logic

## Date API
- **Primary**: `kotlinx-datetime`.
- **Range**: Navigation defined by `sMonth/sYear` to `eMonth/eYear`.
- **Model**: Use `YearMonth` for month-level operations.

## Infinite Scrolling
- **Compose**: `HorizontalPager` or custom scrolling state.
- **XML**: `InfiniteAdapter` with `RecyclerView` snapping.

## Localization
- Don't use `java.time` formatters.
- Use `strings.xml` for day and month names. `:compose` reads them only through the accessors in
  `util/AndroidResources.kt` (`dayOfWeekShortName()`, `monthDisplayName()`); `:xml` uses `R` directly.