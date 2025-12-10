package com.example.bilibili.common.utils

/**
 * 时间格式化工具函数
 * 将毫秒转换为可读的时间格式
 */

/**
 * 格式化时间（毫秒转为"分:秒"格式）
 * @param milliseconds 毫秒数
 * @return 格式化后的时间字符串，如 "05:30"
 */
fun formatTime(milliseconds: Int): String {
    if (milliseconds <= 0) return "00:00"

    val totalSeconds = milliseconds / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60

    return String.format("%02d:%02d", minutes, seconds)
}

/**
 * 格式化时间（秒转为"分:秒"格式）
 * @param seconds 秒数
 * @return 格式化后的时间字符串，如 "05:30"
 */
fun formatTimeFromSeconds(seconds: Int): String {
    if (seconds <= 0) return "00:00"

    val minutes = seconds / 60
    val remainingSeconds = seconds % 60

    return String.format("%02d:%02d", minutes, remainingSeconds)
}

/**
 * 格式化时长（秒转为"小时:分:秒"或"分:秒"格式）
 * @param seconds 秒数
 * @return 格式化后的时间字符串，如 "1:05:30" 或 "05:30"
 */
fun formatDuration(seconds: Int): String {
    if (seconds <= 0) return "00:00"

    val hours = seconds / 3600
    val minutes = (seconds % 3600) / 60
    val remainingSeconds = seconds % 60

    return if (hours > 0) {
        String.format("%d:%02d:%02d", hours, minutes, remainingSeconds)
    } else {
        String.format("%02d:%02d", minutes, remainingSeconds)
    }
}
