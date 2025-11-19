# Stupid Expense

Stupid Expense is a minimalistic, no bullshit app for budget expense management. It behaves like a homescreen widget: open, punch in the latest spend, and move on.

## Highlights
- **Frictionless input** – one text field and a `+` button to keep a running total.
- **Always-on persistence** – total is stored with DataStore, so numbers survive app restarts and device reboots.
- **Quick reset flow** – overflow menu opens a dedicated reset screen with a single confirmation tap.
- **Pure Compose UI** – built entirely with Material 3 components for a lightweight feel and dark/light theme support.

## How it is wired
- `MainActivity` renders the spend input surface, while `ResetActivity` handles wiping the saved total.
- `TotalViewModel` owns UI state, validates input, and exposes intents for adding or resetting amounts.
- `TotalRepository` wraps `DataStore<Preferences>` so persistence is decoupled from the UI layer.

```
app/src/main/java/com/example/stupidexpense
├── MainActivity.kt          # Input + running total screen
├── ResetActivity.kt         # Confirmation screen for clearing the saved total
├── data/TotalRepository.kt  # DataStore persistence
└── ui/TotalViewModel.kt     # State holder shared by both activities
```

## Getting started
1. Install Android Studio (Hedgehog or newer) with the Android 34 SDK and make sure you have JDK 17.
2. Clone this repo and open the root folder in Android Studio.
3. Sync the Gradle project; it uses the Kotlin 1.9.24 toolchain and Compose BOM 2024.09.01.
4. Run the `app` configuration to deploy on a device or emulator (minSdk 26).

Command-line build:
```bash
./gradlew assembleDebug        # Windows: gradlew.bat assembleDebug
```

## Testing
Unit tests live under `app/src/test`. Run them with:
```bash
./gradlew test
```

## Philosophy
Keep expenses stupid-simple: no categories, no charts, no syncing. Just a total that you can bump up all day and reset when you're ready to start fresh.

