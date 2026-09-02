# Jetpack Compose Guidelines (:compose)

For detailed API usage and setup examples, see: [compose/README.md](file://compose/README.md)

## State & Flow
- **Reactive State**: Use `StateFlow` and `.collectAsStateWithLifecycle()`.
- **Statelessness**: Favor stateless composables; lift state to ViewModels or Controllers.
- **Stability**: Use stable data types to minimize recompositions.

## Patterns
- **Previews**: All `@Preview` composables **must** be `internal`.
- **Side Effects**: Use `LaunchedEffect` or `SideEffect` strictly for external state sync.
- **Decoupling**: Keep UI and business logic strictly separated.

## API Usage
- Use `CalendarController` for navigation.
- Use `CalendarEventsStore.eventsFlow` for event data; resolve a day via `event.occursOn(date)`.
- Use `internal` `toStringRes()` extensions for date localization.
- Month grid: `WeekRow` renders events as stacked lanes on a shared column grid; overflow collapses into a per-day "+N".
