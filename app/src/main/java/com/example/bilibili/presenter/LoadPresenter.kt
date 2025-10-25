package com.example.bilibili.presenter

import android.content.Context
import com.example.bilibili.model.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.IOException

/**
 * 离线缓存页面的业务逻辑处理类
 * 按照MVP模式，负责处理缓存数据的加载和管理
 */
class LoadPresenter(private val context: Context) {

    private val gson = Gson()

    /**
     * 缓存视频项数据类
     * 用于在UI中展示缓存视频信息
     */
    data class CacheItem(
        val cacheState: CacheState,
        val video: Video
    ) {
        fun getDisplayTitle(): String = video.title
        fun getDisplayUPName(): String = video.upMasterName
        fun getDisplaySize(): String = cacheState.getFormattedSize()
        fun getDisplayQuality(): String = cacheState.cacheQuality
        fun getDisplayStatus(): String = when (cacheState.cacheStatus) {
            CacheStatus.DOWNLOADING -> "下载中 ${cacheState.getFormattedProgress()}"
            CacheStatus.COMPLETED -> "已完成"
            CacheStatus.PAUSED -> "已暂停"
            CacheStatus.FAILED -> "下载失败"
            CacheStatus.DELETED -> "已删除"
        }
        fun getWatchProgressText(): String = "观看 ${cacheState.getFormattedWatchProgress()}"
    }

    /**
     * 加载缓存视频列表
     * @return 缓存视频项列表
     */
    fun loadCacheVideos(): List<CacheItem> {
        val cacheStateList = loadCacheStates()
        val videoList = loadVideos()

        return cacheStateList.mapNotNull { cacheState ->
            val video = videoList.find { it.videoId == cacheState.videoId }
            if (video != null) {
                CacheItem(cacheState, video)
            } else {
                null
            }
        }
    }

    /**
     * 从assets加载缓存状态数据
     */
    private fun loadCacheStates(): List<CacheState> {
        return try {
            val jsonString = readFromAssets("data/cache_videos.json")
            val type = object : TypeToken<List<CacheState>>() {}.type
            gson.fromJson(jsonString, type) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    /**
     * 从assets加载视频数据
     */
    private fun loadVideos(): List<Video> {
        return try {
            val jsonString = readFromAssets("data/videos.json")
            val type = object : TypeToken<List<Video>>() {}.type
            gson.fromJson(jsonString, type) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    /**
     * 从assets读取文件内容
     */
    private fun readFromAssets(filename: String): String {
        return try {
            context.assets.open(filename).bufferedReader().use { it.readText() }
        } catch (e: IOException) {
            throw Exception("无法读取文件: $filename", e)
        }
    }

    /**
     * 格式化缓存大小显示
     */
    fun formatCacheSize(sizeBytes: Long): String {
        return when {
            sizeBytes < 1024 -> "${sizeBytes}B"
            sizeBytes < 1024 * 1024 -> "${sizeBytes / 1024}KB"
            sizeBytes < 1024 * 1024 * 1024 -> "${sizeBytes / (1024 * 1024)}MB"
            else -> "${sizeBytes / (1024 * 1024 * 1024)}GB"
        }
    }

    /**
     * 格式化下载时间显示
     */
    fun formatDownloadTime(timestamp: Long): String {
        val now = System.currentTimeMillis()
        val diff = now - timestamp

        return when {
            diff < 60 * 1000 -> "刚刚"
            diff < 60 * 60 * 1000 -> "${diff / (60 * 1000)}分钟前"
            diff < 24 * 60 * 60 * 1000 -> "${diff / (60 * 60 * 1000)}小时前"
            diff < 7 * 24 * 60 * 60 * 1000 -> "${diff / (24 * 60 * 60 * 1000)}天前"
            else -> "${diff / (7 * 24 * 60 * 60 * 1000)}周前"
        }
    }

    /**
     * 获取缓存总览统计信息
     */
    fun getCacheSummary(cacheItems: List<CacheItem>): CacheSummary {
        val totalSize = cacheItems.sumOf { it.cacheState.cacheSize }
        val completedCount = cacheItems.count { it.cacheState.cacheStatus == CacheStatus.COMPLETED }
        val downloadingCount = cacheItems.count { it.cacheState.cacheStatus == CacheStatus.DOWNLOADING }
        val watchedCount = cacheItems.count { it.cacheState.isWatched }

        return CacheSummary(
            totalSize = totalSize,
            totalSizeFormatted = formatCacheSize(totalSize),
            totalCount = cacheItems.size,
            completedCount = completedCount,
            downloadingCount = downloadingCount,
            watchedCount = watchedCount
        )
    }

    /**
     * 缓存统计信息数据类
     */
    data class CacheSummary(
        val totalSize: Long,
        val totalSizeFormatted: String,
        val totalCount: Int,
        val completedCount: Int,
        val downloadingCount: Int,
        val watchedCount: Int
    )

    /**
     * 删除缓存
     */
    fun deleteCache(cacheId: String): Boolean {
        // 这里实现删除缓存的逻辑
        // 由于是虚拟APP，我们只是返回成功
        return true
    }

    /**
     * 暂停下载
     */
    fun pauseDownload(cacheId: String): Boolean {
        // 这里实现暂停下载的逻辑
        // 由于是虚拟APP，我们只是返回成功
        return true
    }

    /**
     * 恢复下载
     */
    fun resumeDownload(cacheId: String): Boolean {
        // 这里实现恢复下载的逻辑
        // 由于是虚拟APP，我们只是返回成功
        return true
    }

    /**
     * 播放缓存视频
     */
    fun playCacheVideo(cacheId: String): Boolean {
        // 这里实现播放缓存视频的逻辑
        // 由于是虚拟APP，我们只是返回成功
        return true
    }
}