package com.example.bilibili.data.model

/**
 * 观看历史数据模型
 * 用于维护用户的视频观看历史记录
 */
data class WatchHistory(
    val historyId: String,                  // 历史记录唯一标识
    val videoId: String,                    // 视频ID
    val videoTitle: String,                 // 视频标题
    val upMasterId: String,                 // UP主ID
    val upMasterName: String,               // UP主名称
    val watchTime: Long = System.currentTimeMillis(),  // 观看时间戳
    var watchProgress: Float = 0f,          // 观看进度（0.0-1.0）
    var watchDuration: Long = 0,            // 观看时长（毫秒）
    var lastWatchPosition: Long = 0,        // 上次观看位置（毫秒）
    var isFinished: Boolean = false         // 是否看完
) {
    /**
     * 更新观看进度
     */
    fun updateProgress(currentPosition: Long, totalDuration: Long) {
        lastWatchPosition = currentPosition
        watchProgress = if (totalDuration > 0) {
            (currentPosition.toFloat() / totalDuration.toFloat()).coerceIn(0f, 1f)
        } else {
            0f
        }
        isFinished = watchProgress >= 0.95f  // 播放超过95%视为看完
    }

    /**
     * 更新观看时长
     */
    fun updateWatchDuration(duration: Long) {
        watchDuration += duration
    }
}
