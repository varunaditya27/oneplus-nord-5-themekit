# ATELIER / Technical architecture

ATELIER is a user-installable **Android HOME application**, an independent `WallpaperService`, and a static browser-based design laboratory. Each owns a specific part of the experience.

## System boundaries

| Owner | Responsibilities |
| :-- | :-- |
| **Android / OxygenOS** | Lock screen, biometric animations, AOD, system shade, secure settings, permission dialogs |
| **ATELIER launcher** | Home, app discovery, local commands, app icon selection, Focus interface, material/motion preferences |
| **ATELIER wallpaper** | Three procedural scenes, wallpaper offsets and touch ripples, surface lifecycle and power-aware redrawing |
| **Web studio** | Interactive visual references, symbol archive and motion demonstrations; no Android system control |

## Native components

- `LauncherActivity`: app entry point, explicit Home role request, Android wallpaper picker and theme import/export document selection.
- `AppCatalog`: launchable activity discovery. No blanket `QUERY_ALL_PACKAGES`.
- `AtelierPreferences`: local material, icon, motion, Focus and wallpaper settings.
- `AtelierThemeBackup`: versioned JSON serialization, size limits and local restore.
- `CommandEngine`: deterministic command parsing. Does not run arbitrary shell commands.
- `AtelierShell`: Compose Home, library, controls, command surface and settings.
- `AtelierSceneRenderer` and `SilkfieldWallpaperService`: Silkfield, Strata and Eclipse; stop rendering when invisible and honor reduced motion/power saver.
- `SignatureClock`: original vector path numerals.
- `AtelierGlyph` and icon resolver: source-controlled semantic icon language with per-app overrides.
- `FocusTileService`: optional Android Quick Settings tile that toggles **local ATELIER Focus**, not Android Do Not Disturb.

## Design data and delivery

`assets/brand/` holds editable logo SVGs. `iconography/` contains symbol paths and exports. `motion/tokens.json` defines motion tokens for documentation and web studies. The web build publishes standalone browser demonstrations on GitHub Pages.

Android CI builds and runs unit tests; the emulator smoke job checks launch and Home/Library/Settings rendering. The versioned public GitHub prerelease includes a **debug-signed APK** and brand-asset ZIP.

## Privacy model

No accounts, cloud synchronization, ad analytics, contacts, location surveillance or notification-content reading are built into the preview. Native battery, RAM and storage gauges read **local device status**, not usage analytics sent to a server. Launcher app discovery is used only to show and open installed apps. Exported themes are user-selected local files.

## Verification gaps

Android compilation and emulator smoke tests are complete for the preview. Physical Nord 5 verification remains necessary for default Home handling, work profiles, gesture conflicts, OLED contrast, long-session wallpaper power draw, TalkBack, text scaling and recoverability. See [Testing](TESTING.md).

Avoid unsupported platform modifications, unreviewed Magisk modules, undocumented privileged APIs and security-compromising shortcuts in the name of visual consistency.
