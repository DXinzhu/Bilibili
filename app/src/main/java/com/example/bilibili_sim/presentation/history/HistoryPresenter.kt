package com.example.bilibili_sim.presentation.history

import android.content.Context
import com.example.bilibili_sim.data.model.Video
import com.example.bilibili_sim.data.model.WatchHistory
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.InputStreamReader

/**
 * 历史记录页面的Presenter
 * 按照MVP模式实现，负责处理历史记录相关的业务逻辑
 */
class HistoryPresenter(private val context: Context) {

    private val gson = Gson()

    // 内存中维护的历史记录列表（支持删除操作）
    private var historyItemsCache: MutableList<HistoryItem>? = null

    /**
     * 历史记录与视频信息的合并数据类
     */
    data class HistoryItem(
        val history: WatchHistory,
        val video: Video?
    )

    /**
     * 加载观看历史数据
     */
    fun loadWatchHistory(): List<WatchHistory> {
        return try {
            val inputStream = context.assets.open("data/watch_history.json")
            val reader = InputStreamReader(inputStream)
            val type = object : TypeToken<List<WatchHistory>>() {}.type
            gson.fromJson(reader, type) ?: emptyList()
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    /**
     * 加载视频数据
     */
    private fun loadVideos(): List<Video> {
        return try {
            val inputStream = context.assets.open("data/videos.json")
            val reader = InputStreamReader(inputStream)
            val type = object : TypeToken<List<Video>>() {}.type
            gson.fromJson(reader, type) ?: emptyList()
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    /**
     * 获取完整的历史记录信息（包含视频封面等）
     * 按观看时间倒序排列（最新的在前）
     */
    fun getHistoryItems(): List<HistoryItem> {
        // 如果缓存存在，直接返回缓存
        if (historyItemsCache != null) {
            return historyItemsCache!!
        }

        val histories = loadWatchHistory()
        val videos = loadVideos()

        // 创建视频ID到视频对象的映射
        val videoMap = videos.associateBy { it.videoId }

        // 合并历史记录和视频信息
        historyItemsCache = histories.map { history ->
            HistoryItem(
                history = history,
                video = videoMap[history.videoId]
            )
        }.sortedByDescending { it.history.watchTime }.toMutableList() // 按观看时间倒序

        return historyItemsCache!!
    }

    /**
     * 删除指定的历史记录
     * @param historyId 要删除的历史记录ID
     * @return 删除成功返回true，否则返回false
     */
    fun deleteHistoryItem(historyId: String): Boolean {
        val cache = historyItemsCache ?: return false
        val itemToRemove = cache.find { it.history.historyId == historyId } ?: return false
        return cache.remove(itemToRemove)
    }

    /**
     * 删除指定的历史记录项
     * @param item 要删除的历史记录项
     * @return 删除成功返回true，否则返回false
     */
    fun deleteHistoryItem(item: HistoryItem): Boolean {
        val cache = historyItemsCache ?: return false
        return cache.remove(item)
    }

    /**
     * 批量删除历史记录
     * @param historyIds 要删除的历史记录ID列表
     * @return 成功删除的数量
     */
    fun deleteHistoryItems(historyIds: List<String>): Int {
        val cache = historyItemsCache ?: return 0
        var deletedCount = 0
        historyIds.forEach { historyId ->
            val itemToRemove = cache.find { it.history.historyId == historyId }
            if (itemToRemove != null && cache.remove(itemToRemove)) {
                deletedCount++
            }
        }
        return deletedCount
    }

    /**
     * 清空所有历史记录
     */
    fun clearAllHistory() {
        historyItemsCache?.clear()
    }

    /**
     * 根据分类筛选历史记录
     * @param category 分类类型: "全部", "视频", "直播", "专栏", "游戏"
     */
    fun filterByCategory(items: List<HistoryItem>, category: String): List<HistoryItem> {
        return when (category) {
            "全部" -> items
            "视频" -> items // 目前所有记录都是视频
            "直播" -> emptyList() // 暂无直播历史
            "专栏" -> emptyList() // 暂无专栏历史
            "游戏" -> emptyList() // 暂无游戏历史
            else -> items
        }
    }

    /**
     * 格式化观看时间为相对时间
     * @param watchTime 观看时间戳
     */
    fun formatWatchTime(watchTime: Long): String {
        val now = System.currentTimeMillis()
        val diff = now - watchTime

        val seconds = diff / 1000
        val minutes = seconds / 60
        val hours = minutes / 60
        val days = hours / 24

        return when {
            days > 0 -> "${days}天前"
            hours > 0 -> "${hours}小时前"
            minutes > 0 -> "${minutes}分钟前"
            else -> "刚刚"
        }
    }

    /**
     * 格式化观看时长
     * @param duration 时长（毫秒）
     */
    fun formatDuration(duration: Long): String {
        val seconds = (duration / 1000).toInt()
        val minutes = seconds / 60
        val remainingSeconds = seconds % 60
        return String.format("%02d:%02d", minutes, remainingSeconds)
    }

    /**
     * 格式化观看进度百分比
     */
    fun formatProgress(progress: Float): String {
        return String.format("%.0f%%", progress * 100)
    }
}
