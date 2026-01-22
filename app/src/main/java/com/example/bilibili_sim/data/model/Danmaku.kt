package com.example.bilibili_sim.data.model

/**
 * 弹幕数据模型
 * 用于维护弹幕信息
 */
data class Danmaku(
    val danmakuId: String,                  // 弹幕唯一标识
    val videoId: String,                    // 所属视频ID
    val content: String,                    // 弹幕内容
    val senderId: String,                   // 发送者ID
    val senderName: String,                 // 发送者名称
    val sendTime: Long = System.currentTimeMillis(),  // 发送时间戳
    val videoTime: Float,                   // 视频播放时间点（秒）
    val color: String = "#FFFFFF",          // 弹幕颜色
    val fontSize: Int = 25,                 // 字体大小
    val type: DanmakuType = DanmakuType.SCROLL  // 弹幕类型
)

/**
 * 弹幕类型枚举
 */
enum class DanmakuType {
    SCROLL,         // 滚动弹幕
    TOP,            // 顶部固定弹幕
    BOTTOM          // 底部固定弹幕
}
