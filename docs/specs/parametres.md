# Écran — Paramètres

## Éléments principaux

1. **En-tête**
   - Titre : « Réglages »

2. **Section 1 — Affichage**
   - Label section : « Affichage »
   
   **Toggle : Mode niveaux de gris**
   - Label : « Mode niveaux de gris »
   - Sous-texte : « Passe l'écran en gris quand tu ouvres une app bloquée. »
   - Toggle (on/off)
   - État 1 (permission accordée) : toggle fonctionnel
   - État 2 (permission non accordée) : toggle grisé + bouton « Activer (configuration requise) »
     - Au tap sur le bouton → affiche un wizard d'instructions ADB pas-à-pas

3. **Section 2 — Configuration (lecture seule)**
   - Label section : « Configuration »
   
   **Mode libre (verrouillé)**
   - Label : « Mode libre »
   - Valeur affichée : « 15 min · 1×/jour · 12h–14h · 10 min délai »
   - Badge : « Verrouillé »
   - Non-cliquable, non-modifiable
   
   **Rapport hebdomadaire (verrouillé)**
   - Label : « Rapport hebdomadaire »
   - Valeur affichée : « Dimanche 20h »
   - Badge : « Verrouillé »
   - Non-cliquable, non-modifiable

4. **Section 3 — Informations**
   - Label section : « À propos »
   
   **Version**
   - Label : « Version NoDoomScroll »
   - Valeur : « 1.0.0 » (ou numéro version actuel)
   
   **Données**
   - Label : « Données »
   - Texte : « 100% local · aucune donnée n'est envoyée ailleurs »
   
   **Confidentialité**
   - Lien cliquable : « Politique de confidentialité »
   - Au tap → ouvre un écran ou une webview avec la politique

5. **Section 4 — Désinstallation**
   - Label section : « Désinstallation »
   
   **Lien informatif**
   - Texte : « Comment désinstaller NoDoomScroll ? »
   - Au tap → affiche un modal / écran avec instructions :
     1. « Aller à Paramètres > Apps > NoDoomScroll »
     2. « Appuyer sur "Avancé" > "Administrateur d'appareil" »
     3. « Révoquer l'accès administrateur »
     4. « Désinstaller l'app »
   - Ton honnête, pas de dark patterns

---

## Comportements

### Toggle Mode niveaux de gris

**État 1 — Permission `WRITE_SECURE_SETTINGS` accordée**
- Toggle actif (on/off switchable)
- Affiche l'état actuel (activé ou désactivé)
- Au tap, active/désactive le grayscale immédiatement

**État 2 — Permission non accordée ou refusée**
- Toggle grisé (non-interactif)
- Bouton à côté : « Activer (configuration requise) »
- Au tap sur le bouton :
  - Affiche un **wizard d'instructions ADB pas-à-pas** (dans un modal ou nouvel écran)
  - Instructions claires (copier-coller friendly)
  - Après completion : demande de relancer l'app ou de réessayer

### Sections verrouillées (Mode libre + Rapport)

- Affichage lecture seule (pas de tap, pas de modification possible)
- Badge « Verrouillé » pour clarifier
- Sous-texte optionnel : « Configuré au setup, non modifiable »

### Lien Politique de confidentialité

- Tap ouvre une **webview** ou un **nouvel écran** avec le contenu
- Ou renvoie à une URL externe (si pertinent)

### Désinstallation

- Lien informatif, pas agressif
- Instructions claires et honnêtes (pas de friction malveillante)
- Rappel : « Une fois désinstallé, tous les blocages seront levés »

---

## États spéciaux

- **Première fois** : le toggle grayscale est désactivé et la permission n'est pas encore demandée
- **Permission refusée** : le toggle reste grisé, l'utilisateur peut réessayer via le bouton
- **Grayscale activé** : toggle est on, indication visuelle claire

---

## Navigation

- Écran accessible depuis le menu principal / bottom nav
- Retour : bouton système Android ou swipe back
- Aucune navigation profonde (sauf les liens externes : politique, etc.)

---

## Ton

- Factuel, honnête
- Pas de dark patterns ou friction intentionnelle
- Explications claires (surtout pour ADB et désinstallation)
