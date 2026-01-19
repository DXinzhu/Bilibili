package com.example.bilibili.data.model

/**
 * UP主数据模型
 * 用于维护UP主的基本信息和关注状态
 */
data class UPMaster(
    val upMasterId: String,                 // UP主唯一标识
    val name: String,                       // UP主名称
    val avatarUrl: String = "",             // 头像URL
    val description: String = "",           // 个人简介
    var isFollowed: Boolean = false,        // 是否已关注
    var isMutualFollow: Boolean = false,    // 是否互粉
    var fansCount: Int = 0,                 // 粉丝数
    var videoCount: Int = 0,                // 视频数量
    val videoList: MutableList<String> = mutableListOf(),  // 视频ID列表
    val followTime: Long? = null,           // 关注时间（如果已关注）
    var lastUpdateTime: Long = System.currentTimeMillis()  // 最后更新时间
) {
    /**
     * 切换关注状态
     */
    fun toggleFollow(): Long? {
        isFollowed = !isFollowed
        lastUpdateTime = System.currentTimeMillis()

        // 更新粉丝数
        if (isFollowed) {
            fansCount++
            return lastUpdateTime
        } else {
            fansCount = maxOf(0, fansCount - 1)
            return null
        }
    }

    /**
     * 添加视频
     */
    fun addVideo(videoId: String) {
        if (!videoList.contains(videoId)) {
            videoList.add(videoId)
            videoCount++
            lastUpdateTime = System.currentTimeMillis()
        }
    }
}
