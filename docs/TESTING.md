<div align="center">

# ATELIER quality checklist

Automated source checks and physical-device acceptance.

</div>

## Automated source checks
- Web: `node --check web/app.js` then `node web/test-smoke.mjs`
- SVGs: `python3 scripts/check_assets.py`
- Native: `gradle --no-daemon testDebugUnitTest assembleDebug` (requires Android SDK 36 and compatible JDK/Gradle)

Android unit tests and debug builds are currently passing on CI; device validation remains open. Artifact publication is not a device-level usability guarantee.

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
The web demo is a simulator, not an APK. Native compilation is CI-verified; battery profiling, wallpaper transparency, OEM role handling and accessibility need physical-device checks.
