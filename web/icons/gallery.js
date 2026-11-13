(() => {
  'use strict';
  const byId = id => document.getElementById(id);
  const state = { category: 'all', family: 'contour', size: 24, active: false, query: '', selected: 'home' };
  const strokes = { contour: 1.65, core: 2.10, etch: 1.25 };
  let icons = [], categories = [];
  let toastTimer;
  const escape = s => String(s).replaceAll('&','&amp;').replaceAll('<','&lt;').replaceAll('>','&gt;').replaceAll('"','&quot;');
  function iconSvg(icon,family,size,active=false) {
    const label = escape(icon.label);
    const path = escape(icon.d);
    if (family === 'inlay') return '<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 64 64" width="'+size+'" height="'+size+'" role="img" aria-label="'+label+'">'+
      '<rect x="6" y="6" width="52" height="52" rx="14" fill="#151516" stroke="#353538" stroke-width="1"/>'+
      '<g transform="translate(17 17) scale(1.25)" fill="none" stroke="#F2F2EE" stroke-width="1.65" stroke-linecap="round" stroke-linejoin="round"><path d="'+path+'"/></g>'+
      (active?'<path d="M43 53h9" fill="none" stroke="#CDA664" stroke-width="1.7" stroke-linecap="round"/>':'')+'</svg>';
    return '<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" width="'+size+'" height="'+size+'" role="img" aria-label="'+label+'" fill="none" stroke="#F2F2EE" stroke-width="'+strokes[family]+'" stroke-linecap="'+(family==='etch'?'square':'round')+'" stroke-linejoin="round"><path d="'+path+'"/></svg>';
  }
  function toast(text) {
    const el=byId('toast');el.textContent=text;el.classList.add('show');
    clearTimeout(toastTimer);toastTimer=setTimeout(()=>el.classList.remove('show'),2400);
  }
  function filtered() {
    const q=state.query.toLowerCase().trim();
    return icons.filter(i => (state.category==='all'||i.category===state.category)
      &&(!q || [i.name,i.label,i.category,...i.aliases].some(x=>x.toLowerCase().includes(q))));
  }
  function syncControls() {
    document.querySelectorAll('[data-family]').forEach(el=>el.setAttribute('aria-pressed',String(el.dataset.family===state.family)));
    document.querySelectorAll('[data-size]').forEach(el=>el.setAttribute('aria-pressed',String(Number(el.dataset.size)===state.size)));
    byId('active').checked=state.active;
    byId('sizeAdvice').textContent=state.family==='etch'&&state.size<32?'ETCH IS FOR 32 DP AND ABOVE':
      state.family==='core'?'CORE / COMPACT OPTICAL WEIGHT':state.family==='inlay'?'INLAY / 64 UNIT TILE':
      'CONTOUR / 24 DP DEFAULT';
  }
  function renderCategories(){
    const counts=Object.fromEntries(categories.map(x=>[x,icons.filter(icon=>icon.category===x).length]));
    byId('categories').innerHTML=[['all','All symbols',icons.length],...categories.map(x=>[x,x[0].toUpperCase()+x.slice(1),counts[x]])].map(([category,label,count])=>
      '<button type="button" data-category="'+escape(category)+'" class="'+(state.category===category?'active':'')+'" aria-pressed="'+String(state.category===category)+'">'+
      escape(label)+'<span>'+count+'</span></button>').join('');
  }
  function renderGrid(){
    const matched=filtered();
    byId('resultCount').textContent=matched.length+' / '+icons.length+' SEMANTIC GLYPHS';
    byId('empty').hidden=matched.length!==0;
    byId('grid').innerHTML=matched.map(i=>{
      const family=state.family, size=family==='inlay'?64:state.size;
      return '<button type="button" class="icon-card '+(i.name===state.selected?'selected':'')+'" data-icon="'+escape(i.name)+'" aria-label="'+escape(i.label)+', '+escape(i.category)+'" aria-pressed="'+String(i.name===state.selected)+'">'+
        '<span class="visual">'+iconSvg(i,family,size,state.active)+'</span><span class="icon-name">'+escape(i.name)+'</span><span class="icon-category">'+escape(i.category.toUpperCase())+'</span></button>';
    }).join('');
  }
  function exportSvgString(icon){
    return iconSvg(icon,state.family,state.family==='inlay'?64:24,state.active).replace('width="'+(state.family==='inlay'?64:24)+'" height="'+(state.family==='inlay'?64:24)+'"','width="'+(state.family==='inlay'?64:24)+'" height="'+(state.family==='inlay'?64:24)+'"');
  }
  function renderInspector(){
    const icon=icons.find(x=>x.name===state.selected);
    if(!icon)return;
    byId('inspectorContent').innerHTML=
      '<h2>'+escape(icon.label)+'</h2><div class="category">'+escape(icon.category)+'</div>'+
      '<div class="stage">'+iconSvg(icon,state.family,state.family==='inlay'?115:92,state.active)+'</div>'+
      '<p class="notice">Compare the optical treatments below. Geometry stays recognizable across the entire family, including inactive and selected states.</p>'+
      '<div class="variant-row">'+['contour','core','etch','inlay'].map(f=>
        '<div class="variant">'+iconSvg(icon,f,f==='inlay'?35:30,false)+'<label>'+escape(f.toUpperCase())+'</label></div>').join('')+'</div>'+
      '<div class="ruler"></div><dl class="detail-grid"><dt>NAME</dt><dd>'+escape(icon.name)+'</dd>'+
      '<dt>CATEGORY</dt><dd>'+escape(icon.category)+'</dd>'+
      '<dt>GRID</dt><dd>24 × 24</dd>'+
      '<dt>ALIASES</dt><dd>'+escape(icon.aliases.join(', ')||'None')+'</dd></dl>'+
      '<div class="actions"><button class="primary" type="button" id="copySvg">COPY SVG</button><button type="button" id="saveSvg">SAVE SVG</button></div>';
  }
  async function copySelected(){
    const icon=icons.find(x=>x.name===state.selected);if(!icon)return;
    try {
      await navigator.clipboard.writeText(exportSvgString(icon));
      toast('SVG COPIED / '+icon.name);
    }catch(_){toast('CLIPBOARD UNAVAILABLE / USE SAVE SVG');}
  }
  function saveSelected(){
    const icon=icons.find(x=>x.name===state.selected);if(!icon)return;
    const file=new Blob([exportSvgString(icon)],{type:'image/svg+xml;charset=utf-8'});
    const url=URL.createObjectURL(file),link=document.createElement('a');
    link.href=url;link.download='atelier-'+state.family+'-'+icon.name+'.svg';
    document.body.append(link);link.click();link.remove();setTimeout(()=>URL.revokeObjectURL(url),1500);
    toast('SVG SAVED / '+icon.name);
  }
  function draw(){syncControls();renderCategories();renderGrid();renderInspector();}
  document.addEventListener('click',e=>{
    const target=e.target.closest('button');if(!target)return;
    if(target.dataset.category){state.category=target.dataset.category;draw();}
    if(target.dataset.family){state.family=target.dataset.family;draw();}
    if(target.dataset.size){state.size=Number(target.dataset.size);draw();}
    if(target.dataset.icon){state.selected=target.dataset.icon;draw();}
    if(target.id==='copySvg')copySelected();
    if(target.id==='saveSvg')saveSelected();
  });
  document.addEventListener('keydown',e=>{
    if(e.key==='/'&&!['INPUT','TEXTAREA'].includes(document.activeElement.tagName)){
      e.preventDefault();byId('query').focus();
    }
    if(e.key==='Escape'&&document.activeElement===byId('query')){byId('query').value='';state.query='';draw();}
  });
  byId('query').addEventListener('input',e=>{state.query=e.target.value;renderGrid();});
  byId('active').addEventListener('change',e=>{state.active=e.target.checked;draw();});
  fetch('./catalog.json',{cache:'no-store'})
    .then(response=>{if(!response.ok)throw Error('HTTP '+response.status);return response.json();})
    .then(data=>{
      if(data.schema!=='atelier.icon.catalog.v1'||!Array.isArray(data.icons))throw Error('Unexpected icon catalog');
      icons=data.icons;categories=[...new Set(icons.map(i=>i.category))];state.selected=icons.find(i=>i.name==='home')?.name||icons[0]?.name;
      draw();
    }).catch(err=>{
      byId('resultCount').textContent='ICON CATALOG COULD NOT LOAD';
      byId('empty').hidden=false;
      byId('empty').textContent='Serve the web/ folder using a local HTTP server or open the published GitHub Pages site. '+err.message;
      byId('inspectorContent').innerHTML='<p class="side-description">Catalog is unavailable. This is not a rendered icon preview.</p>';
    });
})();
