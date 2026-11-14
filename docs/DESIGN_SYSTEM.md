# The ATELIER design system

## Thesis
The interface should feel engineered even when every control is absent. Resist shiny glass cards, decorative noise, fake terminal readouts and ornamental grids.

## Tokens
| Token | Hex | Job |
|--|--|--|
| `ink/000` | `#080809` | Main material, AMOLED black-adjacent |
| `ink/100` | `#151516` | Raised quiet panel |
| `ink/200` | `#313134` | Hairlines and boundaries |
| `paper/000` | `#F2F2EE` | Primary foreground |
| `paper/300` | `#9C9C9C` | Muted foreground |
| `au/500` | `#CDA664` | Active mark / deliberate one-point emphasis |

## Three dark finishes

| Finish | Background | Elevated surface | Intent |
|--|--|--|--|
| Ink | `#080809` | `#151516` | Neutral black, the default |
| Graphite | `#111315` | `#1B1E21` | Cooler mineral shadows |
| Sable | `#100F0E` | `#1B1917` | Warmer black with understated brass |

The system is **dark-only**. Ivory (`paper/000`) denotes the text color, not a light-mode alternative. Existing browser preferences saved as `paper` migrate to Sable; old Android `PAPER` settings fall back to Ink.

**Color allocation:** 85–95% near-black, 5–12% neutral, ideally under 2% brass. Never use brass for paragraphs.

## Typeface hierarchy
- **Display**: Hubot Sans variable, widen or condense selectively, with an engineered uppercase vocabulary.
- **Interface**: Spline Sans in body-sized labels; exceptional x-height and spacing at small sizes.
- **Technical**: Departure Mono for real technical context only: command entry, timestamps, identifiers and build states.
- **Signature numerals**: geometric vector path language, drawn as strokes. They are not Hubot glyphs and should never be substituted silently.

**Licensing:** Typography must remain replaceable. Never bundle Neue Machina or other proprietary fonts without permission.

## Official mark

ATELIER uses a geometric open A with a single short brass register, not a standard typeface letter. The canonical source is [mark.svg](../assets/brand/mark.svg), with coordinated [wordmark](../assets/brand/wordmark.svg) and [lockup](../assets/brand/lockup.svg). The mark contains no gradient, glow, ornament or trademark imitation. Atmospheric texture belongs to the surrounding surface, not the logo.

See the complete [Brand identity guide](BRAND_IDENTITY.md).

## Geometry
Use an 8pt spacing skeleton, 1px optical hairlines and sharply tiered radii. Controls should be comfortable to tap (minimum 48dp recommended) even when visually small. Grid density should be controlled by content, not desire to fill empty space.

## Motion
- Surface reveal: opacity and short translation, ~180–260ms.
- Focused interaction: tactile feedback and small compression, under 180ms.
- Clock drawing: a restrained one-shot reveal, not constant re-animation every second.
- Ambient wallpaper: barely visible drift, stop when hidden.
- Honor reduced motion; keep stable visual state without animation.

## Surface signatures
**Lock concept:** vector clock, one line of information, little else. OxygenOS controls the actual secure lock screen.
**Home:** one statement element, app shortcuts where the thumb expects them, command interface one gesture away.
**Library:** prioritize retrieval and fast scanning, not neon categories.
**Controls concept:** exceptional contrast; distinguish actual custom tile APIs from stock Quick Settings.
**Ambient wallpaper:** delicate silk-like strata in graphite; a gold glint only where material bends.

## Rejection list
Reject default Playfair/Sora/Poppins pairings, prominent gradients, cyberpunk neon, excessive terminal glyphs, fake CPU telemetry, excessive cardification and constant ambient animation.
