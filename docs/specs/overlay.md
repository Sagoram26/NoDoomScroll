# NoDoomScroll — Overlay de blocage — Éléments

---

## Couches (de bas en haut)

1. App bloquée en arrière-plan (non-interactif)
2. Surface plein écran (bloque toutes les interactions)
3. Carte centrale (contenu)

---

## Contenu de la carte centrale

1. Icône (neutre, non-agressive)
2. Titre : « Contenu bloqué »
3. Identifiant dynamique : « [App] · [Section] » — ex: « Instagram · Reels »
4. Sous-texte : « Tu as choisi de ne pas voir ça. »
5. Bouton primaire : « Retour »
6. Bouton secondaire : « Mode libre » (label variable selon état)

---

## États du bouton Mode libre

| État | Label |
|---|---|
| Disponible | « Mode libre · disponible » |
| Délai en cours | « Mode libre · MM:SS » |
| Quota épuisé | « Prochain créneau : [jour] [heure] » |
| Hors plage horaire | « Mode libre · disponible à [heure] » |
| Jamais configuré | Bouton masqué |

---

## Actions

- **Retour** → navigate vers section autorisée (DMs pour Insta, Abonnements pour YouTube, Chat pour Snap, Home Android pour app custom)
- **Mode libre** → lance le compte à rebours configuré → à expiration ferme l'overlay

---

## Ce qui est absent (intentionnel)

- Pas de croix / fermer
- Pas de swipe pour fermer
- Pas de streak
- Pas de stats
- Back gesture Android = même action que "Retour"
