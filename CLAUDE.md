# Claude Code Rules for CMP MovieDB

This file defines coding standards and conventions for the Kotlin Multiplatform Movie Database project.

## Project Overview
- **Architecture**: Clean Architecture with MVVM pattern
- **Platform**: Kotlin Multiplatform (Android/iOS)
- **UI Framework**: Compose Multiplatform
- **Database**: Room
- **Networking**: Ktor
- **DI**: Koin
- **Design System**: Material 3

## Coding Standards

### Kotlin Conventions
- Use `camelCase` for variables and functions
- Use `PascalCase` for classes and interfaces
- Use `UPPER_SNAKE_CASE` for constants
- Prefer `val` over `var` when possible
- Use explicit types for public APIs

### Architecture Patterns
- Follow Clean Architecture layers: `data`, `domain`, `presentation`
- Use Repository pattern for data access
- Implement MVVM with ViewModels and UiState
- Separate business logic from UI components

### File Organization
```
core/
├── data/           # Data layer (repositories, network, database)
├── database/       # Room database entities and DAOs
├── model/          # Domain models
├── network/        # Network clients and DTOs
└── ui/            # Shared UI components

features/
├── movies/        # Movie-related features
│   ├── data/      # Feature-specific data layer
│   ├── model/     # Feature-specific models
│   └── ui/        # Feature UI screens and components
└── tv-shows/      # TV show features (future)
```

### Compose UI Guidelines
- Use Material 3 design system components
- Create reusable composables in `core/ui`
- Follow single responsibility principle for composables
- Use `@Preview` for component testing
- Prefer stateless composables when possible

### Data Layer Standards
- Use `@Serializable` for data classes with API responses
- Add `@SerialName()` annotations for JSON field mapping
- Implement mapping between data and domain models
- Use nullable types for optional API fields
- Handle API errors gracefully

### Database Conventions
- Use Room for local data persistence
- Create separate entities for each table
- Implement DAOs with Flow for reactive data
- Use database migrations for schema changes
- Store page information for pagination

### Networking Standards
- Use TMDB API with proper authentication
- Implement proper error handling
- Use DTOs (Data Transfer Objects) for API responses
- Map DTOs to domain models
- Cache images and data appropriately

### State Management
- Use `MutableStateFlow` and `StateFlow` for UI state
- Create sealed interfaces/classes for complex states
- Handle loading, success, and error states
- Implement proper state updates with `update {}`

### String Resources
- Always use string resources from `core/ui/src/commonMain/composeResources/values/Strings.xml`
- Never hardcode user-facing strings
- Use meaningful resource names

### Security Best Practices
- Store API keys securely using BuildConfig (Android) and plist (iOS)
- Use `expect/actual` pattern for platform-specific configurations
- Exclude sensitive files from version control
- Implement proper certificate pinning for production

## Code Quality

### Testing
- Write unit tests for ViewModels and repositories
- Use mocking for external dependencies
- Test both success and error scenarios
- Maintain good test coverage

### Documentation
- Add KDoc comments for public APIs
- Include parameter descriptions for complex functions
- Document architecture decisions in README
- Keep this CLAUDE.md file updated

### Performance
- Use `LazyColumn`/`LazyVerticalGrid` for lists
- Implement proper image loading with caching
- Use `derivedStateOf` for expensive calculations
- Avoid unnecessary recompositions

## Commands to Run

### Build Commands
```bash
./gradlew build                    # Build all modules
./gradlew :composeApp:assembleDebug # Build Android debug
```

### Testing Commands
```bash
./gradlew test                     # Run unit tests
./gradlew connectedCheck          # Run instrumented tests
```

### Code Quality
```bash
./gradlew ktlintCheck             # Kotlin linting
./gradlew ktlintFormat            # Auto-format code
```

## API Integration

### TMDB API Setup
1. Get API key from https://developer.themoviedb.org/reference/intro/getting-started
2. Add to `local.properties`: `TMDB_API_KEY=your_key_here`
3. Create `iosApp/Secrets.plist` with apiKey entry
4. Use `expect/actual` pattern for cross-platform access

### Image Loading
- Base URL: `https://image.tmdb.org/t/p/w500/`
- Use `ImageLoader` component from `core/ui`
- Handle missing images gracefully

## UI/UX Guidelines

### Design Principles
- Follow Material Design 3 guidelines
- Maintain consistent spacing (8dp grid system)
- Use proper color schemes for light/dark themes
- Implement smooth animations and transitions

### Component Structure
- Create reusable components in `core/ui`
- Use composition over inheritance
- Implement proper accessibility support
- Handle different screen sizes and orientations

### Error Handling
- Show user-friendly error messages
- Use SnackBars for temporary messages
- Implement retry mechanisms
- Handle network connectivity issues

## Git Conventions

### Commit Messages
- Use conventional commits format
- Start with type: feat, fix, docs, style, refactor, test
- Include scope when relevant: `feat(movies): add pagination`
- Keep messages under 50 characters for title

### Branch Naming
- Use descriptive names: `feature/movie-pagination`
- Follow pattern: `type/short-description`
- Create PRs for code review

## Dependencies Management

### Adding Dependencies
- Add to appropriate module's `build.gradle.kts`
- Use version catalogs when possible
- Document why dependency was added
- Keep dependencies up to date

### Common Libraries
- **UI**: Compose Multiplatform, Material 3
- **Database**: Room, SQLite
- **Network**: Ktor, Kotlinx Serialization
- **DI**: Koin
- **Async**: Coroutines, Flow
- **Image Loading**: Custom ImageLoader component

## Notes for Claude Code

When working on this project:
1. Always check existing patterns before creating new ones
2. Use proper error handling for all network calls
3. Implement loading states for better UX
4. Follow the established architecture patterns
5. Add string resources instead of hardcoding text
6. Test changes on both Android and iOS when possible
7. Keep the TODO list in README updated
8. Run linting and formatting before committing