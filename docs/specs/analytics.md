# NoDoomScroll — Spec page ANALYTICS (complète)

Spec détaillée de la page Analytics avec les deux nouvelles sections : temps gagné mois + temps gagné total.

---

## Layout général

Page scrollable, colonne unique, marges 20dp. Fond gradient sauge/menthe animé (même que Home).

**Ordre de scroll** :
1. Header + switch période
2. Vue du jour OU semaine (selon toggle)
3. **[NOUVEAU] Temps gagné ce mois**
4. **[NOUVEAU] Temps gagné total (all-time)**
5. Heatmap (semaine)
6. Courbe tendance
7. Historique contournement (repliable)

---

## A. EN-TÊTE + PÉRIODE

```
┌─────────────────────────────┐
│ [gradient fond]              │
│                              │
│ Tes stats                    │
│ (title-lg, text-primary)     │
│                              │
│ [Jour] [Semaine] [Mois]      │
│  (segments glass, toggle)    │
└─────────────────────────────┘
```

- **Titre** `title-lg` : « Tes stats »
- **Toggle période** : 3 segments glass (Jour / Semaine / Mois)
  - Light mode : blanc translucide, segment sélectionné en accent sauge
  - Dark mode : bordure sauge clair, segment sélectionné en accent lumineux
- **Spacing** : space-8 en-dessous

---

## B. VUE JOUR (par défaut)

**Affichage en parallèle si mode semaine/mois sélectionné, reste visible sinon.**

### Carte 1 : Temps économisé aujourd'hui

```
┌─────────────────────────────┐
│ [glass surface]              │
│                              │
│ Temps économisé aujourd'hui  │
│ (caption, text-secondary)    │
│                              │
│ 1h 45                        │
│ (display-lg, accent)         │
│                              │
│ +12% vs hier                 │
│ (body-sm, accent color)      │
└─────────────────────────────┘
```

- Carte glass (radius-card)
- Label : `caption`, `text-secondary`
- Chiffre : `display-lg`, color `accent`
- Comparaison : `body-sm`, color `accent` (mieux), ou `text-secondary` (pareil/pire)

### Carte 2 : Tentatives bloquées aujourd'hui

```
┌─────────────────────────────┐
│ [glass surface]              │
│                              │
│ Tentatives bloquées          │
│ (caption, text-secondary)    │
│                              │
│ 12                           │
│ (display-lg, accent)         │
│                              │
│ ┌──────────────────────┐     │
│ │ Instagram   : 5      │     │ ← mini-chart
│ │ YouTube     : 4      │
│ │ Snapchat    : 3      │
│ └──────────────────────┘
└─────────────────────────────┘
```

- Carte glass (radius-card)
- Total tentatives : `display-lg`, `accent`
- Breakdown par app : liste discrète (body-sm, `text-secondary`) avec petite barre horizontale en gradient sauge pour chaque app
- Spacing : space-4 entre les apps

---

## C. VUE SEMAINE

Visible si toggle "Semaine" sélectionné.

### Carte 1 : Temps économisé cette semaine

```
┌─────────────────────────────┐
│ [glass surface]              │
│                              │
│ Temps économisé cette sem.   │
│ (caption)                    │
│                              │
│ 10h 30                       │
│ (display-lg, accent)         │
│                              │
│ ↑ 18% vs semaine précédente  │ ← comparaison colorée
│ (body-sm, accent ou warning) │
└─────────────────────────────┘
```

- Chiffre `display-lg` : temps total
- Comparaison : flèche ↑/↓ + % + couleur (accent si mieux, warning si pire)

### Carte 2 : Tendance graphique (courbe)

```
┌─────────────────────────────┐
│ [glass surface]              │
│                              │
│ Tendance - 7 jours           │ ← caption
│                              │
│     ╱ ╲                       │
│    ╱   ╲____                 │ ← courbe douce
│   ╱         ╲ (gradient sous) │
│  ╱           ╲               │
│ Lun  Mar  Mer ... Dim        │
│                              │
│ Moyenne: 1h 30               │ ← sous la courbe
└─────────────────────────────┘
```

- Courbe douce en accent sauge
- Remplissage sous la courbe en gradient sauge translucide
- Axe X : jours (Lun, Mar, etc.)
- Label moyen en bas : `body-sm`, `text-secondary`

---

## D. [NOUVEAU] TEMPS GAGNÉ CE MOIS

**Positionné après la vue jour/semaine, avant la heatmap.**

Carte glass (pleine largeur, radius-card).

```
┌─────────────────────────────┐
│ [glass surface]              │
│                              │
│ Temps gagné ce mois          │
│ (caption, text-secondary)    │
│                              │
│ 45 h 30                      │
│ (display-lg, accent)         │
│                              │
│ Temps d'écran dépensé        │
│ (caption, text-secondary)    │
│                              │
│ vs. sans NoDoomScroll        │
│ (body-sm, text-tertiary)     │
│                              │
│ [═══════════════▓▓▓] 72%     │ ← progress bar
│ Réduction ce mois            │
│ (caption)                    │
└─────────────────────────────┘
```

**Détail des éléments** :

1. **Label principal** : « Temps gagné ce mois » (`caption`, `text-secondary`)
2. **Chiffre central** : « 45 h 30 » (`display-lg`, color `accent`) — temps économisé depuis le début du mois
3. **Sous-texte** : « Temps d'écran dépensé vs. sans NoDoomScroll » (`body-sm`, `text-tertiary`)
4. **Barre de progression** : 
   - Background : glass-surface translucide
   - Remplissage : gradient sauge (accent clair → accent foncé)
   - Hauteur : 8dp, radius-pill
   - À droite du remplissage : pourcentage « 72% » (`body-sm`, `text-primary`)
   - Label sous la barre : « Réduction ce mois » (`caption`)

**Spacing** : space-6 entre chaque élément vertical

---

## E. [NOUVEAU] TEMPS GAGNÉ TOTAL

**Positionné après la section "Temps gagné ce mois", avant la heatmap.**

Carte glass grande (pleine largeur, radius-lg).

```
┌─────────────────────────────────────┐
│ [glass surface]                      │
│                                      │
│ Temps gagné total (all-time)         │ ← caption
│ (caption, text-secondary)            │
│                                      │
│ ╔════════════════════════════════╗   │
│ ║                                ║   │
│ ║       187 j 16 h 45            ║   │ ← emphasis box
│ ║    (depuis activation)         ║   │
│ ║                                ║   │
│ ╚════════════════════════════════╝   │
│                                      │
│ Données depuis : 23 juin 2024        │
│ (caption, text-tertiary)             │
│                                      │
│ Jours sans scroll consécutifs : 12   │
│ (body-md, accent)                    │
│                                      │
│ Sessions mode libre utilisées : 8    │
│ (body-md, text-secondary)            │
│                                      │
│ Taux de blocage moyen : 94%          │
│ (body-md, text-secondary)            │
└─────────────────────────────────────┘
```

**Détail des éléments** :

1. **Label principal** : « Temps gagné total (all-time) » (`caption`)
2. **Emphasis box** (verre dépoli à bordure accent) :
   - Chiffre énorme : « 187 j 16 h 45 » (`display-xl`, accent color) — le temps **total** depuis activation
   - Sous-texte : « (depuis activation) » (`body-md`, `text-secondary`)
   - Cette box pop visuellement — c'est LE stat mémorable
3. **Date d'activation** : « Données depuis : 23 juin 2024 » (`caption`, `text-tertiary`)
4. **Stats contextuelles** (3 lignes) :
   - « Jours sans scroll consécutifs : 12 » (`body-md`, color `accent`)
   - « Sessions mode libre utilisées : 8 » (`body-md`, `text-secondary`)
   - « Taux de blocage moyen : 94% » (`body-md`, `text-secondary`)

**Spacing** : space-6 entre les lignes, space-8 entre les sections

---

## F. HEATMAP (semaine)

```
┌─────────────────────────────┐
│ [glass surface]              │
│                              │
│ Activité par heure           │ ← caption
│ (caption)                    │
│                              │
│ Lun │ ░░░░░░░░░░             │
│ Mar │ ░░░▓▓▓░░░░ (intensité) │
│ Mer │ ░▓▓▓▓▓▓░░░             │
│ ... │ ...                     │
│                              │
│ Moins          Plus          │
│ (legend)                     │
└─────────────────────────────┘
```

- Grille 7 jours × 24h (ou par tranches de 3h)
- Cellules carrées arrondies (radius-xs)
- Dégradé : clair (peu d'activité) → accent sauge foncé (beaucoup)
- Legend en bas : « Moins » et « Plus »

---

## G. COURBE DE TENDANCE (30 jours)

```
┌─────────────────────────────┐
│ [glass surface]              │
│                              │
│ Tendance - 30 jours          │ ← caption
│                              │
│        ╱╲      ╱╲ ╱          │
│       ╱  ╲    ╱  ╲╱           │ ← courbe
│      ╱    ╲  ╱                │
│     ╱      ╲╱                 │
│                              │
│ Moyenne: 8h 15 / jour        │
│ Pic: 14h 30 (jour X)         │ ← stats courbe
│ Creux: 2h 15 (jour Y)        │
└─────────────────────────────┘
```

- Courbe lissée en sauge
- Remplissage gradient sauge translucide sous
- Points de données légers sur la courbe (quasi invisibles)
- Stats descriptives en bas

---

## H. HISTORIQUE CONTOURNEMENT (repliable)

```
┌─────────────────────────────┐
│ ▼ Tentatives de contournement │
│   (title-sm, couleur text)   │
│                              │
│ ┌─────────────────────────┐  │
│ │ Force stop · sam. 14h30  │  │
│ │ Désactivation VPN · lun. │  │
│ │ Tap répété · mer. 9h15  │  │
│ └─────────────────────────┘  │
└─────────────────────────────┘
```

- Section repliable (collapse/expand)
- Label : « Tentatives de contournement » (`title-sm`)
- Liste :  date + heure + action (body-sm, `text-secondary`)
- Ton neutre, pas culpabilisant
- Max 10 dernières tentatives affichées

---

## États et variations

- **Jour sans données** : message discret « Pas encore de données pour aujourd'hui »
- **Premier mois** : temps gagné ce mois = temps gagné total (affichages compatibles)
- **Mode réduit motion** : courbes statiques (pas d'animation), heatmap pas de gradient animé

---

## Cohérence visuelle

- Toutes les cartes : **surface glass** avec bordures fusionnées (gradient + glow + reflet interne)
- Tous les chiffres importants : **accent sauge color**
- Tous les contextes/secondaire : **text-secondary**
- Spacing vertical : **space-8 entre sections majeures**, space-6 entre éléments mineurs
- Rayon : **radius-card (28dp) pour les cartes**, radius-xs (12dp) pour les petits éléments
- Animations : courbes douce (ease-out), 350ms–600ms selon importance

---

C'est cohérent avec le Home et clairement lisible ?
