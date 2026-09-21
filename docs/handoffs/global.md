# HANDOFF — NoDoomScroll (Global Project)

**Project Name** : NoDoomScroll
**Type** : Personal Android App
**Status** : Design complete, Prototype phase starting
**Last Updated** : 2026-06-25

---

## Executive Summary

NoDoomScroll is a calm, premium Android app that blocks addictive content (Instagram feed/Reels, YouTube Shorts, TikTok, Snapchat Discover) while preserving utility functions (messaging, stories, subscriptions).

**Core Value** : Reduce doomscrolling without being punitive. Simple friction, not shame.

**Current State** :
- ✓ Product requirements defined (PRD)
- ✓ Design complete (maquettes light + dark, all screens)
- ✓ Technical spec finalized
- ○ Implementation starting (Phase 1: Accessibility Service prototype)

---

## Vision & Direction

### Positioning
Personal tool, not a parental control app. For someone conscious of their addiction who wants technical, honest control.

### Design Philosophy
- **Aesthetic** : Liquid glass (glassmorphism Apple-style) + soft sage/mint gradient
- **Mood** : Calm, zen, premium, minimal
- **Interaction** : Smooth animations, fluid transitions, breathing streak signature
- **Tone** : Direct, factual, zero motivational bullshit
- **Dark + Light** : Dual modes — light is luminous/mineral, dark is ultra-sophisticated

### Key Visual
The **streak counter** : verre dépoli centered, halo glowing softly, breathing pulse (4 sec cycle). Signature element that memorably represents "discipline without judgment."

---

## Product Architecture

### Core Problem Solved
Existing solutions are too coarse (block entire app or timer global). NoDoomScroll is **granular** : block only the addictive feeds while keeping useful functions accessible.

### How It Works

#### 1. Accessibility Service (detection layer)
- Monitors app at foreground
- Detects when user navigates to blocked section (Feed, Reels, Shorts, Discover, etc.)
- Triggers blocak action

#### 2. Screen Recognition + Overlay
- Accessibility Service watches for specific activity/fragment names
- When blocked section detected → pose overlay (semi-transparent glass surface)
- Overlay shows "Contenu bloqué" + button "Retour"
- Overlay is non-interactive (blocks all touches below)

#### 3. Device Admin (anti-bypass)
- Protects app from uninstallation without Device Admin revocation
- Forces user to go into Settings > Device Admin to remove protection
- Creates friction without being intrusive

#### 4. VPN Local (Phase 1.5, not MVP)
- Blocks domain-level access to social feeds via DNS
- Covers web browsers (instagram.com, youtube.com in Chrome)
- Fallback if Accessibility Service unavailable
- **Not in Phase 1** — added later for browser coverage

### Apps Natively Supported (Selective Blocking)

| App | Blocked | Allowed |
|---|---|---|
| **Instagram** | Feed, Reels, Explore | Messages, Stories, Profile |
| **YouTube** | Shorts, Homepage feed | Subscriptions, Search, Videos |
| **TikTok** | Everything | (optional: messaging if toggled) |
| **Snapchat** | Discover, Spotlight | Chat, Snaps, Stories |

### Custom Apps (Total Block Only)
User can add any app from their phone. Once added, entire app is blocked (no selective sections).

---

## Core Features

### 1. Home Screen
- **Streak counter** (signature element) : large breathing verre dépoli, halo glow
- **Today's stats** : time saved (1h 45) + attempts blocked (8)
- **Trend card** : mini sparkline showing attempts over last 24h
- **Mode libre button** : initiates temporary unlock (if quota available)
- **Navigation icons** (top-right, discreet) : Stats, Apps, Settings

### 2. Analytics
- **Daily view** : time saved today, attempts blocked
- **Weekly view** : heatmap (7 days × 24h), trend curve, comparison vs last week
- **Monthly stats** (NEW) : time saved this month + progress bar
- **All-time stats** (NEW) : total time saved since activation + context stats (streak, sessions used, block rate)
- **Breakdown by app** : which app caused most attempts
- **Contournement log** : discreet history of bypass attempts (neutral tone)

### 3. Overlay de Blocage
- Appears over blocked content
- Elements : icon, "Contenu bloqué", app+section name, "Tu as choisi de ne pas voir ça.", "Retour" button, "Mode libre" button (if available)
- Non-interactive background
- Fade in + slight scale animation
- Tone : factual, zero judgment

### 4. Onboarding (7 screens)
1. **Bienvenue** : concept explanation
2. **Permissions** : 4 mandatory permissions (Accessibility, Device Admin, Overlay, Notifications)
3. **Apps to block** : toggle Instagram/YouTube/TikTok/Snapchat + add custom apps
4. **Mode libre config** : duration, frequency, time window, delay (one-shot, locked after)
5. **Time estimate** : sliders for estimated daily usage per app
6. **Weekly report** : day/time for Sunday recap
7. **Confirmation** : recap of entire config + warning about lock-in

### 5. Mode Libre (Temporary Unlock)
- User can temporarily unlock blocked content for configured duration
- Configured once at setup, then locked
- Parameters :
  - Duration per session (5/10/15/30 min)
  - Frequency (1x/day, 1x/week, never)
  - Time window (e.g., 12h–14h only)
  - Activation delay (0/10/20/30 min — "time for urge to pass")
- States : available, in-progress (countdown), quota exhausted, out of time window
- Activating mode libre shows countdown overlay

### 6. Streak Counter
- Counts days since last mode libre usage (or since activation)
- Resets if mode libre is used
- Widget on home screen (2×2)
- Milestone notifications at 7d, 14d, 30d, 60d, 90d, 180d, 365d
- Survives reboots (persisted locally)

### 7. Grayscale (Optional)
- Toggle in Settings
- When enabled, app screen turns grayscale when user opens a blocked app
- Makes content less attractive visually
- Uses Android's native daltonizer (niveaux de gris mode)
- Requires WRITE_SECURE_SETTINGS (ADB one-time setup)

### 8. Apps Bloquées (Management)
- List of currently blocked apps
- Shows type : "Filtrage sélectif" (native 4 apps) or "Blocage total" (custom apps) or "Grayscale uniquement" (custom)
- Button "+ Ajouter une app"
- Bottom sheet : list all installed apps, searchable, choose restriction type (blocage total or grayscale)
- Warning : app addition is irreversible (while NoDoomScroll installed)

### 9. Settings
- **Grayscale toggle** (if permission available, else button to enable)
- **Mode libre (read-only)** : shows configured params, locked
- **Weekly report (read-only)** : shows day/time, locked
- **About** : version, "100% local data", privacy policy link
- **Uninstall info** : honest instructions (Settings > Admin > Revoke > Uninstall)

### 10. Widget (2×2 Home Screen)
- Streak (large number + "jours")
- Time saved today
- Mode libre status (dot + text : dispo/épuisé/hors plage)
- Tap → opens app on Home screen
- Updates every minute
- Dark + light modes

---

## Technical Stack

### Language & Framework
- **Language** : Kotlin
- **UI** : Jetpack Compose
- **Storage** : Room (SQLite) — local only
- **Background** : WorkManager + Foreground Service
- **Accessibility** : AccessibilityService API

### Key Dependencies
```gradle
androidx.compose.ui:ui:latest
androidx.room:room-runtime:latest
androidx.work:work-runtime-ktx:latest
androidx.lifecycle:lifecycle-runtime-ktx:latest
```

### Architecture
- **Accessibility Service** : detects app/zone changes, triggers overlay
- **Overlay Manager** : creates/destroys TYPE_ACCESSIBILITY_OVERLAY windows
- **Blocking Engine** : logic for which sections/apps are blocked
- **Analytics** : logs attempts (all local, never sent)
- **Device Admin** : integration with Android device admin
- **Room Database** : persistent storage (streak, mode libre config, attempts log)

### Anti-Bypass Measures
- **Device Admin** : friction to uninstall
- **Foreground Service** : survives kill attempts, auto-restart
- **Boot receiver** : restarts service after reboot
- **Overlay pre-loaded** : instant appearance, no flash
- **Back gesture mapping** : back=navigate to allowed section, not close
- **No close button** : only "Retour" to leave blocked zone

### Limitations (Intentional)
- **No VPN in V1** : only screen recognition (Accessibility Service)
- **Browser not blocked** : user can access instagram.com via Chrome (V1.5 adds VPN)
- **No cloud sync** : all data local (V2+ adds optional cloud backup)
- **No parental features** : not designed as parent control

---

## Design System

### Colors (Light Mode)
- `bg-base` : #F2F7F3 (very pale sage)
- `accent` (primary) : #5E8C72 (soft sage)
- `text-primary` : #22302A (dark sage-green)
- `text-secondary` : #5A6B62
- `text-tertiary` : #8A9890
- `glass-surface` : rgba(255,255,255,0.40) (ultra-translucent)

### Colors (Dark Mode)
- `bg-base` : #121A16 (near-black sage-green)
- `accent` : #9CC4AC (luminous light sage)
- `text-primary` : #E8F0EB (off-white)
- `glass-surface` : rgba(40,56,48,0.35)

### Typography
- **Display** (chiffres, streaks) : Plus Jakarta Sans 700, 72sp
- **Titles** : Plus Jakarta Sans 600, 28sp
- **Body** : Nunito Sans 400, 15–17sp
- **Labels** : Nunito Sans 600, 14sp

### Spacing (base 4dp)
- Standard padding/margin : 16dp, 24dp, 32dp
- Radius : 28dp (cards), 40dp (streak), 999dp (pills/buttons)

### Glassmorphism
- `backdrop-blur` : 40–50px
- `surface-opacity` : 0.40 (light), 0.35 (dark)
- `border` : 2px gradient (light→dark reflet)
- `shadow` : soft, deep, atmospheric
- `reflet-inset` : 2–3px bright top edge (liquid glass effect)

### Animation
- `dur-fast` : 200ms (micro-interactions, tap feedback)
- `dur-base` : 350ms (standard transitions)
- `dur-slow` : 600ms (screen-to-screen)
- `dur-breathe` : 4000ms (streak breathing, boucle)
- `ease-spring` : cubic-bezier(0.34, 1.56, 0.64, 1) — bouncy
- `ease-breathe` : cubic-bezier(0.4, 0, 0.6, 1) — symmetric in/out

---

## Project Files & Organization

### Outputs Folder (`/mnt/user-data/outputs/`)
All design docs and specs :

**PRD & Vision**
- `PRD-NoDoomScroll.md` — complete product requirements

**Design**
- `NoDoomScroll-Design-Brief.md` — creative direction & mood
- `NoDoomScroll-Design-Tokens.md` — exact design values (colors, typo, spacing, animations)
- `NoDoomScroll-Design-Complete.md` — brief + tokens combined

**Screens**
- `NoDoomScroll-Ecrans.md` — screen-by-screen detailed layout

**Feature Specs**
- `Overlay-Elements-Only.md` — overlay de blocage spec
- `Onboarding-Ecran-1.md` through `Onboarding-Ecran-7.md` — onboarding screens
- `Analytics-Spec-Complete.md` — analytics page spec
- `Apps-Bloquees-Spec.md` — app management screen
- `Parametres-Spec.md` — settings screen
- `Widget-Spec.md` — home screen widget

**Design Feedback Trail**
- Various `Feedback-*.md` files showing iteration (glassmorphism intensity, borders, grayscale, etc.)

### Project Structure (Android)
```
NoDoomScroll/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── kotlin/com/aden/nodoomscroll/
│   │   │   │   ├── AccessibilityService.kt
│   │   │   │   ├── BlockingOverlay.kt
│   │   │   │   ├── BlocklistManager.kt
│   │   │   │   ├── MainActivity.kt
│   │   │   │   ├── ui/
│   │   │   │   │   ├── HomeScreen.kt
│   │   │   │   │   ├── AnalyticsScreen.kt
│   │   │   │   │   ├── SettingsScreen.kt
│   │   │   │   │   └── ...
│   │   │   │   ├── data/
│   │   │   │   │   ├── database/
│   │   │   │   │   ├── repository/
│   │   │   │   │   └── models/
│   │   │   │   └── services/
│   │   │   ├── AndroidManifest.xml
│   │   │   └── res/
│   │   │       ├── layout/
│   │   │       ├── drawable/
│   │   │       └── values/
│   │   └── test/
│   └── build.gradle.kts
└── gradle/

```

---

## Implementation Roadmap

### Phase 1 — Accessibility Service Prototype (NOW)
**Duration** : 3–5 days
**Deliverable** : Minimal working prototype that validates core mechanism

- ✓ Accessibility Service detects blocked apps
- ✓ Overlay appears correctly
- ✓ "Retour" button navigates away
- ✓ Logs functional for debug
- ✗ No final UI (just debug screens)
- ✗ No mode libre, analytics, etc.

**Success** : Can open Instagram on Pixel 7A, overlay blocks feed/reels correctly.

### Phase 1.5 — Polish & Validation (Week 2)
- Add Home screen basic UI (streak, today's stats)
- Add real Overlay styling (from design tokens)
- Test on various devices
- Finalize Accessibility Service detection logic

### Phase 2 — Full Feature Implementation (Week 3–4)
- Implement entire UI (Compose + design system)
- Add Analytics page
- Add Onboarding flow
- Add Mode libre countdown
- Add Grayscale toggle
- Add App management

### Phase 3 — Device Admin & Protection (Week 5)
- Integrate Device Admin
- Add VPN local (domain blocking)
- Test anti-bypass measures

### Phase 4 — Polish & Release (Week 6+)
- Animation refinement
- Battery optimization
- Extensive testing
- APK release (personal use)

---

## Known Risks & Decisions

### Risk 1 : Accessibility Service Durability
**Risk** : Google restricts Accessibility Service for non-accessibility use.
**Mitigation** : V1 uses APK direct distribution (not Play Store). Play Store exploration in V2.

### Risk 2 : Activity Name Changes
**Risk** : Instagram/YouTube may change internal fragment/activity names between versions.
**Mitigation** : Detect via accessibility tree structure (bounds, class hierarchy) not just names. Fallback graceful.

### Risk 3 : Overlay Flash
**Risk** : 100ms+ delay before overlay appears = user sees blocked content briefly.
**Mitigation** : Pre-load overlay in memory, make display near-instant.

### Design Decision 1 : No VPN in V1
**Why** : TLS encryption makes URL-level filtering fragile. Accessibility Service is more reliable for in-app blocking.
**Trade-off** : Browser (Chrome) can still access instagram.com. Acceptable for personal use, addressed in V1.5.

### Design Decision 2 : One-Shot Config
**Why** : Once configured, mode libre parameters are locked. Forces intentional setup, prevents impulsive changes.
**Trade-off** : User must reinstall to change. Mitigated by "reset config" option in V2 (72h delay).

### Design Decision 3 : All Data Local
**Why** : Zero server dependency, full privacy, simpler implementation.
**Trade-off** : No cloud sync between devices. V2 adds optional backend if demand exists.

---

## Success Metrics

### Phase 1 (Prototype)
- ✓ App builds & runs without crash
- ✓ Accessibility Service starts
- ✓ Can detect Instagram/YouTube opening
- ✓ Overlay displays correctly
- ✓ "Retour" button works
- ✓ Logcat shows clean event stream

### Phase 2+ (Full Release)
- ✓ Home screen usable
- ✓ Streak visible & updating
- ✓ Analytics showing real data
- ✓ Mode libre countdown working
- ✓ UI matches design (glassmorphism, animations, dark+light)
- ✓ Zero crashes over 30 days daily use
- ✓ Battery impact <3% vs baseline

---

## References & Links

**Design Specs** : All in `/mnt/user-data/outputs/`
**Device** : Pixel 7A (Android 16, API 36), ADB connected
**Development** : Android Studio (Quail 1 | 2026.1.1)
**Languages** : Kotlin, Compose

---

## Contact / Questions

For design clarifications → refer to `/mnt/user-data/outputs/` files
For implementation questions → refer to `HANDOFF-NoDoomScroll-AccessibilityProto.md`
