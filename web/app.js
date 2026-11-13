(() => {
  "use strict";
  const el = id => document.getElementById(id);
  const device = el("device"), viewRoot = el("view"), canvas = el("silkfield");
  const defaultState = {screen:"lock",material:"ink",focus:false,motion:true,wifi:true,bluetooth:false,torch:false,brightness:60};
  let state = {...defaultState};
  try { state = {...defaultState,...JSON.parse(localStorage.getItem("atelier-study") || "{}")}; }
  catch (_) {}
  if (state.material === "paper") state.material = "sable";
  const save = () => { try { localStorage.setItem("atelier-study",JSON.stringify(state)); } catch (_) {} };
  const paths = {
    home:"M4 11.5 12 4l8 7.5V21h-6v-6H10v6H4z",command:"M4 7l6 5-6 5M13 18h7",
    library:"M4 4h7v7H4zM13 4h7v7h-7zM4 13h7v7H4zM13 13h7v7h-7z",
    github:"M9 19c-5.5 2-6-5.5-4-9a9 9 0 0 1 14 0c2 3.5 1.5 11-4 9v-4c0-2 2-3 3-4-3 0-6-1-7-6-2 0-3-2-3-2v-3c2 0 3 1 4 2 3-1 6-1 9 0 2-2 4-2 4-2v3s-1 2-3 2c-1 5-4 6-7 6 1 1 3 2 3 4v4",
    folder:"M3 6h7l2 2h9v12H3z",camera:"M3 8h4l2-3h6l2 3h4v12H3zM12 11a4 4 0 1 0 0 8 4 4 0 0 0 0-8",
    chat:"M4 4h16v13H9l-5 4z",compass:"M12 2a10 10 0 1 0 0 20 10 10 0 0 0 0-20M16.5 7.5l-3 6-6 3 3-6z",
    focus:"M16 3a9 9 0 1 0 5 13A8 8 0 0 1 16 3z",settings:"M12 3v3M12 18v3M3 12h3M18 12h3M5.6 5.6l2.1 2.1M16.3 16.3l2.1 2.1M18.4 5.6l-2.1 2.1M7.7 16.3l-2.1 2.1M12 9a3 3 0 1 0 0 6 3 3 0 0 0 0-6",
    code:"M8 5L2 12l6 7M16 5l6 7-6 7M14 3l-4 18",book:"M3 5h7c2 0 2 1 2 2v14c0-2-1-2-3-2H3zM21 5h-7c-2 0-2 1-2 2v14c0-2 1-2 3-2h6z",
    calendar:"M4 5h16v16H4zM8 2v6M16 2v6M4 10h16M8 14h3M14 14h3",
    music:"M9 18V6l11-3v12M9 18a3 3 0 1 1-6 0 3 3 0 0 1 6 0M20 15a3 3 0 1 1-6 0 3 3 0 0 1 6 0",
    phone:"M7 3l4 4-2 3a16 16 0 0 0 5 5l3-2 4 4-3 4C11 22 2 13 3 6z",
    search:"M11 3a8 8 0 1 0 0 16 8 8 0 0 0 0-16M17 17l5 5",
    work:"M3 8h18v12H3zM8 8V5h8v3M3 13h18",plus:"M12 3v18M3 12h18",
    clock:"M12 2a10 10 0 1 0 0 20 10 10 0 0 0 0-20M12 6v6l4 3",
    lightning:"M13 2L4 13h7l-1 9 10-12h-7z"
  };
  const glyphs = {
    "0":"M22 4C40 4 39 25 39 38C39 59 37 72 22 72C6 72 5 58 5 38C5 20 6 4 22 4",
    "1":"M7 17L25 4V72M11 72H38","2":"M5 20C5 1 40 -2 39 22C39 37 23 45 6 72H40",
    "3":"M6 11C15 2 38 1 38 20C38 30 32 36 21 37C36 36 40 46 39 57C38 77 12 76 5 65",
    "4":"M33 72V4L5 51H41","5":"M39 5H8L5 35C32 22 41 41 39 57C37 78 11 75 5 63",
    "6":"M35 8C13 -4 5 25 5 45C5 67 14 73 24 72C49 70 42 34 24 35C15 35 10 40 5 46",
    "7":"M5 5H40L15 72","8":"M23 37C-1 38 2 3 22 4C42 4 46 36 23 37C-4 38 1 72 22 72C45 72 48 40 23 37",
    "9":"M39 31C39 6 30 4 21 4C-1 4 1 38 20 39C29 40 36 34 39 31C39 53 30 76 7 69"
  };
  const icon = name => '<svg viewBox="0 0 24 24" aria-hidden="true"><path d="'+(paths[name]||paths.plus)+'"/></svg>';
  const numeral = digit => '<svg viewBox="0 0 44 76" style="width:73px;height:135px" aria-label="'+digit+'"><path d="'+(glyphs[digit]||"")+'"/></svg>';
  const digits = value => '<div style="height:134px;display:flex;align-items:center">'+Array.from(value,numeral).join("")+'</div>';
  const now = () => new Date();
  const hhmm = () => now().toLocaleTimeString("en-GB",{hour:"2-digit",minute:"2-digit",hour12:false});
  const day = () => now().toLocaleDateString("en-GB",{weekday:"long",day:"2-digit",month:"short"}).toUpperCase();
  const dayLong = () => now().toLocaleDateString("en-GB",{weekday:"long",day:"numeric",month:"long"}).toUpperCase();
  const apps = [
    ["GitHub","github"],["Shell","command"],["Files","folder"],["Camera","camera"],
    ["Messages","chat"],["Browser","compass"],["Calendar","calendar"],["Projects","work"],
    ["Notes","book"],["Music","music"],["Phone","phone"],["Search","search"],
    ["Focus","focus"],["Settings","settings"],["Reader","book"],["Clock","clock"],
    ["Code","code"],["Library","library"],["Recorder","music"],["Gallery","camera"]
  ];
  const commands = [
    ["/focus on","Enter Focus","Reduce distractions"],["/focus off","Exit Focus","Restore default workspace"],
    ["/view library","Application library","Search the app catalog"],["/view control","Open controls","Preview home controls"],
    ["/material ink","Ink material","Restore near-black"],["/material sable","Sable material","Warm mineral black"],
    ["/material graphite","Graphite material","Soft mineral black"],["/motion off","Reduce motion","Stop ambient animation"],
    ["/motion on","Restore motion","Enable ambient silk strands"]
  ];
  function toast(message) {
    const t=el("toast");t.textContent=message;t.classList.add("show");
    clearTimeout(toast.timer);toast.timer=setTimeout(()=>t.classList.remove("show"),2300);
  }
  function title(name,subtitle) {
    return '<div class="inset"><h3>'+name+'</h3><div class="caption">'+subtitle+'</div></div>';
  }
  function renderLock() {
    const t=hhmm().replace(":","");
    return '<div class="lock"><div><div class="screen-eyebrow">ATELIER / 001</div><div class="bigclock">'+digits(t.slice(0,2))+digits(t.slice(2,4))+'</div><div class="gold-rule"></div><div class="dayline">'+day()+'</div><p class="caption" style="margin-top:10px">THE BLACK SYSTEM / NORD 5</p></div><button class="lock-bottom" data-action="unlock">SWIPE OR TAP TO ENTER</button></div>';
  }
  function appChip(entry) {
    return '<button class="appchip" data-launch="'+entry[0]+'"><span class="iconframe">'+icon(entry[1])+'</span><small>'+entry[0]+'</small></button>';
  }
  function renderHome() {
    return '<div class="home"><div><div class="screen-eyebrow">ATELIER / WORKSPACE</div><div class="home-clock">'+hhmm()+'</div><div class="gold-rule"></div><div class="dayline">'+dayLong()+'</div><p class="caption">'+(state.focus?'FOCUS / ACTIVE':'THE BLACK SYSTEM / READY')+'</p></div><div class="workspace"><div class="screen-eyebrow">TODAY / WORKSPACE</div><h4>'+(state.focus?'Make room for the work.':'Everything in its place.')+'</h4><div class="approw">'+apps.slice(0,4).map(appChip).join("")+'</div><div style="height:23px"></div><button class="command-bar" data-view="command"><b>›</b><span>Search, launch or command</span></button></div></div>';
  }
  function searchField(placeholder) {
    return '<input class="search" type="search" id="surfaceSearch" placeholder="'+placeholder+'" autocomplete="off" aria-label="Search">';
  }
  function renderLibrary() {
    return title("Library","ALL APPLICATIONS / SAMPLE CATALOG")+searchField("Search applications...")+'<div class="grid-apps" id="appGrid"></div><p class="note">Sample applications in this study. The Kotlin launcher discovers real installed apps through LauncherApps.</p>';
  }
  function renderCommand() {
    return title("Command","ACTIONS / SEARCH / LAUNCH")+searchField("/focus on or GitHub")+'<div class="command-list" id="commandList"></div><p class="note">Commands are intentionally allowlisted. This is a launcher command palette, not an unrestricted terminal.</p>';
  }
  function tile(label,kind,active) {
    const symbol = kind==="wifi"?"compass":kind==="bluetooth"?"plus":kind==="torch"?"lightning":"focus";
    return '<button class="tile '+(active?'active':'')+'" data-toggle="'+kind+'"><span class="iconframe">'+icon(symbol)+'</span><div><b>'+label+'</b><small>'+(active?'ACTIVE':'OFF / DEMO')+'</small></div></button>';
  }
  function renderControl() {
    return title("Control","SHELL CONTROLS / INTERACTIVE CONCEPT")+
      '<div class="tiles">'+tile("Internet","wifi",state.wifi)+tile("Bluetooth","bluetooth",state.bluetooth)+
      tile("Focus","focus",state.focus)+tile("Torch","torch",state.torch)+'</div>'+
      '<div class="brightness"><div class="label">BRIGHTNESS / SIMULATION</div><input type="range" id="brightness" min="10" max="100" value="'+state.brightness+'"></div>'+
      '<div class="hairline"></div><div class="label">NOTIFICATIONS</div><p class="note">All caught up.<br>Nothing needs your attention.</p>'+
      '<p class="note">Only Focus is retained as ATELIER study state. Connectivity, torch and brightness here are simulations, not system controls.</p>';
  }
  function setting(label,val,action) {
    return '<div class="setting-line">'+label+'<button data-action="'+action+'">'+val+' ↻</button></div>';
  }
  function renderSettings() {
    return title("Settings","ATELIER / SIGNATURE EDITION")+
      '<div class="settings-banner"><div class="sig">A<span>.</span></div><div><strong>THE BLACK SYSTEM</strong><small>ONEPLUS NORD 5 / CONCEPT</small></div></div>'+
      setting("Material",state.material.toUpperCase(),"material")+
      setting("Focus",state.focus?"ON":"OFF","focus")+
      setting("Ambient motion",state.motion?"ON":"OFF","motion")+
      '<div class="setting-line">Typography <span>HUBOT / SPLINE / DEPARTURE</span></div>'+
      '<div class="setting-line">Clock <span>ATELIER ORIGINAL / 01</span></div>'+
      '<div class="setting-line">Wallpaper <span>SILKFIELD / PROCEDURAL</span></div>'+
      '<p class="note">Only launcher-owned surfaces can be fully themed. OxygenOS retains the secure lock screen, AOD, biometric effects and notification shade.</p>';
  }
  const renderers = {lock:renderLock,home:renderHome,library:renderLibrary,command:renderCommand,control:renderControl,settings:renderSettings};
  function applyMaterial() {
    const palettes = {
      ink: {bg:"#080809",ink:"#F2F2EE",muted:"#9C9C9C",edge:"#313134",card:"#151516"},
      graphite: {bg:"#111315",ink:"#F2F2EE",muted:"#A4A7AA",edge:"#353A3D",card:"#1B1E21"},
      sable: {bg:"#100F0E",ink:"#F2F2EE",muted:"#A9A39C",edge:"#35302C",card:"#1B1917"}
    };
    const selected = palettes[state.material] || palettes.ink;
    device.dataset.material=state.material;
    device.style.setProperty("--dev-bg",selected.bg);
    device.style.setProperty("--dev-ink",selected.ink);
    device.style.setProperty("--dev-muted",selected.muted);
    device.style.setProperty("--dev-edge",selected.edge);
    device.style.setProperty("--dev-card",selected.card);
  }
  function fillSearch(q) {
    const query=q.trim().toLowerCase();
    if(state.screen==="library") {
      const filtered=apps.filter(a=>a[0].toLowerCase().includes(query));
      el("appGrid").innerHTML=filtered.length?filtered.map(a=>
        '<button data-launch="'+a[0]+'"><span class="iconframe">'+icon(a[1])+'</span><small>'+a[0]+'</small></button>'
      ).join(""):'<p class="empty">NO MATCHES FOUND</p>';
    } else if(state.screen==="command") {
      const filtered=commands.filter(a=>(a[0]+" "+a[1]).toLowerCase().includes(query));
      const appMatches=query.startsWith("/")?[]:apps.filter(a=>a[0].toLowerCase().includes(query)).slice(0,5);
      const actions=filtered.map(a=>'<button class="command-item" data-command="'+a[0]+'"><span><b>'+a[1]+'</b><small>'+a[2]+'</small></span><code>'+a[0]+'</code></button>');
      const matching=appMatches.map(a=>'<button class="command-item" data-launch="'+a[0]+'"><span><b>Open '+a[0]+'</b><small>Application / sample</small></span><code>↗</code></button>');
      el("commandList").innerHTML=[...actions,...matching].join("")||'<p class="empty">NO RESULTS</p>';
    }
  }
  function render() {
    if(!renderers[state.screen]) state.screen="lock";
    applyMaterial();
    viewRoot.innerHTML=renderers[state.screen]();
    el("statusTime").textContent=hhmm();
    const names={lock:"LOCK SCREEN",home:"HOME",library:"APPLICATION LIBRARY",command:"COMMAND PALETTE",control:"CONTROL",settings:"SETTINGS"};
    el("viewCaption").textContent=String(Object.keys(names).indexOf(state.screen)+1).padStart(2,"0")+" / "+names[state.screen];
    el("focusState").textContent=state.focus?"FOCUS":"OPEN";
    el("materialState").textContent=state.material.toUpperCase();
    el("motionState").textContent=state.motion?"ON":"OFF";
    el("motion").checked=state.motion;
    document.querySelectorAll("[data-view]").forEach(button => button.classList.toggle("active",button.dataset.view===state.screen));
    document.querySelectorAll("[data-material]").forEach(button => button.classList.toggle("selected",button.dataset.material===state.material));
    if(state.screen==="library"||state.screen==="command") fillSearch("");
    save();
  }
  function show(screen) {
    if(!renderers[screen])return;
    state.screen=screen;render();
    if(screen==="command"||screen==="library") setTimeout(()=>el("surfaceSearch")?.focus(),0);
  }
  function material(value) { if(["ink","graphite","sable"].includes(value)) {state.material=value;render();} }
  function launch(name) {
    if(name==="Settings")return show("settings");
    if(name==="Shell")return show("command");
    if(name==="Library"||name==="Search")return show("library");
    if(name==="Focus"){state.focus=!state.focus;return render();}
    toast("SAMPLE APP / "+name+"  ·  Android launch is native-only");
  }
  function execute(input) {
    const q=input.trim().toLowerCase();
    if(q==="/focus on"){state.focus=true;return show("home");}
    if(q==="/focus off"){state.focus=false;return show("home");}
    if(q==="/motion on"){state.motion=true;return render();}
    if(q==="/motion off"){state.motion=false;return render();}
    if(q.startsWith("/view "))return show(q.slice(6));
    if(q.startsWith("/material "))return material(q.slice(10));
    if(q.startsWith("/"))return toast("UNKNOWN COMMAND / NOT EXECUTED");
    const found=apps.find(a=>a[0].toLowerCase()===q)||apps.find(a=>a[0].toLowerCase().includes(q));
    if(found)return launch(found[0]);
    toast("NO MATCHING APPLICATION");
  }
  document.addEventListener("click", event => {
    const button=event.target.closest("button");
    if(!button)return;
    if(button.dataset.view)return show(button.dataset.view);
    if(button.dataset.material)return material(button.dataset.material);
    if(button.dataset.launch)return launch(button.dataset.launch);
    if(button.dataset.command)return execute(button.dataset.command);
    if(button.dataset.toggle){
      const key=button.dataset.toggle;
      if(key in state){state[key]=!state[key];render();if(key!=="focus")toast(key.toUpperCase()+" / PREVIEW ONLY");}
    }
    if(button.dataset.action==="unlock")return show("home");
    if(button.dataset.action==="material")return material({ink:"graphite",graphite:"sable",sable:"ink"}[state.material]);
    if(button.dataset.action==="focus"){state.focus=!state.focus;render();}
    if(button.dataset.action==="motion"){state.motion=!state.motion;render();}
  });
  document.addEventListener("input", event => {
    if(event.target.id==="surfaceSearch") fillSearch(event.target.value);
    if(event.target.id==="brightness"){state.brightness=Number(event.target.value);save();}
  });
  document.addEventListener("keydown",event => {
    if(event.key==="Escape"){show("home");return;}
    if(event.key==="/"&&!["INPUT","TEXTAREA"].includes(document.activeElement.tagName)){
      event.preventDefault();show("command");return;
    }
    if(event.key==="Enter"&&document.activeElement.id==="surfaceSearch"&&state.screen==="command") execute(document.activeElement.value);
  });
  el("homeGesture").addEventListener("click",()=>show("home"));
  el("reset").addEventListener("click",()=>{state={...defaultState};render();});
  el("motion").addEventListener("change",e=>{state.motion=e.target.checked;render();});
  let startY=0;
  device.addEventListener("touchstart",e=>{startY=e.touches[0].clientY;},{passive:true});
  device.addEventListener("touchend",e=>{
    const dy=e.changedTouches[0].clientY-startY;
    if(state.screen==="lock"&&dy < -75)show("home");
    if(state.screen==="home"&&dy < -75)show("library");
    if(state.screen==="home"&&dy > 75)show("control");
  },{passive:true});
  // Procedural atmosphere: real line geometry, not a video or animated GIF.
  // Render only when the tab is visible and motion is enabled.
  const ctx=canvas.getContext("2d");
  let lastFrame=0;
  function paintFrame(stamp) {
    requestAnimationFrame(paintFrame);
    if(document.hidden||!ctx||!state.motion||window.matchMedia("(prefers-reduced-motion: reduce)").matches)return;
    if(stamp-lastFrame<67)return;
    lastFrame=stamp;
    const rect=canvas.getBoundingClientRect();
    const width=Math.round(rect.width*devicePixelRatio), height=Math.round(rect.height*devicePixelRatio);
    if(canvas.width!==width||canvas.height!==height){canvas.width=width;canvas.height=height;}
    ctx.clearRect(0,0,width,height);
    ctx.lineWidth=1*devicePixelRatio;
    for(let i=0;i<80;i++){
      const p=i/80, drift=Math.sin(stamp/12500+i*.031)*width*.003;
      ctx.beginPath();
      ctx.moveTo(width*(.19+p*.30)+drift,height*1.02);
      ctx.bezierCurveTo(width*(.20+p*.23)+drift,height*.70,width*(.55+p*.13)+drift,height*.49,width*(.54+p*.15)+drift,height*.28);
      const gray=20+(i%11)*3;
      ctx.strokeStyle="rgba("+gray+","+gray+","+(gray+2)+","+(i%13===0?.18:.07)+")";
      ctx.stroke();
    }
    ctx.strokeStyle="rgba(205,166,100,.14)";
    ctx.beginPath();
    ctx.moveTo(width*.27,height*.91);
    ctx.bezierCurveTo(width*.40,height*.76,width*.65,height*.58,width*.68,height*.30);
    ctx.stroke();
  }
  requestAnimationFrame(paintFrame);
  setInterval(()=>{el("statusTime").textContent=hhmm();if(state.screen==="lock"||state.screen==="home")render();},30_000);
  render();
})();
