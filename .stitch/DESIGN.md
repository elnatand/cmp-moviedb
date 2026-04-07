# Design System: MovieDB Screen Redesign — CineDB Noir
**Project ID (original):** `16343578251041556472`
**Project ID (exported screens):** `18145949510860276680`
**Device:** Mobile (390px canvas)
**Color Mode:** Dark
**Font:** Be Vietnam Pro

---

## 1. Visual Theme & Atmosphere

**CineDB Noir** is a premium, cinematic dark-mode experience — dense but breathable, editorial without being cold. The aesthetic draws from high-end streaming services and film noir: deep near-black backgrounds with a subtle cool blue undertone, periwinkle accents that feel electric against the dark canvas, and golden-yellow sparingly reserved for ratings and delight moments.

The design avoids the sterile black of generic dark apps. Surfaces are layered — each level slightly lighter than the last — creating a sense of physical depth without shadows. Poster imagery is the visual hero; the UI recedes to frame it. Motion is implied through gradients and layered surfaces rather than animation.

**Mood:** Cinematic · Premium · Atmospheric · Editorial · Cool-Toned Dark

---

## 2. Color Palette & Roles

### Backgrounds & Surfaces (darkest → brightest)
| Descriptive Name | Hex | Role |
|---|---|---|
| Void Black | `#0e0e13` | Deepest background, behind-screen layer |
| Cinema Black | `#131318` | **Primary app background**, page base |
| Dark Slate | `#1b1b20` | Surface Container Low — subtle elevation |
| Charcoal Card | `#1f1f25` | **Standard cards & section containers** |
| Elevated Slate | `#2a292f` | Surface Container High — modals, sheets |
| Muted Graphite | `#35343a` | Surface Container Highest — top-level overlays |
| Soft Graphite | `#39383e` | Brightest surface — highlight states |

### Primary (Action Blue)
| Descriptive Name | Hex | Role |
|---|---|---|
| Periwinkle Blue | `#b4c5ff` | **Primary accent** — "See all" links, outlines, icon tints, chip borders |
| Deep Action Blue | `#195de6` | **CTA button fill** — "Watch Now", primary actions |
| Pale Sky | `#dbe1ff` | Primary Fixed — rarely used, large display labels |

### Secondary (Neutral Purple-Gray)
| Descriptive Name | Hex | Role |
|---|---|---|
| Lavender Mist | `#c7c5d3` | **Secondary text** — placeholders, metadata, captions |
| Dusk Purple | `#494853` | Secondary Container — inactive chips, subtle backgrounds |
| Soft Lilac | `#b9b7c4` | On-Secondary Container text |

### Tertiary (Golden Accent)
| Descriptive Name | Hex | Role |
|---|---|---|
| Cinema Gold | `#e9c400` | **Star ratings only** — used extremely sparingly |
| Deep Gold | `#c9a900` | Tertiary Container — rating badge background |

### Text & Content
| Descriptive Name | Hex | Role |
|---|---|---|
| Moonlight White | `#e4e1e9` | **Primary text** — headings, body, labels |
| Silver Mist | `#c3c6d7` | **Secondary text** — subtitles, metadata |
| Slate Outline | `#8d90a0` | Borders, dividers, inactive icon stroke |
| Shadow Outline | `#434655` | Subtle borders, separator lines |

### Error & Utility
| Descriptive Name | Hex | Role |
|---|---|---|
| Coral Warning | `#ffb4ab` | Error states |
| Crimson Container | `#93000a` | Error container fill |

---

## 3. Typography Rules

**Family:** Be Vietnam Pro — applied uniformly across all text roles (headline, body, label). A geometric humanist sans-serif that reads as modern and premium without being cold.

**Weight hierarchy:**
- `700` Bold — screen titles, section headers, movie titles, hero text
- `600` Semi-bold — button labels, chip text, card subtitles
- `400` Regular — body copy, metadata, captions

**Letter-spacing:**
- Hero movie titles: `3px` wide tracking in full UPPERCASE — cinematic, poster-like
- Section headers: default tracking
- Labels/chips: slight positive tracking (0.5px) for legibility at small sizes

**Size scale (approximate):**
- Hero title: `headlineMedium` / ~28sp
- Section title: `titleLarge` / ~22sp
- Card overlay title: `labelSmall` / ~11sp bold
- Body / metadata: `bodyMedium` / ~14sp

---

## 4. Component Styling

### Buttons
- **Primary CTA ("Watch Now"):** Deep Action Blue (`#195de6`) fill · Moonlight White text · PlayArrow icon leading · Pill shape (50dp radius) · Equal-width, half the hero row
- **Secondary Action ("View Details"):** Transparent fill · Periwinkle Blue (`#b4c5ff`) outline (1dp) and text · Pill shape (50dp radius) · Same height as primary
- **Padding:** 12dp vertical inside pill buttons

### Cards — Section Containers
- **Background:** Charcoal Card (`#1f1f25`)
- **Shape:** Generously rounded corners (20dp radius)
- **Margin:** 12dp horizontal, 6dp vertical from screen edge
- **Inner padding:** 16dp top/bottom
- **No border, no shadow** — depth via background color contrast with the Cinema Black page background

### Cards — Movie Poster Tiles
- **Size:** 128dp wide × 192dp tall (2:3 portrait ratio)
- **Shape:** Softly rounded corners (12dp radius)
- **Image:** Fills the card; poster is the entire visual content
- **Overlay:** Vertical gradient from transparent (50%) → 90% opacity black at the bottom edge
- **Title:** `labelSmall` bold Moonlight White, bottom-left aligned, 8dp padding, 2-line max with ellipsis
- **Spacing between cards:** 12dp horizontal gap; 16dp content padding on row ends

### Hero Banner
- **Size:** Full width × 300dp tall
- **Shape:** Subtly rounded (16dp radius), no horizontal margin
- **Image:** Poster/backdrop fills the full card
- **Gradient:** `transparent` at top → Cinema Black (`#131318`) at bottom — roughly starts at 55% down, fully opaque at 100%
- **Title:** Large bold uppercase Moonlight White, centered at bottom, 3px letter-spacing
- **Buttons:** Pair of equal-width pill buttons beneath title, 12dp gap between them

### Search Bar
- **Shape:** Full pill (50dp radius)
- **Background:** Charcoal Card (`#1f1f25`)
- **Icon:** Search icon in Lavender Mist (`#c7c5d3`), 20dp
- **Placeholder:** Lavender Mist (`#c7c5d3`), `bodyMedium`
- **Padding:** 16dp horizontal, 12dp vertical
- **Effect:** Glassmorphism backdrop blur (20px) when sticky/overlapping content

### Section Headers
- **Title:** `titleLarge` Bold, Moonlight White, left-aligned
- **"See all →" link:** Periwinkle Blue (`#b4c5ff`), right-aligned, same row
- **Padding:** 16dp horizontal, 4dp vertical

### Bottom Navigation Bar (where present)
- **Background:** Cinema Black (`#131318`) or Charcoal Card (`#1f1f25`) with subtle top separator
- **Active icon:** Deep Action Blue (`#195de6`) or Periwinkle Blue (`#b4c5ff`)
- **Inactive icon:** Slate Outline (`#8d90a0`)
- **Labels:** `labelSmall` below icons; active in Periwinkle Blue

---

## 5. Layout Principles

**Gutters:** 16dp horizontal padding on most content; section containers use 12dp outer margin + 16dp inner padding.

**Vertical rhythm:** 6dp between section cards; 16dp between major page regions (search → hero → sections); 80dp bottom spacer to clear nav bar.

**Scrollbars:** Always hidden (`.no-scrollbar` / `scrollbarWidth: none`) — scroll is implied, never mechanical.

**Horizontal carousels:** `LazyRow` with 16dp content padding on both ends; 12dp gap between items; snap behavior implied.

**No dividers:** Sections are separated by spacing and tonal background shifts, never hairline borders.

**Poster-first:** The UI is a frame. Poster imagery should always be prominent and unclipped by UI chrome.

**Minimum screen height:** 884dp — all screens designed to feel complete at this viewport.
