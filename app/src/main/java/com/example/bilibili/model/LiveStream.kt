package com.example.bilibili.model

/**
 * 直播数据模型
 * 用于维护直播间的基本信息和状态
 */
data class LiveStream(
    val liveId: String,                     // 直播间唯一标识
    val title: String,                      // 直播标题
    val coverImage: String = "",            // 直播封面图片路径
    val upMasterId: String,                 // UP主ID
    val upMasterName: String,               // UP主名称
    val upMasterAvatar: String = "",        // UP主头像路径
    val viewerCount: Int = 0,               // 观看人数
    val tags: List<String> = emptyList(),   // 直播标签
    val isLive: Boolean = true,             // 是否正在直播
    val category: String = "",              // 直播分类
    val startTime: Long = System.currentTimeMillis(),  // 开播时间
    var lastUpdateTime: Long = System.currentTimeMillis()  // 最后更新时间
) {
    /**
     * 格式化观看人数显示
     * 例如: 1234 -> "1234人", 12345 -> "1.2万人"
     */
    fun getFormattedViewerCount(): String {
        return when {
            viewerCount >= 10000 -> "${String.format("%.1f", viewerCount / 10000.0)}万人"
            else -> "${viewerCount}人"
        }
    }

    /**
     * 获取直播时长（分钟）
     */
    fun getLiveDuration(): Long {
        return (System.currentTimeMillis() - startTime) / (1000 * 60)
    }
}
