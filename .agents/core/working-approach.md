# Agent Working Approach

## Analysis
- **Analyze Boundaries**: Inspect files and modules first.
- **Identify Patterns**: Respect existing code style (Indentation, Naming).

## Modification
- **Surgical Edits**: Use `replace_file_content` or `multi_replace_file_content`.
- **Avoid Refactors**: Don't touch unrelated code.
- **API Stability**: Keep public APIs stable.

## Verification
- **Build**: Ensure Gradle sync and build pass.
- **Consistency**: Verify UI implementation matches the target module (Compose vs XML).
