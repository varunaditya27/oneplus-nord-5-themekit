# Publishing the interactive design study

The `web/` directory is a standalone static site. The `pages.yml` workflow deploys it using GitHub Pages via GitHub Actions. It is intentionally **manual-only** until Pages has been enabled, to avoid predictable failing builds.

If Pages isn't enabled yet, visit repository **Settings → Pages → Build and deployment → Source: GitHub Actions**. The workflow may require the repository owner's approval or Actions permissions. Publishing is not confirmed until GitHub reports a successful deployment.

Once Pages is enabled, open **Actions → Publish ATELIER visual study → Run workflow**. After a successful deployment, the likely project URL is:
https://varunaditya27.github.io/oneplus-nord-5-themekit/

This URL is a deployment target, not proof the site is already live. The visual study uses remote open-font CDNs and graceful system-font fallbacks. It does not collect device data or execute arbitrary shell commands.
