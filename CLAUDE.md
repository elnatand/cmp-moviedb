# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview
Kotlin Multiplatform Movie Database app using Compose Multiplatform, targeting Android and iOS with a feature-based multi-module architecture following Clean Architecture and MVI pattern.

## Build Commands

### Basic Build Operations
```bash
# Clean and build entire project
./gradlew clean build

# Build Android app
./gradlew :composeApp:assembleDebug

# Build specific module (example)
./gradlew :core:network:build

# Build release (ensure secrets are configured)
./gradlew :composeApp:assembleRelease
```

### Running the App
```bash
# Run on Android
./gradlew :composeApp:installDebug

# iOS: Open in Android Studio and select iOS target from run configuration
```

### Testing
```bash
# Run all tests
./gradlew test

# Run tests for specific module
./gradlew :core:data:test
```

## Architecture

### Multi-Module Structure
The project uses **convention plugins** in `build-logic/convention/` to standardize module configuration:

- **`moviedb.kotlin.multiplatform`** (KotlinMultiplatformConventionPlugin): Configures KMP with iosArm64 + iosSimulatorArm64 targets, applies `-Xexpect-actual-classes` flag
- **`moviedb.kotlin.composeMultiplatform`** (ComposeMultiplatformConventionPlugin): Applies Compose Multiplatform and compiler plugins
- **`moviedb.android.library`**: Standardizes Android library configuration (compileSdk 36, minSdk 24)

All versions are centralized in `gradle/libs.versions.toml` using Gradle version catalogs.

### Module Organization
```
core/
  ├── common/       # Shared utilities (AppDispatchers for coroutines)
  ├── model/        # Domain models (Movie, TvShow, AppResult<T>, AppLanguage, AppTheme)
  ├── network/      # Ktor HTTP client, TMDB API client, DTOs
  ├── database/     # Room database (movies only; TV shows are in-memory)
  ├── datastore/    # DataStore for preferences and pagination state
  ├── data/         # Repository implementations (movies, tv-shows, person)
  └── ui/           # Shared UI components, Material 3 design system, Compose resources

features/
  ├── movies/       # Movies list + details screens (offline-first with Room)
  ├── tv-shows/     # TV shows list + details screens (in-memory)
  ├── person/       # Cast/crew details screen
  ├── search/       # Multi-type search (movies, TV, people)
  └── profile/      # User profile, language/theme settings

composeApp/         # Main app module with navigation and DI setup
```

### MVI Pattern with Simplified Repositories

**Repository**: Passive data provider
- Returns `Flow<List<T>>` for reactive data streaming
- Handles data operations (fetch, cache, invalidation)
- No UI state management (loading/error states)
- Example: `MoviesRepository.observeMovies()` returns `Flow<List<Movie>>`

**ViewModel**: Active state coordinator
- Manages UI state (loading, error, success)
- Coordinates repository calls
- Handles user events via `onEvent()`
- Emits one-time UI actions via Channel (e.g., show snackbar)
- Example: `MoviesViewModel` collects repository flows and manages `MoviesUiState`

**Key Principles**:
- Repository maintains data integrity
- ViewModel manages UI state presentation
- Clear separation of concerns between layers

### Category Abstraction Pattern
Both Movies and TV Shows use enum-based category abstraction (`MovieCategory`, `TvShowCategory`) following the Open-Closed Principle:

- **Adding new categories**: Simply add to the enum; no code changes needed in ViewModel/Repository
- **Map-based state**: `moviesByCategory: Map<MovieCategory, List<Movie>>`
- **Automatic iteration**: ViewModels iterate over `Category.entries` to handle all categories
- **No hardcoded branches**: No when statements on category types

This pattern appears in:
- `MoviesRepository` / `TvShowsRepository` interfaces
- `MoviesViewModel` / `TvShowsViewModel` implementations
- UI state classes

### Data Storage Strategy

**Movies**: Offline-first with Room database
- 4 entity tables: `MovieEntity`, `MovieDetailsEntity`, `CastMemberEntity`, `VideoEntity`
- Foreign key relationships between entities
- Reactive updates via Flow from DAOs
- Cache persists across app restarts

**TV Shows**: In-memory storage
- Uses `MutableStateFlow` in repository
- Data cleared on app restart
- No Room entities (planned for future)

**Preferences**: DataStore
- App settings (language, theme)
- Pagination state (persists current page per category)
- Survives app restarts

### Language-Aware Cache Invalidation
`LanguageChangeCoordinator` observes language changes and triggers repository `clearAndReload()`:
- Clears stale cached content
- Reloads initial pages with new language
- Ensures UI displays content in selected language

### Error Handling Strategy
**Initial Load Errors**: Block UI with error screen
- User sees full-screen error with retry button
- No cached data available to display

**Pagination Errors**: Non-blocking snackbar
- Keep cached data visible
- Show temporary error message at bottom
- User can continue browsing existing content

## Platform-Specific Configuration

### expect/actual Pattern
Used for platform-specific implementations:

**API Key Loading** (`core/network/model/PlatformKeys.kt`):
- Common: `expect val TMDB_API_KEY: String`
- Android: Reads from `BuildConfig.TMDB_API_KEY` (generated from `secrets.properties`)
- iOS: Reads from `Secrets.plist` bundle resource

**Database Drivers** (`core/database/Database.kt`):
- Android: Uses Android SQLite driver
- iOS: Uses native iOS SQLite driver

**DataStore Path** (`core/datastore/DataStoreFactory.kt`):
- Android: Uses `Context.filesDir`
- iOS: Uses `NSFileManager` document directory

### API Key Setup (Required)
**Android**:
1. Create `secrets.properties` in project root:
   ```properties
   TMDB_API_KEY=your_api_key_here
   ```
2. Key is read via Gradle and injected into `BuildConfig` in `core/network/build.gradle.kts`

**iOS**:
1. Copy template: `cp iosApp/iosApp/Secrets.plist.template iosApp/iosApp/Secrets.plist`
2. Edit `Secrets.plist`:
   ```xml
   <key>tmdbApiKey</key>
   <string>your_api_key_here</string>
   ```
3. File must be in Xcode target's "Copy Bundle Resources"

Both files are gitignored. Never commit API keys.

## Dependency Injection with Koin

**Module Structure**:
- Each feature module defines its own Koin module (e.g., `MoviesModule`, `TvShowsModule`)
- Core modules define DI for repositories, network, database
- Platform-specific modules:
  - `AndroidModule` in `core/network/androidMain` (provides Android-specific network config)
  - `IosModule` in `composeApp/iosMain` (provides iOS-specific dependencies)

**ViewModel Integration**:
- Use `koinViewModel()` in Composables to inject ViewModels
- ViewModels are scoped to navigation backstack entries
- Navigation 3 + Koin integration via `koin-compose-navigation` library

## String Resources

All user-facing strings are in `core/ui/src/commonMain/composeResources/values/Strings.xml` with translations for 4 languages:
- English (`values/Strings.xml`)
- Hebrew (`values-he/Strings.xml`) - RTL
- Arabic (`values-ar/Strings.xml`) - RTL
- Hindi (`values-hi/Strings.xml`)

**Configuration** (`core/ui/build.gradle.kts`):
```kotlin
compose.resources {
    publicResClass = true
    packageOfResClass = "com.elna.moviedb.resources"
    generateResClass = always
}
```

### STRICT RULE: No Hardcoded Strings

**NEVER hardcode user-facing strings in code.** This is a critical rule that must be followed without exception.

When writing code that requires new user-facing strings:

1. **First, check if the string already exists** in `Res.string.*`
2. **If it's a new string, automatically invoke the `/add-strings` skill**:
   - Use the Skill tool to call `/add-strings <key> "<text>"`
   - The skill will automatically add the string to all 4 language files
   - Wait for the skill to complete before proceeding
3. **After the string is added**, generate code using `stringResource(Res.string.key_name)`

**Examples**:
- ❌ **WRONG**: `Text("Save Changes")` (hardcoded)
- ✅ **CORRECT**: First invoke `/add-strings save_button "Save Changes"`, then use `Text(stringResource(Res.string.save_button))`

**Workflow Example**:
```
User: "Add a logout button"
Claude: *Invokes /add-strings logout_button "Logout"*
Claude: *Generates code with stringResource(Res.string.logout_button)*
```

**Why this matters**:
- The app supports 4 languages with proper translations
- Hebrew and Arabic are RTL languages requiring special handling
- Hardcoded strings break localization and create maintenance issues
- All strings must be translatable across all supported languages

**Important**: Always use the `/add-strings` skill automatically when new strings are needed. Never ask the user to run it manually.

## Common Development Tasks

### Adding a New Feature Module
1. Create module structure: `features/new-feature/`
2. Add `build.gradle.kts` with:
   ```kotlin
   plugins {
       alias(libs.plugins.moviedb.kotlinMultiplatform)
       alias(libs.plugins.moviedb.composeMultiplatform)
   }
   ```
3. Define sourceSets with dependencies
4. Create Koin DI module for feature
5. Add to `settings.gradle.kts`
6. Add dependency in `composeApp/build.gradle.kts`

### Adding a New Movie/TV Category
Thanks to category abstraction:
1. Add enum value to `MovieCategory` or `TvShowCategory` in `core/model`
2. Add corresponding API endpoint in network client
3. Add string resource for category name
4. **No changes needed** in ViewModel or Repository implementations

### Modifying UI Theme
Material 3 theme defined in:
- `core/ui/src/commonMain/kotlin/com/elna/moviedb/core/ui/theme/Theme.kt`
- `core/ui/src/commonMain/kotlin/com/elna/moviedb/core/ui/theme/Color.kt`
- System theme detection via expect/actual in `SystemTheme.kt`

## Navigation

Uses **Navigation 3** (Compose Multiplatform navigation):
- Type-safe navigation with `@Serializable` route classes
- Navigation graph in `composeApp/src/commonMain/kotlin/com/elna/moviedb/navigation/RootNavGraph.kt`
- Deep linking support via serializable routes
- Koin integration for ViewModel scoping

## Image Loading

Uses **Coil 3** with:
- Ktor network engine (shares HTTP client with app)
- SVG support via `coil-svg`
- Automatic disk/memory caching
- Base URL: `https://image.tmdb.org/t/p/w500/` for TMDB images

Configuration in `core/ui/build.gradle.kts`:
```kotlin
implementation(libs.coil.compose)
implementation(libs.coil.ktor)
implementation(libs.coil.svg)
```

## Troubleshooting

**Java Version Issues**:
- Use JDK 17-21 (JDK 25 has compatibility issues)
- Check: `./gradlew --version`

**Build Failures**:
- Run `./gradlew clean` to clear build cache
- Check that API keys are configured (Android + iOS)
- Ensure KMP plugin is installed in Android Studio

**iOS Simulator Issues**:
- Use ARM64 simulator on Apple Silicon Macs
- Use x86_64 simulator on Intel Macs (add `iosX64()` target if needed)

**Room/KSP Issues**:
- Room uses KSP for code generation
- Clean build if entities are modified: `./gradlew clean build`
- Check that `@Database`, `@Entity`, `@Dao` annotations are correct

**API Key Not Found**:
- Verify `secrets.properties` exists in project root
- Verify `Secrets.plist` exists in `iosApp/iosApp/`
- Check that files are not gitignored accidentally (only `secrets.properties` and `Secrets.plist` should be ignored)

## Notes for Development

- **Convention Plugins**: When adding common config, update plugins in `build-logic/convention/` rather than duplicating in module build files
- **expect/actual**: Use for platform-specific code; add `-Xexpect-actual-classes` flag (already configured in convention plugins)
- **Version Catalog**: Add new dependencies to `gradle/libs.versions.toml`, not directly to module build files
- **Database Migrations**: Room migrations are required for schema changes; don't forget to increment database version
- **Pull-to-Refresh**: Implemented in Movies screen; uses `clearAndReload()` pattern that can be reused for other features
- **Pagination State**: Persisted via DataStore; survives app restarts; managed per category
