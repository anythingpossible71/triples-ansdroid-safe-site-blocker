# Triples Website Blocker - Style Guide & Implementation Checklist

This document is a living style guide and checklist to ensure the app matches the intended design. Use it for development, review, and QA.

---

## Color Palette
- [ ] Brand Green: #60E329 (main accent)
- [ ] Brand Green Dark: #50C821 (hover/pressed)
- [ ] App Background: #F5F5F5 (light gray)
- [ ] Card/Header Background: #FFFFFF (white)
- [ ] Primary Text: #1F2937 (dark gray)
- [ ] Secondary Text: #6B7280 (medium gray)
- [ ] Blocked/Red: #EF4444 (lock icons, blocked status)
- [ ] Allowed/Green: #60E329 (unlock icons)
- [ ] Red Background: #FEF2F2 (blocked button bg)
- [ ] Green Background: #ECFDF5 (allowed button bg)
- [ ] Gray 50: #F9FAFB (button bg)
- [ ] Gray 100: #F3F4F6 (borders, dividers)
- [ ] Gray 200: #E5E7EB (input borders)
- [ ] Gray 400: #9CA3AF (trash icon)

## Typography
- [ ] App Name: 18sp, Bold, Primary Text
- [ ] App Subtitle: 12sp, Regular, Secondary Text
- [ ] Website URL: 16sp, Bold, Primary Text
- [ ] Status Text: 12sp, Regular, Secondary Text
- [ ] Dialog Title: 20sp, Bold, Primary Text
- [ ] Dialog Subtitle: 14sp, Regular, Secondary Text
- [ ] Button Text: 14sp, Medium, White/Primary, No All Caps

## Spacing & Dimensions
- [ ] Screen Padding: 16dp (sides), 24dp (header)
- [ ] Card Padding: 16dp
- [ ] Card Margin: 12dp (bottom)
- [ ] Header Padding: 16dp (vertical), 24dp (horizontal)
- [ ] Dialog Padding: 24dp
- [ ] Logo: 32dp (header), 80dp (empty state)
- [ ] Favicon: 40dp
- [ ] Action Buttons: 40dp
- [ ] FAB: 40dp
- [ ] Input Height: 56dp

## Corner Radius
- [ ] Cards: 16dp
- [ ] Buttons: 12dp
- [ ] Dialog: 24dp
- [ ] Favicon Container: 12dp
- [ ] Action Buttons: 12dp

## Layout Structure
- [ ] Header: White, logo left, title/subtitle, FAB right, 12dp margin between logo/text, 4dp elevation
- [ ] Main Content: Light gray bg, 16dp padding, RecyclerView fills space
- [ ] Website List: White cards, 16dp radius, 1dp elevation, 12dp margin, favicon left, text center, actions right
- [ ] Empty State: Centered, logo 80dp/30% opacity, title 18sp bold, subtitle 14sp, button, 24dp spacing

## Component Specs
- [ ] FAB: 40dp, #60E329, 20dp white plus, 8dp elevation
- [ ] Card: White, 16dp radius, 1dp elevation
- [ ] Favicon: 40dp, round, gray 50 bg, 1dp gray 200 border, 12dp radius, 8dp padding
- [ ] Action Buttons: 40dp, gray 50 bg, red/green bg for state, 12dp radius, 8dp margin

## Icon Specs
- [ ] App Logo: 32dp (header), 80dp (empty state, 30% opacity)
- [ ] Lock: Red #EF4444, 24dp
- [ ] Unlock: #60E329, 24dp
- [ ] Plus: White, 20dp
- [ ] Trash: #9CA3AF, 24dp

## Visual Hierarchy & Accessibility
- [ ] Header/FAB most prominent
- [ ] Website cards main content
- [ ] Action buttons clear state
- [ ] Status text clear
- [ ] Empty state guidance
- [ ] All buttons have content descriptions
- [ ] Color contrast meets WCAG AA
- [ ] Button states distinguishable beyond color

## Animation & Transitions
- [ ] FAB press: scale down
- [ ] Button press: bg color change
- [ ] Card: fade in
- [ ] Dialog: slide up/fade
- [ ] Toggle: smooth color/icon transition
- [ ] Icon: cross-fade lock/unlock

## Responsive & Touch
- [ ] Website URLs ellipsize if too long
- [ ] Min 40dp touch targets
- [ ] 8dp spacing between touch targets
- [ ] RecyclerView smooth scrolling, edge-to-edge

---

**Use this checklist to review every screen and component. Update as you implement or refine the UI.** 