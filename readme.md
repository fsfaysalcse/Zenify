# Zenify

Zenify is a modern Android music player app built with Kotlin, Jetpack Compose, and Android's latest architecture components. It features audio playback, playlist management, favorites, and queue controls, all with persistent state and a clean, reactive UI.

## Features

- Browse and play local audio files
- Manage playlists and playback queue
- Mark tracks as favorites
- Persistent playback state (remembers last played track and state)
- Mini player with play/pause, next, previous, shuffle, and repeat controls
- Error handling with user feedback
- Built using Jetpack Compose for a modern UI

## Tech Stack

- **Kotlin**
- **Jetpack Compose**
- **Android Architecture Components** (ViewModel, StateFlow, SavedStateHandle)
- **Media3** for audio playback
- **DataStore** for persistent state
- **Koin** for dependency injection
- **Coroutines** for async operations

## Project Structure

- `app/src/main/java/com/faysal/zenify/ui/viewModels/` — ViewModels for UI state and logic
- `app/src/main/java/com/faysal/zenify/data/datastore/` — DataStore managers for playback and playlist state
- `app/src/main/java/com/faysal/zenify/data/service/` — Music service connection
- `app/src/main/java/com/faysal/zenify/domain/` — Use cases and repositories
- `app/src/main/java/com/faysal/zenify/ui/` — UI components and screens

## Getting Started

1. **Clone the repository:**

2. **Open in Android Studio**  
   Open the project folder in Android Studio (2023.1.1 or newer recommended).

3. **Build the project**  
   Let Gradle sync and build the project.

4. **Run the app**  
   Connect an Android device or use an emulator, then click Run.

## Dependency Injection

Zenify uses Koin for DI. All core dependencies (ViewModels, DataStore, ServiceConnection) are provided in `AppModule.kt`.  
Make sure to add the following to your Koin module if not present:

```kotlin
single { com.faysal.zenify.data.datastore.PlaylistDataStore(androidContext()) }
```

## Contributing
Contributions are welcome! If you find a bug or have a feature request, please open an issue or submit a pull request.
