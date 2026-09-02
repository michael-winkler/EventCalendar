# Event Management

## Data Models
- **`Event`**: Plain data class (KMP-ready).
- **`EventTimeRange`**: Defines start/end times.
- **Visibility**: `internal` where possible.

## Logic Layer
- **ViewModels**: Process and group events. Use `viewModelScope`.
- **Stores**: `CalendarEventsStore` (Compose) or list properties (XML).
- **Update Flow**: Unidirectional updates. Setter triggers re-render.
