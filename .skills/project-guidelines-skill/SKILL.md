---
name: project-guidelines-skill
description: Core architectural principles and agentic workflows for the EventCalendar project. Optimized for KMP readiness.
metadata:
  author: Michael Winkler
  version: "1.2"
---

# EventCalendar Project Guidelines

This skill defines the overarching development philosophy and operational standards for the
EventCalendar repository. It is optimized for Kotlin Multiplatform (KMP) readiness.

## Project Vision

A hybrid application demonstrating both modern (Compose) and classic (XML) UI implementations. The
project is designed to be KMP-ready by avoiding platform-specific date/time APIs and serialization.

## General Expertise & Rules

### 1. KMP Compatibility

- **Date/Time**: Always use `kotlinx-datetime`. Avoid `java.time` or `java.util.Calendar` for new
  logic.
- **Serialization**: Use `kotlinx-serialization` for all data models that need to be serializable (
  e.g., `Event`, `EventTimeRange`). Use custom serializers for platform-specific types (like
  `Color`).
- **JVM Dependencies**: Minimize dependencies on the JVM standard library (e.g., `java.util.*`,
  `java.time.*`).

### 2. Modular Boundaries

- **:app**: The entry point. Coordinates between UI modules.
- **:compose**: Standalone UI library using Jetpack Compose (targeting Compose Multiplatform
  readiness).
- **:xml**: Standalone XML/View library.
- **Rule**: `:compose` and `:xml` must never depend on each other.

### 3. Surgical Code Edits

- Always prefer `replace_file_content` or `multi_replace_file_content` over overwriting entire files
  unless the change is massive.
- Follow existing indentation and naming conventions.

## Maintenance

- This skill should be updated whenever `AGENTS.md` or the project structure changes significantly.
- Author: Michael Winkler.
