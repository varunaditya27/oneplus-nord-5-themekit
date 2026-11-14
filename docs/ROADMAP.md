# ATELIER / Engineering roadmap

[Download the current preview](https://github.com/varunaditya27/oneplus-nord-5-themekit/releases/tag/v0.9.0-preview.2) · [Testing protocol](TESTING.md) · [Setup guide](NORD5_SETUP.md)

This document separates **implemented**, **automatically verified**, and **physically verified** work. A source feature is not automatically a Nord 5-certified feature.

## Shipped in the 0.9 preview

- [x] Original brand identity, typography system, icon source and generator
- [x] Native Kotlin/Compose HOME application, app discovery, search and allowlisted commands
- [x] Four app-icon strategies and a searchable per-app 155-glyph editor
- [x] Favorite pinning, local preferences, JSON export/import
- [x] Original vector clock and three procedural wallpaper scenes
- [x] Native kinetic interactions, gesture feedback and Still/Signature/Expressive modes
- [x] Real battery, RAM and storage instruments
- [x] Skippable opening logo reveal and locally controlled haptics
- [x] Public GitHub release with a test APK, brand assets and checksums
- [x] Android build/unit tests, app startup and Home/Library/Settings emulator smoke tests

## P0 / Physical Nord 5 acceptance

- [ ] Default HOME role assignment and safe return to stock OxygenOS launcher
- [ ] Correct app launching, work/personal profiles and empty-catalog handling
- [ ] Gestures and 3-button navigation, screen insets, rotation and cutouts
- [ ] OLED contrast, typography, scaling and TalkBack interaction
- [ ] Long-session wallpaper FPS, thermal behavior and battery impact
- [ ] Still mode, battery saver and reduced-motion correctness under real OEM behavior
- [ ] Quick Settings Focus tile and wallpaper picker compatibility

## P1 / Functional depth

- [ ] App folders, richer favorite organization and drag/drop customization
- [ ] Stronger app search ranking, aliases and discovery
- [ ] Widget hosting, with permission-aware user controls
- [ ] Durable settings migration and optional backup diff/preview
- [ ] Better landscape, predictive Back and large-screen behavior

## P2 / Release engineering

- [ ] Add full instrumented UI coverage, screenshot baselines and performance benchmarks
- [ ] Test update paths across build variants and preserve stable application signing
- [ ] Review package visibility, crash handling and backup threat surface
- [ ] Choose a project license with the owner's approval
- [ ] Device-tested, production-signed release candidate

### Definition of done

A feature moves from *implemented* to *verified* only with suitable tests; from *verified* to *device-ready* only after hardware acceptance. The secure lock screen, system notification shade, native AOD and biometric animations remain outside the scope of an unprivileged Android launcher.
