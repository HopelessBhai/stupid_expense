# Stupid Expense

Stupid Expense is a minimalistic, no bullshit app for budget expense management. It behaves like a homescreen widget: open, punch in the latest spend, and move on.

## Highlights
- **Frictionless input** – one text field and a `+` button to keep a running total.
- **Always-on persistence** – total is stored with DataStore, so numbers survive app restarts and device reboots.
- **Quick reset flow** – overflow menu opens a dedicated reset screen with a single confirmation tap.
- **Pure Compose UI** – built entirely with Material 3 components for a lightweight feel and dark/light theme support.
- **Homescreen widget quick add** – a tappable pill launches a dialog-style activity for number entry, so it works consistently on Android 14 and below.

## Homescreen widget
1. Long-press on your launcher, pick **Widgets**, and drop *Stupid Expense Widget* on the home screen.
2. Tap the quick-add pill or `+` button; a lightweight dialog pops up with a numeric field plus **Add**/**Cancel** actions.
3. Enter the amount, hit **Add**, and the dialog closes while the widget refreshes with the updated total.
4. Tap anywhere outside the pill to jump into the full Compose experience.

> RemoteInput/EditText inside widgets is no longer required. The dialog flow works across Android 14 and older devices, matching the platform restriction that inline typing only becomes available starting Android 15.

## How it is wired
- `MainActivity` renders the spend input surface, while `ResetActivity` handles wiping the saved total.
- `TotalViewModel` owns UI state, validates input, and exposes intents for adding or resetting amounts.
- `TotalRepository` wraps `DataStore<Preferences>` so persistence is decoupled from the UI layer and is reused by widgets.
- `StupidExpenseWidgetProvider` renders the homescreen UI and launches `WidgetQuickAddActivity` for numeric input.
- `WidgetQuickAddActivity` is a dialog-themed activity that validates the number, saves it via the repository, and broadcasts widget updates.

```
app/src/main/java/com/example/stupidexpense
├── MainActivity.kt          # Input + running total screen
├── ResetActivity.kt         # Confirmation screen for clearing the saved total
├── data/TotalRepository.kt  # DataStore persistence + helper for widget additions
├── ui/TotalViewModel.kt     # State holder shared by both activities
└── widget/
    ├── StupidExpenseWidgetProvider.kt  # RemoteViews + PendingIntent wiring
    └── dialog/WidgetQuickAddActivity.kt # Dialog-style quick add flow
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

