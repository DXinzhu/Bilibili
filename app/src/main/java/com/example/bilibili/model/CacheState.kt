package com.example.bilibili.model

/**
 * 缓存状态数据模型
 * 用于维护视频的缓存信息和状态
 */
data class CacheState(
    val cacheId: String,                    // 缓存唯一标识
    val videoId: String,                    // 关联的视频ID
    val cacheSize: Long,                    // 缓存文件大小（字节）
    val cacheProgress: Float,               // 缓存进度 (0.0-1.0)
    val cacheQuality: String,               // 缓存画质质量
    val cacheStatus: CacheStatus,           // 缓存状态
    val cachePath: String,                  // 缓存文件路径
    val downloadTime: Long = System.currentTimeMillis(),        // 下载完成时间
    val lastWatchTime: Long = System.currentTimeMillis(),       // 最后观看时间
    val isWatched: Boolean = false,         // 是否已观看
    val watchProgress: Float = 0.0f         // 观看进度 (0.0-1.0)
) {
    /**
     * 格式化缓存大小显示
     */
    fun getFormattedSize(): String {
        return when {
            cacheSize < 1024 -> "${cacheSize}B"
            cacheSize < 1024 * 1024 -> "${cacheSize / 1024}KB"
            cacheSize < 1024 * 1024 * 1024 -> "${cacheSize / (1024 * 1024)}MB"
            else -> "${cacheSize / (1024 * 1024 * 1024)}GB"
        }
    }

    /**
     * 格式化缓存进度显示
     */
    fun getFormattedProgress(): String {
        return "${(cacheProgress * 100).toInt()}%"
    }

    /**
     * 格式化观看进度显示
     */
    fun getFormattedWatchProgress(): String {
        return "${(watchProgress * 100).toInt()}%"
    }

    /**
     * 更新缓存进度
     */
    fun updateProgress(newProgress: Float): CacheState {
        return this.copy(
            cacheProgress = newProgress.coerceIn(0f, 1f),
            cacheStatus = if (newProgress >= 1.0f) CacheStatus.COMPLETED else CacheStatus.DOWNLOADING
        )
    }

    /**
     * 更新观看进度
     */
    fun updateWatchProgress(newWatchProgress: Float): CacheState {
        return this.copy(
            watchProgress = newWatchProgress.coerceIn(0f, 1f),
            isWatched = newWatchProgress > 0f,
            lastWatchTime = System.currentTimeMillis()
        )
    }

    /**
     * 标记为已观看
     */
    fun markAsWatched(): CacheState {
        return this.copy(
            isWatched = true,
            lastWatchTime = System.currentTimeMillis()
        )
    }
}

/**
 * 缓存状态枚举
 */
enum class CacheStatus {
    DOWNLOADING,    // 下载中
    COMPLETED,      // 已完成
    PAUSED,         // 已暂停
    FAILED,         // 下载失败
    DELETED         // 已删除
}

/**
 * 缓存视频项数据类
 * 用于在缓存列表中展示
 */
data class CacheVideoItem(
    val cacheState: CacheState,              // 缓存状态信息
    val video: Video                         // 视频基本信息
) {
    /**
     * 获取显示用的标题
     */
    fun getDisplayTitle(): String = video.title

    /**
     * 获取显示用的UP主名称
     */
    fun getDisplayUPName(): String = video.upMasterName

    /**
     * 获取显示用的缓存大小
     */
    fun getDisplaySize(): String = cacheState.getFormattedSize()

    /**
     * 获取显示用的画质
     */
    fun getDisplayQuality(): String = cacheState.cacheQuality

    /**
     * 获取显示用的缓存状态
     */
    fun getDisplayStatus(): String {
        return when (cacheState.cacheStatus) {
            CacheStatus.DOWNLOADING -> "下载中 ${cacheState.getFormattedProgress()}"
            CacheStatus.COMPLETED -> "已完成"
            CacheStatus.PAUSED -> "已暂停"
            CacheStatus.FAILED -> "下载失败"
            CacheStatus.DELETED -> "已删除"
        }
    }
}