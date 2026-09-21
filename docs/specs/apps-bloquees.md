# Écran — Apps bloquées

## Éléments principaux

1. **En-tête**
   - Titre : « Apps bloquées »
   - Description/sous-texte : « Gérer les apps filtrées »

2. **Liste des apps filtrées**
   Affiche toutes les apps actuellement bloquées (celles sélectionnées au setup)
   
   Chaque app dans la liste contient :
   - Icône app (vraie icône de l'app)
   - Nom app
   - Type de blocage (badge) : « Filtrage sélectif » ou « Blocage total »
   - Sous-texte décrivant ce qui est bloqué vs gardé (pour apps à filtrage sélectif uniquement)
   
   Exemple :
   ```
   [Icône] Instagram
           Filtrage sélectif
           Bloqué: Feed, Reels, Explore · Gardé: Messages, Stories
   ```

3. **Bouton d'ajout d'app custom**
   - Bouton : « + Ajouter une app »
   - Positionnement : après la liste des 4 apps supportées, avant la note
   - Action au tap : ouvre un bottom sheet

4. **Bottom sheet — Ajout d'app custom**
   Apparaît quand on tap « + Ajouter une app »
   
   Éléments du bottom sheet :
   - Titre : « Ajouter une app »
   - Champ de recherche : « Rechercher une app »
   - Liste scrollable des apps installées :
     - Icône app + nom
     - Affiche uniquement les apps non déjà bloquées
     - Filtrage en temps réel par le champ recherche
   - Tap sur une app → confirmation + ajout immédiat
   
   Confirmation avant ajout (optionnel mais recommandé) :
   - Dialog : « Bloquer [App] entièrement ? »
   - Sous-texte : « C'est irréversible. »
   - Boutons : « Annuler » / « Bloquer »

5. **Note de limitation (importante)**
   - Positionnement : en bas de la liste
   - Texte : « Une app ajoutée ne peut pas être retirée tant que NoDoomScroll est installé. »
   - Style : caption / text-tertiary (discret)

---

## Comportements

- **Chaque app de la liste** : affiche son statut (filtrage sélectif vs blocage total)
- **Bouton « + Ajouter une app »** : toujours visible et accessible
- **Bottom sheet d'ajout** : filtre les apps déjà bloquées (ne les affiche pas)
- **Search en temps réel** : filtre la liste des apps par nom
- **Validation d'ajout** : confirmation avant d'ajouter (optionnel mais bon UX)

---

## États spéciaux

- **Aucune app custom ajoutée** : la liste affiche juste les 4 apps natives (Instagram, YouTube, TikTok, Snapchat)
- **Des apps custom ajoutées** : elles s'affichent dans la même liste, avec badge « Blocage total »
- **Bottom sheet vide** : si toutes les apps sont déjà bloquées (cas rare, mais afficher message « Aucune app disponible »)

---

## Navigation

- Écran accessible depuis le menu principal / bottom nav
- Retour : bouton système Android ou swipe back
