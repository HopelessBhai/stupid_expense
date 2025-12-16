# Stupid Expense

Stupid Expense is a minimalistic, no bullshit app for budget expense management. It behaves like a homescreen widget: open, punch in the latest spend, and move on.

## Highlights
- **Frictionless input** – one text field and a `+` button to keep a running total.
- **Automatic SMS reading** – automatically monitors incoming SMS messages from supported banks, parses debit transactions, and adds them to your total without any manual input.
- **Always-on persistence** – total is stored with DataStore, so numbers survive app restarts and device reboots.
- **Quick reset flow** – overflow menu opens a dedicated reset screen with a single confirmation tap.
- **Pure Compose UI** – built entirely with Material 3 components for a lightweight feel and dark/light theme support.
- **Homescreen widget quick add** – a tappable pill launches a dialog-style activity for number entry, so it works consistently on Android 14 and below.

## SMS Auto-Add Feature
The app automatically monitors incoming SMS messages from supported banks and adds debit transactions to your total. No manual input needed! When you receive a debit transaction SMS from a supported bank, the app will automatically parse the amount and add it to your running total.

### Supported Banks
The following banks are currently supported (detected by SMS sender keywords):
- **HDFC Bank** (keyword: HDFC)
- **ICICI Bank** (keyword: ICICI)
- **SBI** - State Bank of India (keyword: SBI)
- **AXIS Bank** (keyword: AXIS)
- **Kotak Mahindra Bank** (keyword: KOTAK)
- **PNB** - Punjab National Bank (keyword: PNB)
- **IDFC First Bank** (keyword: IDFC)
- **YES Bank** (keyword: YES)
- **IndusInd Bank** (keyword: INDUS)
- **BOB** - Bank of Baroda (keyword: BOB)
- **DCB** - Development Credit Bank (keyword: DCBANK)

### Adding Your Bank
If your bank is not in the list above, you can easily add it by modifying the code:
1. Open `app/src/main/java/com/example/stupidexpense/BankSmsParser.kt`
2. Find the `BANK_SENDER_KEYWORDS` list (around line 3-6)
3. Add your bank's SMS sender keyword to the list. This is usually the bank's short name or abbreviation as it appears in SMS sender IDs (e.g., if SMS comes from "MYBANK", add "MYBANK" to the list)
4. Rebuild and install the app

**Example:** If you want to add "Federal Bank", and their SMS sender ID contains "FEDERAL", add `"FEDERAL"` to the `BANK_SENDER_KEYWORDS` list.

**Note:** The app requires `RECEIVE_SMS` and `READ_SMS` permissions to use this feature. You'll be prompted to grant these permissions when you first launch the app.

## Homescreen widget
1. Long-press on your launcher, pick **Widgets**, and drop *Stupid Expense Widget* on the home screen.
2. Tap the quick-add pill or `+` button; a lightweight dialog pops up with a numeric field plus **Add**/**Cancel** actions.
3. Enter the amount, hit **Add**, and the dialog closes while the widget refreshes with the updated total.
4. Tap anywhere outside the pill to jump into the full Compose experience.

## How it is wired
- `MainActivity` renders the spend input surface, while `ResetActivity` handles wiping the saved total.
- `TotalViewModel` owns UI state, validates input, and exposes intents for adding or resetting amounts.
- `TotalRepository` wraps `DataStore<Preferences>` so persistence is decoupled from the UI layer and is reused by widgets.
- `SmsReceiver` listens for incoming SMS broadcasts, parses debit transactions via `BankSmsParser`, and automatically adds them to the total.
- `BankSmsParser` extracts transaction amounts and types from bank SMS messages using regex patterns and keyword matching.
- `StupidExpenseWidgetProvider` renders the homescreen UI and launches `WidgetQuickAddActivity` for numeric input.
- `WidgetQuickAddActivity` is a dialog-themed activity that validates the number, saves it via the repository, and broadcasts widget updates.

```
app/src/main/java/com/example/stupidexpense
├── MainActivity.kt          # Input + running total screen
├── ResetActivity.kt         # Confirmation screen for clearing the saved total
├── SmsReceiver.kt           # BroadcastReceiver for SMS messages
├── BankSmsParser.kt         # Parser for extracting transaction data from SMS
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
4. If you just want to try it out, grab the latest signed APK from the Releases page of this repository.
5. Otherwise, run the `app` configuration to deploy on a device or emulator (minSdk 26).

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

