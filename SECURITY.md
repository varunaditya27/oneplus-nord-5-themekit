# Security policy

ATELIER v0.9.0 is a **public preview** of an Android HOME application and live wallpaper. It is not a privileged system modification.

## Supported release

| Version | Status |
| :-- | :-- |
| v0.9.0-preview.2 | Current public debug-signed preview |
| Earlier pre-release builds | Superseded; no security-update guarantee |

No production support or vulnerability-response SLA is promised.

## Reporting security issues

Please **do not publish exploit details, private screenshots, credentials or sensitive device information in public issues**.

If the repository's **Security → Report a vulnerability** feature is available, use that private reporting route. Otherwise, open a public issue containing only a non-sensitive request for a private reporting channel. Do not attach a proof of concept publicly.

Include the ATELIER version, Android/OxygenOS version, reproducible steps and potential impact, without sharing personal data.

## Security boundaries

- ATELIER requests the Android HOME role only with user approval; the stock launcher remains available.
- The icon catalog reads launchable apps. No blanket package-visibility permission is required.
- The command interface accepts allowlisted actions, not arbitrary terminal instructions.
- The optional Focus tile changes only ATELIER's local state, not system Do Not Disturb.
- Theme export/import uses the Android document picker and locally validated JSON.
- The launcher does not require root, notification collection, screen recording, contact access or accessibility surveillance.
- CI produces a **debug-signed** APK. Signing keys can differ between builds, so treat updates carefully and back up your theme before uninstalling.

Do not commit private signing keys, tokens, proprietary fonts, personally identifying diagnostics or unreviewed third-party binaries. The [security and device testing checklist](docs/TESTING.md) lists remaining validation work.
