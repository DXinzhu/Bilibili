package com.example.bilibili.data.model

/**
 * 评论数据模型
 * 用于维护视频评论信息
 */
data class Comment(
    val commentId: String,                  // 评论唯一标识
    val videoId: String,                    // 所属视频ID
    val content: String,                    // 评论内容
    val authorId: String,                   // 评论作者ID
    val authorName: String,                 // 评论作者名称
    val userLevel: Int = 1,                 // 用户等级
    val publishTime: Long = System.currentTimeMillis(),  // 发布时间戳
    var likeCount: Int = 0,                 // 点赞数
    val parentCommentId: String? = null,    // 父评论ID（用于回复评论）
    val replyList: MutableList<Comment> = mutableListOf(),  // 回复列表
    var isLiked: Boolean = false            // 当前用户是否已点赞该评论
) {
    /**
     * 添加回复
     */
    fun addReply(reply: Comment) {
        replyList.add(reply)
    }

    /**
     * 切换点赞状态
     */
    fun toggleLike() {
        if (isLiked) {
            likeCount = maxOf(0, likeCount - 1)
            isLiked = false
        } else {
            likeCount++
            isLiked = true
        }
    }
}
