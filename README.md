# Darija Stickers 🇲🇦

Android Kotlin project with two original demo WhatsApp sticker packs (3 static WebP stickers each). Arabic UI.

## Build free APK
1. Install Android Studio (current stable), Android SDK Platform 35, JDK 17.
2. Open this folder in Android Studio, allow Gradle sync to download Android Gradle Plugin 8.7.3 and Kotlin 2.0.21.
3. Build > Build Bundle(s) / APK(s) > Build APK(s).
4. Find `app/build/outputs/apk/debug/app-debug.apk` and install it on your Android phone.
5. Install WhatsApp, open Darija Stickers, press **إضافة إلى واتساب** and confirm.

For GitHub Actions builds, see `.github/workflows/android.yml`.

## Customization
- Sticker packs: `app/src/main/res/raw/sticker_packs.json`
- Images: `app/src/main/assets/` (512x512 WebP, tray 96x96 WebP)
- Arabic UI: `app/src/main/java/com/darijastickers/app/MainActivity.kt`
- Package / authority: update **both** AndroidManifest.xml and StickerProvider.kt and `applicationId` together.
- Every pack should contain at least 3 stickers. Validate WhatsApp static-sticker size limits (<=100 KB each) before publishing.

## Important
This is a **source project**, not a precompiled APK. WhatsApp import has **not been tested on a real phone** in this environment; validate on a device. The sample artwork is demo only. WhatsApp may update its integration contract. No commercial relationship with WhatsApp or Meta.
