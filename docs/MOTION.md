# ATELIER / Kinetic design, v0.2

**Thesis:** Motion is the change in information, not a decoration layered on top of it. The interface should look precise even with animations completely disabled.

## The motion grammar

| Token | Duration | Mechanism | Trigger |
|:--|--:|:--|:--|
| `press` | velocity-based, non-bouncy | 1.000 → 0.955 → 1.000 scale | Finger down, finger up |
| `state` | 180ms | Color / opacity | Focus mode, selected control |
| `page` | 220ms | Crossfade | Home / library / command transition |
| `reveal` | 560ms max | Vector path drawing | Clock first appears or numerals update |
| `selection` | 150ms | Single brass marker opacity | App favorite or active tab |
| `ambient` | 100ms sample interval, slow changes | Mathematical wallpaper geometry | Visible live wallpaper only |

These are initial targets, not validated hardware timings. A durable kinetic system depends on *relative hierarchy*: touch feedback first, state feedback second, navigation third, cinematic reveal only when information actually changes.

### No-go effects

- No bounce, elastic overshoot, rubber-band wobble, random spin, incessant heartbeat.
- No pulsating neon glows, lens flares, fake terminal glitches, glassmorphism.
- No path-morphing between unrelated icons; recognizable identities should remain stable.
- No animating all 155 icons on an app-grid entrance.
- No continuous dynamic shading behind ordinary interface controls.
- No selection animation that implies a system setting changed when the launcher merely opened Android Settings.

### Material effects

**Contact shadow:** no drop shadow. Pressure is conveyed through physical scale and subtle substrate darkening.

**Brass inlay:** a short, single-purpose gold rule for selection, never a glowing gold stroke around the whole tile.

**Optical continuity:** symbol geometry remains unchanged across selection, and a transition may change *weight* only if it improves visibility.

**Ambient atmosphere:** Silkfield already draws slow graphite strands. It stops when hidden; idle battery draw must be measured on a physical Nord 5 before raising its frame rate. Do not infer 144 fps is appropriate simply because a panel supports high refresh rates.

### Performance and accessibility

- Native animations use Compose's draw-layer transforms, not UI re-layout loops.
- Native presses use an interruptible, non-bouncy spring.
- Reduced Motion in the launcher suppresses optional scaling and cancels cinematic reveals. The web studio also respects `prefers-reduced-motion`.
- Interactive hit areas stay at least 48dp where possible, independent of visual compression.
- Only actions with meaningful confirmation receive haptics, such as pinning or toggling Focus.
- Nonessential animations stop when browser tab or wallpaper is not visible.
- Do not call a web simulator a working Android system control.

## Where motion is owned

**Native app:** app press, pinning, Focus tile, navigation.
**Web studio:** explores interaction tokens and example choreography.
**WallpaperService:** atmospheric movement, render cadence, lifecycle.
**OxygenOS:** actual lock-screen transitions, biometric effects, native Quick Settings and AOD. The launcher cannot override those through ordinary APIs.

## References

- [Android Compose quick animation guide](https://developer.android.com/develop/ui/compose/animation/quick-guide)
- [Compose animation specs and springs](https://developer.android.com/develop/ui/compose/animation/customize)
- [MDN reduced-motion guidance](https://developer.mozilla.org/en-US/docs/Web/CSS/@media/prefers-reduced-motion)
- [WCAG 2.3.3, Animation from Interactions](https://www.w3.org/WAI/WCAG22/Understanding/animation-from-interactions)

## Live behavior studio

[Open ATELIER Motion Studio](https://varunaditya27.github.io/oneplus-nord-5-themekit/motion/).

Six interactive experiments let reviewers test Contact, Inlay, Focus, Navigation, Numerals, and Silkfield without any rendered animation files. Their behavior uses actual state, CSS transforms and opacity, original SVG strokes and a visibility-aware Canvas loop.

The default web motion specification is maintained in [motion/tokens.json](../motion/tokens.json) and mirrored in [web/motion/tokens.json](../web/motion/tokens.json), with CI checking parity.
