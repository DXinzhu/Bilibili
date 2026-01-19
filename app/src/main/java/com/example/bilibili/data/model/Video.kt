package com.example.bilibili.data.model

/**
 * 视频数据模型
 * 用于维护视频的基本信息和状态
 */
data class Video(
    val videoId: String,                    // 视频唯一标识
    val title: String,                      // 视频标题
    val coverImage: String = "",            // 视频封面图片路径
    val videoPath: String = "",             // 视频文件路径
    val upMasterId: String,                 // UP主ID
    val upMasterName: String,               // UP主名称
    val duration: Int = 0,                  // 视频时长（秒）
    var isLiked: Boolean = false,           // 是否已点赞
    var isFavorited: Boolean = false,       // 是否已收藏
    var isShared: Boolean = false,          // 是否已分享
    var likeCount: Int = 0,                 // 点赞数
    var dislikeCount: Int = 0,              // 不喜欢数
    var coinCount: Int = 0,                 // 投币数
    var favoriteCount: Int = 0,             // 收藏数
    var shareCount: Int = 0,                // 分享数
    val viewCount: Int = 0,                 // 播放量
    val commentCount: Int = 0,              // 评论数
    val onlineViewers: Int = 0,             // 在线观看人数
    val tags: List<String> = emptyList(),   // 话题标签
    val danmakuList: MutableList<Danmaku> = mutableListOf(),    // 弹幕列表
    val commentList: MutableList<Comment> = mutableListOf(),    // 评论列表
    val createdTime: Long = System.currentTimeMillis(),         // 视频创建时间
    var lastUpdateTime: Long = System.currentTimeMillis(),      // 最后更新时间
    val category: String = "",              // 视频分类 (cartoon=番剧)
    val ranking: Int = 0,                   // 排行榜排名 (0表示不在榜)
    val episodeInfo: String = ""            // 番剧集数信息 (如"更新至165话")
) {
    /**
     * 切换点赞状态
     */
    fun toggleLike() {
        isLiked = !isLiked
        if (isLiked) {
            likeCount++
        } else {
            likeCount = maxOf(0, likeCount - 1)
        }
        lastUpdateTime = System.currentTimeMillis()
    }

    /**
     * 切换不喜欢状态
     */
    fun toggleDislike(): Boolean {
        dislikeCount++
        lastUpdateTime = System.currentTimeMillis()
        return true
    }

    /**
     * 投币
     */
    fun addCoin() {
        coinCount++
        lastUpdateTime = System.currentTimeMillis()
    }

    /**
     * 切换收藏状态
     */
    fun toggleFavorite() {
        isFavorited = !isFavorited
        if (isFavorited) {
            favoriteCount++
        } else {
            favoriteCount = maxOf(0, favoriteCount - 1)
        }
        lastUpdateTime = System.currentTimeMillis()
    }

    /**
     * 分享
     */
    fun markAsShared() {
        isShared = true
        shareCount++
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
