# ATELIER / Icon system specification

## Identity
The icons should feel cut into a matte industrial instrument, not floating in a sci-fi interface. An unfamiliar glyph must earn its existence through clarity.

## Geometry
- Nominal viewport: 24×24 units.
- Primary silhouette region: x=3..21, y=3..21, with optical overhang of up to 1 unit for circles/arrows.
- Baseline accents and interior divisions must be optically aligned, not automatically snapped to the same lattice for every semantic.
- Contour: stroke 1.65, round caps/joins.
- Core: stroke 2.10, round caps/joins, intended at 16–20dp.
- Etch: stroke 1.25, square cap/round joins, >=32dp only.
- Inlay: matte 64×64 tile (neutral fill #151516; hairline #323235) + 24×24 Contour; active state may add a very small bottom-right gold dash, never a full gold border.

## Usage
| Surface | Selected family | Rendered size | Parent target |
| --- | --- | --- | --- |
| Launcher rail | Core | 18–20dp | >=48dp |
| Command palette | Contour | 20dp | >=48dp |
| App library | Inlay | 52–56dp | >=56dp |
| Quick controls | Core | 20–24dp | >=48dp |
| Cinematic title/empty state | Etch | >=32dp | contextual |
| Status metadata | Core only if necessary | >=16dp | informational |

## States
Inactive icons = ivory with reduced opacity based on contrast context.
Active = ivory geometry with gold selection *indication* (micro-rule/dot outside artwork) or gold tint where semantically required.
Disabled = reduced contrast and no hover/press feedback. Destructive action uses explicit accompanying label and confirmation.
Hover/press may animate scale 0.98–1.00 and background opacity, not trace every stroke or spin icons for decoration.

## Naming
Semantic names are lowercase kebab-case, stable across SVG and VectorDrawable. Prefer `cloud-download` / `cloud-upload` over ambiguous `cloud-1`. Actual brands are NOT faked with generic marks.
Catalog contains `label`, `category`, `path`, optionally `aliases`, and an explanation for novel symbols.

## Accessibility
Every button gets the proper localized content description. Search and unknown symbols always have labels. Directional arrows must be transformed thoughtfully for RTL, not indiscriminately mirrored (play, phone, file-code remain consistent).

## Review gate
A glyph passes only when its full-size and 20px exports communicate the same action, when a same-row neighbor can't be mistaken for it, and when it remains visually balanced alongside the rest of its category.
