# MyoScopeAlert

MyoScopeAlert is an Android application designed to analyze heartbeat audio files and predict heart conditions such as Myocardial Infarction (MI) or Normal. The app provides a user-friendly interface for recording, uploading, and analyzing heartbeat sounds, as well as viewing prediction history and managing user profiles.
This app integrated with a IoT digital stethoscope via Bluetooth Classic, enabled semi real-time heartbeat recording, analysis, and visualization with intuitive UI/UX supporting on-device TensorFlow Lite AI

## Features

- **Heartbeat Audio Analysis and Visualization:** Record or pick heartbeat audio files (WAV format) and analyze them using AI-based prediction.
- **Offline Prediction with Embedded AI Model:** Supports both offline prediction using on-device models and online prediction via API.
- **Recording History:** View and manage a history of analyzed files and their results.
- **User Authentication:** Login and profile management for secure access.
- **File Management:** Pick, preview, and manage audio files within the app.
- **Permissions Handling:** Handles all necessary permissions for audio recording, storage, and Bluetooth.
- **Modern UI:** Built with Jetpack Compose for a responsive and modern user experience.

## Technology Stack

- **Kotlin** & **Jetpack Compose** for UI
- **Android Architecture Components** (ViewModel, StateFlow, etc.)
- **Coroutines** for asynchronous operations
- **Media APIs** for audio recording and playback
- **Retrofit/OkHttp** for API communication (if used)
- **DataStore** for local data persistence
- **Material 3** for UI components

## Screenshots

![myoscopr-tumbnail.png](archive%2Fmyoscopr-tumbnail.png)

## Getting Started

1. **Clone the repository:**
   ```sh
   git clone https://github.com/bonifasiustrg/MyoScopeAlert.git
   ```
2. **Open in Android Studio or VS Code.**
3. **Configure API endpoints and keys** in the appropriate files if needed.
4. **Build and run** the app on your device or emulator.

## Permissions

The app requests the following permissions:
- Audio Recording
- Read/Write External Storage
- Bluetooth (for device connectivity)
- Internet (for online prediction)

## License
