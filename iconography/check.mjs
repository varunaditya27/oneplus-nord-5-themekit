#!/usr/bin/env node
import assert from 'node:assert/strict';
import { readFileSync } from 'node:fs';
import { execFileSync } from 'node:child_process';
const doc=JSON.parse(readFileSync(new URL('./catalog.json', import.meta.url),'utf8'));
assert.equal(doc.schema,'atelier.icon.catalog.v1');
assert.equal(doc.viewport,24);
assert.ok(doc.icons.length>=150);
const names=new Set(doc.icons.map(it=>it.name));
assert.equal(names.size,doc.icons.length);
assert.ok(doc.icons.every(it=>it.d.length>12));
for(const required of ['home','settings','clock','folder','terminal','wifi','bluetooth','search','camera','mail','focus','wallpaper']){
  assert.ok(names.has(required),'Missing necessary semantic: '+required);
}
const forbidden=['github','twitter','google','apple','chrome','spotify','telegram'];
for(const branded of forbidden)assert.ok(!names.has(branded),'No counterfeit brand symbol: '+branded);
execFileSync(process.execPath,[new URL('./build.mjs',import.meta.url).pathname,'--check'],{stdio:'inherit'});
console.log('Icon catalog, unique geometry, and all generated outputs validated.');
