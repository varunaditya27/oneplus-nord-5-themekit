<div align="center">

# ATELIER quality checklist

Automated source checks and physical-device acceptance.

</div>

## Automated source checks
- Web: `node --check web/app.js` then `node web/test-smoke.mjs`
- SVGs: `python3 scripts/check_assets.py`
- Native: `gradle --no-daemon testDebugUnitTest assembleDebug` (requires Android SDK 36 and compatible JDK/Gradle)

Android unit tests and debug builds currently pass in CI. A separate API 35 emulator smoke test verifies launch, Home/Library/Settings accessibility text, no blocking dialogs, live process, distinct screenshots and near-black background. This does not certify Nord 5 hardware.

## Nord 5 acceptance passes

Device checks are not yet completed. Use [the setup guide](NORD5_SETUP.md) for reversible installation.
- Switch ATELIER to HOME using the Android role prompt; switch safely back to OxygenOS Launcher.
- Discover and open launchable apps (including multiple activities and optional work profiles).
- Verify HOME and BACK navigation, 3-button and gesture system navigation, screen rotation and narrow displays.
- Check TalkBack descriptions, 200% text scaling, contrast, touch targets and reduced-motion behavior.
- Inspect screenshot consistency across wallpapers, home, library, command, control and settings.
- Leave Silkfield active for several hours, measure battery consumption and confirm rendering pauses while hidden.
- Confirm ATELIER Focus tile changes only local launcher state, not Android Do Not Disturb.
- Confirm no unexpected network access, accessibility permission prompts, notification capture, or privileged settings modifications.

## Known restrictions
The web demo is a simulator, not an APK. Native compilation is CI-verified; battery profiling, OEM role handling, OxygenOS live wallpaper visibility, and accessibility need physical-device checks.

## Cinematic preview acceptance list

- [x] GitHub Actions: unit tests pass and debug APK compiles
- [x] API 35 emulator: activity starts, no AndroidRuntime fatal errors, Home/Library/Settings accessibility checks and dark screenshot tests pass
- [x] Automated theme-backup JSON round-trip and invalid-input tests
- [ ] Physical Nord 5: verify default HOME role recovery on OxygenOS 16
- [ ] Physical Nord 5: confirm Silkfield / Strata / Eclipse render correctly as live wallpaper
- [ ] Physical Nord 5: verify touch, parallax, animation cadence and memory usage
- [ ] Physical Nord 5: compare Still, Signature and Expressive under 60Hz / high-refresh configurations
- [ ] Physical Nord 5: check battery drain overnight and thermals during long sessions
- [ ] Physical Nord 5: test TalkBack, large text, font scaling and gesture conflicts
- [ ] Physical Nord 5: validate per-app overrides across work/personal profiles
- [ ] Physical Nord 5: confirm installation/uninstallation and stock launcher fallback

A successful CI build is not the same as physical-device certification.
