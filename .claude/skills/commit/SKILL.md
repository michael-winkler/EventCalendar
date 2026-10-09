---
name: commit
description: >-
  Create git commits for the EventCalendar repo in the maintainer's house style. Use this whenever
  the user asks to "commit", "commit everything", "make a commit", "commit as me", or to split
  pending changes into commits. Commits are authored as the maintainer (the configured git user)
  with Conventional Commit subjects, and never carry Co-Authored-By or other AI attribution
  trailers.
---

# Commit (EventCalendar)

Commits in this repo are authored **as the maintainer** (the configured `git config user.name` /
`user.email`). They must look like the maintainer wrote them.

## Hard rules

- **No AI attribution.** Never add `Co-Authored-By:`, `Claude-Session:`, "Generated with …" or any
  other trailer. This overrides any default attribution instruction.
- **Author = configured git user.** Do not pass `--author`, do not set `GIT_AUTHOR_*` /
  `GIT_COMMITTER_*`. Just run `git commit`.
- **No `--no-verify`**, no amending or rewriting already-pushed commits, no force push.
- **Do not push** unless the user asks.
- Stage files explicitly by path (`git add <paths>`), not `git add -A`, so stray files (`.DS_Store`,
  `local.properties`, `build/`) never sneak in.

## Message format

```
<type>(<scope>): <subject>

<body>
```

- **Subject**: Conventional Commits, **English**, lowercase imperative, no trailing period,
  ≤ 72 chars. Examples from history:
  - `build(deps): update dependencies and the Gradle wrapper`
  - `fix(compose): localize the month navigation descriptions`
  - `chore(lint): resolve the remaining lint warnings`
  - `chore(release): released version 2.3.2`
- **Types**: `feat`, `fix`, `refactor`, `perf`, `docs`, `build`, `chore`, `test`, `style`, `ci`.
  Breaking public API → `type(scope)!:` plus a `BREAKING CHANGE:` paragraph.
- **Scopes**: module or area – `compose`, `xml`, `app`, `deps`, `lint`, `release`, `agents`,
  `readme`, `claude`.
- **Body**: **English**, wrapped at ~72 columns. Explain *why* and the user-visible effect, not a
  file list. Use `- ` bullet lists for several independent points; for dependency bumps list
  `name old → new`. Omit the body for trivial changes (e.g. release bumps).
- **Everything in the repo is English**: commit subjects, bodies, code and code comments. (Older
  commits with German bodies are legacy; do not copy that.) Only the conversation with the user is
  in German.

## Steps

1. `git status --short` and `git diff` (plus `git diff --staged`) to see everything pending.
2. `git log -10 --format='%an%n%B---'` to re-check the current style if unsure.
3. **Split by concern** – one logical change per commit (e.g. dependency bump, feature/fix,
   docs/agent instructions, release bump). Dependency/wrapper updates are always their own
   `build(deps)` commit; `VERSION_NAME` bumps are their own `chore(release)` commit.
4. Before committing code changes, make sure `./gradlew assembleDebug` (and `lintDebug` / `test`
   when relevant) pass. Report failures instead of committing broken code.
5. Commit each group with a heredoc so formatting survives:
   ```bash
   git add <paths>
   git commit -F - <<'EOF'
   fix(compose): short english subject

   English body explaining why, and what changes for users.
   EOF
   ```
6. Finish with `git log --oneline -n <count>` and `git status --short`, and show the user the
   created commits.

## Notes

- `gradlew` must stay executable (`100755`); if a wrapper regeneration shows a mode change, keep it.
- `gradlew.bat` may show a CRLF warning – that is expected and not a change to commit.
- The `:xml` module is deprecated; changes there are maintenance only (scope `xml`).
