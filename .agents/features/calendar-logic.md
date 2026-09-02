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
- Use `strings.xml` for day names (e.g., `R.string.ecv_monday`) and month names.
