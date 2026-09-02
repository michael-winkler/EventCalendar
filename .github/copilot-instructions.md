# GitHub Copilot Instructions - EventCalendar

## Project Overview
An Android calendar library with dual implementations: `:compose` and `:xml`.

## Architecture Rules
1. **Module Independence**: `:compose` and `:xml` must never depend on each other.
2. **KMP Compatibility**: Use `kotlinx-datetime`. Avoid `java.time` in UI modules to support API 23.
3. **Layering**: UI -> Logic (ViewModels) -> Domain (Models).
4. **Visibility**: Use `internal` for all implementation details.

## Coding Style
- Idiomatic Kotlin.
- Surgical edits only.
- Resource-based localization for calendar names.

## Instruction Modules
Refer to the following files for detailed guidelines:
- `.agents/core/architecture.md`
- `.agents/modules/compose.md`
- `.agents/modules/xml.md`
- `.agents/features/calendar-logic.md`
