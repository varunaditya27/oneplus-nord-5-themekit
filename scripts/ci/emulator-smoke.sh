#!/usr/bin/env bash
# One shell process. The emulator-runner invokes separate commands if a YAML
# multi-line `script` is supplied, which previously broke our if/fi block.
set -Eeuo pipefail

mkdir -p dist/emulator
report="dist/emulator"
package="dev.atelier.black"
activity="dev.atelier.LauncherActivity"

collect_diagnostics() {
  adb logcat -d -v brief -s AndroidRuntime:E > "$report/crashes.txt" || true
  adb shell dumpsys activity activities > "$report/activities.txt" || true
  adb shell dumpsys window > "$report/window.txt" || true
}
trap collect_diagnostics EXIT

adb install -r app/build/outputs/apk/debug/app-debug.apk
adb logcat -c
adb shell am start -W -n "$package/$activity"
sleep 8

# A screenshot succeeding is insufficient if the activity immediately crashed.
adb shell pidof "$package" > "$report/process-id.txt"
test -s "$report/process-id.txt"

capture() {
  local label="$1"
  adb exec-out screencap -p > "$report/$label.png"
  test -s "$report/$label.png"
  adb shell uiautomator dump /sdcard/window.xml >/dev/null 2>&1 || true
  adb pull /sdcard/window.xml "$report/$label.xml" >/dev/null 2>&1 || true
}

capture home

# Tap the launcher-owned bottom navigation. Use relative coordinates so this
# remains applicable to different emulator resolutions.
resolution="$(adb shell wm size | sed -n 's/.*Physical size: \([0-9]*\)x\([0-9]*\).*/\1 \2/p' | tail -1)"
read -r width height <<< "$resolution"
if [[ -z "${width:-}" || -z "${height:-}" ]]; then
  echo "Cannot inspect emulator size" >&2
  exit 1
fi
nav_y=$((height * 927 / 1000))
adb shell input tap $((width * 27 / 100)) "$nav_y"
sleep 2
capture library
adb shell input tap $((width * 92 / 100)) "$nav_y"
sleep 2
capture settings

# Ensure there were no fatal Java crashes in any of the three screens.
collect_diagnostics
if grep -q 'FATAL EXCEPTION' "$report/crashes.txt"; then
  echo "ATELIER threw an AndroidRuntime fatal exception:" >&2
  cat "$report/crashes.txt"
  exit 1
fi

adb shell pidof "$package" > "$report/process-id.txt"
test -s "$report/process-id.txt"

echo "Emulator verified: launch, Home, Library and Settings screenshots; no fatal AndroidRuntime crashes."
