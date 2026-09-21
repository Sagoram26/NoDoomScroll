# Widget — Home screen Android (2×2)

## Format

- **Taille** : 2×2 (standard Android)
- **Ratios supportés** : portrait et paysage
- **Refresh** : mis à jour en temps réel (ou au moins chaque minute)

---

## Éléments affichés

### 1. Streak counter (élément signature)

- **Affichage** : grand chiffre centré (le nombre de jours)
- **Label** : « jours » en-dessous du chiffre
- **Positionnement** : haut-centre du widget
- **Exemple** : 
  ```
  12
  jours
  ```

### 2. Temps économisé aujourd'hui

- **Format** : « X h YY » ou « XX min »
- **Label** : « économisé aujourd'hui »
- **Positionnement** : sous le streak
- **Exemple** :
  ```
  1h 45
  économisé aujourd'hui
  ```

### 3. Statut Mode libre

- **Format** : pastille / point de couleur + texte court
- **Affichage selon l'état** :
  - **Disponible** : point vert + « dispo »
  - **Épuisé** : point gris + « épuisé »
  - **Hors plage** : point orange + « hors plage »
- **Positionnement** : bas-droit du widget
- **Exemple** :
  ```
  ● dispo
  ```

---

## Layout exact (suggéré)

```
┌─────────────────┐
│                 │
│      12         │ ← streak (grand)
│     jours       │
│                 │
│   1h 45         │ ← temps économisé
│ économisé auj.  │
│                 │
│  ● dispo        │ ← statut mode libre (bas-droit)
│                 │
└─────────────────┘
```

---

## Comportements

### Tap sur le widget

- **Action** : ouvre l'app sur l'écran Home
- Navigation immédiate au Home screen

### Mise à jour

- **Fréquence** : actualisé chaque minute (ou plus fréquent si possible)
- **Real-time** : si le streak change (midnight), mise à jour immédiate
- **Offline** : affiche les dernières données si pas de connektivité

### Différentes configurations widget

- **Lanceur par défaut** : affichage standard des 3 éléments
- **Lanceur custom** : peut supporter affichage légèrement différent (mais garder les 3 éléments essentiels)

---

## Dark + Light modes

- **Light mode** : couleurs claires, fond translucide (si possible)
- **Dark mode** : couleurs sombres, fond très sombre (noir/vert-gris)
- **Switch auto** : suit le thème système

---

## État absent (intentionnel)

- **Pas de bouton Mode libre** : le widget est informatif seulement
- **Pas de graph** : trop petit pour être lisible
- **Pas de mini-stats** : garder simple et efficace

---

## Données critiques

Les données affichées doivent être **100% fiables et à jour** :
- Streak : depuis la dernière réinitialisation (ou depuis activation)
- Temps économisé : calculé en temps réel depuis le dernier reboot
- Statut mode libre : basé sur la config et l'heure actuelle

---

## Accessibilité

- **Content description** : « Streak: 12 jours, Temps économisé: 1h 45, Mode libre: disponible »
- Les éléments doivent être lisibles à la taille widget
