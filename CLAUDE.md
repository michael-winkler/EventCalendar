# EventCalendar - Agent Guidelines (Claude)

This project contains two UI implementations of a calendar: `:compose` and `:xml`.

## 🛠 Tech Stack
- **Language**: Kotlin (Idiomatic, KMP-ready)
- **UI**: Jetpack Compose (M3) & XML Views (Classic)
- **Date/Time**: `kotlinx-datetime` (Primary), `java.util.Calendar` (Legacy XML internal)
- **Min SDK**: 23

## 📋 Build & Dev Commands
- **Build**: `./gradlew assembleDebug`
- **Test**: `./gradlew test`
- **Lint**: `./gradlew lint`
- **Sync**: Re-sync Gradle in Android Studio.

## 📐 Architecture & Rules
We use a modular, instruction-dense approach. **Read only what you need:**

- [General Architecture & KMP](file://.agents/core/architecture.md)
- [Working Approach](file://.agents/core/working-approach.md)
- [Compose Guidelines](file://.agents/modules/compose.md)
- [XML Guidelines](file://.agents/modules/xml.md)
- [Calendar & Event Logic](file://.agents/features/calendar-logic.md)

## ⚠️ Critical Constraints
1. **API 23 Compatibility**: Avoid `java.time` in UI. Use `kotlinx-datetime` or resources.
2. **Strict Module Separation**: Never mix `:compose` and `:xml` dependencies.
3. **Internal by Default**: Expose only the primary View/Composable to `:app`.
4. **Surgical Edits**: Prefer `replace_file_content` over overwriting files.

---
*Refer to `AGENTS.md` for the main instruction index.*
