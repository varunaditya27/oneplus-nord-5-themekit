# Publishing the ATELIER interactive visual studies

**GitHub Pages is enabled and the website deploys successfully.**

- [Main cinematic interface](https://varunaditya27.github.io/oneplus-nord-5-themekit/)
- [155-symbol icon archive](https://varunaditya27.github.io/oneplus-nord-5-themekit/icons/)
- [Publishing workflow](https://github.com/varunaditya27/oneplus-nord-5-themekit/actions/workflows/pages.yml)

## Deployment policy

The workflow now runs automatically when the `web/` source, icon catalog, or icon compiler changes. It also supports manual runs from **Actions → Publish ATELIER visual study → Run workflow**.

The **build** job validates both visual studies, rebuilds icon exports from the canonical catalog, checks source consistency, and packages the `web/` folder with locally bundled fonts. The **deploy** job checks the Pages configuration, publishes the artifact and shows the live URL.

No hosted image/CDN services are necessary to render the icon archive.

## Previous Pages 404

The original run failed because Pages had not been initialized. The repository owner enabled **Settings → Pages → Build and deployment → Source: GitHub Actions**, and the deployment workflow subsequently completed.

If a new repository is forked, the owner must separately enable GitHub Pages for that fork. A default workflow token should not attempt to initialize Pages using `enablement: true`.

## Local testing

```sh
node iconography/build.mjs
node iconography/check.mjs
python3 -m http.server --directory web 8080
```

Then open http://localhost:8080/ and http://localhost:8080/icons/.

The website is a design archive. It does not control OxygenOS, launch Android applications, or replace biometric and lock-screen interfaces.
