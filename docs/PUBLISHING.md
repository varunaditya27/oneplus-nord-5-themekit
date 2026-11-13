# Publishing the interactive ATELIER visual study

ATELIER's `web/` directory is a standalone static website. It can be hosted on GitHub Pages without a Node build, root permissions, or third-party deployment secrets. The site's fonts are bundled locally and its asset paths are relative to `web/index.html`.

## One-time repository setup

**Required before the first deployment:**

1. Open [ATELIER repository Pages settings](https://github.com/varunaditya27/oneplus-nord-5-themekit/settings/pages).
2. Under **Build and deployment**, set **Source** to **GitHub Actions** (not *Deploy from a branch*).
3. Save the selection if prompted.
4. Open [Actions → Publish ATELIER visual study](https://github.com/varunaditya27/oneplus-nord-5-themekit/actions/workflows/pages.yml).
5. Choose **Run workflow**, branch `main`, then **Run workflow**.

Once the workflow completes successfully, the expected site URL is:

https://varunaditya27.github.io/oneplus-nord-5-themekit/

This URL is not proof of publication until the deployment run reports success.

## Why the first deployment failed

The November 13, 2026 run [#3](https://github.com/varunaditya27/oneplus-nord-5-themekit/actions/runs/43496823685) failed at `actions/configure-pages@v5`:

```text
Get Pages site failed. Error: Not Found (HTTP 404)
```

The Pages site had not been initialized. This is a repository configuration requirement, **not an HTML, JavaScript, or artifact-path failure**.

The action's `enablement: true` parameter is not a workaround with the default `GITHUB_TOKEN`. Enabling Pages programmatically requires a separate token with appropriately privileged permissions. For a personal repository, the one-time Settings change is safer.

The same old run warned that `actions/checkout@v4` and `actions/configure-pages@v5` used Node.js 20. This workflow now uses `checkout@v6` and `configure-pages@v6` (Node.js 24) and `upload-pages-artifact@v4`.

## The deployment workflow

The workflow has two separate jobs:

1. **Build** validates `web/index.html`, checks JavaScript syntax, runs the local website smoke tests, then uploads `./web` as a Pages artifact.
2. **Deploy** checks the repository's Pages configuration through GitHub's API, initializes the action context, deploys that artifact, and prints the published URL in the job summary.

If Pages is still disabled, the new workflow will fail early with **one clear action to take**, instead of an opaque `configure-pages` stack trace. This means a red deployment run does not imply that the website files themselves are broken.

The workflow stays manual-only until GitHub Pages is initialized once. After your first successful deployment, it can be switched to automatic publishing on changes in `web/**`.

## Local viewing

To preview the site without Pages:

```sh
python3 -m http.server --directory web 8080
```

Open http://localhost:8080/ in a browser. App launching, secure lock-screen changes, and Android system controls are not functional in this web study.

## Troubleshooting

- **HTTP 404 at Pages setup:** Pages not enabled. Check **Settings → Pages → Source: GitHub Actions**.
- **HTTP 403 or access-denied error:** Check repository Actions policy, Pages permissions, and whether the workflow token can deploy.
- **Wrong publishing source:** Switch from branch publishing to **GitHub Actions**.
- **Deployment succeeds but site is 404:** Check the `page_url` output, GitHub Pages propagation, and `web/index.html` in the uploaded artifact.
- **Missing fonts:** Confirm the `web/fonts/` files are included in the artifact. They are part of the repository and require no CDN.

The old failing run does not change when workflow files are edited. **Run the updated workflow on `main`** after configuring Pages.

Sources: [GitHub Pages publishing configuration](https://docs.github.com/en/pages/getting-started-with-github-pages/configuring-a-publishing-source-for-your-github-pages-site), [custom workflow guidance](https://docs.github.com/en/pages/getting-started-with-github-pages/using-custom-workflows-with-github-pages), and [configure-pages input requirements](https://github.com/actions/configure-pages/blob/main/action.yml).
