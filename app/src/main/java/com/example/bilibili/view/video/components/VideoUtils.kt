package com.example.bilibili.view.video.components

/**
 * <öôëÒlb: mm:ss <	
 */
fun formatTime(timeMs: Int): String {
    if (timeMs <= 0) return "00:00"
    val totalSeconds = timeMs / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format("%02d:%02d", minutes, seconds)
}
