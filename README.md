# Bilibili Android 模拟应用

这是一个模拟Bilibili应用的Android项目，用于AI操作测试。该应用不需要联网，所有数据都是预设的静态数据。

## 项目概述

本项目采用MVP架构模式，使用Jetpack Compose构建UI，实现了Bilibili应用的核心页面和功能。

## 技术栈

- **语言**: Kotlin
- **最小SDK**: API 33 (Android 13)
- **目标SDK**: API 33
- **编译SDK**: API 36
- **架构模式**: MVP (Model-View-Presenter)
- **UI框架**: Jetpack Compose
- **依赖管理**: Gradle (Kotlin DSL)

## 主要依赖

- **AndroidX Core KTX**: Android核心库
- **Jetpack Compose**: 声明式UI框架
- **Material 3**: Material Design 3组件库
- **Gson 2.10.1**: JSON解析库
- **Coil 2.5.0**: 图片加载库
- **Material Icons Extended**: 扩展图标库

## 项目结构

```
app/src/main/java/com/example/bilibili/
├── MainActivity.kt                    # 主Activity，包含底部导航
├── model/                             # 数据模型层
│   ├── User.kt                        # 用户数据模型
│   ├── Video.kt                       # 视频数据模型
│   ├── Comment.kt                     # 评论数据模型
│   ├── Danmaku.kt                     # 弹幕数据模型
│   ├── UPMaster.kt                    # UP主数据模型
│   ├── WatchHistory.kt                # 观看历史模型
│   └── TaskState.kt                   # 任务状态模型
├── presenter/                         # 业务逻辑层
│   └── MePresenter.kt                 # "我的"页面业务逻辑
├── view/                              # 视图层
│   ├── MeTab.kt                       # "我的"页面UI
│   ├── RecommendTab.kt                # 推荐页面UI（占位）
│   └── ActionTab.kt                   # 动态页面UI（占位）
└── ui/theme/                          # 主题配置
    ├── Color.kt
    ├── Theme.kt
    └── Type.kt
```

```
app/src/main/assets/
├── data/                              # 预设数据
│   ├── user.json                      # 用户数据（小明）
│   ├── videos.json                    # 视频数据
│   ├── comments.json                  # 评论数据
│   ├── danmaku.json                   # 弹幕数据
│   ├── upmasters.json                 # UP主数据
│   └── watch_history.json             # 观看历史
└── avatar/                            # 头像图片资源
    └── *.jpg                          # 各种头像图片
```

## 已实现功能

### 主界面 (MainActivity)
- 底部导航栏，包含5个标签：
  - **首页** (推荐页面 - 待开发)
  - **关注** (动态页面 - 待开发)
  - **+** (发布按钮 - 占位)
  - **会员购** (占位页面)
  - **我的** (已完成)

### "我的"页面 (MeTab)

#### 顶部工具栏
- 互连图标
- 扫一扫图标
- 皮肤切换图标
- 夜间模式图标

#### 中部信息栏（固定不滚动）
- **用户信息区**:
  - 用户头像（圆形，带粉色边框）
  - 用户昵称：小明
  - 用户等级：LV5
  - 会员状态：正式会员
  - B币余额：0.0
  - 硬币数量：988
  - 空间入口

- **统计数据区**:
  - 动态数量：1
  - 关注数量：87
  - 粉丝数量：0

- **会员中心横幅**:
  - "成为大会员"提示
  - "会员中心"按钮

- **快捷功能区**:
  - 离线缓存
  - 历史记录
  - 我的收藏
  - 稍后再看

#### 底部服务列表（可滚动）
- **发布视频提示卡片**
- **游戏中心**:
  - 我的游戏
  - 我的预约
  - 找游戏
  - 游戏排行榜

- **我的服务**:
  - 我的课程
  - 免流量服务
  - 个性装扮
  - 我的钱包
  - 会员购
  - 我的直播
  - 漫画
  - 必火推广
  - 创作中心
  - 社区中心
  - 哔哩哔哩公益
  - 工房
  - 能量加油站

- **更多服务**:
  - 联系客服
  - 听视频
  - 未成年人守护
  - 设置

## 数据说明

### 用户数据 (user.json)
- 用户ID: user001
- 用户名: 小明
- 头像: avatar/spring.jpg
- 等级: 5
- B币: 0.0
- 硬币: 988
- 会员状态: 正式会员
- 动态/关注/粉丝: 1/87/0

所有数据都存储在 `app/src/main/assets/data/` 目录下的JSON文件中，应用启动时通过Gson解析加载。

## MVP架构说明

### Model层
负责数据的定义和管理，所有数据模型类都在 `model/` 包中。

### View层
使用Jetpack Compose构建UI，所有Composable函数都在 `view/` 包中。View层只负责UI展示，不包含业务逻辑。

### Presenter层
处理业务逻辑，连接Model和View。例如 `MePresenter` 负责加载用户数据、处理用户交互等。

## 开发规范

1. **遵循MVP模式**: 严格分离Model、View、Presenter三层
2. **避免单文件过长**: 将复杂组件拆分为多个小组件
3. **使用预设数据**: 所有数据都从assets/data目录加载
4. **图标选择**: 优先使用Material Icons，找不到合适的可用emoji
5. **不实现动态功能**: 所有数据都是静态的，不需要网络请求
6. **不需要数据库**: 使用JSON文件存储数据即可

## 构建与运行

### 构建项目
```bash
./gradlew build
```

### 运行Debug版本
```bash
./gradlew installDebug
```

### 清理项目
```bash
./gradlew clean
```

## 待开发功能

根据页面树定义，以下页面待后续开发：
- [ ] RecommendTab - 推荐视频页面
- [ ] ActionTab - 关注动态页面
- [ ] 会员购页面

## 注意事项

1. 本应用仅用于AI操作测试，不连接真实的Bilibili服务器
2. 所有交互都是本地模拟，不会产生真实的数据变化
3. 头像图片存储在 `assets/avatar/` 目录下
4. 不需要申请任何特殊权限（网络、通知等）

## 版本历史

### v1.0.0 (2025-01-18)
- ✅ 实现MVP架构基础框架
- ✅ 创建数据模型层 (User, Video, Comment, Danmaku, UPMaster, WatchHistory, TaskState)
- ✅ 实现MainActivity主界面和底部导航
- ✅ 完成"我的"页面 (MeTab)
  - 顶部工具栏
  - 中部用户信息栏（固定）
  - 底部服务列表（可滚动）
- ✅ 添加RecommendTab和ActionTab占位页面
- ✅ 集成Gson、Coil等必要依赖
- ✅ 配置用户预设数据 (小明)

## 许可证

本项目仅供学习和研究使用。
