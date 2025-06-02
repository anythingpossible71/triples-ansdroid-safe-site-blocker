# Triples VPN Website Blocker - Project Plan & Checklist

This checklist is a living document to track our progress and next steps for the Triples VPN Website Blocker app. Update as we advance!

---

## Phase 1: Project Setup
- [x] Initialize Android project with Kotlin & Jetpack Compose
- [x] Configure build.gradle with all required dependencies
- [ ] Set up MVVM project structure
- [x] Add brand colors and theme
- [x] Import SVG icons as vector drawables
- [x] Add placeholder graphics to app/graphics

## Phase 2: UI Scaffolding
- [x] Create main screen layout (header, website list, add button)
- [x] Implement Website card UI (favicon, URL, status, lock/unlock, delete)
- [ ] Add empty state UI
- [x] Add modal dialog for adding websites
- [x] Integrate custom icons into UI

## Phase 3: Data Layer
- [x] Create Website data model
- [ ] Set up Room database for website storage
- [ ] Implement DAO for website operations
- [ ] Create WebsiteRepository
- [ ] Set up DataStore for app preferences
- [ ] Implement URL validation/cleaning utilities

## Phase 4: Core Functionality
- [x] Add website to block list
- [x] Remove website from block list
- [x] Toggle website block state (locked/unlocked)
- [x] Display website favicons
- [ ] Persist website list across app restarts

## Phase 5: VPN Integration
- [ ] Implement local VpnService
- [ ] Intercept and filter web requests
- [ ] Block/allow websites based on list
- [ ] Show VPN status in app and notification
- [ ] Handle VPN permissions and lifecycle

## Phase 6: Polish & QA
- [ ] Refine UI/UX (spacing, colors, animations)
- [ ] Test on multiple Android versions/devices
- [ ] Fix bugs and edge cases
- [ ] Prepare for release (icons, screenshots, Play Store listing)

---

**Notes:**
- Toasts are now shown for block/unblock actions.

# Suggested Next Steps
1. Add empty state UI for when no websites are present (Phase 2)
2. Refine UI/UX (spacing, colors, animations) (Phase 6)
3. (Optional) Start on VPN integration (Phase 5) 