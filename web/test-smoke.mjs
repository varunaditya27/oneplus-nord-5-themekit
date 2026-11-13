import { readFileSync, readdirSync } from 'node:fs';
import assert from 'node:assert/strict';

const html = readFileSync(new URL('./index.html', import.meta.url), 'utf8');
const css = readFileSync(new URL('./styles.css', import.meta.url), 'utf8');
const js = readFileSync(new URL('./app.js', import.meta.url), 'utf8');

for (const id of ['view','device','statusTime','silkfield','homeGesture','reset','motion']) {
  assert.match(html, new RegExp('id="' + id + '"'), 'Missing element: ' + id);
}
for (const screen of ['lock','home','library','command','control','settings']) {
  assert.ok(html.includes('data-view="' + screen + '"'), 'Missing screen: ' + screen);
  assert.ok(js.includes(screen), 'Missing screen handler: ' + screen);
}
assert.ok(css.includes('prefers-reduced-motion'), 'Reduced motion styling missing');
assert.ok(js.includes('document.hidden'), 'Wallpaper should pause when hidden');
assert.ok(js.includes('SAMPLE APP'), 'Prototype must not pretend to launch Android apps');
assert.ok(js.includes('UNKNOWN COMMAND / NOT EXECUTED'), 'Unknown commands must be rejected');
console.log('ATELIER web source smoke checks passed.');
