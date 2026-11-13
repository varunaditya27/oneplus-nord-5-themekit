<div align="center">

# Engineering roadmap

Source milestones and outstanding Nord 5 acceptance work.

</div>

## P0 / Source foundation
- [x] Define design tokens and visual identity
- [x] Establish Android module structure
- [x] Implement command parser and test cases in source
- [x] Create original vector clock logic and procedural assets
- [x] Create independent interactive web study
- [x] Verify Gradle build and unit tests on GitHub Actions (emulator/physical-device QA remains)
- [ ] Test default HOME role flow on Nord 5

## P1 / Minimum delightful launcher
- [ ] Polish app catalog loading, work profiles and error states
- [x] Native monogram/icon switch and original glyphs (device QA remains)
- [ ] Search ranking and command discovery
- [ ] Favorite apps, folders, drag behavior and quick launch
- [ ] Day summary populated with real device data with explicit permissions
- [x] Source implementation of Home swipes and Back navigation (device QA remains)

## P2 / Signature motion
- [ ] Fine-tune clock vector geometry across numeral samples
- [ ] Honor both reduced motion and battery saver in every animation (reduced-motion setting implemented, battery saver pending)
- [ ] GPU shader alternative and wallpaper fallback
- [ ] Animation frame and energy profiling on physical device
- [ ] Native haptics and sound cues with user toggles

## P3 / Companion surfaces
- [ ] Widget host integration and selected original native widgets
- [ ] Settings detail screens and backup/restore
- [ ] Quick Settings tile OEM testing
- [ ] Profile switching with opt-in scheduled focus state
- [ ] Better compatibility with Android predictive back, landscape and accessibility

## P4 / Release readiness
- [ ] CI, unit and UI instrumentation tests
- [ ] Audit launcher permissions and Android package visibility
- [ ] Review font rights and settle repository/project license
- [ ] Produce signed test APK + checksums
- [ ] Test rollback to OxygenOS Launcher

### Definition of done
No item is complete solely because code exists. Build, interaction, accessibility and device checks all matter.
