package com.example.bilibili_sim.data.model

/**
 * 动态数据模型
 * 支持视频动态和文字动态两种类型
 */
data class Post(
    val postId: String,                     // 动态唯一标识
    val type: PostType,                     // 动态类型(视频/文字)
    val upMasterId: String,                 // 发布者UP主ID
    val upMasterName: String,               // 发布者UP主名称
    val upMasterAvatar: String,             // 发布者头像
    val publishTime: String,                // 发布时间(如"29分钟前")
    val content: String = "",               // 文字内容/视频标题
    val videoUrl: String = "",              // 视频URL(仅视频动态)
    val videoCover: String = "",            // 视频封面(仅视频动态)
    val videoDuration: String = "",         // 视频时长(仅视频动态)
    val videoPlayCount: String = "",        // 播放次数(仅视频动态)
    val images: List<String> = emptyList(), // 图片列表(仅文字动态)
    var forwardCount: Int = 0,              // 转发数
    var commentCount: Int = 0,              // 评论数
    var likeCount: Int = 0,                 // 点赞数
    var isLiked: Boolean = false,           // 是否已点赞
    var collectCount: Int = 0,              // 收藏数
    var isCollected: Boolean = false,       // 是否已收藏
    var coinCount: Int = 0,                 // 投币数
    var isCoined: Boolean = false,          // 是否已投币
    var lastUpdateTime: Long = System.currentTimeMillis()
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
     * 增加评论数
     */
    fun addComment() {
        commentCount++
        lastUpdateTime = System.currentTimeMillis()
    }

    /**
     * 增加转发数
     */
    fun addForward() {
        forwardCount++
        lastUpdateTime = System.currentTimeMillis()
    }

    /**
     * 切换收藏状态
     */
    fun toggleCollect() {
        isCollected = !isCollected
        if (isCollected) {
            collectCount++
        } else {
            collectCount = maxOf(0, collectCount - 1)
        }
        lastUpdateTime = System.currentTimeMillis()
    }

    /**
     * 投币（一次投一个币）
     */
    fun addCoin() {
        if (!isCoined) {
            isCoined = true
            coinCount++
            lastUpdateTime = System.currentTimeMillis()
        }
    }
}

/**
 * 动态类型枚举
 */
enum class PostType {
    VIDEO,      // 视频动态
    TEXT        // 文字动态
}
