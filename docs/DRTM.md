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