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
- `core/model`: Cross-feature shared models (`AppResult`, `CastMember`, `Video`, `AppTheme`, `AppLanguage`). Feature-specific domain models (Movie, TvShow, etc.) live in each feature's `domain` module.
- `core/network`: Ktor client + TMDB API.
- `core/database`: Room (Offline-first Movies).
- `core/ui`: Design system, Resources, Shared components, navigation routes.
- `features/`: Isolated feature modules (Movies, TV, Search, etc.). Each is split into `presentation`/`domain`/`data`.
    - **Exception — `profile`:** settings-only feature, `presentation` module only. It consumes the shared `core/datastore` `AppSettingsPreferences` (an already-segregated abstraction) directly, so no `domain`/`data` layer is warranted. Don't treat it as the template for data-backed features.
- `composeApp/`: Navigation graph (`RootNavGraph.kt`) and DI entry point.

## Architecture Patterns
- **MVI:** ViewModel (State/Events/Actions) + Repository (Passive `Flow<List<T>>`).
    - **Repositories are passive:** `observe*()` only exposes the cached stream — no side effects. The ViewModel triggers the initial/refresh load explicitly via `load*NextPage()` (CQS: keep queries and commands separate).
- **Offline-first:** Movies are cached in Room; TV Shows are currently in-memory.
- **Module deps default to `implementation`.** Even types in a module's public API (e.g. `core.model`'s `AppResult`, `Flow`) are declared `implementation`, not `api`; consumers declare the foundational deps they use directly. Reserve `api` for the rare case where consumers genuinely shouldn't need to know about the transitive module.
- **Language Sync:** `LanguageChangeCoordinator` handles cache invalidation on locale change.
- **Tests live in each module's `src/commonTest`** (not the feature grouping dir). The android target has no host-test source set, so `commonTest` runs on the iOS sim target: `./gradlew :features:<feature>:presentation:iosSimulatorArm64Test` (or `allTests`). To also run them on the JVM, add `withHostTest {}` to the android target in the convention plugin.

## Commands & Skills
- **Build:** `./gradlew :composeApp:installDebug` (Android), `./gradlew test`.
- **New Feature:** Use `/add-feature-module` skill.
- **API Keys:** Reads from `secrets.properties` (Android) and `Secrets.plist` (iOS). **Never commit keys.**