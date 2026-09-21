# Graph Report - NoDoomScroll  (2026-09-16)

## Corpus Check
- Corpus is ~45,470 words - fits in a single context window. You may not need a graph.

## Summary
- 428 nodes · 778 edges · 37 communities (18 shown, 15 thin omitted)
- Extraction: 90% EXTRACTED · 10% INFERRED · 0% AMBIGUOUS · INFERRED: 76 edges (avg confidence: 0.85)
- Token cost: 0 input · 0 output

## Community Hubs (Navigation)
- Core Service Architecture
- Data Persistence
- UI Components
- Analytics
- Onboarding Flow
- Theme & Design System
- Widget System
- Blocking Mechanics
- State Management
- Accessibility Integration
- App Configuration
- Testing Infrastructure
- Build Configuration
- Documentation
- Icon Assets
- MockUp Designs
- Community 16
- Community 17
- Community 18
- Community 19
- Community 20
- Community 21
- Community 22
- Community 23
- Community 24
- Community 28
- Community 29
- Community 30
- Community 31
- Community 32
- Community 33
- Community 34
- Community 35

## God Nodes (most connected - your core abstractions)
1. `NdsRepository` - 32 edges
2. `createRuntime()` - 22 edges
3. `NoDoomScrollService` - 20 edges
4. `AppViewModel` - 20 edges
5. `get()` - 20 edges
6. `GlassCard()` - 15 edges
7. `ModeLibreConfig` - 12 edges
8. `ModeLibreController` - 12 edges
9. `OnboardingScreen()` - 12 edges
10. `boot()` - 12 edges

## Surprising Connections (you probably didn't know these)
- `Project Configuration (Kotlin LSP, UTF-8 encoding)` --references--> `NoDoomScroll`  [INFERRED]
  .serena/project.yml → CLAUDE.md
- `Technical Stack (Kotlin, Compose, Room, WorkManager)` --references--> `NoDoomScroll`  [EXTRACTED]
  docs/handoffs/global.md → CLAUDE.md
- `Reel-swipe blocking problem (infinite swiping after deliberate open)` --references--> `Reel-swipe blocking (separate code path)`  [EXTRACTED]
  docs/handoffs/reel-swipe.md → CLAUDE.md
- `NoDoomScrollService` --references--> `BlocklistManager`  [EXTRACTED]
  app/src/main/java/com/aden/nodoomscroll/NoDoomScrollService.kt → app/src/main/java/com/aden/nodoomscroll/BlocklistManager.kt
- `AppViewModel` --references--> `StreakState`  [EXTRACTED]
  app/src/main/java/com/aden/nodoomscroll/ui/AppViewModel.kt → app/src/main/java/com/aden/nodoomscroll/data/Entities.kt

## Import Cycles
- None detected.

## Hyperedges (group relationships)
- **he_1** —  [INFERRED]
- **he_2** —  [INFERRED]
- **he_3** —  [INFERRED]
- **** — docs_specs_onboarding_screen_1_welcome, docs_specs_onboarding_screen_2_permissions, docs_specs_onboarding_screen_3_apps_selection, docs_specs_onboarding_screen_4_mode_libre, docs_specs_onboarding_screen_5_time_estimate, docs_specs_onboarding_screen_6_weekly_report, docs_specs_onboarding_screen_7_confirmation [INFERRED 1.00]
- **** — docs_specs_parametres_screen, docs_specs_overlay_blocking_screen, docs_specs_widget_home [INFERRED 0.95]
- **** — docs_specs_onboarding_screen_3_apps_selection, docs_specs_apps_bloquees_screen, docs_specs_overlay_blocking_screen [INFERRED 0.85]

## Communities (37 total, 15 thin omitted)

### Community 0 - "Core Service Architecture"
Cohesion: 0.07
Nodes (63): boot(), collectProps(), compileAttr(), compileTemplate(), createComponentFactory(), getDC(), Dispatcher(), createExternalModules() (+55 more)

### Community 1 - "Data Persistence"
Cohesion: 0.05
Nodes (20): AndroidViewModel, AppEstimateDao, BlockAttemptDao, BlockedAppDao, Flow, Settings, ModeLibreDao, SettingsDao (+12 more)

### Community 2 - "UI Components"
Cohesion: 0.11
Nodes (41): androidx, AnalyticsScreen(), AddAppSheet(), AppRow(), AppsScreen(), HomeScreen(), Modifier, mmss() (+33 more)

### Community 3 - "Analytics"
Cohesion: 0.11
Nodes (11): StreakDao, StreakState, Available, Context, Flow, Settings, ModeLibreStatus, NdsRepository (+3 more)

### Community 4 - "Onboarding Flow"
Cohesion: 0.15
Nodes (6): AccessibilityEvent, AccessibilityService, BlockingOverlay, AccessibilityNodeInfo, NoDoomScrollService, View

### Community 5 - "Theme & Design System"
Cohesion: 0.11
Nodes (8): Format, Context, StreakWidget, StreakWidgetReceiver, FormatTest, GlanceAppWidget, GlanceAppWidgetReceiver, GlanceId

### Community 6 - "Widget System"
Cohesion: 0.11
Nodes (21): Blocking Mechanics, One-Time Configuration Locking, Custom Apps Irreversibility Constraint, Blocked Apps Management Screen, Complete Onboarding Flow, Onboarding Screen 1: Welcome, Onboarding Screen 2: Permissions, Onboarding Screen 3: Apps Selection (+13 more)

### Community 7 - "Blocking Mechanics"
Cohesion: 0.15
Nodes (8): EventLog, StateFlow, Context, Notifications, Context, WeeklyReportWorker, CoroutineWorker, Result

### Community 8 - "State Management"
Cohesion: 0.18
Nodes (10): BlocklistManager, Escape, BACK, HOME, AccessibilityNodeInfo, Mode, BLOCK_WHOLE_APP, SELECTIVE (+2 more)

### Community 9 - "Accessibility Integration"
Cohesion: 0.24
Nodes (10): DebugScreen(), isServiceEnabled(), Context, Modifier, MainActivity, NoDoomScrollTheme(), Radius, Space (+2 more)

### Community 10 - "App Configuration"
Cohesion: 0.18
Nodes (12): Blocking mechanics (overlay, audio focus, GLOBAL_ACTION_BACK), BlockingOverlay, BlocklistManager, Detection Strategy (resource-id + isVisibleToUser), NdsRepository, NoDoomScrollService, NoDoomScroll, Reel-swipe blocking (separate code path) (+4 more)

### Community 11 - "Testing Infrastructure"
Cohesion: 0.23
Nodes (7): StateFlow, ModeLibreController, Phase, Active, Delay, Idle, State

### Community 13 - "Documentation"
Cohesion: 0.25
Nodes (8): Glassmorphism (liquid glass aesthetic), Streak counter (signature animated element), Animation Tokens (dur-fast, dur-base, dur-slow, dur-breathe), Color Tokens (light/dark mode CSS variables), Glassmorphism CSS Recipe (gradient border, backdrop-filter, box-shadow), Typography System (Plus Jakarta Sans, Nunito Sans), Home Screen Feature (streak, today stats, mode libre), Home Screen Mockup (UI implementation)

### Community 14 - "Icon Assets"
Cohesion: 0.40
Nodes (5): Screen, Analytics, Apps, Home, Settings

### Community 15 - "MockUp Designs"
Cohesion: 0.50
Nodes (4): App Launcher Icon (HDPI), App Launcher Icon (MDPI), App Launcher Icon (XXHDPI), NoDoomScroll App Branding Identity

### Community 16 - "Community 16"
Cohesion: 0.83
Nodes (3): gradlew script, die(), warn()

### Community 19 - "Community 19"
Cohesion: 0.67
Nodes (3): Instagram (selective: Feed/Reels/Explore blocked; Messages/Stories allowed), Core Problem (granular blocking vs coarse solutions), Documentation Structure (specs, design, handoffs, mockups, archive)

### Community 20 - "Community 20"
Cohesion: 0.67
Nodes (3): Analytics Feature (daily/weekly view, heatmap, trends), Analytics Daily View (time saved today, attempts blocked), Analytics Weekly View (time saved, trend curve, heatmap)

## Knowledge Gaps
- **33 isolated node(s):** `BLOCK_WHOLE_APP`, `SELECTIVE`, `BACK`, `HOME`, `Rule` (+28 more)
  These have ≤1 connection - possible missing edges or undocumented components. (Counts symbols only; 112 node(s) total have ≤1 connection when file, concept and rationale nodes are included.)
- **15 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `NdsRepository` connect `Analytics` to `Data Persistence`, `Onboarding Flow`, `Theme & Design System`, `Blocking Mechanics`?**
  _High betweenness centrality (0.142) - this node is a cross-community bridge._
- **Why does `AppViewModel` connect `Data Persistence` to `Testing Infrastructure`, `UI Components`, `Analytics`, `Blocking Mechanics`?**
  _High betweenness centrality (0.092) - this node is a cross-community bridge._
- **Why does `NoDoomScrollService` connect `Onboarding Flow` to `State Management`?**
  _High betweenness centrality (0.081) - this node is a cross-community bridge._
- **Are the 7 inferred relationships involving `createRuntime()` (e.g. with `adoptParsed()` and `dcUpdate()`) actually correct?**
  _`createRuntime()` has 7 INFERRED edges - model-reasoned connections that need verification._
- **What connects `BLOCK_WHOLE_APP`, `SELECTIVE`, `BACK` to the rest of the system?**
  _33 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `Core Service Architecture` be split into smaller, more focused modules?**
  _Cohesion score 0.06918918918918919 - nodes in this community are weakly interconnected._
- **Should `Data Persistence` be split into smaller, more focused modules?**
  _Cohesion score 0.05367231638418079 - nodes in this community are weakly interconnected._