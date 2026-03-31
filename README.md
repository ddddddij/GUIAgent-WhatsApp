# WhatsApp Sim

A local WhatsApp UI simulation app for Android, built with Jetpack Compose + MVVM architecture.

## Tech Stack
- **Language**: Kotlin
- **UI**: Jetpack Compose + Material3
- **Architecture**: MVVM
- **Data**: Local JSON assets (no database, no network)
- **Dependencies**: Gson, lifecycle-viewmodel-compose, material-icons-extended

## Completed Pages
| Page | Status |
|------|--------|
| ChatsTab | ✅ Done |
| YouTab | ✅ Done |
| UpdatesTab | ✅ Done |
| CallsTab | ✅ Done |
| CommunitiesTab | ✅ Done |

## Project Structure
```
app/src/main/java/com/example/whatsapp_sim/
├── data/
│   ├── local/AssetsHelper.kt          # JSON loader
│   └── repository/
│       ├── ChatRepositoryImpl.kt
│       ├── AccountRepositoryImpl.kt
│       ├── CallRepository.kt
│       └── CommunityRepository.kt
├── domain/
│   ├── model/                         # Data models
│   └── repository/                    # Repository interfaces
├── ui/
│   ├── components/                    # Shared UI components
│   │   ├── WhatsAppBottomNavigation.kt
│   │   ├── EmptyTabScreen.kt
│   │   └── SettingsItem.kt
│   ├── screen/
│   │   ├── chats/                     # ChatsTab screen + ViewModel
│   │   ├── you/                       # YouTab screen + ViewModel
│   │   ├── updates/                   # UpdatesTab screen + ViewModel
│   │   ├── calls/                     # CallsTab screen + ViewModel
│   │   └── communities/               # CommunitiesTab screen + ViewModel
│   └── theme/
└── MainActivity.kt

app/src/main/assets/data/
├── accounts.json
├── conversations.json
├── messages.json
└── ...
```

## Notes
- All button interactions show "Coming soon" toast
- Current user is `user_001` (Alex Johnson)
- No real networking or database; all data is preset and static
