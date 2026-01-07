# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build Commands

```bash
# Build debug APK
./gradlew assembleDebug

# Build release APK
./gradlew assembleRelease

# Run unit tests
./gradlew test

# Run a single test class
./gradlew test --tests "com.example.boondocks_led.ExampleUnitTest"

# Run instrumented tests (requires connected device/emulator)
./gradlew connectedAndroidTest

# Clean build
./gradlew clean

# Check for dependency updates
./gradlew dependencyUpdates
```

## Architecture Overview

**BoondocksLED** is an Android app for controlling LED lighting systems via Bluetooth Low Energy (BLE). It manages up to 4 independent LED controllers with different configurations.

### Core Patterns
- **MVVM** with Jetpack Compose UI
- **Hilt** for dependency injection
- **Coroutines + Flow** for async operations and reactive state

### Package Structure
- `ble/` - BLE connection management, GATT operations, auto-reconnect logic
- `data/` - Business logic for LED controllers, JSON message building, repository
- `ui/components/` - Reusable Compose components (color picker, brightness sliders)
- `ui/ledcontroller/` - Main screen ViewModel and state
- `ui/navigation/` - Tab navigation for 4 controllers

### Controller Types
Each LED controller supports one of three modes:
- **RGBW**: Single RGBA color channel
- **RGB+1**: RGB strip + separate white channel
- **4CHAN**: Four independent channels (R, G, B, W)

### BLE Communication
- Scans for device named "BoonLED"
- Messages are JSON-encoded and written to GATT characteristics
- Write queue serializes BLE operations to prevent collisions
- Auto-reconnect with exponential backoff (250ms → 10s cap)

### Key UUIDs (in BleManagerImpl.kt)
- `LedSet` - Set LED colors
- `BrightSet` - Set brightness values
- `AllOff` - Turn all LEDs off
- `CtrlTypeSet` - Configure controller type

### Message Format Examples
```json
// Set RGBW color on controller 1
{ "1": { "R": 255, "G": 0, "B": 0, "W": 0 } }

// Set brightness
{ "1": { "R": 3, "G": 3, "B": 3, "W": 3 } }

// Turn off controller
{ 1: "off" }
```

### State Flow
1. `LEDController` holds mutable state for one controller
2. `LEDControllerViewModel` collects state and handles UI events
3. `BleManager` queues and sends JSON messages over BLE
4. Brightness changes: live updates during drag, committed on release

## Required Permissions
- `BLUETOOTH_SCAN`, `BLUETOOTH_CONNECT` - BLE operations
- `ACCESS_FINE_LOCATION`, `ACCESS_COARSE_LOCATION` - Required for BLE scanning
