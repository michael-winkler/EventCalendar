# XML / View-Based Guidelines (:xml)

For detailed API usage and setup examples, see: [xml/README.md](file://xml/README.md)

## Component Logic
- **Primary View**: `EventCalendarView` (FrameLayout).
- **Navigation**: Uses `InfiniteAdapter` with `RecyclerView`.
- **State**: `InstanceState` handles config changes in-memory.

## Styling & Resources
- **Namespacing**: Use `ecv_` prefix for all resources/IDs.
- **Attributes**: Configurable via `attrs.xml`. Read in `init` with `withStyledAttributes`.
- **Expressive Mode**: Handle `ecv_expressive_ui` for specific UI transformations.

## Patterns
- **Adapters**: Keep adapters and ViewHolders `internal`.
- **ViewBinding**: Always use `binding` for type-safe access.
- **Hybrid Dates**: Map `kotlinx-datetime` to `java.util.Calendar` for internal View logic via `Utils`.
