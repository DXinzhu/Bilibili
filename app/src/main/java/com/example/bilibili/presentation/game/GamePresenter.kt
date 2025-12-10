package com.example.bilibili.presentation.game

import android.content.Context
import com.example.bilibili.data.model.Comment
import com.example.bilibili.data.model.Video
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.InputStreamReader

/**
 * 游戏搜索页面的Presenter
 * 负责加载游戏相关视频和评论数据
 */
class GamePresenter(private val context: Context) {

    private val gson = Gson()

    /**
     * 加载游戏相关的视频
     * 返回视频3、5、6
     */
    fun loadGameVideos(): List<Video> {
        return try {
            val inputStream = context.assets.open("data/videos.json")
            val reader = InputStreamReader(inputStream)
            val type = object : TypeToken<List<Video>>() {}.type
            val allVideos: List<Video> = gson.fromJson(reader, type)
            // 筛选出vid003, vid005, vid006
            allVideos.filter { it.videoId in listOf("vid003", "vid005", "vid006") }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    /**
     * 加载所有评论数据
     */
    fun loadComments(): List<Comment> {
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
     * 为游戏视频生成评论
     * 根据视频ID获取相关评论
     */
    fun getCommentsForVideo(videoId: String, allComments: List<Comment>): List<Comment> {
        // 生成游戏相关的评论
        return when (videoId) {
            "vid003" -> {
                // 为游戏实况视频生成评论
                listOf(
                    Comment(
                        commentId = "game_cmt001",
                        videoId = videoId,
                        content = "这个游戏操作真的太秀了！",
                        authorId = "user101",
                        authorName = "游戏玩家小王",
                        publishTime = System.currentTimeMillis() - 3600000,
                        likeCount = 234,
                        parentCommentId = null,
                        replyList = mutableListOf(),
                        isLiked = false
                    )
                )
            }
            "vid005" -> {
                // 为vid005生成评论
                listOf(
                    Comment(
                        commentId = "game_cmt002",
                        videoId = videoId,
                        content = "原神角色排行榜太实用了，终于知道该练哪些角色了！",
                        authorId = "user102",
                        authorName = "原神玩家",
                        publishTime = System.currentTimeMillis() - 7200000,
                        likeCount = 456,
                        parentCommentId = null,
                        replyList = mutableListOf(),
                        isLiked = true
                    )
                )
            }
            "vid006" -> {
                // 为英雄联盟视频生成评论
                listOf(
                    Comment(
                        commentId = "game_cmt003",
                        videoId = videoId,
                        content = "S13世界赛太精彩了！那个五杀简直神了，看得热血沸腾",
                        authorId = "user103",
                        authorName = "LOL电竞粉",
                        publishTime = System.currentTimeMillis() - 10800000,
                        likeCount = 789,
                        parentCommentId = null,
                        replyList = mutableListOf(),
                        isLiked = false
                    )
                )
            }
            else -> emptyList()
        }
    }

    /**
     * 格式化播放量显示
     */
    fun formatViewCount(count: Int): String {
        return when {
            count >= 10000 -> "${count / 10000}万"
            else -> count.toString()
        }
    }

    /**
     * 格式化时间显示
     */
    fun formatPublishTime(timestamp: Long): String {
        val now = System.currentTimeMillis()
        val diff = now - timestamp
        val days = diff / (1000 * 60 * 60 * 24)

        return when {
            days < 1 -> "今天"
            days < 30 -> "${days}天前"
            days < 365 -> "${days / 30}月前"
            else -> "${days / 365}年前"
        }
    }
}
