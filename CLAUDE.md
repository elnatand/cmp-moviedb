# CLAUDE.md

## Project Context
Kotlin Multiplatform Movie DB app (Android/iOS). Architecture: Feature-based multi-module, Clean Architecture, MVI.
**Key Stack:** Compose Multiplatform, Koin, Ktor, Room (Movies), DataStore (Prefs/Pagination), Navigation 3, Coil 3.

## Critical Development Rules
- **STRICT: No Hardcoded Strings.** All user-facing text must go to `Strings.xml` (4 languages).
    - **Action:** If new string needed, call skill: `/add-strings <key> "<text>"`
    - **Usage:** Use `stringResource(Res.string.key)`.
- **Category Abstraction:** Follow the Enum-based pattern for Movies/TV. Use `/add-category` skill for new categories. No hardcoded branches in UI/VM.
- **Dependency Injection:** Use `koinViewModel()` in Composables. Define modules per feature.
- **Convention Plugins:** Update `build-logic/` for common config; don't duplicate in module `build.gradle.kts`.

## Module Map
- `core/model`: Domain models (Movie, TvShow, AppResult).
- `core/network`: Ktor client + TMDB API.
- `core/database`: Room (Offline-first Movies).
- `core/ui`: Design system, Resources, Shared components.
- `features/`: Isolated feature modules (Movies, TV, Search, etc.).
- `composeApp/`: Navigation graph (`RootNavGraph.kt`) and DI entry point.

## Architecture Patterns
- **MVI:** ViewModel (State/Events/Actions) + Repository (Passive `Flow<List<T>>`).
- **Offline-first:** Movies are cached in Room; TV Shows are currently in-memory.
- **Language Sync:** `LanguageChangeCoordinator` handles cache invalidation on locale change.

## Commands & Skills
- **Build:** `./gradlew :composeApp:installDebug` (Android), `./gradlew test`.
- **New Feature:** Use `/add-feature-module` skill.
- **API Keys:** Reads from `secrets.properties` (Android) and `Secrets.plist` (iOS). **Never commit keys.**