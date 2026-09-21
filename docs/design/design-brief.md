# NoDoomScroll — Brief de design

**Type** : Application Android personnelle
**Format livrable** : Maquettes Figma (via Claude Design)
**Date** : 2026-06-25

---

## Le produit en une phrase

NoDoomScroll neutralise le scroll infini (feed, Reels, Shorts) sans casser l'usage utile des apps. Ce n'est pas un bloqueur punitif : c'est un outil calme qui rend ton téléphone fonctionnel sans le rendre addictif.

## L'utilisateur

Toi. Quelqu'un de conscient de sa dépendance au scroll, qui ne cherche pas à se faire culpabiliser mais à reprendre le contrôle sereinement. L'app doit donner une sensation de **maîtrise apaisée**, pas de combat.

---

## Direction artistique

### Le mood en trois mots
**Calme. Épuré. Premium.**

L'app doit respirer. Beaucoup d'espace négatif, peu d'éléments à l'écran, aucune agressivité visuelle. À l'opposé exact des apps qu'elle bloque — là où TikTok sature et stimule, NoDoomScroll apaise et ralentit.

### Le langage visuel : liquid glass sur fond sauge

Le cœur de l'identité, c'est le **glassmorphism** façon liquid glass d'Apple : des surfaces translucides, du flou d'arrière-plan, des reflets subtils, une impression de profondeur et de matière. Les cartes, boutons et barres flottent comme du verre dépoli au-dessus d'un dégradé doux.

Le dégradé de fond est en **vert sauge / menthe** — une teinte zen, naturelle, qui évoque le calme et le végétal. Jamais criard, toujours feutré. Le gradient bouge très lentement en arrière-plan (ambient), comme une surface d'eau.

### Formes
Tout est **très arrondi**. Coins généreux, organiques, doux. Aucun angle dur. Les cartes ressemblent à des galets polis. Les boutons sont des capsules douces.

### Typographie
Sans-serif **humaniste**, chaleureuse et hautement lisible. Rien de froid ou de corporate.
- **Plus Jakarta Sans** pour les titres et les grands chiffres (le streak, les stats) — du caractère, du premium.
- **Nunito Sans** pour le corps de texte — douceur, rondeur, confort de lecture.

### Mouvement
Animations **fluides et riches**, mais jamais frénétiques. Tout glisse, fond en douceur, répond au toucher avec souplesse. Les transitions entre écrans sont lentes et continues. Les micro-interactions (tap d'un bouton, ouverture d'une carte) ont un léger rebond élastique. Le mouvement doit renforcer le calme, pas le casser.

### Dark + Light
Les deux modes, switch automatique selon le système.
- **Light** : fond sauge très clair, presque blanc, surfaces de verre lumineuses.
- **Dark** : fond vert-gris profond (jamais noir pur), surfaces de verre sombres et translucides, accents sauge plus lumineux.

---

## L'élément signature : le streak qui respire

Le **compteur de streak** est le héros de l'app. Il s'affiche au centre du home comme un objet en verre dépoli, entouré d'un halo de gradient sauge qui **pulse très lentement** — une animation de respiration (inspiration / expiration sur ~4 secondes). Ce détail unique lie tout : le concept (discipline calme), le visuel (glass + gradient), le mouvement (respiration zen). C'est ce dont on se souvient en ouvrant l'app.

Tout le reste de l'interface reste discret et silencieux autour de ce point focal.

---

## Principes de copy (texte de l'interface)

- **Ton** : calme, direct, jamais moralisateur. On parle à un adulte, pas à un enfant qu'on culpabilise.
- **Pas de bullshit motivationnel** : pas de "Tu peux le faire !", pas d'émojis enthousiastes. Juste des faits posés.
- **Voix de l'app, pas d'une personne** : les blocages expliquent ce qui se passe ("Contenu bloqué"), ils ne s'excusent pas et ne font pas la morale.
- **Actif et concret** : "Activer le mode libre", pas "Soumettre". Le bouton dit exactement ce qu'il fait.
- **Les écrans vides invitent à l'action**, ils ne meublent pas avec du décor.

Exemple de ton pour l'overlay de blocage :
> **Contenu bloqué**
> Tu as choisi de ne pas voir ça.
> [Retour]   [Mode libre]

Pas :
> ~~Oups ! 🙈 On dirait que tu essaies de scroller ! Reste fort, tu gères ! 💪~~

---

## Écrans à designer

1. **Onboarding** (7 écrans) — Bienvenue, Permissions, Apps cibles, Mode libre, Temps estimé, Rapport hebdo, Confirmation
2. **Home** — streak central qui respire, stats du jour, bouton mode libre
3. **Analytics** — dashboard quotidien + hebdo, heatmap
4. **Apps bloquées** — liste avec statuts, ajout d'app
5. **Paramètres** — toggle grayscale, infos
6. **Overlay de blocage** — l'écran qui s'affiche par-dessus une app bloquée
7. **Widget home screen** — streak + temps économisé + statut mode libre

---

## Ce qu'on veut éviter absolument

- L'esthétique "app de productivité générique" (bleu corporate, cartes blanches plates, Material Design brut)
- Tout ce qui rappelle les apps addictives elles-mêmes (rouge, notifications agressives, badges)
- Le ton coach sportif / culpabilisant
- La surcharge : si un élément ne sert pas, on le coupe
