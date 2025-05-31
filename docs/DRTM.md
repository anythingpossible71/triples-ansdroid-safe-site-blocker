# DRTM: Do Not Repeat This Mistake

## Issue: Unresolved reference 'bumptech' when using Glide

### Root Cause Analysis
- **What happened:**
  - The error `Unresolved reference 'bumptech'` appeared when trying to use the Glide image loading library in the code.
- **Why it happened:**
  - Glide is a third-party library and is not included in Android projects by default.
  - The code attempted to use `com.bumptech.glide.Glide` without first adding the Glide dependency to the project's Gradle build file.

### How to Fix
1. **Add Glide to your dependencies:**
   - Open `app/build.gradle.kts` (or `build.gradle` if using Groovy).
   - Add the following lines inside the `dependencies` block:
     ```kotlin
     implementation("com.github.bumptech.glide:glide:4.16.0")
     kapt("com.github.bumptech.glide:compiler:4.16.0")
     ```
   - If you are not using kapt, you can omit the second line, but it is recommended for full Glide functionality.
2. **Sync Gradle:**
   - Click "Sync Now" in the bar that appears, or go to File > Sync Project with Gradle Files in Android Studio.
3. **Rebuild and run the app.**

### How to Avoid This in the Future
- **Always add required dependencies before using third-party libraries in your code.**
- **Check the official documentation for the correct dependency version and setup instructions.**
- **Sync Gradle after editing dependencies to ensure the IDE recognizes new libraries.**
- **If you see 'Unresolved reference' errors for a library, check your Gradle dependencies first.**

---

**Lesson:**
> Never use a third-party library in your code before adding it to your build.gradle dependencies and syncing Gradle.

---

## Issue: Card background color change not visible in app

### Root Cause Analysis
- **What happened:**
  - We updated the website card background color using `android:background` on `MaterialCardView`, but did not see the change reflected in the app.
- **Why it happened:**
  - `MaterialCardView` does not use `android:background` for its card background. Instead, it uses the `app:cardBackgroundColor` attribute.
  - Setting `android:background` only affects the view background, not the card's actual background color, which is drawn by the Material library.
  - This is a common pitfall when working with Material Components in Android.

### How to Fix
1. **Use the correct attribute:**
   - Set the card background color using `app:cardBackgroundColor` instead of `android:background`.
   - Example:
     ```xml
     <com.google.android.material.card.MaterialCardView
         ...
         app:cardBackgroundColor="@color/card_white"
         ...>
     ```
2. **Rebuild and rerun the app** to see the change.

### How to Avoid This in the Future
- **Always check the official documentation for the correct attributes for Material Components.**
- **If a UI change is not visible, review the component's documentation and attribute precedence.**
- **Search for component-specific attributes (e.g., `cardBackgroundColor` for `MaterialCardView`).**
- **Test small changes incrementally and verify after each change.**

---

**Lesson:**
> When working with Material Components, always use the component-specific attributes for styling (e.g., `app:cardBackgroundColor` for cards) instead of generic Android attributes. 