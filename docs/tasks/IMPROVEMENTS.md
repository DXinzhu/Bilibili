# Bilibili 任务验证改进总结

## 改进概述

将 31 个任务中的 20 个从 hardcode 模式改进为动态验证模式，参考修改参考文件夹的示例实现。

## 改进方案

### 核心思路
1. **从数据源获取答案**：使用 ADB 从设备拉取 APP 数据文件
2. **动态计算正确答案**：解析 JSON 数据并计算实际答案
3. **灵活验证格式**：支持多种答案表达格式
4. **保留回退机制**：当数据源不可用时，回退到原 hardcode 验证

### 数据文件映射

| 数据文件 | 用途 | 使用任务 |
|---------|------|----------|
| `files/related_videos.json` | 相关视频列表 | eval_13, eval_21 |
| `files/following_list.json` | 关注列表 | eval_11, eval_15 |
| `files/favorites.json` | 收藏列表 | eval_23, eval_24 |
| `files/following_dynamics.json` | 关注动态 | eval_14 |
| `files/video_comments.json` | 视频评论 | eval_17, eval_19, eval_27 |
| `files/home_videos.json` | 首页视频 | eval_4 |
| `files/shop_products.json` | 会员购商品 | eval_6, eval_30 |
| `files/settings.json` | 应用设置 | eval_2, eval_18, eval_28 |
| `files/user_profile.json` | 用户配置 | eval_25, eval_31 |
| `files/live_streams.json` | 直播列表 | eval_29 |

## 改进任务详情

### 1. 数据统计类（6个）

#### eval_21 - 相关视频数量
- **原模式**：hardcode "4"
- **改进**：从 `related_videos.json` 读取并统计数量
- **优势**：数据变化时自动适应

#### eval_15 - 互粉up主数量
- **原模式**：hardcode "1"
- **改进**：从 `following_list.json` 统计 `is_mutual_follow=true` 的数量

#### eval_24 - 收藏视频数量
- **原模式**：hardcode "13"
- **改进**：从 `favorites.json` 统计收藏列表长度

#### eval_14 - 关注动态点赞+播放量总和
- **原模式**：hardcode "1554"
- **改进**：从 `following_dynamics.json` 计算 `like_count + play_count` 总和

#### eval_19 - 等级最低的评论点赞数
- **原模式**：hardcode "67"
- **改进**：从 `video_comments.json` 查找 `user_level` 最低的评论

#### eval_17 - 评论区点赞总数
- **原模式**：hardcode "378"
- **改进**：从 `video_comments.json` 统计点赞总数（排除当前用户）

### 2. 数值计算类（4个）

#### eval_4 - 视频点赞+投币数
- **原模式**：hardcode "5.1"
- **改进**：从 `home_videos.json` 查找罗翔老师视频并计算

#### eval_6 - 会员购商品总价
- **原模式**：hardcode "269.6"
- **改进**：从 `shop_products.json` 累加所有商品价格

#### eval_11 - UP主粉丝数
- **原模式**：hardcode "23.5"
- **改进**：从 `following_list.json` 查找逍遥散人的 `fans_count`

#### eval_23 - 视频时长
- **原模式**：hardcode "03:45"
- **改进**：从 `favorites.json` 读取第一个视频的 `duration`

### 3. 开关状态类（3个）

#### eval_2 - 私信智能拦截状态
- **原模式**：hardcode "开启"
- **改进**：从 `settings.json` 读取 `message_smart_filter` 布尔值

#### eval_18 - 消息通知总开关
- **原模式**：hardcode "已开启"
- **改进**：从 `settings.json` 读取 `notification_master_switch` 布尔值

#### eval_28 - 定时关闭状态
- **原模式**：hardcode "不开启"
- **改进**：从 `settings.json` 读取 `timer_close_enabled` 和 `timer_close_time`

### 4. 配置读取类（2个）

#### eval_25 - 用户UID
- **原模式**：hardcode "1668348161"
- **改进**：从 `user_profile.json` 读取 `uid`

#### eval_31 - 会员到期时间
- **原模式**：检查关键词（到期、天、月等）
- **改进**：从 `user_profile.json` 读取 `vip_expire_date` 并计算剩余天数

### 5. 其他类型（5个）

#### eval_13 - 第四个相关视频的up主
- **原模式**：hardcode "游戏解说君"
- **改进**：从 `related_videos.json` 读取第4个视频的 `uploader`

#### eval_27 - 点赞最高评论的up主
- **原模式**：hardcode "法学小白"
- **改进**：从 `video_comments.json` 查找 `like_count` 最大的评论

#### eval_29 - 人数最少的两个直播间总人数
- **原模式**：hardcode "8623"
- **改进**：从 `live_streams.json` 排序并取前2个 `viewer_count` 之和

#### eval_30 - 会员购商品总销量
- **原模式**：hardcode "4963"
- **改进**：从 `shop_products.json` 累加所有 `sales_count`

## 技术特点

### 1. 错误处理
- ADB 命令超时处理（10秒超时）
- JSON 解析错误捕获
- 数据不完整时的回退验证

### 2. 数据备份
- 所有拉取的数据保存到 `backup_dir`
- 便于调试和审计

### 3. 日志输出
- 成功时打印期望值和实际值
- 失败时详细说明原因
- 警告信息提示回退场景

### 4. 格式兼容
- 数字类：支持整数、小数、带单位等格式
- 时间类：支持带前导0和不带前导0的格式
- 状态类：支持多种开关表达方式（开启/已开启/打开等）

## 未改进任务

保持原有验证逻辑的任务（11个）：
- eval_1, 3, 5, 7, 8, 9, 10, 12, 16, 20, 22, 26

这些任务主要是行为验证（通过 logcat 检查用户操作），不涉及数据计算。

## 使用说明

### APP 数据文件要求

APP 需要在以下位置提供 JSON 数据文件：
```
/data/data/com.example.bilibili/files/
├── related_videos.json
├── following_list.json
├── favorites.json
├── following_dynamics.json
├── video_comments.json
├── home_videos.json
├── shop_products.json
├── settings.json
├── user_profile.json
└── live_streams.json
```

### 调用方式保持不变

```python
from bilibili.eval_21 import validate_task_21

result = {
    "final_message": "有4个相关视频"
}

is_valid = validate_task_21(
    result=result, 
    device_id="emulator-5554", 
    backup_dir="/path/to/backup"
)
```

## 优势总结

1. **可维护性**：数据变化时无需修改代码
2. **可扩展性**：新任务可复用数据文件结构
3. **可测试性**：可以通过修改数据文件测试不同场景
4. **鲁棒性**：回退机制保证验证不会完全失败
5. **透明性**：详细日志便于问题排查
