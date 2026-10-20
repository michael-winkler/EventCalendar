---
name: release
description: >-
  Ship a new EventCalendar release end to end: update dependencies, lint/build/test, commit, bump
  VERSION_NAME, write the changelog, tag, push, build the unsigned sample APK and create the GitHub
  release with the APK attached. Use this whenever the user asks to "make a release", "cut a
  release", "release X.Y.Z", "ein Release machen", "Release vorbereiten" or "publish a new
  version" — even if they don't name a version. Runs every step itself without asking the user to
  do anything.
---

# Release (EventCalendar)

The maintainer wants a release to happen **without them doing anything**. Run every step below
yourself, in order, and only stop to ask if a step fails in a way you cannot fix safely (failing
tests, lint errors you cannot resolve, a tag that already points to a different commit). Invoking
this skill is the authorization to commit, tag, push to `origin/main` and publish the GitHub
release — no extra confirmation needed.

Conversation with the user is German; everything in the repo (commits, changelog) is English.

## Facts about this repo

- Published via **JitPack** from the GitHub tag; `VERSION_NAME` in `gradle.properties` is the
  artifact version (the sample app reads it as `versionName`). `jitpack.yml` limits the JitPack
  build to `:compose`/`:xml` publishing; keep it in sync if library modules are added or renamed.
- Tags are **plain version numbers** (`2.4.0`, no `v`), annotated, pointing at the
  `chore(release): released version X.Y.Z` commit.
- GitHub release: title = version, body = changelog, one asset **`app-release-unsigned.apk`**.
- The sample's `release` build type is signed with the debug key (for Baseline Profile/benchmarks),
  so the unsigned APK is built with `unsigned-release.init.gradle` from this skill's folder.
- `gh` may not be on the PATH of the Bash/PowerShell tools. Use
  `"/c/Program Files/GitHub CLI/gh.exe"` (Bash) if `gh` is not found. If it is not installed or not
  logged in (`gh auth status`), that is the only thing the user must do — ask them to run
  `! winget install --id GitHub.cli -e` and/or `! "C:\Program Files\GitHub CLI\gh.exe" auth login`,
  then continue.
- Local `.idea/` changes come from Android Studio syncs; never commit them.

## Steps

### 1. Preflight
```bash
git fetch origin --tags
git status --short          # must be clean apart from .idea/ (leave those alone)
git status -sb | head -1    # on main, not behind origin/main (pull --ff-only if behind)
grep VERSION_NAME gradle.properties   # previous version = "from" side of the compare link
```

### 2. Dependencies up to date
Compare every version in `gradle/libs.versions.toml` (and the Gradle wrapper) with the newest
**stable** release. Query Maven metadata (Google Maven `https://dl.google.com/dl/android/maven2/...`
for `androidx`/`com.android`/`com.google.android.material`, Maven Central for the rest), drop
`alpha|beta|rc|dev|snapshot` and odd variants like `-0.6.x-compat`, `sort -V | tail -1`. Gradle:
`https://services.gradle.org/versions/current`.

- Bump outdated entries in the catalog; update the wrapper with
  `./gradlew wrapper --gradle-version <v>` and run `./gradlew wrapper` once more.
- Never move to pre-releases for a release.
- A `gradlew` that shows as modified without a content diff is only line endings; it's fine to
  stage it, keep mode `100755`.

### 3. Verify
```bash
./gradlew assembleDebug test lint
```
Run it in the background (takes ~1–2 min). Then check that every lint SARIF is empty:
```bash
for f in */build/reports/lint-results-debug.sarif; do echo "$f: $(grep -c '"ruleId"' $f)"; done
```
`NewerVersionAvailable` findings mean step 2 missed something — fix and re-run. Fix real lint
issues in their own commit (`chore(lint): ...`). Do not continue with failures.

### 4. Commit pending work
Follow the **commit** skill (maintainer as author, no AI trailers, explicit `git add <paths>`).
Dependency/wrapper bumps are one `build(deps): ...` commit listing `name old → new`.

### 5. Version and changelog
Follow the **changelog** skill for the version choice and the Markdown:
- Version: the user's, otherwise semver from `git log <previous>..HEAD` (features/new API/
  deprecations → minor, only fixes/deps → patch, breaking without shim → major). Tell the user
  which version you picked.
- Set `VERSION_NAME=<new>` in `gradle.properties`, commit alone:
  `chore(release): released version <new>` (no body).
- Write the changelog to a temp file outside the repo (e.g. the scratchpad) as
  `release-notes-<new>.md`.

### 6. Tag and push
```bash
git tag -a <new> -m "<new>"      # skip if the user already created it — then verify it
git rev-parse <new>^{}           # must equal HEAD (the release commit)
git push origin main
git push origin <new>
```
If the tag exists but points elsewhere, stop and ask — never move or force-push a tag.

### 7. Unsigned APK
```bash
./gradlew :app:assembleRelease -I .claude/skills/release/unsigned-release.init.gradle
```
Check `app/build/outputs/apk/release/`: `app-release-unsigned.apk` exists, `output-metadata.json`
has `"versionName": "<new>"`, and `unzip -l` shows no `META-INF/*.RSA|SF|EC|DSA`.

### 8. GitHub release
```bash
gh release create <new> --verify-tag --title "<new>" \
  --notes-file <path>/release-notes-<new>.md \
  app/build/outputs/apk/release/app-release-unsigned.apk
```
Verify with
`curl -s https://api.github.com/repos/michael-winkler/EventCalendar/releases/tags/<new>`
(`draft: false`, `prerelease: false`, asset present).

### 9. Report (German, short)
Release link, version (and why if you chose it), dependency updates, lint/test result, commits
created. Mention that JitPack builds the version on first request
(https://jitpack.io/#michael-winkler/EventCalendar).
