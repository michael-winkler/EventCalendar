# Architecture & Boundaries

## Module Structure
- **`:app`**: Coordinator. No business logic.
- **`:compose`**: Independent UI library (Compose). KMP-ready.
- **`:xml`**: Independent UI library (Views). Legacy/Classic.
- **Constraint**: `:compose` and `:xml` **must not** depend on each other.

## Layering (Both Modules)
1. **UI Layer**: visual components (Stateless favor).
2. **Logic Layer**: ViewModels & Controllers.
3. **Domain Layer**: Models (plain data classes).

## Compatibility & KMP
- **Min SDK**: Android API 23.
- **Date/Time**: Use `kotlinx-datetime`. **Avoid** `java.time` in UI layer to support API 23.
- **Localization**: Use resource-based localization for day/month names.
- **Serialization**: Use `kotlinx-serialization`. No `Parcelable` in common models.

## Visibility
- **Default**: `internal` for all implementation details.
- **Public**: Only main entry points (e.g., `EventCalendarView`, `EventCalendarCompose`).
