# Vivi Music - Golden Hacker Edition

## Changes Made

### Stream Extraction Fixes
- **Removed dead clients**: `ANDROID_VR`, `TVHTML5`, `TVHTML5_SIMPLY_EMBEDDED_PLAYER`, `ANDROID_VR_NO_AUTH`, `ANDROID_VR_1_61_48`, `ANDROID_VR_1_43_32`, `ANDROID_NO_SDK`, `VISIONOS`, `ANDROID_MUSIC`, `IOS_MUSIC`
- **Main client** switched from `ANDROID_VR_1_43_32` to `IOS` (tier-1 mobile, direct URLs, no PoToken)
- **Fallback order**: `WEB_REMIX` -> `IOS` -> `WEB_CREATOR` -> `ANDROID_CREATOR` -> `IPAD_OS` -> `MOBILE` -> `WEB`
- **Bumped newpipeextractor** from `v0.25.2` to `v0.26.5`
- Botguard bypass now working

### Golden Hacker Theme
- **Obsidian Black** (#0A0A0C) background with **Gold** (#D4AF37) accents
- Custom dark and light color schemes
- Toggle in Settings > Theme > "Golden Hacker" switch
- Gold 1px border on AccentCard when active
- Monospace font + gold color for technical metadata (bitrate, codec, sample rate, etc.) in media info

### UI Improvements
- Staggered shimmer reveal animation (`ShimmerHost.kt`)
- Enhanced empty placeholder with fade-in, subtitle support, and primary color tint (`EmptyPlaceholder.kt`)
- AccentCard composable for featured content with gradient overlay (`Items.kt`)

## Build

### On-device (Termux / termux-on-device)
```bash
./gradlew assembleArm64FossRelease
```

### Requires
- JDK 17+
- Android SDK (compileSdk 37)
- NDK 27.0.12077973

## Files Modified
| File | Change |
|------|--------|
| `gradle/libs.versions.toml` | newpipeextractor bumped to v0.26.5 |
| `innertube/.../YouTubeClient.kt` | Purged 9 dead client definitions |
| `innertube/.../InnerTube.kt` | UA routing updated |
| `innertube/.../PlayerBody.kt` | Integrity dimensions unchanged |
| `app/.../YTPlayerUtils.kt` | MAIN_CLIENT=IOS, fallback reordered |
| `app/.../CanvasArtworkPlayer.kt` | Removed ANDROID_VR UA branch |
| `app/.../PreferenceKeys.kt` | Added `GoldenHackerKey` |
| `app/.../Theme.kt` | Golden Hacker color schemes, `goldenBorder()`, `rememberGoldenHacker()` |
| `app/.../ThemeScreen.kt` | Golden Hacker toggle in theme settings |
| `app/.../MainActivity.kt` | Wired `GoldenHackerKey` into `vivimusicTheme()` |
| `app/.../Items.kt` | `goldenBorder()` applied to `AccentCard` |
| `app/.../ShowMediaInfo.kt` | Monospace + gold for values when Golden Hacker active |
| `app/.../ShimmerHost.kt` | Staggered reveal animation |
| `app/.../EmptyPlaceholder.kt` | Enhanced empty state UI |
