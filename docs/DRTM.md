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

---

## Issue: Glide RequestListener Kotlin/Java Interop Regression

### Root Cause Analysis
- **What happened:**
  - Build failures occurred with errors like "does not implement abstract member" for Glide's `RequestListener` when using Kotlin anonymous classes.
- **Why it happened:**
  - Glide's `RequestListener` interface has subtle nullability and method signature differences between Java and Kotlin, especially in Glide 4.16.0+.
  - Kotlin's type inference does not always match Glide's Java interface, causing cryptic build errors.
  - Reverting to a Kotlin anonymous listener reintroduced the regression.

### How to Fix
1. **Always use a Java implementation for Glide listeners:**
   - Use `FaviconRequestListener.java` (or any Java-based listener) for all Glide `.listener()` callbacks.
   - If custom logic is needed, extend the Java class or use a callback/lambda pattern.
2. **Never use Kotlin anonymous classes for Glide listeners:**
   - Do **not** use `object : RequestListener<Drawable> { ... }` in Kotlin for Glide.
   - If you see a Kotlin anonymous Glide listener, refactor it to use the Java class.

### How to Avoid This in the Future
- If you need custom fallback or error logic, extend the Java listener or use a callback.
- If you see a Kotlin anonymous Glide listener, refactor it to use the Java class.

**Lesson:**
> For Glide listeners, always use a Java implementation to avoid Kotlin/Java interop issues.

---

## Project Rule: Use Only `adapterPosition` in ViewHolders (Kotlin)

**Description:**
Always use `adapterPosition` to get the position of a ViewHolder in Kotlin code. Do not use `bindingAdapterPosition` unless the project is explicitly upgraded to a RecyclerView version that supports it and there is a clear need (e.g., merged adapters in Java). 

**Reason:**
- `bindingAdapterPosition` is not available in the current version of the AndroidX RecyclerView library used by this project.
- Using `bindingAdapterPosition` causes unresolved reference build errors in Kotlin.
- `adapterPosition` is the standard and supported property for getting the ViewHolder position in Kotlin for this codebase.

**Fix:**
- Replace any usage of `bindingAdapterPosition` with `adapterPosition` in all ViewHolder implementations.
- Only consider `bindingAdapterPosition` if the project is upgraded and there is a specific advanced use case.

**Summary:**
This rule prevents build errors caused by referencing properties that do not exist in the current environment. Always use `adapterPosition` for compatibility and reliability. 

---

## Project Rule: Always Add Room Dependencies and Enable KAPT When Using Room

**Description:**
Whenever you use Room annotations (such as @Entity, @Dao, @Database), you must add the required Room dependencies and enable kapt (Kotlin annotation processing) in your build.gradle.kts file.

**Reason:**
- If Room dependencies or kapt are missing, the annotation processor cannot generate the required code, resulting in build errors like `@error.NonExistentClass` and `incompatible types: NonExistentClass cannot be converted to Annotation`.
- This error is cryptic and can be avoided by always including the correct dependencies and plugins.

**Fix:**
- Add the following to your dependencies block:
  ```kotlin
  implementation("androidx.room:room-runtime:2.6.1")
  kapt("androidx.room:room-compiler:2.6.1")
  implementation("androidx.room:room-ktx:2.6.1")
  ```
- Ensure you have `id("kotlin-kapt")` in your plugins block.
- Sync Gradle after making these changes.

**Summary:**
This rule prevents build failures caused by missing annotation processors for Room. Always add the required Room dependencies and enable kapt before using Room annotations in your codebase. 

---

## Issue: VPN Service Start/Stop Reliability and Seamless Operations

### Root Cause Analysis
- **What happened:**
  - The VPN toggle sometimes failed to stop the VPN service, leaving the VPN running in the background or not releasing resources properly.
  - Attempts to stop the service using `stopService()` or UI toggles alone were unreliable.
- **Why it happened:**
  - The VPN service (`TriplesVpnService`) requires explicit cleanup and must call `stopSelf()` to fully release the VPN interface and system resources.
  - Communication between the UI and the service was not robust enough to guarantee proper lifecycle handling.
  - Lack of detailed logging made it hard to diagnose where the stop/start process was failing.

### How to Fix
1. **Use explicit intent actions for service control:**
   - Start and stop the VPN service using explicit intent actions (e.g., `"STOP_VPN"`).
   - Ensure the service listens for these actions and performs the correct lifecycle operations.
2. **Ensure proper cleanup in the service:**
   - Always close the VPN interface and call `stopSelf()` when stopping the service.
   - Release all resources and update notifications/UI as needed.
3. **Add robust logging:**
   - Log all major lifecycle events (start, stop, cleanup, errors) in the service and related UI code.
4. **Synchronize UI and service state:**
   - Keep the UI toggle in sync with the actual VPN state, using LiveData, BroadcastReceivers, or similar mechanisms if needed.
5. **Test on real devices:**
   - Always verify VPN behavior on actual hardware, as emulator behavior may differ.

### How to Avoid This in the Future
- **Always use explicit, intent-based communication for service lifecycle control.**
- **Ensure the service performs full cleanup and calls `stopSelf()` on stop.**
- **Maintain detailed logs for all VPN operations.**
- **Keep UI and service state synchronized.**
- **Document the correct start/stop approach in the codebase and DRTM.**

**Lesson:**
> Seamless VPN operations require explicit lifecycle management, robust intent-based communication, thorough cleanup in the service, detailed logging, and UI-state synchronization. Always document and follow these practices to avoid regressions and ensure reliability. 