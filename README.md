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

## 📸 App Showcase

<table>
  <tr>
    <td align="center">
      <strong>Login Screen</strong><br/>
      <img src="https://github.com/user-attachments/assets/c67655be-5d0e-4849-baa9-f4216991b7f7" alt="Login Screen" width="60%"/>
    </td>
    <td align="center">
      <strong>Home Screen</strong><br/>
      <img src="https://github.com/user-attachments/assets/8c3709a7-a3a2-4a0b-a181-fd440928611b" alt="Home Screen" width="60%"/>
    </td>
  </tr>
  <tr>
    <td align="center">
      <strong>Navigation Drawer</strong><br/>
      <img src="https://github.com/user-attachments/assets/4fea1fb4-f47e-4028-a9fc-3bc562e887cf" alt="Navigation Drawer" width="60%"/>
    </td>
    <td align="center">
      <strong>Switching between themes</strong><br/>
      <img src="https://github.com/user-attachments/assets/7ac5f9a9-f333-44e9-b1b9-76729e9c95db" alt="Theme Selector" width="60%"/>
    </td>
  </tr>
  <tr>
    <td align="center">
      <strong>Locations Table</strong><br/>
      <img src="https://github.com/user-attachments/assets/ff125222-b4f1-4b8b-aed2-0d12133679c1" alt="Locations Table" width="60%"/>
    </td>
    <td align="center">
      <strong>Update Location</strong><br/>
      <img src="https://github.com/user-attachments/assets/fb45e7a0-f061-45ef-a5dc-770f7e2bffae" alt="Update Location" width="60%"/>
    </td>
  </tr>
</table>

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
