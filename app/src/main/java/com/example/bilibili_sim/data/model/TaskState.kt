package com.example.bilibili_sim.data.model

/**
 * 任务状态管理器
 * 用于管理所有GUI Agent任务的状态和数据
 */
class TaskState {
    // 视频状态映射表 (videoId -> Video)
    private val videoMap: MutableMap<String, Video> = mutableMapOf()

    // UP主状态映射表 (upMasterId -> UPMaster)
    private val upMasterMap: MutableMap<String, UPMaster> = mutableMapOf()

    // 观看历史列表
    private val watchHistoryList: MutableList<WatchHistory> = mutableListOf()

    // 关注列表 (upMasterId列表)
    private val followingList: MutableSet<String> = mutableSetOf()

    // ========== 视频相关操作 ==========

    /**
     * 获取或创建视频对象
     */
    fun getOrCreateVideo(
        videoId: String,
        title: String,
        upMasterId: String,
        upMasterName: String
    ): Video {
        return videoMap.getOrPut(videoId) {
            Video(
                videoId = videoId,
                title = title,
                upMasterId = upMasterId,
                upMasterName = upMasterName
            )
        }
    }

    /**
     * 获取视频对象
     */
    fun getVideo(videoId: String): Video? {
        return videoMap[videoId]
    }

    /**
     * 点赞/取消点赞视频
     */
    fun toggleVideoLike(videoId: String): Boolean {
        val video = videoMap[videoId] ?: return false
        video.toggleLike()
        return video.isLiked
    }

    /**
     * 收藏/取消收藏视频
     */
    fun toggleVideoFavorite(videoId: String): Boolean {
        val video = videoMap[videoId] ?: return false
        video.toggleFavorite()
        return video.isFavorited
    }

    /**
     * 分享视频
     */
    fun shareVideo(videoId: String): Boolean {
        val video = videoMap[videoId] ?: return false
        video.markAsShared()
        return true
    }

    /**
     * 检查视频是否点赞
     */
    fun isVideoLiked(videoId: String): Boolean {
        return videoMap[videoId]?.isLiked ?: false
    }

    /**
     * 检查视频是否收藏
     */
    fun isVideoFavorited(videoId: String): Boolean {
        return videoMap[videoId]?.isFavorited ?: false
    }

    /**
     * 检查视频是否分享
     */
    fun isVideoShared(videoId: String): Boolean {
        return videoMap[videoId]?.isShared ?: false
    }

    // ========== 弹幕相关操作 ==========

    /**
     * 发送弹幕
     */
    fun sendDanmaku(danmaku: Danmaku): Boolean {
        val video = videoMap[danmaku.videoId] ?: return false
        video.addDanmaku(danmaku)
        return true
    }

    /**
     * 获取视频的弹幕列表
     */
    fun getVideoDanmakuList(videoId: String): List<Danmaku> {
        return videoMap[videoId]?.danmakuList ?: emptyList()
    }

    /**
     * 检查弹幕是否存在
     */
    fun isDanmakuExists(videoId: String, danmakuContent: String): Boolean {
        return videoMap[videoId]?.danmakuList?.any { it.content == danmakuContent } ?: false
    }

    // ========== 评论相关操作 ==========

    /**
     * 发布评论
     */
    fun publishComment(comment: Comment): Boolean {
        val video = videoMap[comment.videoId] ?: return false
        video.addComment(comment)
        return true
    }

    /**
     * 获取视频的评论列表
     */
    fun getVideoCommentList(videoId: String): List<Comment> {
        return videoMap[videoId]?.commentList ?: emptyList()
    }

    /**
     * 检查评论是否存在
     */
    fun isCommentExists(videoId: String, commentContent: String): Boolean {
        return videoMap[videoId]?.commentList?.any { it.content == commentContent } ?: false
    }

    // ========== UP主相关操作 ==========

    /**
     * 获取或创建UP主对象
     */
    fun getOrCreateUPMaster(
        upMasterId: String,
        name: String,
        avatarUrl: String = "",
        description: String = ""
    ): UPMaster {
        return upMasterMap.getOrPut(upMasterId) {
            UPMaster(
                upMasterId = upMasterId,
                name = name,
                avatarUrl = avatarUrl,
                description = description
            )
        }
    }

    /**
     * 获取UP主对象
     */
    fun getUPMaster(upMasterId: String): UPMaster? {
        return upMasterMap[upMasterId]
    }

    /**
     * 关注/取消关注UP主
     */
    fun toggleFollowUPMaster(upMasterId: String): Boolean {
        val upMaster = upMasterMap[upMasterId] ?: return false
        val followTime = upMaster.toggleFollow()

        if (upMaster.isFollowed) {
            followingList.add(upMasterId)
        } else {
            followingList.remove(upMasterId)
        }

        return upMaster.isFollowed
    }

    /**
     * 检查是否关注UP主
     */
    fun isFollowingUPMaster(upMasterId: String): Boolean {
        return followingList.contains(upMasterId)
    }

    /**
     * 获取关注列表
     */
    fun getFollowingList(): List<String> {
        return followingList.toList()
    }

    // ========== 观看历史相关操作 ==========

    /**
     * 添加观看历史
     */
    fun addWatchHistory(watchHistory: WatchHistory) {
        // 检查是否已存在相同视频的历史记录
        val existingIndex = watchHistoryList.indexOfFirst { it.videoId == watchHistory.videoId }
        if (existingIndex >= 0) {
            // 如果存在，更新记录
            watchHistoryList[existingIndex] = watchHistory
        } else {
            // 如果不存在，添加新记录
            watchHistoryList.add(watchHistory)
        }
    }

    /**
     * 获取观看历史列表
     */
    fun getWatchHistoryList(): List<WatchHistory> {
        return watchHistoryList.sortedByDescending { it.watchTime }
    }

    /**
     * 检查视频是否在观看历史中
     */
    fun isInWatchHistory(videoId: String): Boolean {
        return watchHistoryList.any { it.videoId == videoId }
    }

    /**
     * 获取特定视频的观看历史
     */
    fun getVideoWatchHistory(videoId: String): WatchHistory? {
        return watchHistoryList.find { it.videoId == videoId }
    }

    // ========== 统计和查询 ==========

    /**
     * 获取所有点赞的视频ID列表
     */
    fun getLikedVideoIds(): List<String> {
        return videoMap.filter { it.value.isLiked }.keys.toList()
    }

    /**
     * 获取所有收藏的视频ID列表
     */
    fun getFavoritedVideoIds(): List<String> {
        return videoMap.filter { it.value.isFavorited }.keys.toList()
    }

    /**
     * 清空所有状态（用于测试或重置）
     */
    fun clearAll() {
        videoMap.clear()
        upMasterMap.clear()
        watchHistoryList.clear()
        followingList.clear()
    }
}
