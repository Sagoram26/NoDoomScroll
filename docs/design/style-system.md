# NoDoomScroll — Style System (source de vérité)

Extrait du design produit après itérations. À coller dans chaque nouveau DC.

---

## 1. Variables CSS — Thèmes

### Light mode `.light`
```css
.light {
  --bg-base:    #F4FAF6;
  --g1:         #F6FBF8;   /* gradient fond top */
  --g2:         #EEF7F1;   /* gradient fond mid */
  --g3:         #E3F1E9;   /* gradient fond bottom */

  /* Glass surfaces */
  --glass:      rgba(255,255,255,0.28);
  --gborder:    rgba(255,255,255,0.6);   /* (unused — remplacé par bgtop/bgbot) */
  --ghi:        rgba(255,255,255,0.80);  /* inset rim top */
  --sheen:      rgba(255,255,255,0.38);  /* sheen gradient top */
  --bgtop:      rgba(255,255,255,0.95);  /* gradient border top */
  --bgbot:      rgba(255,255,255,0.50);  /* gradient border bottom */
  --bw:         1.5px;
  --glow-border: 0 0 8px rgba(255,255,255,0.25);
  --reflet:     inset 0 1px 1.5px rgba(255,255,255,0.60);

  /* Couleurs */
  --accent:     #5E8C72;
  --accent2:    #4A7560;
  --accentsoft: #A8C9B5;
  --onacc:      #FFFFFF;    /* texte sur bouton accent */
  --tp:         #22302A;    /* text primary */
  --ts:         #5A6B62;    /* text secondary */
  --tt:         #8A9890;    /* text tertiary */
  --blocked:    #9A8A7E;
  --warn:       #C99A5B;

  /* Ombres + effets */
  --gshadow:    0 10px 34px rgba(94,140,114,0.10);
  --blur:       50px;
  --haloblur:   28px;       /* halo streak */
  --glow:       rgba(94,140,114,0.32);   /* halo streak color */

  /* Blobs background */
  --blobA:      rgba(168,201,181,0.5);
  --blobB:      rgba(200,230,212,0.6);
  --blobC:      rgba(255,255,255,0.7);

  /* Caustics */
  --clight:     rgba(88,178,130,0.52);   /* spots de lumière verte */

  /* Emphasis box (Total) */
  --emphbg:     rgba(168,201,181,0.22);
}
```

### Dark mode `.dark`
```css
.dark {
  --bg-base:    #070C09;    /* quasi-noir sage */
  --g1:         #090F0B;
  --g2:         #0C1611;
  --g3:         #101F17;

  /* Glass surfaces */
  --glass:      rgba(42,60,51,0.32);
  --gborder:    rgba(180,224,200,0.55);
  --ghi:        rgba(205,255,224,0.55);
  --sheen:      rgba(200,245,222,0.18);
  --bgtop:      rgba(200,230,212,0.65);
  --bgbot:      rgba(156,196,172,0.30);
  --bw:         2px;
  --glow-border: 0 0 12px rgba(200,230,212,0.15);
  --reflet:     inset 0 1px 2px rgba(220,255,230,0.20);

  /* Couleurs */
  --accent:     #9CC4AC;
  --accent2:    #B4D6C2;
  --accentsoft: #3A5446;
  --onacc:      #0F1712;
  --tp:         #EAF2ED;
  --ts:         #A7B8AE;
  --tt:         #6E7E75;
  --blocked:    #8A7D72;
  --warn:       #D4B483;

  --gshadow:    0 32px 84px rgba(0,0,0,0.66);
  --blur:       58px;
  --haloblur:   50px;
  --glow:       rgba(156,200,174,0.88);

  --blobA:      rgba(64,150,108,0.6);
  --blobB:      rgba(110,190,150,0.42);
  --blobC:      rgba(38,100,74,0.62);

  --clight:     rgba(178,242,208,0.28);
  --emphbg:     rgba(58,84,70,0.4);
}
```

---

## 2. Recette Glassmorphism (classe `.glass`)

**Technique gradient border** — supporte border-radius et `border-radius` arrondi.

```css
.glass {
  background:
    linear-gradient(180deg, var(--sheen), transparent 44%) padding-box,
    var(--glass) padding-box,
    linear-gradient(180deg, var(--bgtop), var(--bgbot)) border-box;
  backdrop-filter: blur(var(--blur)) saturate(200%);
  -webkit-backdrop-filter: blur(var(--blur)) saturate(200%);
  border: var(--bw) solid transparent;
  box-shadow:
    var(--gshadow),         /* ombre extérieure atmosphérique */
    var(--glow-border),     /* glow subtil autour */
    var(--reflet),          /* reflet inset interne */
    inset 0 1px 0 var(--ghi),              /* rim supérieur */
    inset 0 -14px 32px rgba(255,255,255,0.05); /* lueur inférieure */
}
```

**3 couches de bordure** :
1. Gradient (top → bottom) via `border-box` background
2. Outer glow via box-shadow
3. Inset reflet via box-shadow inset

---

## 3. Fond animé

```css
/* Base gradient */
.bg {
  position: absolute; inset: 0; z-index: 0;
  background: linear-gradient(160deg, var(--g1), var(--g2) 55%, var(--g3));
}

/* Blobs flottants */
.blob { position: absolute; border-radius: 50%; filter: blur(42px); }
.blob.a { width:360px; height:360px; left:-110px; top:10px;
  background: radial-gradient(circle, var(--blobA), transparent 66%);
  animation: drift1 16s ease-in-out infinite; }
.blob.b { width:320px; height:320px; right:-120px; top:290px;
  background: radial-gradient(circle, var(--blobB), transparent 68%);
  animation: drift2 19s ease-in-out infinite; }
.blob.c { width:300px; height:300px; left:30px; bottom:-90px;
  background: radial-gradient(circle, var(--blobC), transparent 70%);
  animation: drift1 23s ease-in-out infinite; }

/* Caustics (positions randomisées via componentDidMount JS) */
.caustic {
  position: absolute; border-radius: 50%; pointer-events: none;
  background: radial-gradient(circle, var(--clight), transparent 68%);
}
/* 5 instances .caustic.a/b/c/d/e avec animations cf1/cf2/cf3 */
```

---

## 4. Animations

```css
/* Drift des blobs */
@keyframes drift1 { 0%,100%{transform:translate(0,0)} 50%{transform:translate(70px,55px)} }
@keyframes drift2 { 0%,100%{transform:translate(0,0)} 50%{transform:translate(-60px,-45px)} }

/* Caustics (lumière d'eau) */
@keyframes cf1 {
  0%,100%{transform:translate(0,0) scale(1) rotate(0deg)}
  33%{transform:translate(22px,-16px) scale(1.18) rotate(14deg)}
  66%{transform:translate(-14px,22px) scale(.88) rotate(-9deg)} }
@keyframes cf2 {
  0%,100%{transform:translate(0,0) scale(1)}
  40%{transform:translate(-20px,14px) scale(1.22) rotate(6deg)}
  70%{transform:translate(16px,-11px) scale(.84) rotate(-11deg)} }
@keyframes cf3 {
  0%,100%{transform:translate(0,0) scale(1) rotate(0deg)}
  50%{transform:translate(12px,24px) scale(1.12) rotate(22deg)} }

/* Streak breathing */
@keyframes breathe { 0%,100%{transform:scale(1)} 50%{transform:scale(1.035)} }
@keyframes halopulse {
  0%,100%{opacity:.5; transform:scale(.95)}
  50%{opacity:1; transform:scale(1.1)} }

/* Reduced motion */
.still .streakwrap, .still .halo, .still .blob, .still .caustic { animation: none; }
```

### Durées et courbes
| Token | Valeur | Usage |
|---|---|---|
| `dur-fast` | 200ms | Micro-interactions, taps |
| `dur-base` | 350ms | Transitions standard |
| `dur-slow` | 600ms | Transitions d'écran |
| `dur-breathe` | 4000ms | Streak breathing |
| `ease-spring` | `cubic-bezier(0.34,1.56,0.64,1)` | Rebond boutons |
| `ease-breathe` | `cubic-bezier(0.4,0,0.6,1)` | Breathing |
| `ease-out-soft` | `cubic-bezier(0.22,1,0.36,1)` | Apparitions |

---

## 5. Typographie

| Token | Font | Taille | Poids | Usage |
|---|---|---|---|---|
| `display-xl` | Plus Jakarta Sans | 86px | 800 | Chiffre streak |
| `display-lg` | Plus Jakarta Sans | 44px | 700 | Grands chiffres stats |
| `title-lg` | Plus Jakarta Sans | 28px | 600 | Titre d'écran |
| `title-sm` | Plus Jakarta Sans | 18px | 600 | Titres section |
| `label` | Plus Jakarta Sans | 16px | 600 | Titres carte, labels |
| `acap` | Nunito Sans | 12px | 600 | UPPERCASE caption labels |
| `body-md` | Nunito Sans | 15px | 400 | Corps standard |
| `body-sm` | Nunito Sans | 13px | 400 | Texte secondaire |
| `caption` | Nunito Sans | 11-12px | 500-600 | Légendes |

**Import Google Fonts** :
```html
<link href="https://fonts.googleapis.com/css2?family=Plus+Jakarta+Sans:wght@600;700;800&family=Nunito+Sans:wght@400;600;700&display=swap" rel="stylesheet">
```

---

## 6. Border radius

| Token | Valeur | Usage |
|---|---|---|
| `radius-pill` | 999px | Boutons capsule, toggles, iconbtn |
| `radius-streak` | 74px | Conteneur streak |
| `radius-card` | 28px | Cartes standard (`.acard`) |
| `radius-phone` | 48px | Frame téléphone |
| `radius-xs` | 5-6px | Petits éléments (hcell, barres) |

---

## 7. Spacing

Base 4dp :
`space-1=4` `space-2=8` `space-3=12` `space-4=16` `space-5=20` `space-6=24` `space-8=32`

- Marges écran (horizontal) : `20px`
- Gap entre cartes : `14px`
- Padding interne carte `.acard` : `22px`
- Gap interne carte (éléments) : `12-15px`

---

## 8. Composants clés

### Téléphone container
```css
.phone {
  width:412px; height:892px; border-radius:48px;
  overflow:hidden; position:relative;
  background:var(--bg-base);
  box-shadow:0 40px 90px rgba(20,30,25,0.28);
  font-family:'Nunito Sans',sans-serif; color:var(--tp);
}
```

### Status bar
```css
.statusbar {
  height:46px; display:flex; align-items:center;
  justify-content:space-between; flex-shrink:0; padding:0 4px;
}
```

### Icônes navigation (top-right glass circles)
```css
.iconbtn {
  width:42px; height:42px; border-radius:999px;
  display:flex; align-items:center; justify-content:center;
  color:var(--ts); border:none; cursor:pointer; padding:0;
}
/* + classe .glass */
```

### Segmented switch
```css
.seg { display:flex; border-radius:999px; padding:4px; gap:3px; flex:1; }
.segbtn {
  flex:1; text-align:center; padding:8px 2px; border-radius:999px;
  font-size:12px; font-weight:700; font-family:'Plus Jakarta Sans',sans-serif;
  color:var(--ts); cursor:pointer; border:none; background:transparent;
  transition:all .25s cubic-bezier(.34,1.56,.64,1); white-space:nowrap;
}
.segbtn.on { background:var(--accent); color:var(--onacc); }
/* + classe .glass sur .seg */
```

### Streak (Home — élément signature)
```css
.streakwrap {
  position:relative; display:flex; align-items:center; justify-content:center;
  margin-top:26px; height:266px;
  animation: breathe 4s cubic-bezier(.4,0,.6,1) infinite;
}
.halo {
  position:absolute; width:384px; height:384px; border-radius:50%;
  background: radial-gradient(circle, var(--glow), transparent 60%);
  filter: blur(var(--haloblur));
  animation: halopulse 4s cubic-bezier(.4,0,.6,1) infinite;
}
.streak {
  width:228px; height:228px; border-radius:74px;
  display:flex; flex-direction:column; align-items:center; justify-content:center;
}
/* + classe .glass sur .streak */
```

### Bouton Mode libre (bas de Home)
```css
.modebtn {
  border-radius:30px; padding:15px 18px; margin-bottom:8px;
  display:flex; flex-direction:column; gap:12px; cursor:pointer;
}
/* + classe .glass */
/* États : .modebtn.off { opacity:.6; cursor:default; } */
```

### Progress bar
```css
.progrow { display:flex; align-items:center; gap:12px; margin-top:18px; }
.prog { flex:1; height:8px; border-radius:999px; background:var(--accentsoft); overflow:hidden; }
.progfill { height:100%; border-radius:999px;
  background: linear-gradient(90deg, var(--accent), var(--accent2)); }
```

### Emphasis box (Total all-time)
```css
.emph {
  border-radius:28px; padding:24px 20px; text-align:center;
  border:2px solid var(--accent); background:var(--emphbg); margin-top:14px;
}
.emphnum {
  font-family:'Plus Jakarta Sans',sans-serif;
  font-weight:800; font-size:34px; letter-spacing:-1.5px;
  color:var(--accent); line-height:1;
}
```

### Heatmap (activité par heure)
```css
.heatgrid { display:flex; flex-direction:column; gap:6px; margin-top:12px; }
.heatrow { display:grid; grid-template-columns:34px repeat(8,1fr); gap:5px; align-items:center; }
.heatlabel { font-size:11px; color:var(--tt); font-weight:600; }
.hcell { aspect-ratio:1; border-radius:5px; background:var(--accent); /* opacity via JS */ }
```

### Per-app bars
```css
.approw { display:flex; align-items:center; gap:12px; margin-top:15px; }
.appname { font-size:13px; width:78px; color:var(--tp); font-weight:600; flex-shrink:0; }
.appbar { flex:1; height:8px; border-radius:999px; background:var(--accentsoft); overflow:hidden; }
.appfill { height:100%; border-radius:999px;
  background: linear-gradient(90deg, var(--accent), var(--accent2)); }
.appcount { font-size:14px; font-weight:700; color:var(--ts); width:24px; text-align:right; }
```

### Trend SVG (courbe)
```html
<svg class="trendsvg" viewBox="0 0 300 90" preserveAspectRatio="none">
  <path d="{{ areaPath }}" fill="var(--accent)" fill-opacity="0.16"></path>
  <path d="{{ linePath }}" fill="none" stroke="var(--accent)"
        stroke-width="2.5" stroke-linejoin="round" stroke-linecap="round"></path>
</svg>
```
Paths calculés en JS via `renderVals()`.

---

## 9. Randomisation des caustics (componentDidMount)

```js
componentDidMount() {
  // Randomize caustic light spots
  const root = ReactDOM.findDOMNode(this);
  if (!root) return;
  root.querySelectorAll('.caustic').forEach(c => {
    c.style.top    = (Math.random() * 72).toFixed(1) + '%';
    c.style.left   = (Math.random() * 72).toFixed(1) + '%';
    c.style.width  = Math.round(70 + Math.random() * 170) + 'px';
    c.style.height = Math.round(70 + Math.random() * 170) + 'px';
    c.style.animationDuration = (5 + Math.random() * 9).toFixed(1) + 's';
    c.style.animationDelay    = '-' + (Math.random() * 12).toFixed(1) + 's';
  });
  // Hide native scrollbar
  const sc = root.querySelector('.ascroll');
  if (sc) { sc.style.scrollbarWidth = 'none'; sc.style.msOverflowStyle = 'none'; }
  if (!document.getElementById('_noscroll')) {
    const s = document.createElement('style');
    s.id = '_noscroll';
    s.textContent = '.ascroll::-webkit-scrollbar{display:none}';
    document.head.appendChild(s);
  }
}
```

---

## 10. Copy & ton

- Calme, direct, jamais moralisateur
- Pas de bullshit motivationnel, pas d'émojis
- Actif et concret : « Activer le mode libre » pas « Soumettre »
- Labels toujours en minuscules sauf `UPPERCASE caption` pour les étiquettes de stat
- Chiffres importants en `display-lg/xl`, couleur `--accent`
- Texte secondaire/contexte en `--ts` (text-secondary)

---

## 11. Dualité light / dark

| Aspect | Light | Dark |
|---|---|---|
| Fond | Near-white sage (#F4FAF6) | Near-black (#070C09) |
| Vibe | Minéral, cristallin, léger | Obscur, sophistiqué, luxe |
| Glass opacity | 0.28 | 0.32 |
| Bordure | Blanc translucide discret | Sauge lumineux visible (2px) |
| Shadow | Légère, presque imperceptible | Massive, noire, profonde |
| Halo streak | Discret | Ultra-lumineux |
| Caustics | Spots verts (#5E8C72) | Spots sage clair |
| Blur | 50px | 58px |

---

## 12. Iconographie

- Style : **line icons** arrondis, trait 2dp, terminaisons rondes.
- Set recommandé : **Phosphor Icons** (variante "regular" ou "light"). Alternative : Lucide.
- Taille standard : 24dp. Petite : 20dp. Grande (vide d'écran) : 48dp.
- Couleur : `--ts` (text-secondary) au repos, `--accent` si actif.

## 13. Grille & layout

- Largeur de contenu : pleine largeur moins marge `20px` de chaque côté.
- Colonne unique (mobile portrait).
- Safe areas respectées (status bar, navigation gesture).

---

*Généré automatiquement depuis les itérations design NoDoomScroll — juin 2026*
