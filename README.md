# 📱 ClockInGo

<p align="center">
  <a href="https://github.com/dufacoga/ClockInGo/issues"><img src="https://img.shields.io/github/issues/dufacoga/clockingo"/></a>
  <a href="https://github.com/dufacoga/ClockInGo/stargazers"><img src="https://img.shields.io/github/stars/dufacoga/ClockInGo"/></a>
  <a href="https://github.com/dufacoga/ClockInGo/network/members"><img src="https://img.shields.io/github/forks/dufacoga/ClockInGo"/></a>
  <a href="https://github.com/dufacoga/ClockInGo/commits/master"><img src="https://img.shields.io/github/last-commit/dufacoga/ClockInGo"/></a>
  <a href="https://github.com/dufacoga/ClockInGo/blob/master/CODE_OF_CONDUCT.md"><img src="https://img.shields.io/badge/contributions-welcome-brightgreen.svg?style=flat"/></a>
  <a href="https://github.com/dufacoga/ClockInGo/blob/master/LICENSE"><img src="https://img.shields.io/github/license/dufacoga/ClockInGo"/></a>
</p>

A modern time attendance application for Android built with **Kotlin** and **Jetpack Compose**. ClockInGo allows users to scan QR coded locations, take selfies as proof of presence and manage users, roles and locations. The app works offline thanks to a local Room database and synchronizes data with the [FlexiQueryAPI](https://github.com/dufacoga/FlexiQueryAPI) when a connection is available.

---

## ✨ Features

- 📷 **QR scanning and selfie capture** using CameraX and ML Kit
- 💾 **Offline first** with Room database and WorkManager sync
- 🗂️ **MaterialDataTable** components via [MaterialDataTableLibrary](https://github.com/dufacoga/MaterialDataTableLibrary)
- 🎨 **Multiple color themes** and light/dark/system modes
- 🌐 **English and Spanish** translations
- 🔄 **MVVM architecture** with repositories and view models

---

## 📂 Project Structure

```
app/
 ├── src/main/
 │   ├── java/com/example/clockingo/
 │   │   ├── data/        # local & remote data layer
 │   │   ├── domain/      # models, repositories and use cases
 │   │   ├── presentation # Compose UI screens & view models
 │   │   └── ui/theme     # theme definitions
 │   └── res/             # resources and translations
 └── build.gradle.kts
```

---

## 🔧 How to Run Locally

```bash
chmod +x gradlew
./gradlew installDebug
```

Then install the generated APK on your device or open the project with Android Studio.

---

## 🛠️ Built With

- [Kotlin](https://kotlinlang.org/) and [Jetpack Compose](https://developer.android.com/jetpack/compose)
- [CameraX](https://developer.android.com/training/camerax) & [ML Kit Barcode Scanning](https://developers.google.com/ml-kit/vision/barcode-scanning)
- [Room](https://developer.android.com/jetpack/androidx/releases/room) & [WorkManager](https://developer.android.com/topic/libraries/architecture/workmanager)
- [Retrofit](https://square.github.io/retrofit/) connecting to [FlexiQueryAPI](https://github.com/dufacoga/FlexiQueryAPI)
- [MaterialDataTableLibrary](https://github.com/dufacoga/MaterialDataTableLibrary)

---

## 📄 License

This project is open source under the [MIT License](LICENSE).

---

## 👤 Author

**Douglas Cortes**  
💼 [LinkedIn](https://www.linkedin.com/in/dufacoga)
🌐 [dufacoga.github.io](https://dufacoga.github.io)
