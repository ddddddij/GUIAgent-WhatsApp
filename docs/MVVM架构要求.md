# Android MVVM Architecture with Jetpack Compose
- 这是一个标准的Android MVVM架构项目，使用Jetpack Compose构建UI。
## 1. 项目结构
app/src/main/
├── assets/                  # 资源文件目录 ⭐
│   ├── config/             # 配置文件
│   │   └── app_config.json
│   └── data/              # 数据文件（可选）
├── java/com/example/mvvm/
│   ├── data/                    # 数据层
│   │   ├── local/              # 本地数据源（Room Database）
│   │   │   ├── AppDatabase.kt
│   │   │   ├── AssetsHelper.kt
│   │   │   ├── UserDao.kt
│   │   │   └── entity/
│   │   │       └── UserEntity.kt
│   │   ├── mapper/             # 数据映射器
│   │   │   └── UserMapper.kt
│   │   └── repository/         # 数据仓库实现
│   │       └── RepositoryImpl.kt
│   ├── domain/                  # 领域层
│   │   ├── model/              # 领域模型
│   │   │   └── User.kt
│   │   └── repository/         # 仓库接口
│   │       └── Repository.kt
│   ├── ui/                      # UI层
│   │   ├── MainActivity.kt
│   │   ├── components/         # 可复用的Composable组件 ⭐
│   │   │   ├── LoadingIndicator.kt
│   │   │   ├── ErrorMessage.kt
│   │   │   ├── UserCard.kt
│   │   │   └── EmptyState.kt
│   │   ├── navigation/         # 导航
│   │   │   └── NavGraph.kt
│   │   ├── screen/             # 屏幕
│   │   │   ├── home/          # 包含特定功能组件的 Screen
│   │   │   │   ├── components/ # 该 Screen 专用的组件
│   │   │   │   │   ├── HomeTopBar.kt
│   │   │   │   │   ├── HomeSearchBar.kt
│   │   │   │   │   ├── HomeFilterChips.kt
│   │   │   │   │   ├── HomeUserList.kt
│   │   │   │   │   └── HomeContent.kt
│   │   │   │   ├── HomeScreen.kt
│   │   │   │   └── HomeViewModel.kt
│   │   │   └── settings/      # 不包含 components 的简单 Screen
│   │   │       ├── SettingsScreen.kt
│   │   │       └── SettingsViewModel.kt
│   │   └── theme/              # 主题
│   │       ├── Color.kt
│   │       ├── Theme.kt
│   │       └── Type.kt
│   ├── common/                       # 通用模块
│   │   ├── utils/                   # 工具类
│   │   ├── constants/               # 常量
│   │   └── extension/               # Kotlin扩展函数
│   ├── network/                      # 网络层
│   └── di/                      # 依赖注入
│       ├── MVVMApplication.kt
│       └── AppModule.kt
└── res/                        # 资源文件
├── fonts/             # 字体文件（可选）
└── values/
├── strings.xml
└── themes.xml

## 2. 架构说明
### 2.1 MVVM架构层次
1. UI层 (Presentation Layer)
- ViewModel: 管理UI相关的数据，处理业务逻辑
- Composable: Jetpack Compose UI组件
- components/: 可复用的Composable组件 ⭐
- 使用StateFlow进行状态管理

2. Domain层 (Domain Layer)
- Model: 领域模型，业务实体
- Repository: 仓库接口，定义数据操作契约

3. Data层 (Data Layer)
- RepositoryImpl: 仓库实现，协调本地和远程数据源
- Local: Room数据库，本地数据持久化
- Remote: Retrofit API，网络数据获取
- Mapper: 数据转换器，DTO/Entity与Domain Model之间的转换

### 2.2 组件组织原则
1. 可复用组件 (ui/components/)
- 放置可在多个Screen中复用的通用UI组件
- 例如：LoadingIndicator、ErrorMessage、UserCard、EmptyState等
- 组件应该是无状态的，通过参数接收数据

2. 特定功能组件 (screen/[功能]/components/)
- 如果组件只在一个Screen中使用，且该Screen功能复杂，可以创建components/子目录
- 例如：HomeScreen有搜索、筛选等复杂功能，创建了screen/home/components/目录
- 包含的组件：HomeTopBar、HomeSearchBar、HomeFilterChips、HomeUserList、HomeContent等

3. 简单Screen（不包含components）
- 如果Screen功能简单，UI逻辑不复杂，可以直接在Screen文件中实现
- 例如：SettingsScreen功能简单，直接在SettingsScreen.kt中实现所有UI
- 不需要创建components/子目录

4. 组件命名规范
- 使用描述性的名称，如UserCard、LoadingIndicator
- 特定功能组件使用功能前缀，如HomeTopBar、HomeSearchBar
- 遵循PascalCase命名

5. 何时创建components子目录？
- ✅ Screen功能复杂，包含多个独立的UI模块
- ✅ 需要将Screen拆分成多个小组件以提高可读性
- ✅ 组件可能在未来被复用或扩展

## 3. 扩展方式
### 3.1 添加新功能
1. 在 domain/model 中创建领域模型
2. 在 domain/repository 中定义仓库接口
3. 在 data 层实现数据源和仓库
4. 在 ui/screen 中创建ViewModel和Composable
5. 在 ui/components 中创建可复用的组件
6. 在 di/AppModule.kt 中配置依赖注入

### 3.2 添加新组件
7. 在 ui/components/ 目录下创建新的组件文件
8. 组件应该是纯函数式的Composable
9. 通过参数接收数据和回调函数
10. 保持组件的单一职责原则

## 4. 注意事项
- ViewModel需要使用 @HiltViewModel 注解
- 所有Activity需要使用 @AndroidEntryPoint 注解
- 可复用组件应该放在 ui/components/ 目录下
- 特定功能的组件可以放在对应的 screen/ 目录下
- Assets 文件放在 app/src/main/assets/ 目录下
- 可以通过 AssetManager 访问 assets 中的文件

## 5. 常见错误(需要避免，若出现则纠正)
- 把所有 Composable 都放在一个 Screen 文件里（超过 300 行就该拆分）
- ViewModel 里直接写 UI 逻辑
- 在 Composable 里直接调用 Repository
- 把业务组件放到 ui/components/（那里只放通用组件，能复用的再放）
- 对于需要返回上一级的页面，页面跳转时使用navController.navigate("xxx")，不要加额外参数