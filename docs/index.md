# NoDoomScroll — Index docs/

Point d'entrée. Voir aussi `CLAUDE.md` à la racine du repo (build/install, architecture code, gotchas plateforme).

## specs/ — spécifications produit par écran

Comportement, layout, copy, états. À lire pour implémenter ou modifier un écran.

- [analytics.md](specs/analytics.md) — écran Analytics (jour/semaine, temps gagné, heatmap, courbe de tendance, historique de bypass)
- [apps-bloquees.md](specs/apps-bloquees.md) — écran "Apps bloquées" (liste, ajout, irréversibilité)
- [onboarding.md](specs/onboarding.md) — flow d'onboarding en 7 écrans
- [overlay.md](specs/overlay.md) — overlay de blocage plein écran
- [parametres.md](specs/parametres.md) — écran Paramètres (grayscale, sections lock, à propos)
- [widget.md](specs/widget.md) — widget home-screen 2×2 (streak, temps gagné, mode libre)

## design/ — fondations visuelles

À lire avant de créer un nouveau mockup ou toucher au style.

- [design-brief.md](design/design-brief.md) — pitch produit, cible, direction artistique, anti-goals
- [style-system.md](design/style-system.md) — **source unique** des tokens (couleurs, glass, typo, spacing, animations, composants CSS) ; à coller dans chaque nouveau mockup `.dc.html`

## handoffs/ — état d'avancement engineering

Notes de transfert, pas des specs figées — contexte + état du code à un instant T.

- [global.md](handoffs/global.md) — PRD/vision globale du produit, architecture, stack technique
- [reel-swipe.md](handoffs/reel-swipe.md) — feature en cours: bloquer le swipe infini de reels tout en autorisant un reel ouvert délibérément

## mockups/ — maquettes rendues

13 fichiers `.dc.html` (Claude Design Canvas), un par écran, light+dark. Générés à partir de `design/style-system.md` + `specs/`. Ouverts via l'outil Design Canvas, pas en `file://` direct.

## archive/

Docs hors-cycle, gardés pour référence ponctuelle.

- [prompt-court-home.md](archive/prompt-court-home.md) — prompt one-off utilisé pour générer le mockup Home
