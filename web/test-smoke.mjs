import { readFileSync, existsSync, statSync } from 'node:fs';
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

for (const font of ['hubot-extra-light.woff2','hubot-regular.woff2','spline-sans.ttf','departure-mono.woff2']) {
  const local = new URL('./fonts/' + font, import.meta.url);
  assert.ok(existsSync(local), 'Missing bundled font: ' + font);
  assert.ok(statSync(local).size > 1000, 'Empty font: ' + font);
}
for (const material of ['ink', 'graphite', 'sable']) {
  assert.ok(html.includes('data-material="' + material + '"'), 'Missing dark finish ' + material);
}
assert.ok(!css.includes('fonts.googleapis.com'), 'Web font must not require a CDN');

const motionHtml=readFileSync(new URL('./motion/index.html',import.meta.url),'utf8');
const motionJs=readFileSync(new URL('./motion/studio.js',import.meta.url),'utf8');
const motionCss=readFileSync(new URL('./motion/studio.css',import.meta.url),'utf8');
const motionTokens=JSON.parse(readFileSync(new URL('./motion/tokens.json',import.meta.url),'utf8'));
const masterTokens=JSON.parse(readFileSync(new URL('../motion/tokens.json',import.meta.url),'utf8'));
assert.deepEqual(motionTokens,masterTokens,'Web motion tokens diverge from native specification');
assert.equal(motionTokens.schema,'atelier.motion.v1');
for(const id of ['phone','phoneContent','ambientCanvas','presets','eventName','eventDesc']){
  assert.ok(motionHtml.includes('id="'+id+'"'),'Missing motion studio control '+id);
}
for(const action of ['press','pin','focus','navigate','clock','ambient']){
  assert.ok(motionHtml.includes('data-action="'+action+'"'),'Missing interactive behavior '+action);
}
assert.ok(motionJs.includes("cancelAnimationFrame(frameHandle)"),'Ambient motion must fully stop');
assert.ok(motionJs.includes('document.hidden'),'Ambient motion must pause when hidden');
assert.ok(motionJs.includes('osReduced.matches'),'Reduced-motion detection missing');
assert.ok(motionCss.includes('prefers-reduced-motion'),'Reduced-motion styling missing');
assert.ok(html.includes('./motion/'),'Main visual study should link to Motion Studio');
console.log('ATELIER Motion Studio source and shared token checks passed.');

for(const semantic of ['play','menu','mute']){
 assert.ok(motionHtml.includes('data-morph="'+semantic+'"'),'Missing semantic state '+semantic);
}
assert.ok(motionJs.includes('semanticStates'),'Icon motion must have explicit state');
assert.ok(motionCss.includes('.mute-slash'),'Muted state should draw a semantically meaningful slash');
