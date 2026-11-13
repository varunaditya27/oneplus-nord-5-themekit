<div align="center">

# Glossary

Plain-language terminology for ATELIER's native Android launcher and design system.

</div>

## Android and build tooling

**Android SDK (Software Development Kit):** Tools and API definitions used to compile and test an Android application. ATELIER currently compiles using API 36.

**API (Application Programming Interface):** A supported way for two software components to communicate. The launcher uses Android's public APIs instead of modifying OxygenOS.

**Android Gradle Plugin (AGP):** The build plugin that turns Android source code and resources into an APK.

**Gradle:** A build tool that downloads declared dependencies, runs tasks and produces application packages.

**Jetpack Compose:** Android's declarative interface toolkit. The Kotlin code describes what the launcher looks like for the current state instead of manually changing each view.

**Compose BOM (Bill of Materials):** A version catalogue that keeps Compose libraries on compatible releases.

**APK (Android Package):** An installable Android application file. The CI-generated debug APK is not a signed production release.

**Continuous integration (CI):** Automated checks run on every qualifying code change, such as unit tests and a debug build.

**HOME role:** Android's designation for the application that handles the home screen. The user chooses the default launcher through the system prompt.

**LauncherApps:** An Android service for discovering and launching supported apps, including applications in accessible user profiles.

**Package visibility:** Android rules that limit which installed apps a program can discover. ATELIER asks about launchable activities, not an unrestricted inventory.

**WallpaperService:** An Android component that draws a live wallpaper. Its drawing should pause when the wallpaper becomes invisible.

**Quick Settings tile:** A user-added control in Android's system controls. It can expose an ATELIER action without letting the app redesign the entire system panel.

**OxygenOS:** OnePlus's version of Android. It still controls the secure lock screen, notification shade and biometric effects.

Sources: [RoleManager](https://developer.android.com/reference/android/app/role/RoleManager), [LauncherApps](https://developer.android.com/reference/android/content/pm/LauncherApps), [WallpaperService](https://developer.android.com/reference/android/service/wallpaper/WallpaperService), [Jetpack Compose BOM](https://developer.android.com/develop/ui/compose/bom).

## Interface and animation

**Design token:** A named value shared by interface components, such as `ink` for a background color. Tokens keep different screens consistent.

**Vector path:** A shape described with points and curves, so it stays crisp at different screen sizes. ATELIER's clock numerals use custom paths.

**Renderer:** Code that draws an image or animation. Silkfield currently uses a CPU-backed Canvas renderer, not a GPU shader.

**Shader:** Code generally executed by graphics hardware to calculate color or position across an image. This is a future option for the wallpaper.

**Reduced motion:** A setting that limits optional interface animations for comfort and accessibility.

**Surface:** A major interface view, such as Home, Library, Command, Controls or Settings.

**Command palette:** A searchable interface that maps short user commands to explicitly supported actions. It is not a shell with arbitrary execution privileges.

**State:** The current settings and content that determine what the interface displays, for example whether Focus mode is active.

**Material palette:** A small group of background, foreground, border and accent colors used consistently across screens.
