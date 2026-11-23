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

This entry only makes `/release` available in Claude Code. The skill itself lives in
[.agents/skills/release/SKILL.md](../../../.agents/skills/release/SKILL.md) - read that file and
follow it step by step. Do not copy its content here.