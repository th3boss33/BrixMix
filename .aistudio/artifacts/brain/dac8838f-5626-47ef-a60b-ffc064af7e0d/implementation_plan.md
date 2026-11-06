# BrickForge Builder: Random Lego Creation Generator

A dedicated Android application designed to spark brick-building creativity by generating procedural Lego creations spanning the full spectrum from 10-piece micro-builds to 1,100-piece extreme titan builds. Features categorized parts checklists, modular step-by-step building instructions, and an interactive 1,100 combination matrix.

## User Review & Critical Decisions

> [!IMPORTANT]
> The following product decisions were confirmed through interactive clarification:
> - **Build Scope**: Full spectrum spanning simple micro-builds (10–75 pieces), medium builds (76–250 pieces), advanced models (251–600 pieces), and extreme titan creations (601–1,100 pieces).
> - **Blueprint & Visuals**: Categorized parts checklists (plates, bricks, slopes, technic, minifig/specialty) with clear phase-by-phase building instructions and progress tracking.
> - **Focus & Mechanics**: Clean, uninterrupted focus on building ideas and parts checklists (no speedrun timers or stressful countdowns).
> - **Local Offline Storage**: Seamless on-device persistence for saved custom builds, favorites, and parts collection tags.

---

## 1. Overview & Core Concept

- **What It Does**: BrickForge Builder generates boundless random build ideas across themes (Vehicles, Space & Mechs, Medieval & Fantasy, Architecture, Creatures, Cyberpunk, and Quirky Gadgets). Users can dial in their piece budget anywhere from 10 to 1,100 pieces or click "Roll Random" to generate novel concepts with piece breakdowns, color palettes, and step-by-step build phases.
- **The 1,100 Combination Matrix**: Explores the mathematics of brick combinations, allowing builders to explore exact combinations up to 1,100 pieces, lock specific sub-themes, and filter by available brick bins.
- **Target Audience**: Lego enthusiasts, AFOLs (Adult Fans of Lego), kids, and creative builders looking to overcome builder's block and make the most of their loose brick collections.
- **Key Value**: Instantly turns a chaotic pile of loose bricks into structured, inspiring building projects of any size.

---

## 2. User Experience & Visual Design

### Key User Flows

1. **Random Creation Generator (The Forge)**:
   - User adjusts a piece-count slider (10 to 1,100 pieces) or selects a preset tier (Simple Micro 10–75, Classic 76–250, Master 251–600, Extreme Titan 601–1,100).
   - User can optionally filter by theme or leave it on "All / Wildcard".
   - Tapping **"Roll Random Creation"** generates a fresh build with title, piece count, lore/description, difficulty badge, recommended color palette (Lego official hex hues), and master build techniques.
   - One-tap buttons to **"View Blueprint & Steps"**, **"Save to My Builds"**, or **"Reroll"**.

2. **Blueprint & Step-by-Step Instructions**:
   - **Parts Checklist**: Categorized list of required elements (Standard Bricks, Plates & Tiles, Slopes & Wedges, Technic Pins & Axles, Specialty & SNOT brackets). Each row has an interactive checkmark so builders can sort loose bricks before starting.
   - **Step-by-Step Build Stages**: Phase 1 (Chassis/Foundation) ➔ Phase 2 (Core Framework) ➔ Phase 3 (Structural Body & Mechanicals) ➔ Phase 4 (Exterior Paneling & Cockpit/Roofs) ➔ Phase 5 (Greebling & Fine Accents).
   - Interactive progress bar showing completed stages.

3. **1,100 Combination Explorer**:
   - An interactive combinations dashboard mapping archetype variations, piece densities, and structural styles across 1,100 calculated combinations.
   - Search and filter by piece range, category, or build technique (e.g., SNOT - Studs Not On Top, Hinge Articulation, Plate Sandwiching, Micro-scale).

4. **Saved Builds & Build Diary**:
   - Access saved blueprints offline.
   - Mark creations as "Wishlist", "In Progress", or "Built!".
   - Add personal builder notes and ratings.

### Visual Identity & Theme

- **Aesthetic Direction**: Tactile, modern Lego Master-Builder workshop. Clean dark studio background (`#12151B`) paired with high-contrast authentic Lego hues:
  - **Lego Red**: `#E52521`
  - **Lego Warm Yellow**: `#FFCE00`
  - **Lego Cobalt Blue**: `#006CB7`
  - **Lego Grass Green**: `#009640`
  - **Lego Safety Orange**: `#FA6800`
  - **Stud Base Gray**: `#20242D` / `#2B303C`
- **Typography**: Bold, playful display weights for headings and stats; clean, monospaced numeric readouts for piece counts; legible sans-serif for instruction steps.
- **Tactile Details**: Subtle cylindrical stud badges, beveled card elevation, pill tags for difficulty, and smooth animated state transitions.

---

## 3. Key Product Decisions & Trade-Offs

- **Procedural & Curated Hybrid Generation**:
  - *Chosen Approach*: Blend deep curated blueprint templates (for micro, medium, complex, and 1,100-piece titans) with procedural modifiers (sub-themes, palette generators, build techniques, and parts quota estimators).
  - *Why*: Delivers high-quality, realistic parts checklists and plausible Lego building steps while providing endless fresh combinations.
- **Checklist-Centric Blueprint View**:
  - *Chosen Approach*: Focus on categorized piece gathering and clear step-by-step assembly phases as requested, supplemented with structural layer diagrams.
  - *Why*: Directly aligns with user choice for practical, real-world brick building without clutter or distraction.
- **Local Persistence via Room / In-Memory State**:
  - *Chosen Approach*: Keep generated state reactive in `ViewModel` and save bookmarked creations to local storage.
  - *Why*: Instant launch, 100% offline functionality, and no login required.

---

## 4. Technical Architecture & Data Strategy

### System Component Diagram

```
┌────────────────────────────────────────────────────────┐
│                     MainActivity                       │
│  ┌──────────────────────────────────────────────────┐  │
│  │           M3 Scaffold & Top Navigation           │  │
│  │     [Generator]  [Blueprint]  [Matrix]  [Saved]  │  │
│  └────────────────────────┬─────────────────────────┘  │
└───────────────────────────┼────────────────────────────┘
                            │
            ┌───────────────┴───────────────┐
            ▼                               ▼
┌───────────────────────────────┐ ┌───────────────────────────────┐
│     GeneratorScreen           │ │     BlueprintScreen           │
│ - Piece count slider (10-1100)│ │ - Categorized parts checklist │
│ - Theme filter chips          │ │ - Step-by-step instructions   │
│ - "Roll Creation" button      │ │ - Assembly progress tracker   │
│ - Creation Preview Card       │ │ - Save / Share actions        │
└───────────────┬───────────────┘ └───────────────┬───────────────┘
                │                                 │
                ▼                                 ▼
┌─────────────────────────────────────────────────────────┐
│                    BuildViewModel                       │
│  - currentCreation: StateFlow<LegoCreation>             │
│  - pieceCountBudget: StateFlow<Int>                     │
│  - savedCreations: StateFlow<List<LegoCreation>>        │
│  - checkedParts: StateFlow<Set<String>>                 │
│  - currentStepProgress: StateFlow<Int>                  │
└───────────────────────────┬─────────────────────────────┘
                            │
            ┌───────────────┴───────────────┐
            ▼                               ▼
┌───────────────────────┐       ┌───────────────────────┐
│ CreationGenerator     │       │ LocalBuildRepository  │
│ - 1100 Matrix Engine  │       │ - Saved builds store  │
│ - Archetypes & Lore   │       │ - Check state cache   │
│ - Parts Estimator     │       │                       │
└───────────────────────┘       └───────────────────────┘
```

### Core Data Models

- **`LegoCreation`**: ID, title, subtitle, pieceCount, complexityTier (`SIMPLE`, `MODERATE`, `COMPLEX`, `EXTREME`), category (`VEHICLES`, `SPACE_MECH`, `MEDIEVAL_CASTLE`, `CREATURES`, `ARCHITECTURE`, `CYBERPUNK`, `NOVELTY`), loreDescription, colorPalette (list of named Lego colors + hex), keyTechniques (list of techniques like SNOT, offset pinning), partsList (list of `LegoPart`), buildSteps (list of `BuildStep`), isSaved.
- **`LegoPart`**: ID, name, category (`BRICKS`, `PLATES`, `SLOPES`, `TECHNIC`, `SPECIALTY`), quantity, recommendedColor, checked.
- **`BuildStep`**: stepNumber, title, description, focusPieceTypes, tips.
- **`CombinationMatrixItem`**: combinationIndex (1 to 1100), pieceCount, category, archetype, complexity, tags.
