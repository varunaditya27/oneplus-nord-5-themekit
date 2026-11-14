#!/usr/bin/env bash
# Requires: authenticated GitHub CLI session with administrative permissions.
# Run from the repository root; edits GitHub About metadata, not source files.
set -euo pipefail
cd "$(dirname "$0")/../.."
command -v gh >/dev/null || { echo "Install GitHub CLI (gh) first." >&2; exit 1; }
gh auth status >/dev/null
profile=".github/REPOSITORY_PROFILE.json"
repository="$(python3 -c 'import json,sys; print(json.load(open(sys.argv[1]))["repository"])' "$profile")"
if [[ "$repository" != "varunaditya27/oneplus-nord-5-themekit" ]]; then
  echo "Unexpected repository target: $repository" >&2
  exit 1
fi
echo "Applying ATELIER About metadata to $repository..."
python3 -c 'import json,sys; d=json.load(open(sys.argv[1])); print(json.dumps({"description":d["description"],"homepage":d["homepage"]}))' "$profile" \
  | gh api --method PATCH "repos/$repository" --input - >/dev/null
python3 -c 'import json,sys; d=json.load(open(sys.argv[1])); print(json.dumps({"names":d["topics"]}))' "$profile" \
  | gh api --method PUT "repos/$repository/topics" --input - >/dev/null
gh api "repos/$repository" --jq '{description,homepage,topics}'
echo "Done. The release and source files were not changed."
