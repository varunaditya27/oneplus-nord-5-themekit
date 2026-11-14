# ATELIER / The Black System

## 0.9.0 Preview 2 · Public prerelease

The original ATELIER identity is now implemented across the source repo, Android app and web experience.

- Crossbar-free original A mark and path-drawn wordmark, built without proprietary fonts.
- Android adaptive icon with near-black background, ivory/brass foreground and monochrome themed variant.
- Cinematic opening reveals the actual logo instead of a generic font character.
- Source SVGs for the lockup, favicon, avatar, GitHub cover, social banner, phone splash and brand pattern.
- A bundled brand kit and directly downloadable APK in the public GitHub prerelease.
- Includes all native launcher features described in Preview 1 below.

**Release classification:** public prerelease; debug-signed APK for hands-on evaluation, not a Play Store or production-signed package. Nord 5 hardware performance and battery life remain unverified.

## 0.9.0 Preview 1

A fully native, dark-only Android launcher and procedural live wallpaper for the OnePlus Nord 5, designed around original vector typography, matte blacks, ivory controls and rare brass accents.

### Cinematic materials

- **Silkfield**: flowing layered graphite strands and a single gold contour.
- **Strata**: geological black-on-black ridges and a lit topographic edge.
- **Eclipse**: partial orbital illumination and concentric mineral shadows.
- Wallpaper parallax, touch ripple, scene picker, and launcher overlay use procedurally rendered code instead of looping image files.
- Rendering pauses when hidden and becomes static in battery-saver, Still mode or reduced-motion mode.

### Native interactions and utility

- Expressive / Signature / Still motion presets; bounce-free press physics.
- Directional gestures with physical drag-follow and cinematic page transitions.
- The original vector clock has a controlled reveal with adjustable motion behavior.
- Battery, RAM and storage gauges use real Android readings, not placeholder metrics.
- Four icon languages: themed adaptive, functional ATELIER glyphs, typography lettermarks, original app icons.
- Long-press app editing: searchable 155-glyph selection, pin/unpin, persistent per-app override, and reset.
- Native semantic icon previews: Play/Pause, Menu/Close and Sound/Mute.
- Local preference persistence; no cloud account, telemetry collection or external asset service.
- Optional, skippable opening-title animation with reduced-motion handling.
- Portable theme export/import, including your scenes, motion choices, pinned apps and icon assignments.
- Emulator smoke coverage for Home, Library and Settings with explicit dialog detection and screenshot contrast checks.

### Important limitations

This preview is a **launcher and live wallpaper**, not a rooted system theme. It cannot replace the OxygenOS secure lock screen, biometric animations, Always-On Display or the native Quick Settings/notification shade.

The APK is **debug signed**, for testing. Signing identity is not guaranteed across runs; a future production build needs stable private signing credentials. Device-based compatibility, animation performance and battery endurance have **not yet been certified**.

### Install and recover

Download the `atelier-debug-apk` artifact from a successful Android workflow run or the installable APK attached to a conversation. Install it, open the app, and request the default HOME role. Keep the stock OnePlus launcher installed.

To revert, set **Settings → Apps → Default apps → Home app** back to the stock launcher. See [the complete setup guide](https://github.com/varunaditya27/oneplus-nord-5-themekit/blob/main/docs/NORD5_SETUP.md).

No root or special permissions are required to launch ATELIER. The system separately approves setting it as the HOME app or selecting its wallpaper.
