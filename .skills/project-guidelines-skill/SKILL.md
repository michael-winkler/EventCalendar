---
name: project-guidelines-skill
description: Core architectural principles and agentic workflows for the EventCalendar project. Use this for general project-wide tasks.
metadata:
  author: Michael Winkler
  version: "1.0"
---

# EventCalendar Project Guidelines

This skill defines the overarching development philosophy and operational standards for the
EventCalendar repository.

## Project Vision

A hybrid Android application demonstrating both modern (Compose) and classic (XML) UI
implementations in a modular, clean architecture.

## General Expertise & Rules

### 1. Modular Boundaries

- **:app**: The entry point. It coordinates between the UI modules.
- **:compose**: Standalone Compose library.
- **:xml**: Standalone XML/View library.
- **Rule**: `:compose` and `:xml` must never depend on each other. Common logic should be in a
  shared module (if it exists) or replicated carefully.

### 2. Surgical Code Edits

- Always prefer `replace_file_content` or `multi_replace_file_content` over overwriting entire files
  unless the change is massive.
- Do not refactor code that is unrelated to the current task.
- Follow existing indentation and naming conventions.

### 3. Dependency Management

- All versions should be managed in `gradle/libs.versions.toml`.
- Before adding a new dependency, check if an existing one can fulfill the requirement.

### 4. Agentic Workflow (Step-by-Step)

1. **Analysis**: Read `AGENTS.md` and identifying which module is affected.
2. **Context**: Use `find_declaration` and `find_usages` to understand the impact of a change.
3. **Execution**: Apply changes using surgical edits.
4. **Verification**: If possible, suggest a manual verification plan or check for existing tests.

## Maintenance

- This skill should be updated whenever `AGENTS.md` or the project structure changes significantly.
- Author: Michael Winkler.
