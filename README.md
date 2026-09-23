# Arihant Alishan Society Management System

A comprehensive Android application built with Jetpack Compose, Kotlin Coroutines, and Room Database for **Arihant Alishan Co-operative Housing Society Ltd.** (Sector 35, Kharghar, Navi Mumbai).

---

## 📱 Android Studio APK Build Instructions

To build and run or export the APK locally using Android Studio:

### 1. Prerequisites
- **Android Studio Ladybug (2024.2.1)** or newer
- **JDK 17** (configured as Gradle JDK in Android Studio Settings > Build, Execution, Deployment > Build Tools > Gradle)
- **Android SDK Platform 35** and Build-Tools installed

### 2. Building the Debug APK
1. Open the project folder in Android Studio.
2. Allow Gradle sync to complete automatically.
3. Open the **Build** menu in the top toolbar:
   - Select **Build** > **Build Bundle(s) / APK(s)** > **Build APK(s)**.
4. Once compilation finishes, click **locate** in the popup notification, or find the generated APK at:
   ```
   app/build/outputs/apk/debug/app-debug.apk
   ```

### 3. Command-Line Build (Optional)
You can also assemble the debug APK directly via the terminal:
```bash
gradle :app:assembleDebug
```
The output file will be generated in `app/build/outputs/apk/debug/app-debug.apk`.

---

## 🔒 Production Security & Authorization Notice

> **IMPORTANT NOTICE REGARDING PRODUCTION DEPLOYMENT:**
> 
> - **Client-Side Role Policy vs. Server-Side Enforcement:**
>   The current application enforces strict client-side role-based UI access control (`ScreenAccessPolicy.kt`) and in-app navigation guards. For a production release with multi-tenant cloud synchronization, **real authentication (e.g. Firebase Auth, OAuth 2.0 / OIDC)** and **server-side authorization rules** (e.g., Firestore Security Rules, backend JWT validation) must be implemented to verify and authorize all API requests and database queries on the server.
> - **Debug Keystore vs. Production Keystore:**
>   The debug APK uses standard Android debug signing. For Google Play Store distribution or production signing, generate an official upload keystore and configure release signing credentials in your CI/CD environment or secure secrets manager.

---

## 🏢 Society Structure & Configuration

- **Flat & Household:** Flat K-2903, Kaveh Tower, 29th Floor
- **Flat Owner:** Amit K Roy
- **Family Members:**
  - Nitika (Spouse)
  - Ayaansh Roy (Son, Age 10)
  - Abha Roy (Mother)
- **Executive Committee:**
  - **Chairman:** Mr. Sujit
  - **Hon. Secretary:** Anupam Roy
  - **Hon. Treasurer:** Gautam
