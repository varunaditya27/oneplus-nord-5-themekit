# ATELIER Iconography

**155 original semantic glyphs · 7 functional categories · 5 SVG presentations · 3 Android optical weights**

The canonical geometry is `catalog.json`. All derivative exports are generated with `build.mjs`. Do not edit the generated assets or `AtelierGlyph.kt` manually.

## Visual inspection

- [Comparative overview](preview/overview.svg)
- [Navigation](preview/navigation.svg)
- [Communication](preview/communication.svg)
- [Workspace](preview/workspace.svg)
- [System](preview/system.svg)
- [Developer](preview/developer.svg)
- [Media](preview/media.svg)
- [Status](preview/status.svg)
- [Interactive icon archive](../web/icons/index.html)

## Asset variants

| Family | Drawn at | Purpose |
|:--|:--|:--|
| Contour | 24dp | General interface, 1.65px outline |
| Core | 16–20dp | Legible small controls, 2.1px outline |
| Etch | 32dp and above | Cinematic/editorial, 1.25px outline |
| Inlay | 64-unit tile | Matte app launcher tile with neutral outline |
| Inlay Active | 64-unit tile | Same tile with one restrained brass selection bar |

For Android, the compiler also produces three weights as native VectorDrawable resources and an `AtelierGlyph.kt` enum/resource registry.

**All families share the same geometry** rather than reinventing the meaning of a symbol at different weights. This avoids accidental semantic shifts.

## Generate

```sh
node iconography/build.mjs
node iconography/check.mjs
```

No network dependencies.

## Optical preview and export packs

The [Iconography / quality and export](../.github/workflows/icon-pack.yml) GitHub Action produces:

- `ATELIER-Iconography-001`: complete SVG ZIP for designers and developers
- `ATELIER-Optical-Review-001`: PNG contact sheets and machine-readable coverage report

Both are downloadable from the successful workflow run's **Artifacts** section.

The raster audit checks that each icon remains visible and the Core weight is not unexpectedly lighter than Contour. This is automated *screening*, not a substitute for human review at a real phone's optical sizes.

## Human QA criteria

Reject icon work that replaces established metaphors with vanity shapes, lacks a meaningful interaction, crowds small sizes, or counterfeits a third-party logo. Preserve consistent silhouette, line caps, corner style and padding.

Review details in [ICON_SYSTEM.md](../docs/ICON_SYSTEM.md) and [ICON_AUDIT.md](../docs/ICON_AUDIT.md).
