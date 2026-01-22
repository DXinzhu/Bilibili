package com.example.bilibili_sim.presentation.content

import android.content.Context
import com.example.bilibili_sim.data.model.Comment
import com.example.bilibili_sim.data.model.Video
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.InputStreamReader
import java.text.SimpleDateFormat
import java.util.*

/**
 * 内容页面（评论页）的Presenter
 * 负责加载视频评论、处理评论互动等
 */
class ContentPresenter(private val context: Context) {

    private val gson = Gson()

    /**
     * 加载所有视频数据
     */
    private fun loadAllVideos(): List<Video> {
        return try {
            val inputStream = context.assets.open("data/videos.json")
            val reader = InputStreamReader(inputStream)
            val type = object : TypeToken<List<Video>>() {}.type
            gson.fromJson(reader, type)
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    /**
     * 加载所有评论数据
     */
    private fun loadAllComments(): List<Comment> {
        return try {
            val inputStream = context.assets.open("data/comments.json")
            val reader = InputStreamReader(inputStream)
            val type = object : TypeToken<List<Comment>>() {}.type
            gson.fromJson(reader, type)
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    /**
     * 根据videoId获取视频详情
     */
    fun getVideoById(videoId: String): Video? {
        val videos = loadAllVideos()
        return videos.find { it.videoId == videoId }
    }

    /**
     * 根据videoId获取评论列表
     */
    fun getCommentsByVideoId(videoId: String): List<Comment> {
        val comments = loadAllComments()
        return comments.filter { it.videoId == videoId && it.parentCommentId == null }
    }

    /**
     * 根据commentId获取评论详情
     */
    fun getCommentById(commentId: String): Comment? {
        val comments = loadAllComments()
        return comments.find { it.commentId == commentId }
    }

    /**
     * 格式化播放量
     * 例如: 489000 -> "48.9万"
     */
    fun formatViewCount(count: Int): String {
        return when {
            count >= 100000000 -> String.format("%.1f亿", count / 100000000.0)
            count >= 10000 -> String.format("%.1f万", count / 10000.0)
            else -> count.toString()
        }
    }

    /**
     * 格式化数字（点赞数等）
     * 例如: 40000 -> "4万", 3105 -> "3105"
     */
    fun formatCount(count: Int): String {
        return when {
            count >= 10000 -> String.format("%.1f万", count / 10000.0)
            else -> count.toString()
        }
    }

    /**
     * 格式化发布时间
     * 例如: "2025-10-21 19:35"
     */
    fun formatPublishTime(timestamp: Long): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA)
        return sdf.format(Date(timestamp))
    }

    /**
     * 格式化相对时间（用于评论显示）
     * 例如: "2小时前", "1天前"
     */
    fun formatRelativeTime(timestamp: Long): String {
        val now = System.currentTimeMillis()
        val diff = now - timestamp

        val minute = 60 * 1000L
        val hour = 60 * minute
        val day = 24 * hour
        val week = 7 * day
        val month = 30 * day

        return when {
            diff < minute -> "刚刚"
            diff < hour -> "${(diff / minute)}分钟前"
            diff < day -> "${(diff / hour)}小时前"
            diff < week -> "${(diff / day)}天前"
            diff < month -> "${(diff / week)}周前"
            else -> {
                val sdf = SimpleDateFormat("MM-dd", Locale.CHINA)
                sdf.format(Date(timestamp))
            }
        }
    }

    /**
     * 获取用户头像URL（简单处理，使用占位符）
     */
    fun getUserAvatarUrl(userId: String): String {
        // 这里可以扩展为从用户数据文件读取真实头像
        // 使用avatar文件夹中实际存在的头像，避免使用已被其他界面使用的头像
        // 已使用的头像: spring, autumn, fruit, cloud
        return when {
            userId == "user_current" -> "avatar/spring.jpg"  // 当前用户使用spring头像
            userId.contains("001") -> "avatar/summer.jpg"
            userId.contains("002") -> "avatar/winter.jpg"
            userId.contains("003") -> "avatar/farmer.jpg"
            userId.contains("004") -> "avatar/photo.jpg"
            userId.contains("005") -> "avatar/tea.jpg"
            userId.contains("006") -> "avatar/sowing.jpg"
            userId.contains("007") -> "avatar/tree.jpg"
            userId.contains("008") -> "avatar/walk.jpg"
            userId.contains("009") -> "avatar/flower.jpg"
            userId.contains("010") -> "avatar/bird.jpg"
            userId.contains("011") -> "avatar/gift.jpg"
            userId.contains("012") -> "avatar/vege.jpg"
            userId.contains("013") -> "avatar/teabuy.jpg"
            userId.contains("014") -> "avatar/fire.jpg"
            userId.contains("015") -> "avatar/folk.jpg"
            userId.contains("016") -> "avatar/music.jpg"
            userId.contains("017") -> "avatar/water.jpg"
            userId.contains("018") -> "avatar/mon.jpg"
            userId.contains("019") -> "avatar/min.jpg"
            userId.contains("020") -> "avatar/bike.jpg"
            userId.contains("021") -> "avatar/drink.jpg"
            userId.contains("022") -> "avatar/ice.jpg"
            userId.contains("023") -> "avatar/cul.jpg"
            userId.contains("024") -> "avatar/dinner.jpg"
            userId.contains("025") -> "avatar/corn.jpg"
            userId.contains("026") -> "avatar/leaf.jpg"
            userId.contains("027") -> "avatar/draw.jpg"
            userId.contains("028") -> "avatar/wine.jpg"
            userId.contains("029") -> "avatar/mark.jpg"
            userId.contains("030") -> "avatar/food.jpg"
            userId.contains("031") -> "avatar/hotel.jpg"
            else -> "avatar/video.jpg"  // 默认头像
        }
    }

    /**
     * 获取用户等级
     */
    fun getUserLevel(userId: String): Int {
        // 简单处理，可以根据userId生成等级
        return when {
            userId.contains("001") -> 6
            userId.contains("002") -> 5
            userId.contains("003") -> 4
            userId.contains("004") -> 3
            userId.contains("005") -> 2
            else -> 1
        }
    }

    /**
     * 切换评论点赞状态
     */
    fun toggleCommentLike(comment: Comment): Comment {
        val newComment = comment.copy()
        newComment.toggleLike()
        return newComment
    }

    /**
     * 发布新评论
     */
    fun addComment(videoId: String, content: String): Comment {
        // 生成新的评论ID
        val commentId = "comment_${System.currentTimeMillis()}"

        // 创建新评论对象
        return Comment(
            commentId = commentId,
            videoId = videoId,
            content = content,
            authorId = "user_current", // 当前用户ID
            authorName = "小明",  // 当前用户名称
            publishTime = System.currentTimeMillis(),
            likeCount = 0,
            parentCommentId = null,
            replyList = mutableListOf(),
            isLiked = false
        )
    }

    /**
     * 回复评论
     */
    fun addReply(parentComment: Comment, videoId: String, content: String): Comment {
        // 生成新的回复ID
        val replyId = "reply_${System.currentTimeMillis()}"

        // 创建新回复对象
        return Comment(
            commentId = replyId,
            videoId = videoId,
            content = content,
            authorId = "user_current", // 当前用户ID
            authorName = "小明",  // 当前用户名称
            publishTime = System.currentTimeMillis(),
            likeCount = 0,
            parentCommentId = parentComment.commentId,
            replyList = mutableListOf(),
            isLiked = false
        )
    }
}