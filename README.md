<div align="center">

<img src="assets/brand/atelier-cover.svg" alt="ATELIER: The Black System, expressed in black, ivory and one restrained brass highlight" width="100%">

# ATELIER

**THE BLACK SYSTEM**

A typography-led Android launcher and visual theme kit for the OnePlus Nord 5.

<sub>PRE-ALPHA · NATIVE ANDROID BUILD VERIFIED · DEVICE TESTING PENDING</sub>

[Visual study](web/index.html) · [Design language](docs/DESIGN_SYSTEM.md) · [Build workflow](https://github.com/varunaditya27/oneplus-nord-5-themekit/actions)

</div>

## Why

Most custom Android themes change the wallpaper and icons while leaving the interaction model untouched. ATELIER treats the home screen as a complete interface: original vector clock numerals, functional application discovery, a precise command palette, quiet motion and intentionally restrained materials.

It is not an OEM replacement. Android retains the secure lock screen, biometric interface and system notification shade.

## Principles

- **Subtract first.** If an element is neither useful nor legible, remove it.
- **Make time distinctive.** Draw clock numerals with original geometric paths instead of default fonts.
- **Give color a job.** Near-black ink and ivory do most of the work; antique gold marks deliberate states.
- **Let actions explain themselves.** Motion is short, interruptible and optional.
- **Keep developer behavior real.** Search and allowlisted commands replace decorative terminal jargon.
- **Preserve escape routes.** No root, no undocumented system hooks and no forced default-launcher takeover.

## Architecture

<p align="center"><img src="docs/diagrams/architecture.svg" alt="ATELIER input and launcher layers connect to Android public services, while the secure lock screen and system controls remain Android-owned" width="100%"></p>

Kotlin and Jetpack Compose own the launcher screens. Local preferences store appearance and Focus state; the command engine executes a small, explicit action vocabulary. A lifecycle-aware `WallpaperService` renders Silkfield, and an optional Quick Settings tile changes ATELIER's own Focus state, not Android Do Not Disturb.

## Status

**Pre-alpha, not yet a Nord 5 release.** The Android debug build and unit tests passed on [this GitHub Actions run](https://github.com/varunaditya27/oneplus-nord-5-themekit/actions/runs/43491344459). Physical-device navigation, battery performance, wallpaper lifecycle and accessibility still require verification.

The repository currently includes the native launcher source, procedural wallpapers, an original SVG icon collection and a six-screen [browser design study](web/index.html). The study demonstrates layout and interaction; it does not launch real Android applications.

For the build prerequisites and current limitations, see [Testing](docs/TESTING.md). GitHub Pages publishing is not yet enabled.

## Documentation

- [Design system](docs/DESIGN_SYSTEM.md): color, typography, spacing, icons and visual restraint.
- [Architecture](docs/ARCHITECTURE.md): module responsibilities, permissions and Android boundaries.
- [Roadmap](docs/ROADMAP.md): completed source work and unverified device milestones.
- [Motion](docs/MOTION.md): how animations respond to state and respect reduced motion.
- [Testing](docs/TESTING.md): reproducible automated checks and Nord 5 acceptance criteria.
- [Publishing](docs/PUBLISHING.md): enabling GitHub Pages for the interactive study.
- [Licensing](docs/LICENSING.md): asset provenance and unresolved project license.
- [Glossary](docs/glossary.md): plain-language definitions of Android and design terms.
- [Brand artwork](assets/brand/): original vector cover and interface studies.
- [Surface studies](assets/brand/surface-studies.svg): lock, home and control visual concepts.

## Contributing

Focused, testable improvements are welcome. Please read [CONTRIBUTING.md](CONTRIBUTING.md) and [SECURITY.md](SECURITY.md) before opening a change. For visual contributions, demonstrate the change at phone scale and explain the user-facing benefit.

## Licence

**Project licence not yet selected.** Source is public for evaluation; do not assume permission to redistribute or relicense it. Third-party font licences are separate, and proprietary fonts are not included. See [licensing notes](docs/LICENSING.md).

ATELIER is an independent project, not affiliated with OnePlus.

<div align="center">

<img src="assets/brand/atelier-cover.svg" alt="ATELIER visual identity" width="240">

<sub>ATELIER / THE BLACK SYSTEM</sub>

</div>
