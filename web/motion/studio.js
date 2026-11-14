(() => {
'use strict';
const $ = id => document.getElementById(id);
const osReduced = window.matchMedia('(prefers-reduced-motion: reduce)');
const state={preset:'signature',ambient:true,focus:false,pinned:false,page:'home',selected:'terminal'};
const presets={
still:{press:1,state:0,page:0,release:0,clock:0},
signature:{press:.955,state:180,page:220,release:170,clock:560},
expressive:{press:.94,state:215,page:290,release:220,clock:640}
};
let symbols={};
const digits={
'0':'M22 4C40 4 39 25 39 38C39 59 37 72 22 72C6 72 5 58 5 38C5 20 6 4 22 4',
'1':'M7 17L25 4V72M11 72H38',
'2':'M5 20C5 1 40 -2 39 22C39 37 23 45 6 72H40',
'3':'M6 11C15 2 38 1 38 20C38 30 32 36 21 37C36 36 40 46 39 57C38 77 12 76 5 65',
'4':'M33 72V4L5 51H41',
'5':'M39 5H8L5 35C32 22 41 41 39 57C37 78 11 75 5 63',
'6':'M35 8C13 -4 5 25 5 45C5 67 14 73 24 72C49 70 42 34 24 35C15 35 10 40 5 46',
'7':'M5 5H40L15 72',
'8':'M23 37C-1 38 2 3 22 4C42 4 46 36 23 37C-4 38 1 72 22 72C45 72 48 40 23 37',
'9':'M39 31C39 6 30 4 21 4C-1 4 1 38 20 39C29 40 36 34 39 31C39 53 30 76 7 69'
};
const escape=s=>String(s).replaceAll('&','&amp;').replaceAll('<','&lt;').replaceAll('>','&gt;').replaceAll('"','&quot;');
const reduced=()=>osReduced.matches||state.preset==='still';
const icon=name=>'<svg viewBox="0 0 24 24" role="img" aria-label="'+escape(name)+'" fill="none" stroke="currentColor" stroke-width="1.65" stroke-linecap="round" stroke-linejoin="round"><path d="'+escape(symbols[name]||symbols['apps-grid']||'M4 4h16v16H4z')+'"/></svg>';
const numeral=n=>'<svg class="clock-digit" viewBox="0 0 44 76" aria-label="'+n+'"><path pathLength="100" d="'+digits[n]+'"/></svg>';
const timeNow=()=>{
 const now=new Date(),time=now.toLocaleTimeString('en-GB',{hour:'2-digit',minute:'2-digit',hour12:false});
 return {time,digits:time.replace(':',''),date:now.toLocaleDateString('en-GB',{weekday:'long',day:'2-digit',month:'short'}).toUpperCase()};
};
const digitRows=value=>'<div class="clock-row">'+numeral(value[0])+numeral(value[1])+'</div>'+
'<div class="clock-row">'+numeral(value[2])+numeral(value[3])+'</div>';
const app=(label,name)=>'<button class="launcher-app tactile '+(state.pinned&&name==='terminal'?'selected':'')+'" data-action="launch" data-name="'+name+'" aria-label="Preview '+label+'"><span class="tile">'+icon(name)+'</span><small>'+label+'</small></button>';
const home=()=>{
const moment=timeNow();
return '<div><div class="clock-label">ATELIER / 001</div><div class="clock-stack" id="clockStack">'+
digitRows(moment.digits)+'</div>'+
'<div class="gold-rule"></div><div class="screen-date">'+moment.date+'</div><div class="screen-note">'+(state.focus?'FOCUS / ACTIVE':'WORKSPACE / READY')+'</div></div>'+
'<div class="screen-work"><div class="subheading">WORKSPACE / TODAY</div><h3>'+(state.focus?'Room for the work.':'Everything in its place.')+'</h3>'+
'<div class="row-icons">'+app('Terminal','terminal')+app('Files','folder')+app('Camera','camera')+app('Notes','notes')+'</div></div>'+
'<div><button class="cmd-entry tactile" data-action="command"><b>›</b>Search, launch or command</button>'+
'<div class="phone-menu">'+['home','library','focus'].map(x=>'<button data-action="go" data-go="'+x+'" aria-pressed="'+String(state.page===x)+'">'+x.toUpperCase()+'</button>').join('')+'</div></div>';
};
const library=()=>'<div><h3 class="phone-head">Library</h3><div class="phone-caption">APPS / SEMANTIC GLYPHS</div>'+
'<input class="phone-search" id="appSearch" aria-label="Search preview apps" placeholder="Search applications" autocomplete="off"><div class="phone-apps" id="phoneApps">'+
['terminal','folder','camera','notes','calendar','mail','settings','repository','music'].map(n=>app(n[0].toUpperCase()+n.slice(1),n)).join('')+'</div></div>'+
'<div><div class="phone-footer">ACTIONS / PREVIEW ONLY</div><button class="cmd-entry tactile" data-action="go" data-go="home"><b>‹</b>Return to Home</button></div>';
const focus=()=>'<div><h3 class="phone-head">Control</h3><div class="phone-caption">SHELL CONTROLS / PREVIEW</div>'+
'<div class="phone-focus">'+
'<button class="tactile '+(state.focus?'active':'')+'" data-action="focus">'+icon('focus')+'<span>Focus</span></button>'+
'<button class="tactile" data-action="go" data-go="home">'+icon('home')+'<span>Home</span></button>'+
'</div><div class="screen-note">'+(state.focus?'FOCUS IS ACTIVE':'FOCUS IS OFF')+'</div></div>'+
'<div><div class="phone-footer">FOCUS CHANGES ATELIER STATE ONLY</div><button class="cmd-entry tactile" data-action="go" data-go="home"><b>‹</b>Return to Home</button></div>';
const command=()=>'<div><h3 class="phone-head">Command</h3><div class="phone-caption">ALLOWLIST / EXECUTE</div>'+
'<form id="commandForm"><input id="commandInput" class="phone-search" aria-label="Command input" placeholder="/focus on or /apps" autocomplete="off">'+
'<button class="cmd-entry tactile" type="submit"><b>›</b>Execute command</button></form>'+
'<div class="screen-note" style="margin-top:25px">/focus on · /focus off · /apps · /home</div></div>'+
'<div><div class="phone-footer">NO SYSTEM SHELL ACCESS</div><button class="cmd-entry tactile" data-action="go" data-go="home"><b>‹</b>Return to Home</button></div>';
function event(name,desc){$('eventName').textContent=name;$('eventDesc').textContent=desc;}
function setPreset(name){
 if(!presets[name])return;
 state.preset=name;
 const t=presets[name];
 document.body.dataset.preset=reduced()?'still':name;
 document.documentElement.style.setProperty('--press-scale',t.press);
 document.documentElement.style.setProperty('--state-ms',(reduced()?0:t.state)+'ms');
 document.documentElement.style.setProperty('--page-ms',(reduced()?0:t.page)+'ms');
 document.documentElement.style.setProperty('--release-ms',(reduced()?0:t.release)+'ms');
 document.documentElement.style.setProperty('--clock-ms',(reduced()?0:t.clock)+'ms');
 $('metrics').innerHTML='<dt>CONTACT</dt><dd>'+t.press.toFixed(3)+'×</dd><dt>STATE</dt><dd>'+t.state+'ms</dd><dt>NAVIGATION</dt><dd>'+t.page+'ms</dd><dt>REVEAL</dt><dd>'+t.clock+'ms</dd>';
 document.querySelectorAll('[data-preset]').forEach(b=>b.setAttribute('aria-pressed',String(b.dataset.preset===name)));
 render();
 if(reduced())stopAmbient();else scheduleAmbient();
 event('MOTION / '+name.toUpperCase(),reduced()?'Spatial movement disabled. Essential state information remains visible.':'Preset applied. Touch responses remain restrained and interruptible.');
}
function drawClock(){
 const stack=$('clockStack');if(!stack)return;
 stack.querySelectorAll('path').forEach(path=>{path.classList.remove('reveal');path.style.strokeDashoffset='';});
 if(reduced())return;
 requestAnimationFrame(()=>stack.querySelectorAll('path').forEach(path=>path.classList.add('reveal')));
}
function render(){
 $('stageCaption').textContent=state.page.toUpperCase()+' / '+(state.focus?'FOCUS':'NORMAL');
 $('phoneContent').innerHTML=({home,library,focus,command}[state.page]||home)();
 $('phoneStatusTime').textContent=timeNow().time;
 const panel=$('phoneContent');
 panel.getAnimations().forEach(animation=>animation.cancel());
 if(!reduced() && typeof panel.animate==='function') {
  panel.animate(
   [{opacity:0.1,transform:'translateY(10px)'},{opacity:1,transform:'translateY(0px)'}],
   {duration:presets[state.preset].page,easing:'cubic-bezier(.2,.8,.2,1)'}
  );
 }
 if(state.page==='home'){
  const stack=$('clockStack');
  if(stack)stack.dataset.minute=timeNow().time;
  drawClock();
 }
 $('pinLabel').textContent=state.pinned?'UNPIN APPLICATION':'PIN APPLICATION';
 $('pinBar').classList.toggle('active',state.pinned);
 $('focusLabel').textContent=state.focus?'EXIT FOCUS':'ENTER FOCUS';
 $('ambientLabel').textContent=state.ambient?'PAUSE ATMOSPHERE':'ENABLE ATMOSPHERE';
 $('ambientSwitch').setAttribute('aria-pressed',String(state.ambient));
}
function navigate(next){
 if(!['home','library','focus','command'].includes(next))return;
 state.page=next;render();
 event('NAVIGATION / '+next.toUpperCase(),'Short positional entry with opacity. The previous surface is replaced; no ornamental overscroll.');
 if(next==='command')$('commandInput')?.focus();
}
function syncLiveState(){
 $('stageCaption').textContent=state.page.toUpperCase()+' / '+(state.focus?'FOCUS':'NORMAL');
 $('focusLabel').textContent=state.focus?'EXIT FOCUS':'ENTER FOCUS';
 $('pinLabel').textContent=state.pinned?'UNPIN APPLICATION':'PIN APPLICATION';
 $('pinBar').classList.toggle('active',state.pinned);
 document.querySelectorAll('.launcher-app[data-name=terminal]').forEach(b=>b.classList.toggle('selected',state.pinned));
 if(state.page==='home'){
  const note=$('phoneContent').querySelector('.screen-note');
  const statement=$('phoneContent').querySelector('.screen-work h3');
  if(note)note.textContent=state.focus?'FOCUS / ACTIVE':'WORKSPACE / READY';
  if(statement)statement.textContent=state.focus?'Room for the work.':'Everything in its place.';
 }
 if(state.page==='focus'){
  const focusTile=$('phoneContent').querySelector('[data-action=focus]');
  if(focusTile)focusTile.classList.toggle('active',state.focus);
  const note=$('phoneContent').querySelector('.screen-note');
  if(note)note.textContent=state.focus?'FOCUS IS ACTIVE':'FOCUS IS OFF';
 }
}
function act(action,arg){
 if(action==='press'){event('CONTACT / PRESSED','The surface compresses toward your finger, then returns without bounce. Hit area stays unchanged.');return;}
 if(action==='pin'){state.pinned=!state.pinned;syncLiveState();event('SELECTION / '+(state.pinned?'PINNED':'UNPINNED'),'One brass inlay communicates the new favorite state, without recoloring the whole icon.');return;}
 if(action==='focus'){state.focus=!state.focus;syncLiveState();event('STATE / '+(state.focus?'FOCUS ON':'FOCUS OFF'),'Material contrast and workspace copy respond to a real in-prototype Focus state.');return;}
 if(action==='navigate'){navigate(state.page==='home'?'library':'home');return;}
 if(action==='go'){navigate(arg);return;}
 if(action==='command'){navigate('command');return;}
 if(action==='clock'){state.page='home';render();event('PRECISION / DRAW','Original numerals trace once on request. They do not loop or spin.');return;}
 if(action==='ambient'){state.ambient=!state.ambient;render();if(state.ambient)scheduleAmbient();else stopAmbient();event('ATMOSPHERE / '+(state.ambient?'ON':'OFF'),'Canvas strands '+(state.ambient?'resume when visible':'stop updating')+'.');return;}
 if(action==='launch'){state.selected=arg;event('APP / '+String(arg).toUpperCase(),'Application launch is a browser simulation. No actual Android app was started.');}
}
document.addEventListener('click',e=>{
 const b=e.target.closest('button');if(!b)return;
 if(b.dataset.preset)return setPreset(b.dataset.preset);
 if(b.dataset.action)return act(b.dataset.action,b.dataset.go||b.dataset.name);
 if(b.id==='ambientSwitch')return act('ambient');
});
document.addEventListener('input',e=>{
 if(e.target.id==='appSearch'){const term=e.target.value.toLowerCase();$('phoneApps')?.querySelectorAll('.launcher-app').forEach(b=>b.hidden=!b.dataset.name.includes(term));}
});
document.addEventListener('submit',e=>{
 if(e.target.id!=='commandForm')return;
 e.preventDefault();
 const cmd=$('commandInput').value.trim().toLowerCase();
 if(cmd==='/apps')navigate('library');
 else if(cmd==='/home')navigate('home');
 else if(cmd==='/focus on'){if(!state.focus)act('focus');navigate('home');}
 else if(cmd==='/focus off'){if(state.focus)act('focus');navigate('home');}
 else event('COMMAND / REJECTED','Only declared demo commands execute. No arbitrary shell operations are supported.');
});
osReduced.addEventListener('change',()=>setPreset(state.preset));
const canvas=$('ambientCanvas');
const ctx=canvas?.getContext('2d');
let last=0,frameHandle=0;
function stopAmbient(){
 if(frameHandle)cancelAnimationFrame(frameHandle);
 frameHandle=0;
}
function scheduleAmbient(){
 if(!frameHandle&&ctx&&!document.hidden&&state.ambient&&!reduced())
  frameHandle=requestAnimationFrame(paint);
}
function paint(stamp){
 frameHandle=0;
 if(!ctx||document.hidden||!state.ambient||reduced())return;
 if(stamp-last<100){scheduleAmbient();return;}
 last=stamp;
 const box=canvas.getBoundingClientRect(),dpr=Math.min(window.devicePixelRatio||1,2);
 const w=Math.round(box.width*dpr),h=Math.round(box.height*dpr);
 if(canvas.width!==w||canvas.height!==h){canvas.width=w;canvas.height=h;}
 ctx.clearRect(0,0,w,h);
 for(let i=0;i<58;i++){
  const p=i/58,offset=Math.sin(stamp/16000+i*.035)*w*.002;
  ctx.strokeStyle='rgba(75,75,78,'+(i%11===0?'.17':'.055')+')';
  ctx.lineWidth=dpr*(i%11===0?1.05:.55);
  ctx.beginPath();
  ctx.moveTo(w*(.18+p*.27)+offset,h*1.04);
  ctx.bezierCurveTo(w*(.2+p*.22),h*.72,w*(.56+p*.13),h*.48,w*(.66+p*.12),h*.28);
  ctx.stroke();
 }
 ctx.strokeStyle='rgba(205,166,100,.16)';ctx.lineWidth=dpr*.8;ctx.beginPath();
 ctx.moveTo(w*.24,h*.97);ctx.bezierCurveTo(w*.38,h*.73,w*.64,h*.53,w*.73,h*.3);ctx.stroke();
 scheduleAmbient();
}
document.addEventListener('visibilitychange',()=>document.hidden?stopAmbient():(scheduleAmbient(),refreshClock()));
let lastMinute='';
function refreshClock(){
 if(document.hidden)return;
 const moment=timeNow();
 $('phoneStatusTime').textContent=moment.time;
 if(state.page==='home'){
  const stack=$('clockStack');
  if(stack&&stack.dataset.minute!==moment.time){
   stack.innerHTML=digitRows(moment.digits);
   stack.dataset.minute=moment.time;
   drawClock();
  }
  const label=$('phoneContent').querySelector('.screen-date');
  if(label)label.textContent=moment.date;
 }
 lastMinute=moment.time;
}
setInterval(refreshClock,15000);
Promise.all([fetch('../icons/catalog.json').then(x=>x.json()),fetch('./tokens.json').then(x=>x.json())]).then(([data,tokens])=>{
 if(!data||!Array.isArray(data.icons))throw Error('No icon catalog');
 if(!tokens||tokens.schema!=='atelier.motion.v1')throw Error('No motion tokens');
 presets.signature.press=tokens.press.downScale;
 presets.signature.state=tokens.durationsMs.state;
 presets.signature.page=tokens.durationsMs.page;
 presets.signature.clock=tokens.durationsMs.clockDraw;
 symbols=Object.fromEntries(data.icons.map(x=>[x.name,x.d]));
 document.querySelectorAll('[data-symbol]').forEach(node=>node.innerHTML=icon(node.dataset.symbol));
 setPreset('signature');
 scheduleAmbient();
}).catch(()=>{
 event('STUDIO / DATA UNAVAILABLE','Start a local HTTP server or open the deployed site. Icons cannot load directly from file://.');
});
})();
