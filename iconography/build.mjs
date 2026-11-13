#!/usr/bin/env node
/**
 * ATELIER iconography compiler.
 *
 * Canonical glyph paths live in iconography/catalog.json.
 * All SVG variants, native VectorDrawables, the Compose registry, and
 * web gallery metadata are deterministic derivatives of that source.
 *
 * Usage:
 *   node iconography/build.mjs
 *   node iconography/build.mjs --check
 *
 * No third-party icon font, package, or network access required.
 */
import { readFileSync, writeFileSync, mkdirSync, existsSync } from 'node:fs';
import { fileURLToPath } from 'node:url';
import { dirname, resolve } from 'node:path';

const ROOT = resolve(dirname(fileURLToPath(import.meta.url)), '..');
const catalog = JSON.parse(readFileSync(resolve(ROOT,'iconography/catalog.json'),'utf8'));
const CHECK = process.argv.includes('--check');
const colors = { paper:'#F2F2EE', surface:'#151516', hairline:'#353538', brass:'#CDA664' };
const families = {
  contour:{width:1.65,cap:'round',join:'round'},
  core:{width:2.10,cap:'round',join:'round'},
  etch:{width:1.25,cap:'square',join:'round'}
};

function esc(s) {
  return String(s).replaceAll('&','&amp;').replaceAll('"','&quot;').replaceAll('<','&lt;');
}
function xmlIcon(icon,family) {
  const s=families[family];
  return '<?xml version="1.0" encoding="utf-8"?>\n' +
    '<vector xmlns:android="http://schemas.android.com/apk/res/android"\n' +
    '    android:width="24dp" android:height="24dp"\n' +
    '    android:viewportWidth="24" android:viewportHeight="24">\n' +
    '    <path android:fillColor="#00000000"\n' +
    '        android:strokeColor="'+colors.paper+'"\n' +
    '        android:strokeWidth="'+s.width+'"\n' +
    '        android:strokeLineCap="'+s.cap+'"\n' +
    '        android:strokeLineJoin="'+s.join+'"\n' +
    '        android:pathData="'+esc(icon.d)+'"/>\n' +
    '</vector>\n';
}
function svgIcon(icon,family) {
  const s=families[family];
  return '<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" width="24" height="24" ' +
    'role="img" aria-label="'+esc(icon.label)+'" fill="none" stroke="'+colors.paper+'" stroke-width="'+s.width+'" '+
    'stroke-linecap="'+s.cap+'" stroke-linejoin="'+s.join+'">\n' +
    '  <title>'+esc(icon.label)+'</title>\n' +
    '  <path d="'+esc(icon.d)+'"/>\n</svg>\n';
}
function inlayIcon(icon,active=false) {
  return '<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 64 64" width="64" height="64" ' +
    'role="img" aria-label="'+esc(icon.label)+(active?' active':'')+'" fill="none">\n' +
    '  <title>'+esc(icon.label)+(active?' (active)':'')+'</title>\n' +
    '  <rect x="6" y="6" width="52" height="52" rx="14" fill="'+colors.surface+'" stroke="'+colors.hairline+'" stroke-width="1"/>\n' +
    '  <g transform="translate(17 17) scale(1.25)" stroke="'+colors.paper+'" stroke-width="1.65" stroke-linecap="round" stroke-linejoin="round">\n' +
    '    <path d="'+esc(icon.d)+'"/>\n  </g>\n' +
    (active?'  <path d="M43 53h9" stroke="'+colors.brass+'" stroke-width="1.7" stroke-linecap="round"/>\n':'') +
    '</svg>\n';
}
function categorySheet(category,icons) {
  const cols=9,cellW=134,cellH=145,top=92;
  const rows=Math.ceil(icons.length/cols),W=cols*cellW+70,H=top+rows*cellH+36;
  const pieces=[
    '<svg xmlns="http://www.w3.org/2000/svg" width="'+W+'" height="'+H+'" viewBox="0 0 '+W+' '+H+'">',
    '<rect width="100%" height="100%" fill="#080809"/>',
    '<text x="40" y="44" font-family="Arial,sans-serif" font-size="24" letter-spacing="4" fill="#F2F2EE">ATELIER / '+esc(category.toUpperCase())+'</text>',
    '<path d="M40 62h'+(W-80)+'" stroke="#373739"/>',
    '<path d="M40 62h36" stroke="#CDA664" stroke-width="2"/>'
  ];
  icons.forEach((it,i)=>{
    const x=35+(i%cols)*cellW,y=top+Math.floor(i/cols)*cellH;
    pieces.push('<rect x="'+x+'" y="'+y+'" width="'+(cellW-9)+'" height="'+(cellH-9)+'" rx="13" fill="#151516" stroke="#333336"/>');
    pieces.push('<svg x="'+(x+42)+'" y="'+(y+22)+'" width="43" height="43" viewBox="0 0 24 24" fill="none" stroke="#F2F2EE" stroke-width="1.65" stroke-linecap="round" stroke-linejoin="round"><path d="'+esc(it.d)+'"/></svg>');
    pieces.push('<text x="'+(x+cellW/2-4)+'" y="'+(y+91)+'" text-anchor="middle" font-family="Arial,sans-serif" font-size="11" fill="#E0E0DE">'+esc(it.name)+'</text>');
    pieces.push('<text x="'+(x+cellW/2-4)+'" y="'+(y+108)+'" text-anchor="middle" font-family="monospace" font-size="9" fill="#89898A">24 / CONTOUR</text>');
  });
  pieces.push('</svg>\n');
  return pieces.join('\n');
}
function overviewSvg(items) {
  const featured=['home','search','settings','wifi','camera','calendar','terminal','database','mail','music','focus','folder'];
  const pick=featured.map(n=>items.find(i=>i.name===n)).filter(Boolean);
  const cols=pick.length,cellW=102,W=1480,H=620;
  const parts=['<svg xmlns="http://www.w3.org/2000/svg" width="'+W+'" height="'+H+'" viewBox="0 0 '+W+' '+H+'">',
    '<rect width="'+W+'" height="'+H+'" fill="#080809"/>',
    '<text x="45" y="55" font-family="Arial,sans-serif" font-size="25" letter-spacing="6" fill="#F2F2EE">ATELIER / ICON SYSTEM 001</text>',
    '<text x="45" y="83" font-family="monospace" font-size="12" fill="#9C9C9C">ONE SEMANTIC GRAMMAR / FOUR INTENTIONAL TREATMENTS</text>',
    '<path d="M45 105h1390" stroke="#343437"/>','<path d="M45 105h38" stroke="#CDA664" stroke-width="2"/>'];
  const rows=[['CONTOUR','1.65 / 24 DP'],['CORE','2.10 / 16–20 DP'],['ETCH','1.25 / 32 DP+'],['INLAY','64 / LAUNCHER TILE']];
  rows.forEach((row,k)=>{
    const y=135+k*112;
    parts.push('<text x="45" y="'+(y+30)+'" font-family="Arial,sans-serif" font-size="14" fill="#F2F2EE">'+row[0]+'</text>');
    parts.push('<text x="45" y="'+(y+49)+'" font-family="monospace" font-size="10" fill="#89898B">'+row[1]+'</text>');
    pick.forEach((icon,i)=>{
      const x=245+i*102;
      if(k===3)parts.push('<g transform="translate('+(x+7)+' '+(y-3)+') scale(.82)"><rect x="6" y="6" width="52" height="52" rx="14" fill="#151516" stroke="#353538"/><g transform="translate(17 17) scale(1.25)" fill="none" stroke="#F2F2EE" stroke-width="1.65" stroke-linecap="round" stroke-linejoin="round"><path d="'+esc(icon.d)+'"/></g></g>');
      else{
        const s=families[['contour','core','etch'][k]];
        parts.push('<svg x="'+(x+8)+'" y="'+(y+5)+'" width="44" height="44" viewBox="0 0 24 24" fill="none" stroke="#F2F2EE" stroke-width="'+s.width+'" stroke-linecap="'+s.cap+'" stroke-linejoin="'+s.join+'"><path d="'+esc(icon.d)+'"/></svg>');
      }
      if(k===3)parts.push('<text x="'+(x+31)+'" y="'+(y+70)+'" font-family="Arial,sans-serif" fill="#B5B5B5" font-size="9" text-anchor="middle">'+esc(icon.name)+'</text>');
    });
  });
  parts.push('<text x="45" y="592" fill="#8A8A8D" font-family="monospace" font-size="12">155 ORIGINAL GLYPHS / 7 CATEGORIES / NO BRAND-LOGO IMITATIONS</text></svg>\n');
  return parts.join('\n');
}
function nativeRegistry(items) {
  // Stable, compile-time resource refs. No getIdentifier runtime reflection.
  const aliases={ LIBRARY:'apps-grid', COMMAND:'terminal' };
  const all=items.map(i=>[i.name.toUpperCase().replaceAll('-','_'),i.name]);
  for(const [name,target] of Object.entries(aliases))all.push([name,target]);
  const lines=[
'package dev.atelier.ui',
'',
'import androidx.annotation.DrawableRes',
'import androidx.compose.material3.Icon',
'import androidx.compose.runtime.Composable',
'import androidx.compose.ui.Modifier',
'import androidx.compose.ui.graphics.Color',
'import androidx.compose.ui.res.painterResource',
'import dev.atelier.black.R',
'',
'// GENERATED from iconography/catalog.json. Do not edit manually.',
'enum class AtelierIconWeight { CONTOUR, CORE, ETCH }',
'enum class AtelierGlyph {',
'    '+all.map(x=>x[0]).join(', ')+'',
'}',
'',
'@DrawableRes',
'private fun resource(glyph: AtelierGlyph, weight: AtelierIconWeight): Int = when (weight) {'
];
  for(const w of ['CONTOUR','CORE','ETCH']){
    lines.push('    AtelierIconWeight.'+w+' -> when (glyph) {');
    for(const [name,slug] of all)
      lines.push('        AtelierGlyph.'+name+' -> R.drawable.atelier_'+(w==='CONTOUR'?'':w.toLowerCase()+'_')+slug.replaceAll('-','_'));
    lines.push('    }');
  }
  lines.push('}','',
'@Composable',
'fun AtelierGlyphIcon(',
'    glyph: AtelierGlyph,',
'    tint: Color,',
'    modifier: Modifier = Modifier,',
'    description: String? = null,',
'    weight: AtelierIconWeight = AtelierIconWeight.CORE',
') {',
'    Icon(painter = painterResource(resource(glyph,weight)),',
'         contentDescription = description, modifier = modifier, tint = tint)',
'}','');
  return lines.join('\n');
}
function validate(items){
  const names=new Set(),pathData=new Set();
  const cats=new Set(['navigation','communication','workspace','system','developer','media','status']);
  for(const it of items){
    if(!/^[a-z][a-z0-9-]+$/.test(it.name))throw Error('Invalid name '+it.name);
    if(names.has(it.name))throw Error('Duplicate semantic name '+it.name);
    if(pathData.has(it.d))throw Error('Duplicate drawing; use aliases instead: '+it.name);
    if(!cats.has(it.category))throw Error('Invalid category '+it.name);
    if(!/^[MLHVCSQTAZmlhvcsqtaz0-9+\-.,\s]+$/.test(it.d))throw Error('Unexpected path content '+it.name);
    if(!/[Mm]/.test(it.d)||it.d.length<12)throw Error('Underspecified geometry '+it.name);
    names.add(it.name);pathData.add(it.d);
  }
  if(items.length<150)throw Error('Catalog unexpectedly small');
}
function outputs(data){
  const items=data.icons;
  validate(items);
  const result=[];
  const add=(name,content)=>result.push([name,content]);
  for(const it of items){
    const native=it.name.replaceAll('-','_');
    for(const family of Object.keys(families)){
      add('iconography/svg/'+family+'/'+it.name+'.svg',svgIcon(it,family));
      add('app/src/main/res/drawable/atelier_'+(family==='contour'?'':family+'_')+native+'.xml',xmlIcon(it,family));
    }
    add('iconography/svg/inlay/'+it.name+'.svg',inlayIcon(it,false));
    add('iconography/svg/inlay-active/'+it.name+'.svg',inlayIcon(it,true));
  }
  for(const category of ['navigation','communication','workspace','system','developer','media','status'])
    add('iconography/preview/'+category+'.svg',categorySheet(category,items.filter(i=>i.category===category)));
  add('iconography/preview/overview.svg',overviewSvg(items));
  add('web/icons/catalog.json',JSON.stringify(data,null,2)+'\n');
  add('app/src/main/java/dev/atelier/ui/AtelierGlyph.kt',nativeRegistry(items));
  return result;
}
function main(){
  const files=outputs(catalog);
  let count=0;
  for(const [relative,content] of files){
    const out=resolve(ROOT,relative);
    if(CHECK){
      if(!existsSync(out) || readFileSync(out,'utf8')!==content)
        throw Error('Outdated or missing generated icon asset: '+relative);
    }else{
      mkdirSync(dirname(out),{recursive:true});
      writeFileSync(out,content,'utf8');
    }
    count++;
  }
  console.log((CHECK?'Verified ':'Generated ')+count+' icon-related files for '+catalog.icons.length+' semantic glyphs.');
}
main();
