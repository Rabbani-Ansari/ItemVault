# ItemVault 📦

A modern Android inventory management app built with **Jetpack Compose** and **Material 3** design guidelines.

## ✨ Features

- **Item Management** - Add, edit, and delete items with ease
- **Image Support** - Capture or select multiple images per item with full-screen viewer
- **Star Ratings** - Rate items from 1 to 5 stars
- **Offline-First** - Local Room database with sync capability
- **Responsive UI** - Supports both portrait and landscape orientations
- **Modern Design** - Clean Material 3 interface with smooth animations

## 🛠️ Tech Stack

| Category | Technology |
|----------|-----------|
| **Language** | Kotlin |
| **UI** | Jetpack Compose |
| **Design System** | Material 3 |
| **Database** | Room |
| **Networking** | Retrofit + Gson |
| **Image Loading** | Coil |
| **Navigation** | Navigation Compose |
| **Architecture** | MVVM |
| **Async** | Kotlin Coroutines |

## 📁 Project Structure

```
app/src/main/java/com/locae/itemvault/
├── api/                    # Retrofit API service & client
├── data/
│   ├── model/              # Data classes (Item entity)
│   └── repository/         # Repository & SyncManager
├── navigation/             # Navigation graph
├── presentation/
│   ├── add_edit/           # Add/Edit item screen & ViewModel
│   ├── components/         # Reusable UI components
│   └── home/               # Home screen & ViewModel
└── utils/                  # Helper utilities
```

## 🚀 Getting Started

### Prerequisites
- Android Studio Hedgehog or later
- JDK 8+
- Android SDK 26+ (minimum)

### Installation

1. Clone the repository:
   ```bash
   git clone https://github.com/Rabbani-Ansari/ItemVault.git
   ```

2. Open in Android Studio

3. Sync Gradle and run on device/emulator

## 📱 Screenshots

*Coming soon*

## 📄 License

This project is open source and available under the MIT License.

---

Made with ❤️ using Jetpack Compose
