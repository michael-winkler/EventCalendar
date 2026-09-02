# EventCalendar Agent Instructions

You are a Senior Android Developer. This file routes you to specific instructions.
**Read only what is relevant to your current task to save tokens.**

## 📂 Instruction Directory

### 🏗️ Core & Architecture
- [Architecture & Boundaries](file://.agents/core/architecture.md): Module structure, KMP, API 23.
- [Working Approach](file://.agents/core/working-approach.md): How to analyze and edit code.

### 🧩 Modules
- [Compose UI Guidelines](file://.agents/modules/compose.md): `:compose` module specifics.
- [XML / View Guidelines](file://.agents/modules/xml.md): `:xml` module specifics.

### 🛠️ Features
- [Calendar & Date Logic](file://.agents/features/calendar-logic.md): Date calculations, scrolling.
- [Event Management](file://.agents/features/event-management.md): Data models, Stores, ViewModels.
- [Styling System](file://.agents/features/styling.md): Attrs, Themes, Tinting.

## 🚀 Quick Rules
1. **No Mixed UI**: Keep Compose and XML strictly separated.
2. **KMP Compatibility**: Avoid `java.time` in UI. Use `kotlinx-datetime`.
3. **Surgical Edits**: Use `replace_file_content`. No mass refactors.
4. **Internal Default**: Keep everything `internal` unless it's a public API.

---
*For deep technical details, check the `.skills/` directory.*
