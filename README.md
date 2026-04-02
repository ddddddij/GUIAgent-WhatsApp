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
| New chat bottom sheet | ✅ Done |
| ChatDetailActivity | ✅ Done |
| ContactInfoActivity | ✅ Done |
| YouTab | ✅ Done |
| UpdatesTab | ✅ Done |
| CallsTab | ✅ Done |
| New call bottom sheet | ✅ Done |
| CommunitiesTab | ✅ Done |
| New community bottom sheet | ✅ Done |

## Project Structure
```
app/src/main/java/com/example/whatsapp_sim/
├── ChatDetailActivity.kt             # Chat detail entry activity
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
│   │   ├── SettingsItem.kt
│   │   ├── CallContactItem.kt         # Selectable contact row (with circle toggle)
│   │   ├── InviteContactItem.kt       # Invite contact row (with Invite button)
│   │   └── ContactInfoSettingItem.kt  # Generic settings row (single/double line, Toggle, chevron)
│   ├── screen/
│   │   ├── chatdetail/                # Chat detail screen + ViewModel + components
│   │   ├── contactinfo/               # ContactInfoActivity screen + ViewModel
│   │   ├── chats/                     # ChatsTab, New chat bottom sheet, ViewModels
│   │   ├── you/                       # YouTab screen + ViewModel
│   │   ├── updates/                   # UpdatesTab screen + ViewModel
│   │   ├── calls/                     # CallsTab screen + ViewModel + NewCallBottomSheet
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
- ChatsTab items now open `ChatDetailActivity` with shared in-memory send behavior and list preview refresh on return
- ChatsTab "+" now opens a searchable `New chat` bottom sheet with in-memory conversation creation
- CallsTab "+" now opens a `NewCallBottomSheet` with searchable contacts, multi-select (圆圈切换), alphabetical grouping with sticky headers + right index bar, and Invite section
