# Contributing to ATELIER

ATELIER is an original Android launcher with a deliberate, dark-only visual grammar. Contributions should improve a real interaction, not simply add more decoration.

> **Status:** public v0.9.0 preview. The project does not yet have a project-wide redistribution license. Do not assume that a public repository grants permission to redistribute code or artwork.

## Before opening a pull request

- Open or reference an issue describing the **problem**, the affected surface and why a change would help.
- Keep designs in the near-black/ivory/brass language. Avoid neon, generic icon packs and unrelated Android UI re-skins.
- Preserve Android platform boundaries. No root requirement, secure-lock-screen override, privileged SystemUI hooks or blanket Accessibility Service permissions.
- Contribute only code and assets you have the necessary rights to submit. Bundled fonts are governed by their individual licenses.

## Local development

You'll need JDK 17, Android SDK 36 and Gradle 9.7.0 (the CI workflow uses this version).

```sh
gradle --no-daemon testDebugUnitTest assembleDebug
node --check web/app.js
node --check web/motion/studio.js
node web/test-smoke.mjs
python3 scripts/brand/verify.py
```

The debug APK is generated under `app/build/outputs/apk/debug/`. It is not a production-signed artifact.

Relevant directories: `app/` (Kotlin/Compose + wallpaper), `iconography/` (canonical glyph geometry), `assets/brand/` (original logo art), `web/` (interactive studies), `docs/` (design and verification).

## Review checklist

1. **Explain behavior:** what changed for the person using the launcher?
2. **Prove correctness:** tests for commands, persistence, icon mapping or backup format changes; run relevant source checks.
3. **Show visual evidence:** add a device-scale screenshot or screen recording when visual output changes. Label emulator and concept art accurately.
4. **Protect accessibility:** minimum practical touch targets, clear semantics, Still/reduced-motion behavior and usable text scaling.
5. **Consider battery:** no orphan animation loops, unnecessary background services, forced maximum frame rates or gratuitous sensor polling.
6. **Avoid incompatible APIs:** test the real launcher fallback and respect Android's permissions and secure surface boundaries.
7. **Respect versioning:** do not change or republish an existing release tag as part of ordinary contribution work.

Use the pull request template to document evidence and any limitations. CI is a useful gate, not proof of Nord 5 hardware compatibility.

## Where to start

See the [engineering roadmap](docs/ROADMAP.md), [test plan](docs/TESTING.md), [brand identity](docs/BRAND_IDENTITY.md) and [architecture](docs/ARCHITECTURE.md). Small, reversible changes are easier to validate than broad rebrands.
