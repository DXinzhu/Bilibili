# Bilibili GUI Agent 数据模型设计文档

本目录包含了用于 GUI Agent 任务完成情况检查的所有数据结构。

## 数据模型概览

### 1. Video.kt - 视频数据模型
**用途**: 维护视频的基本信息和状态

**核心字段**:
- `videoId`: 视频唯一标识
- `isLiked`: 点赞状态（用于检查点赞/取消点赞任务）
- `isFavorited`: 收藏状态（用于检查收藏/取消收藏任务）
- `isShared`: 分享状态（用于检查分享任务）
- `danmakuList`: 弹幕列表（用于检查弹幕发送任务）
- `commentList`: 评论列表（用于检查评论发布任务）

**支持的操作**:
- `toggleLike()`: 切换点赞状态
- `toggleFavorite()`: 切换收藏状态
- `markAsShared()`: 标记为已分享
- `addDanmaku()`: 添加弹幕
- `addComment()`: 添加评论

---

### 2. Danmaku.kt - 弹幕数据模型
**用途**: 维护视频的弹幕信息

**核心字段**:
- `danmakuId`: 弹幕唯一标识
- `videoId`: 所属视频ID
- `content`: 弹幕内容
- `videoTime`: 视频播放时间点
- `type`: 弹幕类型（滚动/顶部/底部）

---

### 3. Comment.kt - 评论数据模型
**用途**: 维护视频的评论信息

**核心字段**:
- `commentId`: 评论唯一标识
- `videoId`: 所属视频ID
- `content`: 评论内容
- `parentCommentId`: 父评论ID（支持评论回复）
- `replyList`: 回复列表

**支持的操作**:
- `addReply()`: 添加回复
- `toggleLike()`: 切换评论点赞状态

---

### 4. UPMaster.kt - UP主数据模型
**用途**: 维护UP主的基本信息和关注状态

**核心字段**:
- `upMasterId`: UP主唯一标识
- `isFollowed`: 关注状态（用于检查关注/取消关注任务）
- `videoList`: UP主的视频列表
- `fansCount`: 粉丝数

**支持的操作**:
- `toggleFollow()`: 切换关注状态
- `addVideo()`: 添加视频到UP主列表

---

### 5. WatchHistory.kt - 观看历史数据模型
**用途**: 维护用户的视频观看历史记录

**核心字段**:
- `videoId`: 视频ID
- `watchTime`: 观看时间戳
- `watchProgress`: 观看进度（0.0-1.0）
- `lastWatchPosition`: 上次观看位置
- `isFinished`: 是否看完

**支持的操作**:
- `updateProgress()`: 更新观看进度
- `updateWatchDuration()`: 更新观看时长

---

### 6. TaskState.kt - 任务状态管理器
**用途**: 集中管理所有GUI Agent任务的状态和数据

**主要功能模块**:

#### 视频操作
- `getOrCreateVideo()`: 获取或创建视频对象
- `toggleVideoLike()`: 点赞/取消点赞视频
- `toggleVideoFavorite()`: 收藏/取消收藏视频
- `shareVideo()`: 分享视频
- `isVideoLiked()`, `isVideoFavorited()`, `isVideoShared()`: 状态检查

#### 弹幕操作
- `sendDanmaku()`: 发送弹幕
- `getVideoDanmakuList()`: 获取弹幕列表
- `isDanmakuExists()`: 检查弹幕是否存在

#### 评论操作
- `publishComment()`: 发布评论
- `getVideoCommentList()`: 获取评论列表
- `isCommentExists()`: 检查评论是否存在

#### UP主操作
- `getOrCreateUPMaster()`: 获取或创建UP主对象
- `toggleFollowUPMaster()`: 关注/取消关注UP主
- `isFollowingUPMaster()`: 检查是否关注
- `getFollowingList()`: 获取关注列表

#### 观看历史操作
- `addWatchHistory()`: 添加观看历史
- `getWatchHistoryList()`: 获取历史列表
- `isInWatchHistory()`: 检查视频是否在历史中

#### 统计查询
- `getLikedVideoIds()`: 获取所有点赞视频
- `getFavoritedVideoIds()`: 获取所有收藏视频
- `clearAll()`: 清空所有状态

---

## 任务与数据结构映射

| 任务类型 | 相关数据结构 | 检查方法 |
|---------|-------------|---------|
| 视频点赞/取消点赞 | Video, TaskState | `isVideoLiked(videoId)` |
| 发送弹幕 | Danmaku, Video, TaskState | `isDanmakuExists(videoId, content)` |
| 发布视频评论 | Comment, Video, TaskState | `isCommentExists(videoId, content)` |
| 关注/取消关注UP主 | UPMaster, TaskState | `isFollowingUPMaster(upMasterId)` |
| 收藏/取消收藏视频 | Video, TaskState | `isVideoFavorited(videoId)` |
| 分享视频 | Video, TaskState | `isVideoShared(videoId)` |
| 观看历史 | WatchHistory, TaskState | `isInWatchHistory(videoId)` |

---

## 使用示例

```kotlin
// 创建任务状态管理器
val taskState = TaskState()

// 1. 视频点赞任务
val video = taskState.getOrCreateVideo("v001", "测试视频", "up001", "测试UP主")
taskState.toggleVideoLike("v001")
val isLiked = taskState.isVideoLiked("v001")  // true

// 2. 发送弹幕任务
val danmaku = Danmaku(
    danmakuId = "d001",
    videoId = "v001",
    content = "这个视频真棒！",
    senderId = "user001",
    senderName = "测试用户",
    videoTime = 30.5f
)
taskState.sendDanmaku(danmaku)
val danmakuExists = taskState.isDanmakuExists("v001", "这个视频真棒！")  // true

// 3. 发布评论任务
val comment = Comment(
    commentId = "c001",
    videoId = "v001",
    content = "非常精彩的内容",
    authorId = "user001",
    authorName = "测试用户"
)
taskState.publishComment(comment)
val commentExists = taskState.isCommentExists("v001", "非常精彩的内容")  // true

// 4. 关注UP主任务
val upMaster = taskState.getOrCreateUPMaster("up001", "测试UP主")
taskState.toggleFollowUPMaster("up001")
val isFollowing = taskState.isFollowingUPMaster("up001")  // true

// 5. 收藏视频任务
taskState.toggleVideoFavorite("v001")
val isFavorited = taskState.isVideoFavorited("v001")  // true

// 6. 分享视频任务
taskState.shareVideo("v001")
val isShared = taskState.isVideoShared("v001")  // true

// 7. 观看历史
val history = WatchHistory(
    historyId = "h001",
    videoId = "v001",
    videoTitle = "测试视频",
    upMasterId = "up001",
    upMasterName = "测试UP主"
)
taskState.addWatchHistory(history)
val inHistory = taskState.isInWatchHistory("v001")  // true
```

---

## 设计原则

1. **单一职责**: 每个数据类只负责管理自己的数据和状态
2. **可扩展性**: 设计支持未来添加新的任务类型
3. **状态追踪**: 所有关键操作都有对应的状态标志和时间戳
4. **关系维护**: 通过ID关联不同实体之间的关系
5. **便于测试**: 提供清晰的状态检查方法，方便验证任务完成情况

---

## 注意事项

- 所有数据结构都是纯数据模型，不包含业务逻辑验证
- TaskState 是内存中的状态管理器，需要考虑持久化方案
- 时间戳使用 `System.currentTimeMillis()` 便于排序和查询
- 所有可变状态都使用 `var` 标记，便于状态更新
