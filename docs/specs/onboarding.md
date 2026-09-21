# NoDoomScroll — Onboarding — Détail des 7 écrans

---

## Écran 1 — Bienvenue

**En-tête**
- Barre de progression (1/7 remplis)

**Contenu**
- Titre : « Reprends le contrôle, calmement. »
- Description : « NoDoomScroll bloque le scroll infini — feed, Reels, Shorts — sans toucher à tes messages ni à tes publications. »
- Icône/visual neutre représentant le concept (galet verre, ou similaire)

**Actions**
- Bouton « Commencer »
- Lien discret « Comment ça marche ? » (ouvre modal court avec explication)

---

## Écran 2 — Permissions

**En-tête**
- Barre de progression (2/7 remplis)

**Contenu**
- Titre : « Trois autorisations nécessaires »
- Description : « NoDoomScroll a besoin de ça pour fonctionner. Tout reste sur ton téléphone, rien n'est envoyé ailleurs. »

**Listes de permissions**
4 cartes, chacune avec :
- Icône (représentant la permission)
- Nom clair (côté utilisateur, pas technique)
- Sous-texte (à quoi ça sert)
- Badge d'état : « À autoriser » ou « ✓ Autorisé »
- Bouton « Autoriser » (tap ouvre le réglage système)

**Les 4 permissions** :
1. « Détecter l'app affichée » — « Pour savoir quand bloquer »
2. « Afficher par-dessus les apps » — « Pour montrer l'écran de blocage »
3. « Protection contre la désinstallation » — « Pour que le blocage tienne »
4. « Notifications » — « Pour le rapport hebdo et les rappels »

**Actions**
- Bouton « Continuer » (désactivé tant que permissions obligatoires non accordées)

---

## Écran 3 — Apps à filtrer

**En-tête**
- Barre de progression (3/7 remplis)

**Contenu**
- Titre : « Qu'est-ce qu'on bloque ? »
- Description : « Choisis les apps. On bloque juste le contenu addictif, tu gardes le reste. »

**Listes d'apps supportées**
4 cartes pour les apps nativement supportées, chacune avec :
- Icône app
- Nom (Instagram, YouTube, TikTok, Snapchat)
- Sous-texte listant ce qui est bloqué vs gardé
- Toggle pour activer/désactiver

**Exemple de sous-texte** :
- Instagram : « Bloqué : Feed, Reels, Explore · Gardé : Messages, Stories, Profil »
- YouTube : « Bloqué : Shorts, Page d'accueil · Gardé : Abonnements, Recherche, Vidéos »
- TikTok : « Bloqué : tout »
- Snapchat : « Bloqué : Discover, Spotlight · Gardé : Chat, Snaps, Stories amis »

**Section "Autres apps"**
- Bouton « + Ajouter une app custom »
- Tap ouvre bottom sheet avec liste scrollable des apps installées + champ de recherche

**Avertissement**
- Carte avec avertissement : « Une fois confirmé, on ne pourra plus retirer une app. »

**Actions**
- Bouton « Continuer »

---

## Écran 4 — Mode libre

**En-tête**
- Barre de progression (4/7 remplis)

**Contenu**
- Titre : « Ta soupape de sécurité »
- Description : « Le mode libre lève les blocages un court instant. Tu le configures maintenant — après, c'est verrouillé. »

**4 réglages en cartes** (chacun un paramètre configurable une seule fois) :

**Réglage 1 : Durée par session**
- Label : « Durée par session »
- Options : segments (5 min / 10 min / 15 min / 30 min)
- Un seul sélectionnable

**Réglage 2 : Fréquence**
- Label : « Fréquence »
- Options : segments (1×/jour / 1×/semaine / Jamais)
- Un seul sélectionnable

**Réglage 3 : Plage horaire**
- Label : « Plage horaire autorisée »
- Options : sélecteur (ex. 12h–14h ou « Tout le temps »)
- Permet de définir une fenêtre temporelle

**Réglage 4 : Délai avant activation**
- Label : « Délai avant activation »
- Options : segments (0 min / 10 min / 20 min / 30 min)
- Sous-texte : « Le temps que l'envie passe »

**Avertissement**
- Texte discret : « Ces réglages ne pourront plus changer. »

**Actions**
- Bouton « Continuer »

---

## Écran 5 — Temps estimé

**En-tête**
- Barre de progression (5/7 remplis)

**Contenu**
- Titre : « Combien de temps tu y passes ? »
- Description : « Une estimation, juste pour calculer ce que tu récupères. »

**Sliders par app activée**
Une carte par app qu'on a choisi de bloquer à l'écran 3. Chaque carte contient :
- Icône app + nom
- Slider horizontal (min/max : 0–300 min)
- Valeur affichée à droite (ex. « 45 min / jour »)

**Total**
- Grand chiffre en bas : « 2 h 15 »
- Label : « temps potentiellement récupérable »

**Actions**
- Bouton « Continuer »

---

## Écran 6 — Rapport hebdomadaire

**En-tête**
- Barre de progression (6/7 remplis)

**Contenu**
- Titre : « Ton bilan du dimanche »
- Description : « Chaque semaine, un résumé calme de tes progrès. »

**Réglage**
- Carte avec sélecteur : jour + heure
- Default : Dimanche 20h
- Verrouillé après confirmation

**Aperçu**
- Carte stylisée comme une notification :
  - Titre : « Tes stats de cette semaine »
  - Contenu type :
    - « Streak: 12 jours »
    - « Bloquées: 47 tentatives »
    - « Tendance: ↑ Mieux qu'avant »

**Actions**
- Bouton « Continuer »

---

## Écran 7 — Confirmation

**En-tête**
- Barre de progression (7/7 remplis)

**Contenu**
- Titre : « Tout est prêt »

**Récapitulatif**
3–4 cartes compactes résumant la config :
- Apps filtrées : « Instagram, YouTube, TikTok, Snapchat » (ou celles choisies)
- Mode libre : « 15 min · 1×/jour · 12h–14h »
- Rapport hebdo : « Dimanche 20h »
- Temps estimé : « 2 h 15 / jour »

**Encart important**
- Boîte d'avertissement (bordure accent) :
  - Titre : « Important »
  - Texte : « Ces réglages sont verrouillés une fois activé. Tu pourras toujours désinstaller l'app, mais pas contourner le blocage tant qu'elle est là. »

**Actions**
- Bouton primaire emphase : « Activer NoDoomScroll »
- Au tap : animation transition (fade vers Home)

---

## Éléments transversaux

**Barre de progression**
- Affichée en haut de chaque écran (1/7, 2/7, ... 7/7)
- Indique la position dans l'onboarding

**Navigation**
- Bouton primaire en bas (actionne la progression)
- Pas de bouton « Retour » — progression unidirectionnelle (une fois confirmé, pas d'aller en arrière)

**Ton**
- Clair, direct, pas de bullshit
- Explications courtes
- Aucun message motivationnel

---

## Résumé : ce que l'utilisateur choisit

| Écran | Choix |
|---|---|
| 2 | Permissions (obligatoires) |
| 3 | Apps à bloquer (config setup) |
| 4 | Mode libre (durée, fréquence, plage, délai) |
| 5 | Temps estimé par app |
| 6 | Jour/heure rapport hebdo |
| 7 | Confirmation (verrouillage définitif) |

**Une fois l'écran 7 confirmé**, l'utilisateur arrive sur le Home et l'app est active.
