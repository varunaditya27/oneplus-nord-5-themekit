<div align="center">

# Nord 5 setup guide

Install and evaluate ATELIER on OxygenOS 16 without root or system modifications.

</div>

## Before installing

ATELIER is **pre-alpha**. Its native source compiles in CI but has not been verified on a physical Nord 5. Test with the stock OxygenOS launcher still installed and available.

Target hardware: OnePlus Nord 5, CPH2707, OxygenOS 16. This is a target, not a device certification.

## 1. Get the debug build

Open [Android / pre-alpha verification](../.github/workflows/android.yml) or the repository's **Actions** tab. Select a successful Android workflow run and locate the **atelier-debug-apk** artifact. GitHub may require sign-in to download workflow artifacts.

The APK is a **debug build** intended for testing, not a production release. Verify the run belongs to this repository before installing it. If using Android Studio, you can open the project and build `assembleDebug`.

## 2. Open ATELIER and request the Home role

Open ATELIER like any other installed app. Its Settings screen offers **REQUEST DEFAULT HOME ROLE**. Android should show the system dialog to approve the HOME role. Alternatively use **Settings → Apps → Default apps → Home app** if your OxygenOS build exposes that route.

Choosing ATELIER as HOME is reversible. It does not uninstall the OnePlus launcher and does not change the secure lock screen.

## 3. Explore the surfaces

- **Home:** Signature vector clock, live workspace label, up to four shortcuts, and command field.
- **Swipe up on Home:** App library. The bottom navigation is an alternative.
- **Swipe down on Home:** ATELIER Controls, not the native notification shade.
- **Library:** Search installed launchable applications. Long-press an app icon to pin or unpin it from Home.
- **Command:** Search an app by name or run an allowlisted ATELIER command.
- **Settings:** Material finish, compact clock, app labels, icon style, reduced motion, wallpaper picker and HOME role.

Android Back returns to Home from ATELIER's secondary screens. Android still handles gestures and system navigation outside the launcher.

## 4. Choose one dark finish

| Finish | Background | Character |
|--|--|--|
| Ink | `#080809` | Strict near-black |
| Graphite | `#111315` | Cool mineral black |
| Sable | `#100F0E` | Warm, smoky black |

No light mode is supplied. The ivory color represents readable text.

App icons default to **THEMED**, which uses the installed app's own monochrome adaptive icon when available, otherwise an ATELIER functional glyph or a lettermark. Choose **GLYPHS**, **LETTERS**, or **ORIGINAL** in Settings to adjust the style. The custom glyph modes also offer Contour and Core optical weights; no unofficial brand logos are drawn.

## 5. Install or generate wallpapers

Open ATELIER Settings and select **CHOOSE SILKFIELD WALLPAPER**. Android's live wallpaper picker should present Silkfield as a choice. Android controls whether the wallpaper is applied to Home, Lock or both.

For static wallpapers, open the [Wallpapers / generate and export workflow](../.github/workflows/wallpapers.yml) from Actions and download its `atelier-wallpaper-masters` artifact. The same assets can be produced locally:

```sh
python3 -m pip install -r scripts/requirements.txt
python3 scripts/generate_assets.py
```

The generated files live under `assets/wallpapers/`. They are WebP images sized 1080 × 2400.

The Home surface requests a translucent window so the selected wallpaper can appear behind the interface. Actual layering and power usage depend on OxygenOS and require phone testing.

## 6. Understand the commands

| Input | Behavior |
|--|--|
| `/apps` | Open app library |
| `/settings` | Open ATELIER settings |
| `/focus on` | Reduce nonessential Home context |
| `/focus off` | Return to normal context |
| `/clock` | Switch between vector and compact clock |
| `/wallpaper` | Open Android's wallpaper picker |
| An app name, for example `Camera` | Open a matching installed launcher activity |

The command palette is **not a system shell** and never executes arbitrary terminal commands.

## 7. Align the remaining OxygenOS surfaces

OxygenOS still owns the secure lock screen, fingerprint animation, Always-On Display (AOD), system status bar and notification/Quick Settings shade. You may coordinate their built-in colors, clock style, lock wallpaper and fingerprint settings manually, but ATELIER cannot reliably replace them through supported public Android APIs.

Avoid unreviewed root or SystemUI modifications solely for theme consistency.

## 8. Recover the original launcher

If ATELIER is unstable, return to Android's **Default apps → Home app** settings and select the OnePlus/OxygenOS launcher. Do not delete the stock launcher.

If system navigation or lock-screen controls look wrong, disable ATELIER's live wallpaper separately before troubleshooting the launcher. Recovery and dual-profile behavior still need a device test.

## Unverified on the Nord 5

The following remain open: work-profile app discovery, fast gesture navigation, wallpaper visibility behind the translucent window, battery use over long sessions, app launch error behavior, large fonts/TalkBack, and landscape cutout handling.

Source and automated build verification are not substitutes for these checks.
