# ATELIER / Iconography audit

Status: design review, November 2026. Baseline: 24 existing SVG glyphs under `assets/icons/`, each with a 24×24 viewBox and a mostly 1.35px stroke. The original home and app drawer currently use monograms and original application icons.

## Hard findings

| Existing issue | Why it matters | Resolution |
| --- | --- | --- |
| `github.svg` resembles a proprietary brand mark, but its path is incomplete and visually ambiguous | Unlicensed mimicry is worse than using an honest generic repository symbol | Replace branded imitation with a recognizable generic repository metaphor; keep actual application icons as opt-in |
| `focus.svg` and `clock.svg` have near-overlapping circular silhouettes | Mixed silhouettes read as interchangeable at 18–20dp | Give time a dial and Focus a deliberate moon/quiet state, not two identical loops |
| Default stroke 1.35 at 24×24 | Optical thinning when rendered at 18dp or less | Introduce optical weights, measured at 16/20/24/32dp |
| Few source symbols, manually divergent SVG and Android VectorDrawable files | Inconsistent geometry and silent drift | One reviewed source catalog with deterministic SVG/Android generation |
| Unknown applications represented only by a letter | Type-only dock lacks an immediate visual landmark | Provide app-specific original glyphs for known *function*, with correct fallback to original icon or monogram |
| Controls and app categories share no naming or semantic contract | `compass` can be wrongly used for Wi-Fi, `plus` for Bluetooth | Explicit icon registry, captions, aliases and distinct meanings |
| All controls inherit decorative accent color independently | Gold becomes noise and undermines hierarchy | Symbol geometry stays neutral; accent applied only by active interaction state |
| No side-by-side dense/outline/mobile-size QA | Poorly recognizable shapes pass simply because their SVG is valid | Generate matrices at 16, 20, 24, 32dp and reject confusing silhouettes |
| No RTL and accessibility guidance | Directional icons can indicate the wrong action in right-to-left layouts | Explicit directional semantics and visible labels for non-obvious commands |

## Our direction

**CONTOUR**: 24×24 symbol field, 1.65px stroke at nominal 24dp, round joins, white/ivory semantic marks. Default for settings and actions.

**CORE**: same semantic geometry, optically stronger 2.10px weight at 16–20dp. Used for dense lists, control tiles and tabs. Not called a solid fill, because it is deliberately a stroke family.

**INLAY**: 64×64 launch icon with 24×24 CONTOUR symbol centered on a near-black 52×52 tile, 14px corner radius, hairline border, no ornamental glows. Selected state receives *one* very short brass rule. This is a launcher presentation family rather than a new pictogram vocabulary.

**ETCH**: 1.25px outline for only 32dp+ cinematic/contextual use, never tiny functional buttons.

## Binding design rules

- Use 24×24 source grid, deliberate 2–22 content envelope, balanced optical not geometric centering.
- Max two conceptual elements per glyph where possible; no placeholder squiggles or fake brand logos.
- No gradients, bevel effects, shadows, segmented neon bars, random slashes, or fake circuitry.
- Prefer established physical/functional metaphors (mail envelope, microphone, bell, calendar, folder).
- Respect available negative space; strokes must not close at smaller sizes.
- Keep icon geometry monochromatic. UI controls apply semantic colors, not the asset.
- A symbol is not a 48dp touch target: the parent hit area must be at least 48dp.
- Do not draw unrecognizable symbols merely to reach an impressive inventory count.
- Reject duplicate icon aliases dressed up as distinct shapes; aliases are metadata, not extra artwork.

## Research references (principles, not assets)

- [Apple SF Symbols design guidance](https://developer.apple.com/design/human-interface-guidelines/sf-symbols): optical alignment, weight/scale matching, and meaningful filled vs outlined states
- [Iconoir documentation](https://iconoir.com/docs/faq): consistent 24×24 construction and a coherent stroke family
- [Massachusetts Design System iconography](https://www.mass.gov/info-details/iconography): recognizable semantic icons and supporting text, not standalone guesswork
- [Phosphor design weight comparisons](https://phosphoricons.com/): weights should relate to context and preserve shape identity

We use those as *design methodology*, not as SVG artwork. All ATELIER icon paths in the new catalogue are authored independently.
