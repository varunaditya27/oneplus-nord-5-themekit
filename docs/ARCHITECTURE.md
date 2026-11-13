# Technical architecture

## Goal
A user-installable Android HOME application with genuinely custom Compose UI, real app launching, a pure command parser, a local settings store, one optional Quick Settings tile and one lifecycle-aware procedural live wallpaper.

## Module contracts
- `LauncherActivity` requests HOME role only after an explicit user action.
- `AppCatalog` uses `LauncherApps` / launcher activities, not blanket QUERY_ALL_PACKAGES.
- `AtelierPreferences` stores only local visual and focus preferences.
- `CommandEngine` is a pure parser; never executes arbitrary shell commands or requires terminal privileges.
- `AtelierShell` orchestrates presentation and navigation; no dangerous Android permissions needed for normal app navigation.
- `SignatureClock` draws original vector geometry for 0–9, no licensed display font required.
- `SilkfieldWallpaperService` renders on the Surface lifecycle; always unschedule on invisibility and destruction.
- `FocusTileService` toggles ATELIER's local focus state; it **does not control system Do Not Disturb**.

## Platform boundary
A launcher cannot replace OxygenOS's secure lock screen, biometric animation, system notification shade, or overall system font. Altering those requires OEM support or high-risk hooks outside the project's safe scope.

## Privacy
No accounts, telemetry, network sync, contacts, location, accessibility surveillance, or notification reading in the initial scope. Installed-app enumeration is for displaying and launching apps. Future integrations require explicit opt-in and threat review.

## Engineering quality gates
1. Android Studio dependency resolution and a clean Gradle build.
2. Emulator smoke test: HOME role, app discovery, app launching, screen rotations, empty catalog and locale handling.
3. Nord 5 smoke test: gestures, OEM launcher recovery, correct battery percentage and display cutouts.
4. Wallpaper battery profile: stopped frames on invisibility; measure over a sustained session.
5. TalkBack, dynamic font scaling, reduced motion, color contrast and large touch targets.
6. CI builds and signed APK release process before calling any milestone usable.

## Potential risks
- OEM gesture navigation can look different with third-party launchers.
- Query visibility and profile/work-profile discovery depend on Android APIs and user/profile restrictions.
- Custom Quick Settings tile is opt-in, not a theme of the full panel.
- CPU Canvas wallpaper is acceptable for an early prototype but should be profiled before claiming battery-efficient GPU rendering.
- Dependencies and Gradle wrapper need a verified environment and pinned versions.
