package com.example.bilibili.model

/**
 * 视频数据模型
 * 用于维护视频的基本信息和状态
 */
data class Video(
    val videoId: String,                    // 视频唯一标识
    val title: String,                      // 视频标题
    val upMasterId: String,                 // UP主ID
    val upMasterName: String,               // UP主名称
    var isLiked: Boolean = false,           // 是否已点赞
    var isFavorited: Boolean = false,       // 是否已收藏
    var isShared: Boolean = false,          // 是否已分享
    val danmakuList: MutableList<Danmaku> = mutableListOf(),    // 弹幕列表
    val commentList: MutableList<Comment> = mutableListOf(),    // 评论列表
    val createdTime: Long = System.currentTimeMillis(),         // 视频创建时间
    var lastUpdateTime: Long = System.currentTimeMillis()       // 最后更新时间
) {
    /**
     * 切换点赞状态
     */
    fun toggleLike() {
        isLiked = !isLiked
        lastUpdateTime = System.currentTimeMillis()
    }

    /**
     * 切换收藏状态
     */
    fun toggleFavorite() {
        isFavorited = !isFavorited
        lastUpdateTime = System.currentTimeMillis()
    }

    /**
     * 标记为已分享
     */
    fun markAsShared() {
        isShared = true
        lastUpdateTime = System.currentTimeMillis()
    }

    /**
     * 添加弹幕
     */
    fun addDanmaku(danmaku: Danmaku) {
        danmakuList.add(danmaku)
        lastUpdateTime = System.currentTimeMillis()
    }

    /**
     * 添加评论
     */
    fun addComment(comment: Comment) {
        commentList.add(comment)
        lastUpdateTime = System.currentTimeMillis()
    }
}
