# AGENTS.md

## Project Overview

This repository contains an Android calendar app with two implementations:

- **`compose/`** – Modern UI based on Jetpack Compose.
- **`xml/`** – Classic Android UI based on XML Views.

The project is intentionally structured to keep both UI approaches separate. Changes should be made in the appropriate module and should not mix the two architectures unnecessarily.

## Goals for Agents

- **Respect Structure**: Always respect the existing project structure and module boundaries.
- **Targeted Changes**: Prefer small, surgical changes over large refactoring.
- **Consistent Style**: Follow the coding style and naming conventions of the affected module.
- **Avoid Refactors**: Do not perform unrelated refactors while working on a task.
- **Meaningful Docs**: Add documentation or comments only when they provide real value.

## Working Approach

### Before Making Changes
- **Analyze Boundaries**: Inspect relevant files and module boundaries.
- **Identify Patterns**: Understand existing patterns in the affected area.
- **Architectural Decision**: For UI changes, determine whether they belong in `compose` or `xml`.

### While Making Changes
- **Minimize Impact**: Modify only the files that are actually affected.
- **Preserve Architecture**: Maintain the existing architecture and package structure.
- **API Stability**: Avoid changing public APIs unless absolutely necessary.
- **Compatibility**: Consider backward compatibility if library or module boundaries are involved.

### After Making Changes
- **Verify Consistency**: Check local consistency and alignment with project goals.
- **Communicate Changes**: If build, API, or architecture changes were made, mention them clearly.
- **Test Awareness**: If tests are missing, note potential impacts and manual verification steps.

## Project Structure & Modularization

- **Module Independence**: Treat `compose/` and `xml/` as independent, standalone UI libraries. They must not depend on each other.
- **Layered Architecture**: Both modules should follow consistent internal layering:
    - **UI Layer**: Visual components (Composables or Custom Views/Layouts).
    - **Logic Layer**: ViewModels and Controllers managing state and business rules.
    - **Domain Layer**: Models and data structures specific to the calendar logic.
- **Resource Encapsulation**: Keep resources (drawables, strings, layouts) strictly within the module's own `res` directory. Avoid cross-module resource references to maintain portability.
- **Encapsulated API**: Use `internal` visibility by default for all implementation details. Only expose the main entry points (e.g., the primary View or Composable) to the `:app` module.

## Best Practices

### Kotlin / Android
- **Idiomatic Kotlin**: Write clean, modern, and idiomatic Kotlin code.
- **Null Safety**: Respect and leverage Kotlin's null safety features.
- **Pure Functions**: Minimize side effects in UI-adjacent functions.
- **Clear Naming**: Keep naming clear, descriptive, and consistent.
- **Testability**: Move complex logic into testable units when possible.

### Jetpack Compose
- **State Separation**: Keep state sources clearly separated from UI components.
- **Reusability**: Prefer small, highly reusable composables.
- **Stability**: Favor recomposition-friendly structures and stable data types.
- **Decoupling**: Keep UI and logic strictly decoupled.
- **Side Effects**: Use `LaunchedEffect`, `SideEffect`, etc., deliberately and correctly.
- **Internal Previews**: Keep all `@Preview` composables `internal` to avoid polluting the public API.

### ViewModel & State Management
- **ViewModel Logic**: Move complex logic and data processing into ViewModels to keep UI components lightweight.
- **Reactive State**: Use `StateFlow` to expose UI state for consistent, lifecycle-aware data streams.
- **Configuration Changes**: Ensure ViewModels retain state and ongoing tasks (e.g., API calls) during Activity recreation.
- **Scope Management**: Launch long-running operations in `viewModelScope` to prevent interruption when the UI is destroyed.

### XML / View-Based UI
- **Encapsulation**: Encapsulate reusable components (Custom Views) appropriately.
- **Separation of Concerns**: Keep adapters, view logic, and state management clearly separated.
- **Resource Naming**: Use consistent and descriptive naming for resources and IDs.

## Build and Dependencies

- **Minimal Changes**: Modify Gradle files only when necessary.
- **Dependency Review**: Review version updates and new dependencies carefully.
- **Shared Config**: When changing shared configuration, check both modules for impact.

## Documentation

- **Relevance**: Update README files only when changes are relevant to users or developers.
- **Agent Guidance**: This file (`AGENTS.md`) serves as the source of truth for agent behavior.
- **Maintenance**: Update `AGENTS.md` whenever the project structure or best practices change significantly.

## Guidance for Future Changes

1. **Module First**: Determine which module (`compose` or `xml`) the feature belongs to.
2. **Reuse Patterns**: Reuse existing patterns and utilities within that module.
3. **Cross-Module Impact**: Consider the impact of changes on the other UI implementation.
4. **Surgical Edits**: Touch only the files required for the task.
5. **Clarity**: Keep the intent of changes clear and easy to follow.